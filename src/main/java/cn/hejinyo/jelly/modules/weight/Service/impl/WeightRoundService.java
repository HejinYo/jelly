package cn.hejinyo.jelly.modules.weight.Service.impl;

import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.common.utils.RedisUtils;
import cn.hejinyo.jelly.modules.sys.dao.SysLogDao;
import cn.hejinyo.jelly.modules.sys.model.SysLog;
import cn.hejinyo.jelly.modules.weight.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/6/2 20:48
 */
@Slf4j
@Service
public class WeightRoundService {
    /**
     * 进行数据初始化或重置操作的锁 key
     */
    private final static String INIT_LOCK = "allot:88:initLock";

    /**
     * 获取当前队列数据状态，如果是true，则可以开始算法，否则去获得锁，进行数据初始化操作
     */
    private final static String INIT_STATUS = "allot:88:initStatus";
    private final static String SORT_LIST = "allot:88:sort";
    private final static String FULL_SET = "allot:88:set";

    //private Jedis redisUtils;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Autowired
    private ListOperations<String, Object> listOperations;
    @Autowired
    private SetOperations<String, Object> setOperations;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SysLogDao sysLogDao;

    /**
     * 锁的有效时长 60秒
     */
    private static Long LOCK_EXPIRE = 1000 * 60L;
    /**
     * 拿不到锁的等待时长 1 秒
     */
    private static Long LOCK_SLEEP = 1000 * 1L;

    /**
     * 拿到锁以后，主动释放锁的延时 0.5秒
     */
    private static Long UN_LOCK = 500L;

    /**
     * 初始化数据
     */
    private List<User> init() {
        // 模拟数据库查询，排序好，权重 desc 排序号 asc
        List<User> list = new ArrayList<>();
        list.add(new User("A", 1005L, 5, 1));
        list.add(new User("B", 2003L, 3, 1));
        list.add(new User("C", 3003L, 3, 2));
        list.add(new User("D", 4002L, 2, 1));
        list.add(new User("E", 5001L, 1, 1));
        return list;
    }


    public User weater(int i) {
        // 检查状态或者进行初始化
        checkStatusOrInit();

        //获取并记录当前局版本号 TODO

        User choseUser = null;
        while (choseUser == null) {
            //队列右出对获得一个人员，并将它进行左入队
            Object userString = listOperations.rightPopAndLeftPush(SORT_LIST, SORT_LIST);

            // list长度为0
            if (userString == null) {
                return null;
            }

            // 给此人分配，查询此人是否有分配的权利
            User user = JsonUtil.fromJson(String.valueOf(userString), User.class);

            // 获取当前人员的固定权重
            Integer weigth = user.getWeigth();
            // incr 当前人员的分配权重
            Long newWgt = valueOperations.increment(getCwtKey(user.getUserId()), 1L);
            if (newWgt <= weigth) {
                //当前人员分配一个以后比固定权重小或者相等，说明分配正常，直接返回此人员ID
                choseUser = user;
                break;
            }
            //如果分配以后大于固定权限，说明已经有另外的线程分配了，此人已经超过分配限制，原子减一
            valueOperations.increment(getCwtKey(user.getUserId()), -1L);
            //把分配满了的放进set集合中，然后对比set集合长度和当前人员list长度，如果相等或者大于，说明都已经排满，进行数据初始化
            setOperations.add(FULL_SET, String.valueOf(user.getUserId()));
            long setSize = setOperations.size(FULL_SET);
            long listSize = listOperations.size(SORT_LIST);
            if (listSize <= setSize) {
                // 都已经达到饱和，重置数据
                log.debug("本局已经达到饱和，重置数据");
                // 设置团队状态不可用
                redisUtils.set(INIT_STATUS, false);
                //重置数据
                checkStatusOrInit();
            }
            //此局此人分配已经满了，将这个商机开始分配给下一个人
            //重复上面的步骤，如果回到这个已经分配满的人，则开始重新初始化数据，继续分配，直到找到一个分配的人为止
        }
        log.debug("商机:\t" + i + "\t -- > " + JsonUtil.toJson(choseUser));
        SysLog log = new SysLog();
        log.setUserName(choseUser.getUserName());
        log.setParams(JsonUtil.toJson(choseUser));
        log.setOperation(String.valueOf(i));
        sysLogDao.save(log);
        return choseUser;
    }

    /**
     * 检查状态或者进行初始化
     */
    private void checkStatusOrInit() {
        // 团队可用状态
        Boolean initStatus = redisUtils.get(INIT_STATUS, Boolean.class);

        // 直到团队状态可用为止
        while (initStatus == null || !initStatus) {
            //如果当前团队信息不可用,拿锁进行初始化
            // 锁的到期时间
            Long lockTimes = System.currentTimeMillis() + LOCK_EXPIRE;
            //向redis中获取初始化操作的锁，value为到期时间
            Boolean initLock = valueOperations.setIfAbsent(INIT_LOCK, String.valueOf(lockTimes));

            if (initLock) {
                log.debug("拿到了锁，进行团队信息初始化操作");

                // 设置锁的失效时间，避免死锁，但是可能会发送失败，还是造成死锁，暂时无解决办法，除非对锁的操作再进行加锁，或者设置锁的时候设置超时时间，一次请求
                redisTemplate.expire(INIT_LOCK, LOCK_EXPIRE, TimeUnit.MILLISECONDS);

                // 数据库查询的用户信息列表，包含重要的用户ID和固定权重值
                List<User> userList = init();

                //检查超时时间，如果时间超时，则放弃本次初始化，因为锁的到期时间到了，可能被别的线程先进行初始化了
                if (System.currentTimeMillis() <= lockTimes) {
                    //清除历史数据
                    redisUtils.delete(FULL_SET);
                    redisUtils.delete(SORT_LIST);
                    redisUtils.cleanKey("allot:88:cwt:*");

                    // 设置排序队列leftPush,先分配的放在队列右侧，所以选择左入队
                    userList.forEach(value -> {
                        listOperations.leftPush(SORT_LIST, JsonUtil.toJson(value));
                    });

                    // 设置团队状态可用
                    redisUtils.set(INIT_STATUS, true);

                    // 延时自动释放锁，不选择立即释放
                    redisTemplate.expire(INIT_LOCK, UN_LOCK, TimeUnit.MILLISECONDS);
                    //退出循环
                    break;
                }
            }

            log.debug("等待1秒进行重试");
            // 没有拿到锁，说明已经有线程正在进行初始化，或者锁被拿了但是线程掉线，不管怎么样，反正就是没有拿到锁
            // 休眠，然后检查团队信息状态，重复以上步骤，直到拿到锁或者团队信息可用为止
            try {
                Thread.sleep(LOCK_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //再次检查团队是否可用
            initStatus = redisUtils.get(INIT_STATUS, Boolean.class);
        }
    }


    /**
     * 用户当前权重redisKey
     */
    private String getCwtKey(Long userId) {
        return "allot:88:cwt:" + userId;
    }
}

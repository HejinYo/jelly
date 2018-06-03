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
     * 更新排序列表 的锁 key
     */
    private final static String INIT_LOCK = "allot:88:initLock";
    /**
     * 数据初始化状态
     */
    private final static String INIT_STATUS = "allot:88:init";
    /**
     * 重置操作的锁 key
     */
    private final static String RESET_LOCK = "allot:88:restLock";

    /**
     * 数据重置状态
     */
    private final static String RESET_STATUS = "allot:88:reset";
    private final static String SORT_LIST = "allot:88:sort";
    private final static String FULL_SET = "allot:88:set";
    private final static String RESET_VERSINO = "allot:88:version";

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
    private static Long LOCK_SLEEP = 500L;

    /**
     * 拿到锁以后，主动释放锁的延时 0.5秒
     */
    private static Long UN_LOCK = 1000 * 1L;

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
        Boolean hasSort = redisTemplate.hasKey(SORT_LIST);
        if (!hasSort) {
            log.info("排序队列Key不存在进行初始化");
            // 设置初始状态
            valueOperations.setIfAbsent(INIT_STATUS, String.valueOf(true));
            //排序队列Key不存在进行初始化
            initSort();
        }

        // 检查数据初始化状态，如果需要对排序序列进行初始化，需要修改此标识，让所有线程进行等待，对排序序列重操作以后，恢复
        Boolean initStatus = redisTemplate.hasKey(INIT_STATUS);
        while (initStatus) {
            initStatus = redisTemplate.hasKey(INIT_STATUS);
            // 休眠，然后检查团队初始状态，重复以上步骤，直到拿到锁或者初始状态可用为止
            log.info("等待排序序列操作完成");
            try {
                Thread.sleep(LOCK_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //获取并记录当前局版本号 TODO
        while (true) {


            //队列右出对获得一个人员，并将它进行左入队
            Object userString = listOperations.rightPopAndLeftPush(SORT_LIST, SORT_LIST);

            // list长度为0
            if (userString == null) {
                log.info("商机:\t " + i + "list长度为0");
                return null;
            }

            // 给此人分配，查询此人是否有分配的权利
            User user = JsonUtil.fromJson(String.valueOf(userString), User.class);

            // 获取当前人员的固定权重
            Integer weigth = user.getWeigth();
            // incr 当前人员的分配权重
            Long newWgt = valueOperations.increment(getCwtKey(user.getUserId()), 1L);

            if (newWgt <= weigth) {
                //当前人员分配一个以后比固定权重小或者相等，说明分配正常，直接返回此人员
                // log.info("商机:\t" + i + "\t -- > " + JsonUtil.toJson(user));
                SysLog log = new SysLog();
                log.setUserName(user.getUserName());
                log.setParams(JsonUtil.toJson(user));
                log.setOperation(String.valueOf(i));
                sysLogDao.save(log);
                return user;
            }

            //把分配满了的放进set集合中，然后对比set集合长度和当前人员list长度，如果相等或者大于，说明都已经排满，进行数据初始化
            setOperations.add(FULL_SET, String.valueOf(user.getUserId()));
            long listSize = listOperations.size(SORT_LIST);
            // 当前版本号
            Long version = Long.valueOf(valueOperations.get(RESET_VERSINO));
            long setSize = setOperations.size(FULL_SET);
            if (listSize <= setSize) {
                //重置数据
                resetData(version);
            }
            //此局此人分配已经满了，将这个商机开始分配给下一个人
            //重复上面的步骤，如果回到这个已经分配满的人，则开始重新初始化数据，继续分配，直到找到一个分配的人为止
        }
    }


    /**
     * 进行数据重置，一局分配完成
     */
    private void resetData(Long version) {
        while (true) {

            // 等待版本号升级
            Long newVer = Long.valueOf(valueOperations.get(RESET_VERSINO));
            if (newVer > version) {
                break;
            }

            // 锁的到期时间
            Long lockTimes = System.currentTimeMillis() + LOCK_EXPIRE;
            //向redis中获取初始化操作的锁，value为到期时间
            Boolean resetLock = valueOperations.setIfAbsent(RESET_LOCK, String.valueOf(lockTimes));

            if (newVer.equals(version) && resetLock) {
                log.info("当前版本号：{}", version);

                // 设置锁的失效时间，避免死锁，但是可能会发送失败，还是造成死锁，暂时无解决办法，除非对锁的操作再进行加锁，或者设置锁的时候设置超时时间，一次请求
                redisTemplate.expire(RESET_LOCK, LOCK_EXPIRE, TimeUnit.MILLISECONDS);

                //检查超时时间，如果时间超时，则放弃本次初始化，因为锁的到期时间到了，可能被别的线程先进行初始化了
                if (System.currentTimeMillis() <= lockTimes) {
                    //删除已经分配满了的队列
                    redisUtils.delete(FULL_SET);
                    //已经分配的计数器
                    redisUtils.cleanKey("allot:88:cwt:*");
                    // 延时自动释放锁，不选择立即释放
                    redisTemplate.expire(RESET_LOCK, UN_LOCK, TimeUnit.MILLISECONDS);
                    // 增加版本号
                    valueOperations.increment(RESET_VERSINO, 1L);
                    //退出循环
                    break;
                }
            }

            // 休眠，然后检查团队信息状态，重复以上步骤，直到拿到锁或者团队信息可用为止
            try {
                Thread.sleep(LOCK_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从数据库中把排序列表写入redis
     */
    private void initSort() {
        while (true) {
            // 团队初始状态，用于控制是否初始化数据
            Boolean initStatus = redisTemplate.hasKey(INIT_STATUS);

            // 初始状态不存在为止
            if (!initStatus) {
                break;
            }

            // 锁的到期时间
            Long lockTimes = System.currentTimeMillis() + LOCK_EXPIRE;
            //向redis中获取初始化操作的锁，value为到期时间
            Boolean initLock = valueOperations.setIfAbsent(INIT_LOCK, String.valueOf(lockTimes));
            if (initLock) {
                log.info("拿到了锁，进行初始化操作");
                // 设置锁的失效时间，避免死锁，但是可能会发送失败，还是造成死锁，暂时无解决办法，除非对锁的操作再进行加锁，或者设置锁的时候设置超时时间，一次请求
                redisTemplate.expire(INIT_LOCK, LOCK_EXPIRE, TimeUnit.MILLISECONDS);

                // 数据库查询的用户信息列表，包含重要的用户ID和固定权重值
                List<User> userList = init();

                //检查超时时间，如果时间超时，则放弃本次初始化，因为锁的到期时间到了，可能被别的线程先进行初始化了
                if (System.currentTimeMillis() <= lockTimes) {

                    redisUtils.delete(SORT_LIST);
                    // 设置排序队列leftPush,先分配的放在队列右侧，所以选择左入队
                    userList.forEach(value -> {
                        listOperations.leftPush(SORT_LIST, JsonUtil.toJson(value));
                    });
                    redisUtils.set(RESET_VERSINO, 1);
                    // 设置团队重置状态为false,直接删除key
                    redisUtils.delete(INIT_STATUS);
                    // 延时自动释放锁，不选择立即释放
                    redisTemplate.expire(INIT_LOCK, UN_LOCK, TimeUnit.MILLISECONDS);
                    //退出循环
                    break;
                }
            }

            // 休眠，然后检查团队初始状态，重复以上步骤，直到拿到锁或者初始状态可用为止
            try {
                Thread.sleep(LOCK_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用户当前权重redisKey
     */
    private String getCwtKey(Long userId) {
        return "allot:88:cwt:" + userId;
    }
}

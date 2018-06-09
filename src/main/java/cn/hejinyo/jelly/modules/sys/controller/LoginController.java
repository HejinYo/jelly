package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.UserMenuDTO;
import cn.hejinyo.jelly.modules.sys.service.LoginService;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统用户登录控制器
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/16 22:14
 */
@RestController
@RequestMapping("/")
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SysResourceService sysResourceService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ShiroService shiroService;


    /**
     * 执行登录,返回userToken
     */
    @PostMapping(value = "/login")
    public Result login(@RequestBody LoginUserDTO loginUser) {
        String userName = loginUser.getUserName();
        String userPwd = loginUser.getUserPwd();
        if (StringUtils.isEmpty(userName)) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isEmpty(userPwd)) {
            return Result.error("密码不能为空");
        }
        return Result.ok(loginService.doLogin(userName, userPwd));
    }

    /**
     * 获得当前用户redis中的用户信息
     */
    @GetMapping(value = "/logout")
    public Result logout(HttpServletRequest request) {
        String userToken = request.getHeader(Constant.AUTHOR_PARAM);
        if (StringUtils.isEmpty(userToken)) {
            return Result.ok();
        }
        try {
            //token中获取用户名
            Integer userId = Tools.tokenInfoInt(userToken, Constant.JWT_TOKEN_USERID);
            //查询缓存中的用户信息
            LoginUserDTO userDTO = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_TOKEN, LoginUserDTO.class);
            if (null != userDTO) {
                //验证token是否有效
                Tools.verifyToken(userToken, userDTO.getUserPwd());
                //清除用户原来所有缓存
                redisUtils.delete(RedisKeys.storeUser(userId));
            }
            return Result.ok();
        } catch (Exception e) {
            return Result.ok();
        }
    }

    /**
     * 获得当前用户redis中的用户信息
     */
    @GetMapping("/userInfo")
    public Result getToken() {
        return Result.ok(redisUtils.hget(RedisKeys.storeUser(loginUserId()), RedisKeys.USER_TOKEN, LoginUserDTO.class));
    }

    /**
     * 获得用户菜单和权限
     */
    @GetMapping("/userMenu")
    public Map<String, Object> userMenu() {
        List<UserMenuDTO> menu = sysResourceService.getUserMenuTree(loginUserId());
        Set<String> perm = shiroService.getUserPermSet(loginUserId());
        Set<String> role = shiroService.getUserRoleSet(loginUserId());
        return Result.ok().add("menu", menu).add("perm", perm).add("role", role);
    }

    /**
     * 解锁屏幕，验证密码
     */
    @PostMapping("/unlock")
    public Result lockScreen(@RequestBody LoginUserDTO loginUser) {
        if (getCurrentUser().getUserPwd().equals(ShiroUtils.userPassword(loginUser.getUserPwd(), getCurrentUser().getUserSalt()))) {
            return Result.ok();
        }
        return Result.error("密码错误");
    }

    /**
     * 单文件上传获取token
     */
 /*   @GetMapping(value = "/fileUploadToken")
    public Result uploadUserAvatar() {
        CloudStorageConfig0ld config = new CloudStorageConfig0ld();
        config.setQiniuAccessKey("GqZQG6TvEZGPkCXzm5O7QN1jipLdeI4CXXsR6N3G");
        config.setQiniuSecretKey("qodIX8q2zqaX4eSAiOvcS1YNLeKU_cxyNtSFkWf9");
        config.setQiniuBucketName("skye-user-avatar");
        String token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).uploadToken(config.getQiniuBucketName());
        return Result.ok(UUID.randomUUID().toString(), token);
    }*/


}

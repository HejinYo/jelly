package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.annotation.SysLogger;
import cn.hejinyo.jelly.common.consts.Constant;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.common.validator.RestfulValid;
import cn.hejinyo.jelly.modules.sys.model.dto.LoginUserDTO;
import cn.hejinyo.jelly.modules.sys.model.dto.PhoneLoginDTO;
import cn.hejinyo.jelly.modules.sys.service.LoginService;
import cn.hejinyo.jelly.modules.sys.service.ShiroService;
import cn.hejinyo.jelly.modules.sys.service.SysResourceService;
import cn.hejinyo.jelly.modules.sys.shiro.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
     * 发送手机登录验证码,暂时不做相关限制
     */
    @PostMapping(value = "/login/code/{phone}")
    @SysLogger("发送验证码")
    public Result sendLoginCode(@PathVariable("phone") String phone) {
        if (loginService.sendPhoneCode(phone)) {
            return Result.ok();
        }
        return Result.error("验证码发送失败");
    }

    /**
     * 手机号,验证码登录,返回userToken
     */
    @PostMapping(value = "/login/phone")
    @SysLogger("手机号登录")
    public Result phoneLogin(@Validated(RestfulValid.POST.class) @RequestBody PhoneLoginDTO phoneLogin) {
        return Result.result(loginService.doLogin(loginService.phoneLogin(phoneLogin)));
    }


    /**
     * 执行登录,返回userToken
     */
    @PostMapping(value = "/login")
    public Result login(@Validated(RestfulValid.POST.class) @RequestBody LoginUserDTO loginUser) {
        return Result.result(loginService.doLogin(loginService.checkUser(loginUser)));
    }

    /**
     * 登出
     */
    @PutMapping(value = "/logout")
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
     * 获得用户菜单
     */
    @GetMapping("/userMenu")
    public Result userMenu() {
        return Result.ok(sysResourceService.getUserMenuTree(loginUserId()));
    }

    /**
     * 获得用户权限
     */
    @GetMapping("/userPerm")
    public Result userPerm() {
        return Result.ok(shiroService.getUserPermSet(loginUserId()));
    }

    /**
     * 获得用户角色
     */
    @GetMapping("/userRole")
    public Result userRole() {
        return Result.ok(shiroService.getUserRoleSet(loginUserId()));
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

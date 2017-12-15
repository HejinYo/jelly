package cn.hejinyo.jelly.modules.sys.controller;

import cn.hejinyo.jelly.common.cloudstorage.CloudStorageConfig;
import cn.hejinyo.jelly.common.consts.StatusCode;
import cn.hejinyo.jelly.common.utils.*;
import cn.hejinyo.jelly.modules.sys.model.dto.CurrentUserDTO;
import cn.hejinyo.jelly.modules.sys.service.SysUserService;
import cn.hejinyo.jelly.modules.sys.shiro.token.StatelessLoginToken;
import com.qiniu.util.Auth;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


//@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/")
public class LoginController extends BaseController {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SysUserService sysUserService;


    /**
     * 执行登录,返回userToken
     */
    @PostMapping(value = "/login")
    public Result login(@RequestBody CurrentUserDTO loginUser, HttpServletRequest request) {
        try {
            StatelessLoginToken loginToken = new StatelessLoginToken(loginUser.getUserName(), loginUser.getUserPwd());
            //委托给Realm进行登录
            SecurityUtils.getSubject().login(loginToken);
            CurrentUserDTO userDTO = (CurrentUserDTO) SecurityUtils.getSubject().getPrincipal();
            //创建jwt token
            String token = Tools.createToken(12, userDTO.getUserId(), userDTO.getUserName(), userDTO.getUserPwd());
            userDTO.setUserToken(token);
            userDTO.setLoginIp(WebUtils.getIpAddr(request));
            //token写入缓存
            redisUtils.set(RedisKeys.getTokenCacheKey(userDTO.getUserName()), userDTO, 1800);
            //清除授权缓存
            redisUtils.delete(RedisKeys.getAuthCacheKey(userDTO.getUserName()));
            sysUserService.updateUserLoginInfo(userDTO);
            return Result.ok(StatusCode.SUCCESS, userDTO);
        } catch (Exception e) {
            //登录失败
            logger.error("[" + loginUser.getUserName() + "] 登录失败：", e.getMessage());
            if (e instanceof UnknownAccountException) {
                return Result.error(StatusCode.LOGIN_USER_NOEXIST);
            }
            if (e instanceof IncorrectCredentialsException) {
                return Result.error(StatusCode.LOGIN_PASSWORD_ERROR);
            }
            if (e instanceof ExcessiveAttemptsException) {
                return Result.error(StatusCode.LOGIN_EXCESSIVE_ATTEMPTS);
            }
            if (e instanceof LockedAccountException) {
                return Result.error(StatusCode.LOGIN_USER_LOCK);
            }
            return Result.error(StatusCode.LOGIN_FAILURE);
        }
    }

    /**
     * 退出登录
     *
     * @return
     */
    @GetMapping(value = "/logout")
    public Result logout() {
        return Result.ok();
    }


    /**
     * 单文件上传获取token
     */
    @GetMapping(value = "/fileUploadToken")
    public Result uploadUserAvatar() {
        CloudStorageConfig config = new CloudStorageConfig();
        config.setQiniuAccessKey("GqZQG6TvEZGPkCXzm5O7QN1jipLdeI4CXXsR6N3G");
        config.setQiniuSecretKey("qodIX8q2zqaX4eSAiOvcS1YNLeKU_cxyNtSFkWf9");
        config.setQiniuBucketName("skye-user-avatar");
        String token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).uploadToken(config.getQiniuBucketName());
        return Result.ok(UUID.randomUUID().toString(), token);
    }


}

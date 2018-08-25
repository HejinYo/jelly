package cn.hejinyo.jelly.modules.oauth.controller;

import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.modules.oauth.model.dto.TencentUserDTO;
import cn.hejinyo.jelly.modules.oauth.service.TencentOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : heshuangshuang
 * @date : 2018/8/3 16:48
 */
@RestController
@RequestMapping("/oauth")
public class OauthController {
    @Autowired
    private TencentOauthService tencentOauthService;

    /**
     * 登录重定向的url
     */
    @GetMapping("/login")
    public Result loginPage() {
        return Result.result(tencentOauthService.loginUrl());
    }

    @GetMapping("/v2")
    public Result LoginRedirect(@RequestParam("code") String code, @RequestParam("state") String state) {
        TencentUserDTO userDTO = tencentOauthService.loginRedirect(code, state);
        if (userDTO != null) {
            return Result.ok("登录测试成功", userDTO.getNickname() + ":谢谢你的登录，目前还没结合业务，mua~>");
        }
        return Result.ok("登录测试失败");

    }
}

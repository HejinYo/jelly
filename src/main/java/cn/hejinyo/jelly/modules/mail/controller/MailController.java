package cn.hejinyo.jelly.modules.mail.controller;

import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.modules.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author : heshuangshuang
 * @date : 2018/7/31 18:03
 */
@RestController
@RequestMapping("/test")
public class MailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/mail")
    public Result test(@RequestParam String addr) {
        //创建邮件正文
        HashMap<String, Object> context = new HashMap<>();
        context.put("mail", addr);
        mailService.sendTemplateMail("emailTemplate", context, addr, "Lolipop：请激活你的帐号");
        return Result.ok();
    }
}

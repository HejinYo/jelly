package cn.hejinyo.jelly.modules.mail.service;

import java.util.HashMap;

/**
 * @author : heshuangshuang
 * @date : 2018/7/31 17:46
 */
public interface MailService {
    void sendSimpleMail(String to, String subject, String content);

    void sendHtmlMail(String to, String subject, String content);

    void sendTemplateMail(String template, HashMap<String, Object> param, String to, String subject);
}

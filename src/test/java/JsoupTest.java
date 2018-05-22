import cn.hejinyo.jelly.common.exception.InfoException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.http.protocol.HTTP.USER_AGENT;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/5/22 22:25
 */
@Slf4j
public class JsoupTest {

    public static void main(String[] args) {
        System.out.println(test3());
    }

    private static String test3() {
        HashMap<String, String> map = new HashMap<>();
        map.put("hotline", "22042057");
        map.put("cno", "82481");
        map.put("password", "hq!!2018happy");
        map.put("protocol", "https:");
        try {
            String URL = "http://www.clink.cn/agent_login";
            Connection.Response rs = Jsoup.connect(URL)
                    .postDataCharset("GB2312")//编码格式
                    .data(map)//请求参数
                    .userAgent(USER_AGENT)
                    .timeout(10000)//超时
                    .method(Connection.Method.POST)
                    .execute();

            String html = rs.body();

            Optional<Map<String, String>> cookie = Optional.of(rs.cookies());
            //遍历cookie
            cookie.ifPresent(map1 -> map1.forEach((key, value) -> {
                log.debug("{}:{}", key, value);
            }));

            return cookie.map(m -> m.get("SECURITY_TOKEN")).orElseGet(() -> {
                //解析字符串获得document对象
                Document doc = Jsoup.parse(html);
                //没有Token,显示失败信息
                Optional<Element> errorSpan = Optional.ofNullable(doc.getElementById("errorSpan"));
                return errorSpan.map(Element::html).orElseGet(() -> {
                    log.error("天润登陆失败，未知原因：\n{}\n\n", html);
                    throw new InfoException("天润登陆失败，未知原因：" + html);
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new InfoException("请求天润登录发生未知异常：" + e.getMessage());
        }
    }

    public static void test2() {
        //暂时用字符串代替从页面取来的dom结构
        String html = "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<!-- 头部 end -->\n" +
                "\t<div class=\"bar\">\n" +
                "\t\t<!-- 中部 start -->\n" +
                "\t\t<div class=\"center\">\n" +
                "\t\t\t<img src=\"/images/login_sling.png\" style=\"float: right; margin-top: -137px; margin-right: 84px\"></img>\n" +
                "\t\t\t<div class=\"barpic\"></div>\n" +
                "\t\t\t<div class=\"login\" id=\"naTab\">\n" +
                "\t\t\t\t<ul class=\"logintab\">\n" +
                "\t\t\t\t\t<li class=\"cur\" id=\"agentTab\">座席登录</li>\n" +
                "\t\t\t\t\t<li class=\"nor\" id=\"consoleTab\">管理员登录</li>\n" +
                "\t\t\t\t</ul>\n" +
                "\t\t\t\t<div class=\"loginbox\">\n" +
                "\t\t\t\t\t<form id=\"loginForm\" method=\"post\">\n" +
                "\t\t\t\t\t\t<input id=\"returnUrl\" name=\"returnUrl\" type=\"hidden\" value=\"\" />\n" +
                "\t\t\t\t\t\t<input id=\"protocol\" name=\"protocol\" type=\"hidden\" value=\"\" />\n" +
                "\t\t\t\t\t\t<div class=\"form_item\">\n" +
                "\t\t\t\t\t\t\t<input id=\"hotline\" name=\"hotline\" type=\"text\" placeholder=\"热线号码\" class=\"text\" maxlength=\"11\" value=\"22042057\" />\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"form_item\" id=\"cnoFormItem\">\n" +
                "\t\t\t\t\t\t\t<input id=\"cno\" name=\"cno\" type=\"text\" placeholder=\"座席号\" class=\"text\" maxlength=\"6\"  value=\"8248\"/>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"form_item\" id=\"usernameFormItem\" style=\"display:none;\">\n" +
                "\t\t\t\t\t\t\t<input id=\"username\" name=\"username\" type=\"text\" placeholder=\"管理员帐号\" class=\"text\" maxlength=\"20\" value=\"\" />\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"form_item\">\n" +
                "\t\t\t\t\t\t\t<input id=\"password\" name=\"password\" type=\"password\" placeholder=\"密码\" class=\"text\"> \n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"form_item\" id=\"securityCodeFormItem\" style=\"display:none;\">\n" +
                "\t\t\t\t\t\t\t<input id=\"securityCode\" name=\"securityCode\" type=\"text\" style=\"margin-top: 6px; height: 34px\" class=\"text_code\" placeholder=\"验证码\" maxlength=\"4\">\n" +
                "\t\t\t\t\t\t\t<img id=\"securityCodeImg\" border=\"0\" alt=\"验证码\" title=\"点击更新验证码\" class=\"code\" />\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"form_item\">\n" +
                "\t\t\t\t\t\t\t<input id=\"loginBtn\" class=\"loginbtn\" type=\"submit\" value=\"登录系统\" style=\"font-size: 20px;\">\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div id=\"errorSpan\" class=\"form_msg\">帐号或密码错误，您还有4次机会重试。</div>\n" +
                "\t\t\t\t\t</form>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "</body>\n" +
                "</html>";
        //解析字符串获得document对象
        Document doc = Jsoup.parse(html);
        //从doc对象中取得id为hehe的元素然后获取其中的文字值
        System.out.println(doc.getElementById("agentTab").text());
        //从doc对象中取得id为hehe的元素然后获取其中的html对象
        System.out.println(doc.getElementById("errorSpan").html());
        //从doc对象中取得id为hehe的元素然后回溯出整体
        // System.out.println(doc.getElementById("agentTab").root());
        //你可以把document对象看做后台版的js,通过class找，name找甚至利用jQuery都是支持的
    }

    public static void test1() {
        //暂时用字符串代替从页面取来的dom结构
        String html =
                "<html>" +
                        "<head>" +
                        "<title>First parse</title>" +
                        "</head>" +
                        "<body>" +
                        "<p id='hehe'>Parsed HTML into a doc.</p>" +
                        "</body>" +
                        "</html>";
        //解析字符串获得document对象
        Document doc = Jsoup.parse(html);
        //从doc对象中取得id为hehe的元素然后获取其中的文字值
        System.out.println(doc.getElementById("hehe").text());
        //从doc对象中取得id为hehe的元素然后获取其中的html对象
        System.out.println(doc.getElementById("hehe").html());
        //从doc对象中取得id为hehe的元素然后回溯出整体
        System.out.println(doc.getElementById("hehe").root());
        //你可以把document对象看做后台版的js,通过class找，name找甚至利用jQuery都是支持的
    }
}

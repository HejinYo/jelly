import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/5/22 22:10
 */
public class CookiesTest {
    public static void main(String[] args) {
        doPost(new HashMap<String, String>(), "");
    }

    public static String doPost(Map<String, String> map, String charset) {
        CloseableHttpClient httpClient;
        HttpPost httpPost;
        String result = null;
        try {
            CookieStore cookieStore = new BasicCookieStore();
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            httpPost = new HttpPost("http://www.clink.cn/agent_login?hotline=22042057&cno=8248&password=hq!!2018happy&protocol=https:");
          /*  List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }*/
            httpClient.execute(httpPost);
            String SECURITY_TOKEN = null;
            String SECURITY_TOKEN_AGENT = null;
            List<Cookie> cookies = cookieStore.getCookies();
            /**
             * AWSELB=53E9031F1A6DDFE2621105597F3EB7AFEA231314B2AC8F81B707236C0B6C2D7E53CFD222E7AE815D2C0A1C4540B46E5B9F39612CBE40D83F13DA31A5127FC5A76863015A1E
             * CNO=8248
             * ENTERPRISEID=3004297
             * HOTLINE=22042057
             * PLATFORM=58
             * SECURITY_TOKEN=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGllbnRJZCI6MTM2MDg2LCJjbm8iOiI4MjQ4IiwiaXNzIjoiY2xpbmstYWdlbnQiLCJlbnRlcnByaXNlSWQiOjMwMDQyOTcsInBvd2VyIjowfQ.KNopB8vi2oW09bcDQ6Bl0Gnc34MgRRMnfPuy34Ma6B98gAD-r6aVICRzv2pR-GioVTQjMkAc_ekRoqWjUtfSV3QPjYpGVPS2om7VPB404rI1zgxIX2LkOpwNUOgWhxVaEVxa5SZ1oZBGhyAsE802O1loEOBKWoBosEt1_96gObw
             * SECURITY_TOKEN_AGENT=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjbGllbnRJZCI6MTM2MDg2LCJjbm8iOiI4MjQ4IiwiaXNzIjoiY2xpbmstYWdlbnQiLCJlbnRlcnByaXNlSWQiOjMwMDQyOTcsInBvd2VyIjowfQ.KNopB8vi2oW09bcDQ6Bl0Gnc34MgRRMnfPuy34Ma6B98gAD-r6aVICRzv2pR-GioVTQjMkAc_ekRoqWjUtfSV3QPjYpGVPS2om7VPB404rI1zgxIX2LkOpwNUOgWhxVaEVxa5SZ1oZBGhyAsE802O1loEOBKWoBosEt1_96gObw
             */
            for (Cookie cooky : cookies) {
                System.out.println(cooky.getName() + "=" + cooky.getValue());
                if (cooky.getName().equals("SECURITY_TOKEN")) {
                    SECURITY_TOKEN = cooky.getValue();
                }
                if (cooky.getName().equals("SECURITY_TOKEN_AGENT")) {
                    SECURITY_TOKEN_AGENT = cooky.getValue();
                }
            }
           /* if (cookie_user != null) {
                result = JSESSIONID;
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}

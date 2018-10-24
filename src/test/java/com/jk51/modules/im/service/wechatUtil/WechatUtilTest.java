package com.jk51.modules.im.service.wechatUtil;

import com.jk51.Bootstrap;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by admin on 2018/6/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class WechatUtilTest {

    @Autowired
    private WechatUtil wechatUtil;

    @Test
    public void getToken() {
//        String accessToken = wechatUtil.getAccessToken(100262);
        //SELECT wx_appid,wx_secret FROM yb_merchant_ext WHERE merchant_id = 100262
        String result = null;
        try {
            result = HttpClient.doHttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+"getWx_appid"+"&secret="+"getWx_secret");
            Map<String,Object> accessToken = JacksonUtils.json2map(result);

            if(!StringUtil.isEmpty(accessToken.get("access_token"))){
                Integer timeout=Integer .parseInt(accessToken.get("expires_in")+"");
//                setToRedisAccessToken(siteId, accessToken.get("access_token").toString(),timeout);
//                return accessToken.get("access_token").toString();
                System.out.print("----------------------------"+accessToken.get("access_token").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

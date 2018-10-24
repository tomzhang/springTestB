package com.jk51.index;

import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.index.controller.UploadImage;
import com.jk51.mq.ProducerMsgTest;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class UploadImageTest {
    private static Logger logger = LoggerFactory.getLogger(ProducerMsgTest.class);

    @Value("${fileserver.uploadimageurl}")
    private String remote_url;// 第三方服务器请求地址 "http://img-dev.51jk.com/api/upload"


    /**
     * 测试聊天图片上传单个
     */
    @Test
    public void test(){
        Map<String, Object> resultMap = new HashMap<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        File file = new File("D:/image111.jpg");
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("image_base", file, ContentType.MULTIPART_FORM_DATA, "iii::image");// 文件流
            builder.addTextBody("re_upload", "0");// 类似浏览器表单提交，对应input的name和value
            HttpEntity entity = builder.build();

            HttpPost httpPost = new HttpPost(remote_url);
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
                resultMap = JacksonUtils.json2map(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("上传结果:{}",resultMap);
    }



}

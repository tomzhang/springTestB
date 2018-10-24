package com.jk51.modules.index.controller;

import com.jk51.commons.json.JacksonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping("/upload")
public class UploadImage {

    @Value("${fileserver.uploadimageurl}")
    private String remote_url;// 第三方服务器请求地址 "http://img-dev.51jk.com/api/upload"

    /**
     *聊天图片上传
     * @param file
     * @return
     */
    @RequestMapping("/chatimage")
    public Map<String,Object> uploadChatImage(MultipartFile file){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        if(file!=null && !file.isEmpty()){
            try {
                String fileName = file.getOriginalFilename();

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("image_base", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
                builder.addTextBody("re_upload", "0");// 类似浏览器表单提交，对应input的name和value
                HttpEntity entity = builder.build();

                HttpPost httpPost = new HttpPost(remote_url);
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);// 执行提交
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    // 将响应内容转换为字符串
                    result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
                    resultMap =  JacksonUtils.json2map(result);
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
        }
        return resultMap;
    }


}

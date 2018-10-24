package com.jk51.modules.pay.controller;

import com.jk51.modules.pay.service.wx.WxConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-20
 * 修改记录:
 */
@RestController
public class WxVerifyFileController {

    private static final Logger log = LoggerFactory.getLogger(WxVerifyFileController.class);
    @Autowired
    WxConfig wxConfig;

    @RequestMapping("/MP_verify_{seqId}.txt")
    public String downloadVerifyFile(@PathVariable  String seqId) throws IOException {
        String location = wxConfig.getMp_verify_path() + File.separator + "MP_verify_"+ seqId +".txt";
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);
        try {
            int available = is.available();
            byte[] dst = new byte[available];
            is.read(dst);
            return new String(dst);
        } catch (IOException e) {
            log.error("IOException:找不到微信授权回调页面域名文件", e);
        }finally {
            if(is != null) is.close();
        }
        return null;
    }
}

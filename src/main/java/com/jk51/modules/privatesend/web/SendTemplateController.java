package com.jk51.modules.privatesend.web;

import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.privatesend.core.PrivateSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @Author: chen
 * @Description:
 * @Date: created in 2018/8/22
 * @Modified By:
 */
@Controller
@RequestMapping("/template")
public class SendTemplateController {
    @Autowired
    private PrivateSend privateSend;
    @Autowired
    private StoresMapper storesMapper;


    @RequestMapping("/ecgSuccessMessage")
    public @ResponseBody Map ecgSuccessMessage(Integer siteId, String userOpenid, String url, String first, String remark,  String keyword1, String keyword2){
        return privateSend.ecgSuccessMessage(siteId,userOpenid,url,first,remark,storesMapper.getStoreName(siteId,keyword2),keyword2);
    }

}

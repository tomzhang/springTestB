package com.jk51.modules.appInterface.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.appInterface.service.ChatService;
import com.jk51.modules.appInterface.util.ValidateIMParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-07
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping(value="/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 获取联系人
     * 新节点
     * */

    @RequestMapping(value="/getanswerrelation")
    public Map<String,Object> getanswerrelation(@RequestBody Map<String,Object> body){

        String sender = (String) body.get("sender");
        return chatService.getanswerrelation(sender);

    }

    /**
     * 修改昵称
     *
     * */
    @RequestMapping(value="/editremark")
    public Map<String,Object> editremark(@RequestBody Map<String,Object> body){

        String sender = (String) body.get("sender");
        String receiver = (String) body.get("msg_type");//body.get("receiver");
        String remark = (String) body.get("remark");

        return chatService.editremark(sender,receiver,remark);

    }

    /**
     * 获取昵称
     * */
    @RequestMapping(value="/getuserremark")
    public Map<String,Object> getuserremark(@RequestBody Map<String,Object> body){

        String sender = (String) body.get("sender");
        String receiver = (String) body.get("receiver");

        return chatService.getuserremark(sender,receiver);

    }

    /**
     * 获取下单手机号
     * */
    @RequestMapping(value="/gettelbyopenidoruid")
    public Map<String,Object> gettelbyopenidoruid(@RequestBody Map<String,Object> body){

        String sender = (String) body.get("sender");
        String authToken = (String) body.get("AuthToken");
        return chatService.gettelbyopenidoruid(sender,authToken);

    }


}

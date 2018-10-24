package com.jk51.modules.wechatcardbag.service;

import org.springframework.stereotype.Service;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName WechatCardBagConfig
 * @Description 微信卡包接口
 * @Date 2018-05-02 10:46
 */
@Service
public class WechatCardBagConfig {

    //logo upload
    public static final String UPLOADLOGO = "/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";

    //query merchant list
    public static final String QUERYCARDID = "/card/batchget?access_token=";

    //query user card list
    public static final String QUERYUSERCARD = "/card/user/getcardlist?access_token=";

    //query api_ticket
    public static final String QUERYAPITICKET = "/cgi-bin/ticket/getticket?type=wx_card&access_token=";

    //update merchant card
    public static final String UPDATEMERCHANTCARD = "/card/update?access_token=";

    //query card code
    public static final String QUERYCARDCODE = "/card/user/getcardlist?access_token=";

    //query card detail info
    public static final String QUERYCARDINFO = "/card/membercard/userinfo/get?access_token=";

    //activate member card by interface
    public static final String ACTIVATEMEMBERCARD = "/card/membercard/activate?access_token=";

    //query card module url
    public static final String QUERYCARDMODULE ="/card/membercard/activate/geturl?access_token=";
}

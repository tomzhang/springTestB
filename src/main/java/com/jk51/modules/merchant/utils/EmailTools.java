package com.jk51.modules.merchant.utils;

import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.merchant.MerchantApplyDto;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.merchant.job.message.SendFailedMailToMerchantDto;
import com.jk51.modules.merchant.job.message.SendMailTo51Dto;
import com.jk51.modules.merchant.job.message.SendSuccessMailToMerchantDto;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：邮箱工具类
 * 作者: XC
 * 创建日期: 2018-09-18 13:44
 * 修改记录:
 **/
@Component
public class EmailTools {


    private static final Logger logger = LoggerFactory.getLogger(EmailTools.class);

    /**
     * 给51后台发送建站申请审批通知
     * @param header
     * @param content
     */
    private void sendMail(String header,String content,String accepter){
        HtmlEmail email = new HtmlEmail();
        email.setDebug(true);
        email.setHostName("smtphm.qiye.163.com");
        email.setAuthenticator(new DefaultAuthenticator("jianzhan@51jk.com", "WWW.51jk.com"));//发送方
        try {
            email.setFrom("jianzhan@51jk.com"); //发送方,这里可以写多个
            email.addTo(accepter); // 接收方
            email.setCharset("GB2312");
            email.setSubject(header); // 标题
            email.setMsg(content);// 内容
            email.setHtmlMsg(content);
            email.send();
        } catch (EmailException e) {
            logger.error("提醒后台审批建站申请邮件发送失败。报错信息{}" , ExceptionUtil.exceptionDetail(e));
        }
    }

    private String getHeaderTo51(String merchant_name){
        return "申请建站通知-【"+ merchant_name +"】";
    }

    private String getAccepterTo51(){
        return "575783434@qq.com";
    }

    private String getContentTo51(List<MerchantApplyDto> list){
        String content = "<table  border=\"1\" cellspacing=\"0\">\n" +
            "\t<tr style=\"background-color: #E0E0E0\">\n" +
            "\t\t<td>公司名称</td>\n" +
            "\t\t<td>行业</td>\n" +
            "\t\t<td>对接人姓名</td>\n" +
            "\t\t<td>对接人电话</td>\n" +
            "\t\t<td>申请日期</td>\n" +
            "\t\t<td>代理商编码</td>\n" +
            "\t\t<td>操作</td>\n" +
            "\t</tr>\n" ;
        for(MerchantApplyDto merchantApplyDto : list){
            content = content + "<tr>\n" +
                "\t\t<td>"+merchantApplyDto.getCompany_name()+"</td>\n" +
                "\t\t<td>"+merchantApplyDto.getBusiness_category()+"</td>\n" +
                "\t\t<td>"+merchantApplyDto.getLegal_name()+"</td>\n" +
                "\t\t<td>"+merchantApplyDto.getLegal_mobile()+"</td>\n" +
                "\t\t<td>"+dateFormat(merchantApplyDto.getCreate_time())+"</td>\n" +
                "\t\t<td>"+merchantApplyDto.getAgent_code()+"</td>\n" +
                "\t\t<td><a href=\"http://100190.busm.test.51jk.com/jk51b/applyDetail/"+merchantApplyDto.getMerchant_id()+"\" style=\"color: blue; text-decoration:none;\">现在去审核</a></td>\n" +
                "\t</tr>\n";
        }
        content = content + "</table>";
        return content;
    }

    public void sendMailTo51(SendMailTo51Dto smd){
        sendMail(getHeaderTo51(smd.getMerchant_name()),getContentTo51(smd.getMerchantApplyDtos()),getAccepterTo51());
    }

    public void sendFailMailToMerchant(SendFailedMailToMerchantDto sfmmd){
        sendMail("【51健康】建站失败通知",getFailMailContent(sfmmd.getLegal_name(),sfmmd.getReason()),sfmmd.getAccepter());
    }

    private String getFailMailContent(String legal_name,String reason){
        return "<h4>\n" +
            "\t尊敬的【"+legal_name+"】您好\n" +
            "</h4>\n" +
            "<br>\n" +
            "<br>\n" +
            "<p>\n" +
            "\t您申请的建站未通过审核，失败原因如下：\n" +
            "</p>\n" +
            "<br>\n" +
            "<p style=\"color:blue\">\n" +
            "\t"+reason+"\n" +
            "</p>\n" +
            "\n" +
            "<p>\n" +
            "<br>\n" +
            "\t点击链接在此申请 <a href=\"http://www.51jk.com/apply.html\">http://www.51jk.com/apply.html</a>\n" +
            "</p>\n" +
            "<br>\n" +
            "<br>\n" +
            "<p>\n" +
            "\t如有疑问请联系51健康运营人员\n" +
            "</p>\n" +
            "<p>\n" +
            "\t联系人：冯经理\n" +
            "</p>\n" +
            "<p>\n" +
            "\t联系电话：021-65798989转826\n" +
            "</p>\n" +
            "\n" +
            "<br>\n" +
            "<br>\n" +
            "<p style=\"color:gray\">\n" +
            "\t此邮件为自动发送，请勿回复，如有疑问请联系客服\n" +
            "</p>\n" +
            "\n" +
            "\n" +
            "<br>\n" +
            "<br>\n" +
            "<p>\n" +
            "\t----------------------------------------------------------------------------------\n" +
            "</p>\n" +
            "<p>\n" +
            "\t51健康科技有限公司\n" +
            "</p>\n" +
            "<p>\n" +
            "\t官网：www.51jk.com\n" +
            "</p>\n" +
            "<p>\n" +
            "\t客服电话：021-65798989\n" +
            "</p>\n";
    }

    public void sendSuccessMailToMerchant(SendSuccessMailToMerchantDto ssmmd){
        sendMail("【51健康】申请建站成功通知",getSuccessMailContent(ssmmd.getMerchant(),ssmmd.getMerchantExt(),ssmmd.getSeller_pwd()),ssmmd.getAccepter());
    }

    private String getSuccessMailContent(YbMerchant merchant, MerchantExtTreat merchantExt,String seller_pwd){
        return "<html>\n" +
            "<body>\n" +
            "<h4>\n" +
            "\t尊敬的【"+merchant.getLegal_name()+"】您好\n" +
            "</h4>\n" +
            "<br>\n" +
            "<br>\n" +
            "<p>\n" +
            "\t您申请的建站已经成功，以下为网站信息\n" +
            "</p>\n" +
            "<br>\n" +
            "\n" +
            "<div style=\"color:blue\">\n" +
            "\t总部后台域名："+merchant.getShop_url()+"\n" +
            "<br>\n" +
            "\t登陆账号：admin\n" +
            "<br>\n" +
            "\t登陆密码："+seller_pwd+"\n" +
            "</div>\n" +
            "<br>\n" +
            "\n" +
            "<div style=\"color:blue\">\n" +
            "\t门店后台域名: "+merchantExt.getStore_url()+"\n" +
            "<br>\n" +
            "\t门店后台登录账号需在总部后台设置，请自行设置，如需帮助请联系客服\n" +
            "</div>\n" +
            "<br>\n" +
            "<div style=\"color:blue\">\n" +
            "\t微信商城域名："+merchant.getShopwx_url()+"\n" +
            "</div>\n" +
            "\n" +
            "<br>\n" +
            "<div style=\"color:red\">\n" +
            "\t以上信息请妥善保管，切勿泄露\n" +
            "</div>\n" +
            "<br>\n" +
            "<br>\n" +
            "\n" +
            "<div>\n" +
            "\t建站后仅获得了账号和域名，如需正常使用网站，还需其他设置\n" +
            "<br>\n" +
            "\n" +
            "\t请联系51健康运营人员完成最终的设置\n" +
            "<br>\n" +
            "\t联系人：冯经理\n" +
            "<br>\n" +
            "\t联系电话：021-65798989转826\n" +
            "</div>\n" +
            "<br>\n" +
            "<br>\n" +
            "\n" +
            "<div styl=\"color:gray\">\n" +
            "\t此邮件为自动发送，请勿回复，如有疑问请联系客服\n" +
            "</div>\n" +
            "<br>\n" +
            "<br>\n" +
            "\n" +
            "<div>\n" +
            "\t----------------------------------------------------------------------------------\n" +
            "</div>\n" +
            "\n" +
            "<div>\n" +
            "\t51健康科技有限公司\n" +
            "<br>\n" +
            "\t官网：www.51jk.com\n" +
            "<br>\n" +
            "\t客服电话：021-65798989\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    private String dateFormat(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}

package com.jk51.modules.smallTicket.controller;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.smallTicket.mapper.SmallTicketMapper;
import com.jk51.modules.smallTicket.service.SmallTicketService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Created by Administrator on 2018/6/5.
 */
@RestController
@RequestMapping("/smallTicket")
public class SmallTicketController {
    private static final Logger logger = LoggerFactory.getLogger(SmallTicketController.class);

    @Autowired
    private SmallTicketService smallTicketService;
    @Autowired
    private SmallTicketMapper smallTicketMapper;

    /**
     *扫二维码查询小票
     * @param siteId
     * @param openId
     * @param mobile
     * @param date
     * @param status 为空，则是通过扫二维码进入小票详情页，不为空则为通过个人中心进入小票详情页
     * @return
     */
    @RequestMapping("/getSmallTickets")
    public ReturnDto getSmallTickets(Integer siteId, String openId, String mobile, String date, String status){
        logger.info("查询小票开始------");
        if (Objects.isNull(siteId))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        if (StringUtils.isBlank(mobile))
            return ReturnDto.buildFailedReturnDto("mobile不能为空！");
        if (null != openId)
            openId = openId.trim();
        if (null != mobile)
            mobile = mobile.trim();
        List<Map<String, Object>> ticketList = smallTicketService.getBradesInvoiceBySiteIdAndOpenIdOrMobile(siteId, openId, mobile, date, status);
        logger.info("查询小票结束------");
        if (CollectionUtils.isEmpty(ticketList))
            return ReturnDto.buildFailedReturnDto("暂无电子小票信息！");
        return ReturnDto.buildSuccessReturnDto(ticketList);
    }

    //根据openid和会员手机号获取每个人的小票记录数
    @RequestMapping("/getbtradesinvoiceCount")
    public ReturnDto getbtradesinvoiceCount(Integer siteId,String mobile,String openId){
        if (Objects.isNull(siteId))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        if(StringUtils.isBlank(mobile))
            return ReturnDto.buildFailedReturnDto("mobile不能为空");
        if (null != openId)
            openId = openId.trim();
        if (null != mobile)
            mobile = mobile.trim();
        Integer ticketsinvoiceCount = smallTicketMapper.getSmallTicketsCountSiteIdAndOpenIdOrMobile(siteId,mobile,openId);
        return ReturnDto.buildSuccessReturnDto(ticketsinvoiceCount < 10 ? ticketsinvoiceCount : 9);
    }
}

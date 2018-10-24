package com.jk51.modules.pc.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.pc.Notice;
import com.jk51.model.pc.UdNotice;
import com.jk51.modules.pc.service.PCNoticeService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 公告
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
@Controller
@RequestMapping("notice")
public class PCNoticeController {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(PCNoticeController.class);

    @Autowired private PCNoticeService noticeService;



    @RequestMapping("add")
    public @ResponseBody Integer add(Notice notice){
        return noticeService.add(notice);
    }

    @RequestMapping("upd")
    public @ResponseBody Integer upd(Notice notice){
        return noticeService.upd(notice);
    }

    @RequestMapping("del")
    public @ResponseBody Integer del(Integer siteId, Integer id){
        return noticeService.del(siteId, id);
    }

    @RequestMapping("getLst")
    public @ResponseBody PageInfo<Notice> getLst(Integer siteId, Integer pageNum, Integer pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Notice> list = noticeService.getLst(siteId);
        return new PageInfo<Notice>(list);
    }

    @RequestMapping("getById")
    public @ResponseBody Notice getById(Integer siteId,Integer id){

        Notice notice = noticeService.getById(siteId,id);
        return notice;
    }

    @RequestMapping("getUpAndDownById")
    public @ResponseBody
    UdNotice getUpAndDownById(Integer siteId, Integer id){

        UdNotice notice = noticeService.getUpAndDownById(siteId,id);
        return notice;
    }

}

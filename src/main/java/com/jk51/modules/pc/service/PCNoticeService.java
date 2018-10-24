package com.jk51.modules.pc.service;

import com.jk51.model.pc.Notice;
import com.jk51.model.pc.UdNotice;
import com.jk51.modules.pc.mapper.NoticeMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
@Service
public class PCNoticeService {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(PCNoticeService.class);

    @Autowired private NoticeMapper noticeMapper;

    public Integer add(Notice notice) {
        return noticeMapper.add(notice);
    }

    public Integer upd(Notice notice) {
        return noticeMapper.upd(notice);
    }

    public Integer del(Integer siteId, Integer id) {
        return noticeMapper.del(siteId, id);
    }

    public List<Notice> getLst(Integer siteId) {
        return noticeMapper.getLst(siteId);
    }

    public Notice getById(Integer siteId, Integer id) {
        return noticeMapper.getById(siteId, id);
    }

    public UdNotice getUpAndDownById(Integer siteId, Integer id) {

        UdNotice udNotice = new UdNotice();
        Notice notice1 = noticeMapper.getUpById(siteId, id);
        Notice notice2 = noticeMapper.getDownById(siteId, id);

        udNotice.setUpNotice(notice1);
        udNotice.setDownNotice(notice2);

        return udNotice;
    }
}

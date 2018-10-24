package com.jk51.modules.pc.service;

import com.jk51.model.pc.Help;
import com.jk51.modules.pc.mapper.HelpMapper;
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
public class HelpService {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(HelpService.class);

    @Autowired private HelpMapper helpMapper;

    public Integer add(Help help) {
        return helpMapper.add(help);
    }

    public Integer upd(Help help) {
        return helpMapper.upd(help);
    }

    public List<Help> getLst(Integer siteId) {
        return helpMapper.getLst(siteId);
    }

    public Help getBySecTitle(Integer siteId, String firTitle, String secTitle) {
        return helpMapper.getBySecTitle(siteId, firTitle, secTitle);
    }

    public List<Help> getLstByFirTitle(Integer siteId, String firTitle) {
        return helpMapper.getLstByFirTitle(siteId, firTitle);
    }
}

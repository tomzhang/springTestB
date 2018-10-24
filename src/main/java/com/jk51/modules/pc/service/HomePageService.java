package com.jk51.modules.pc.service;

import com.jk51.model.pc.HomePage;
import com.jk51.modules.pc.mapper.HomePageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/16
 * 修改记录:
 */
@Service
public class HomePageService {

    @Autowired
    private HomePageMapper homePageMapper;


    public Integer add(HomePage homePage) {
        return homePageMapper.add(homePage);
    }

    public Integer upd(HomePage homePage) {
        return homePageMapper.upd(homePage);
    }

    public List<HomePage> getLst(Integer siteId,Integer bfrom) {
        return homePageMapper.getLst(siteId,bfrom);
    }
}

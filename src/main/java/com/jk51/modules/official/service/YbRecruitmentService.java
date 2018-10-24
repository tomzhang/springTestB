package com.jk51.modules.official.service;

import com.jk51.model.official.YbRecruitment;
import com.jk51.modules.official.mapper.YbRecruitmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */
@Service
public class YbRecruitmentService {
    @Autowired
    YbRecruitmentMapper ybRecruitmentMapper;

    public int add(YbRecruitment ybRecruitment){
        return ybRecruitmentMapper.add(ybRecruitment);
    }

    public List<YbRecruitment> queryList(YbRecruitment ybRecruitment){
        return ybRecruitmentMapper.queryList(ybRecruitment);
    }

    public YbRecruitment queryOne(int id){
        return ybRecruitmentMapper.queryOne(id);
    }
}

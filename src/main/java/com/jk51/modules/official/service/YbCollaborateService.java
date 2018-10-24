package com.jk51.modules.official.service;

import com.jk51.model.official.YbCollaborate;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.official.mapper.YbCollaborateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */
@Service
public class YbCollaborateService {
    @Autowired
    YbCollaborateMapper ybCollaborateMapper;

    public int add(YbCollaborate ybCollaborate){
        return ybCollaborateMapper.add(ybCollaborate);
    }

    public int update(YbCollaborate ybCollaborate){
        return ybCollaborateMapper.updateById(ybCollaborate);
    }

    public List<YbCollaborate> queryList(YbCollaborate ybCollaborate){
        return ybCollaborateMapper.queryList(ybCollaborate);
    }

    public YbCollaborate queryById(int id){
        return ybCollaborateMapper.queryById(id);
    }
}

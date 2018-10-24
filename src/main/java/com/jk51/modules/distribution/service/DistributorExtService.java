package com.jk51.modules.distribution.service;

import com.jk51.modules.distribution.mapper.DistributorExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Administrator on 2017/4/27.
 */
@Service
public class DistributorExtService {
    @Autowired
    DistributorExtMapper distributorExtMapper;

    public int insert(Map<String,Object> param){
        return  distributorExtMapper.insertSelective(param);
    }

    public int updateByExampleSelective(Map<String,Object> param){
        return  distributorExtMapper.updateByDisSelective(param);
    }
}

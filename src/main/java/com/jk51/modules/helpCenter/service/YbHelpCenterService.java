package com.jk51.modules.helpCenter.service;

import com.jk51.model.YbHelpCenter;
import com.jk51.modules.helpCenter.mapper.YbHelpCenterMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/4.
 */
@Service
public class YbHelpCenterService {
    @Autowired
    private YbHelpCenterMapper helpCenterMapper;

    public List<YbHelpCenter> getHelpList(Map<String,Object> param){
        return  helpCenterMapper.getHelpList(param);
    }

    public  YbHelpCenter selectByPrimaryKey(@Param("id") int id){
        return helpCenterMapper.selectByPrimaryKey(id);
    }

    public  int update(YbHelpCenter helpCenter){
        return  helpCenterMapper.updateByPrimaryKeySelective(helpCenter);
    }

    public  int insert(YbHelpCenter helpCenter){
        return helpCenterMapper.insertSelective(helpCenter);
    }


}

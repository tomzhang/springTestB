package com.jk51.modules.official.service;

import com.jk51.model.official.YbStation;
import com.jk51.modules.official.mapper.YbStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
@Service
public class YbStationService {
    @Autowired
    private YbStationMapper ybStationMapper;

    public int add(YbStation ybStation){
        return ybStationMapper.add(ybStation);
    }

    public int update(YbStation ybStation){
        return ybStationMapper.update(ybStation);
    }

    public YbStation queryStationById(int id){
        return ybStationMapper.queryStationById(id);
    }

    public List<YbStation> queryList(YbStation ybStation){
        return ybStationMapper.queryStationList(ybStation);
    }
}

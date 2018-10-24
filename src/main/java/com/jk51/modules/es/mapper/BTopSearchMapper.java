package com.jk51.modules.es.mapper;


import com.jk51.model.BTopSearch;

import java.util.List;

public interface BTopSearchMapper {


    int insertSelective(BTopSearch record);

    List<BTopSearch> queryBtopSearch(String siteId);

    int updateByPrimaryKeySelective(BTopSearch record);

}
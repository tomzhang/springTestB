package com.jk51.modules.index.mapper;

import com.jk51.model.FirstWeight;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Administrator on 2017/2/13.
 */
@Mapper
public interface FirstWeightMapper {


    List<FirstWeight> findFirstWeightByOwner(String owner);

    int insertFirstWeight(FirstWeight firstWeight);

    int updateFirstWeight(FirstWeight firstWeight);

    FirstWeight getFirstWeightByPrimaryKey(String first_weight_id);

    List<FirstWeight> getAllFirstWeight();

    List<FirstWeight> getFirstWeightBySiteId(int site_id);
}

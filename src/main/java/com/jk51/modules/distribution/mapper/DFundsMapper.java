package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.DFunds;

public interface DFundsMapper {

    int insert(DFunds record);
    
    DFunds selectByPrimaryKey(Integer id);

    DFunds selectByDistributorId(Integer distributorId);

//    int updateByPrimaryKeySelective(DFunds record);
//
//    int updateByPrimaryKey(DFunds record);
}
package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.DWithdrawRecord;
import com.jk51.model.distribute.Distributor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DWithdrawRecordMapper {
    Integer withdrawRecordAdd(DWithdrawRecord record);
    Integer updateWithdrawRecordPayStatus(@Param("moneyRecordId")Long moneyRecordId,@Param("siteId") Integer siteId,@Param("payStatus") Integer pay_status);
}
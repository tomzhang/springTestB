package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.RecruitRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by admin on 2017/2/9.
 */
@Mapper
public interface RecruitRecordMapper {

    public int insertRecruitRecord(RecruitRecord recruitRecord);

}
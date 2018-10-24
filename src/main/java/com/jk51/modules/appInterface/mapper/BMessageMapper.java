package com.jk51.modules.appInterface.mapper;

import com.jk51.model.BMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BMessageMapper {
    int insertSelective(BMessage record);
}
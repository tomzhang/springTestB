package com.jk51.modules.official.mapper;

import com.jk51.model.goods.PageData;
import com.jk51.model.official.ApplyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApplyUserMapper {
    ApplyUser login(@Param("uphone") String uphone,@Param("upwd") String upwd);
    ApplyUser sendCode(@Param("uphone") String uphone);
    Integer registUser(ApplyUser applyUser);
    List<PageData> queryUser(Map<String,Object> params);
}

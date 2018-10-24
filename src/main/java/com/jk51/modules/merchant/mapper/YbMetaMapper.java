package com.jk51.modules.merchant.mapper;

import com.jk51.model.YbMeta;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface YbMetaMapper {

    YbMeta findFirst(@Param("siteId") int siteId, @Param("metaKey") String metaKey);

    boolean update(YbMeta record);

    int save(YbMeta record);

    List<Map> findMetaKey(@Param("metaKeys") List<String> categoryIconsKeys, @Param("siteId") Integer siteId);
}
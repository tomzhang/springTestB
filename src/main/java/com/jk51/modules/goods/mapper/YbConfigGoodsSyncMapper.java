package com.jk51.modules.goods.mapper;

import com.jk51.model.YbConfigGoodsSync;
import org.apache.ibatis.annotations.Param;

public interface YbConfigGoodsSyncMapper {
    YbConfigGoodsSync findByDetailTplFirst(@Param("detail_tpl") int detail_tpl);
}
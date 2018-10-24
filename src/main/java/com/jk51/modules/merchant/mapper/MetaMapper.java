package com.jk51.modules.merchant.mapper;

import com.jk51.model.order.Meta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-02
 * 修改记录:
 */
@Mapper
public interface MetaMapper {
    Integer insertSelective(Meta meta);

    Meta findBySiteIdAndMetaId(int siteId, int metaId);

    int addMeta(Meta meta);

    void updateMeta(Meta meta);

    void updateMetaByMetaTypeMetaKey(Meta meta);

    Meta findIndexPageBySiteId(Integer siteId);

    Meta selectByMetaTypeAndKey(Integer siteId, String metaType, String metaKey);

    List<Meta> selectMetesTypeAndKey(int siteId, String metaType, String metaKey);

    Meta selectBysiteIdAndMetaType(int siteId, String metaType);

    Integer updateByPrimaryKeys(Meta meta);

    Meta selectBySiteIdAndKey(@Param("site_id") Integer siteId, @Param("meta_key") String Key);

    int delAutoCode(@Param("site_id") Integer siteId, @Param("store_id") String storeId);

    int updateStatus(@Param("site_id") Integer siteId, @Param("meta_type") String metaType);
}

package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SMeta;
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
public interface StmpMetaMapper {

    Integer insertSelective(SMeta meta);

    SMeta findBySiteIdAndMetaId(int siteId, int metaId);

    int addMeta(SMeta meta);

    Integer updateMeta(SMeta meta);

    Integer updateMetaByMetaTypeMetaKey(SMeta meta);

    SMeta findIndexPageBySiteId(Integer siteId);

    SMeta selectByMetaTypeAndKey(@Param("siteId") Integer siteId,@Param("metaType") String metaType,@Param("metaKey") String metaKey,@Param("themeId") Integer themeId);

    List<SMeta> selectMetesTypeAndKey(int siteId, String metaType, String metaKey);

    SMeta selectBysiteIdAndMetaType(int siteId, String metaType);

    Integer updateByPrimaryKeys(SMeta meta);

    SMeta selectBySiteIdAndKey(@Param("site_id") Integer siteId, @Param("meta_key") String Key);

    int delAutoCode(@Param("site_id") Integer siteId, @Param("store_id") String storeId);

    int updateStatus(@Param("site_id") Integer siteId, @Param("meta_type") String metaType);

    List<SMeta> getWxAdvertiseListBySiteId(@Param("siteId") Integer siteId);

    List<SMeta> getMetasSiteId(@Param("siteId") Integer siteId);
}

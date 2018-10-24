package com.jk51.modules.offline.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-09-27
 * 修改记录:
 */
@Mapper
public interface BStoresStorageMapper {

    int insertERPStorage(Map map);

    int insertStorageList(List<Map<String, String>> mapList);

    int insertStorageCopy(Map map);

    int updateERPStorage(Map map);

    int updateStorageByList(List<Map<String, String>> mapList);

    int batchupdateStorageByListId(List<Map<String, String>> mapList);

    int updateStatus(@Param("siteId") Integer siteId);

    Map selectByUidAndCode(@Param("siteId") Integer siteId, @Param("uid") String uid, @Param("goodsCode") String goodsCode);

    List<Map> selectStorageBySiteId(@Param("siteId") Integer siteId, @Param("goods_name") String goods_name, @Param("goods_code") String goods_code,
                                    @Param("stores_number") String stores_number, @Param("stores_name") String stores_name, @Param("status") Integer status);

    List<Map<String, Object>> selectStorageByOrder(Map map);

    //查询备份表中当前类型是覆盖型的最大值
    String selectBackMAX(@Param("siteId") Integer siteId, @Param("type") String type);

    List<Map<String, Object>> selectStorageList(@Param("siteId") Integer siteId, @Param("type") String type, @Param("create_time") String create_time);

    int backUpStorage(Map map);

    int backUpStorage2(Map map);

    List<Map<String, Object>> loadstorageStores(@Param("siteId") Integer siteId, @Param("status") Integer status);

    int judgeCountBySiteId(@Param("siteId") Integer siteId, @Param("status") Integer status);

}

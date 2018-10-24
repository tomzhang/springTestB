package com.jk51.modules.userScenarios.mapper;

import com.jk51.model.qrcode.BConcern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-13
 * 修改记录:
 */

@Mapper
public interface QrcodeMapper {

    /**
     * 更新店员二维码
     * @param siteId
     * @param clerkCode
     * @param url
     * @return
     */
    Integer insertQrcodeIntoStoreadmin(@Param("siteId") Integer siteId,@Param("clerkCode") String clerkCode,@Param("url") String url);
    /**
     * 更新店员二维码生活号
     * @param siteId
     * @param storeadminId
     * @param url
     * @return
     */
    Integer insertAliQrcode(@Param("siteId") Integer siteId,@Param("storeadminId") Integer storeadminId,@Param("url") String url);

    Integer queryStroeAdminId(@Param("siteId") Integer siteId, @Param("clerkCode") String clerkCode);

    Integer queryStroeId(@Param("siteId") Integer siteId, @Param("storesNumber") String storesNumber);

    Integer insertConcern(@Param("concern") BConcern concern);

    Map<String, Object> queryAppidAndSecret (@Param("siteId") Integer siteId);

    Map<String, Object> queryAdminInfo (@Param("siteId") Integer siteId, @Param("id") Integer id);

    List<Integer> merchantList();

    List<Map<String, Object>> allStoreAdminId(@Param("siteId") Integer siteId);

    Integer isExist (@Param("siteId") Integer siteId, @Param("openId") String openId, @Param("createTime") Long createTime);

    String queryOpenid(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    Integer insertOpenid(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("openid") String openid);

    Integer insertAliUserId(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("aliUserId") String aliUserId);

    /**
     * 取消关注时更新concern_status
     * @param siteId
     * @param open_id
     * @return
     */
    Integer cancelConcern(@Param("site_id") Integer siteId, @Param("open_id") String open_id);

    /**
     * 查询concern_status
     * @param siteId
     * @param open_id
     * @return
     */
    Map queryConcernStatus(@Param("site_id") Integer siteId, @Param("open_id") String open_id);


}

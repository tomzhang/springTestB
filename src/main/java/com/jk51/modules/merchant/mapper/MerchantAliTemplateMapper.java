package com.jk51.modules.merchant.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/6/20
 * 修改记录:
 */
@Mapper
public interface MerchantAliTemplateMapper {
    Integer add(@Param("merchantId") Integer siteId,@Param("templateName") String templateName,@Param("templateId") String templateId);

    Integer upd(@Param("merchantId") Integer siteId,@Param("id") Integer id,@Param("templateId") String templateId);

    Integer del(@Param("id") Integer id,@Param("isdel") Integer isdel);

    List<Map> getAliTemplateLst(Integer siteId);

    String getAliTemplateIdBySiteIdAndName(@Param("merchantId") Integer siteId,@Param("templateName") String templateName);
}

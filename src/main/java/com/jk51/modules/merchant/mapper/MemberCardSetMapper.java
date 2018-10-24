package com.jk51.modules.merchant.mapper;

import com.jk51.model.merchant.MemberCardSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/2
 * 修改记录:
 */
@Mapper
public interface MemberCardSetMapper {

    MemberCardSet getBySiteId(Integer siteId);

    Integer updCardSet(MemberCardSet memberCardSet);

    Integer addCardSet(MemberCardSet memberCardSet);

    List<Map<String,Object>> queryCategoryList(Map<String, Object> parameterMap);

    Map<String,Object> queryCategoryLog(@Param("siteId") String siteId, @Param("cateCode") String cateCode);

    int insertClassifyLog(Map<String, Object> parameterMap);

    int updateClassifyLog(Map<String, Object> parameterMap);

    //查询已存在分类集合
    List<Map<String,Object>> queryAlreadyExist(@Param("siteId") Integer siteId);

    int addClassifyList(Map<String, Object> map);

    int updateClassifyList(Map<String, Object> map);

    Integer clearRelevanceLog(Map<String, Object> parameterMap);

    List<Map<String,Object>> selectCateName(Map<String, Object> parameterMap);

    Map<String,Object> selectClassifyByName(Map<String, Object> parameterMap);

    Integer pauseOrStart(Map<String, Object> parameterMap);
}

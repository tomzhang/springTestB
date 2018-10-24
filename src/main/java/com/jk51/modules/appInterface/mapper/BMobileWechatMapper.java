package com.jk51.modules.appInterface.mapper;

import com.jk51.model.BMobileWechat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@Mapper
public interface BMobileWechatMapper {

    List<String> findMobile(@Param("userId") String userId, @Param("site_id") int site_id);
    int insert(Map<String, Object> map);
    Map<String,Object> findBMobileWechat(@Param("mobile") String mobile, @Param("site_id") int site_id);
    int updateByPrimaryKey(Map<String, Object> map);
}

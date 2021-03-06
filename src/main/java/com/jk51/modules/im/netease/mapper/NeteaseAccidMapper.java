package com.jk51.modules.im.netease.mapper;

import com.jk51.model.netease.NeteaseAccid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
@Mapper
public interface NeteaseAccidMapper {

     int inseart(NeteaseAccid neteaseAccid);

     NeteaseAccid findByAccid(@Param("accid")String accid);
}

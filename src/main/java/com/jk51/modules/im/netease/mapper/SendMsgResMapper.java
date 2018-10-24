package com.jk51.modules.im.netease.mapper;

import com.jk51.modules.im.netease.response.SendMsgRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/4
 * 修改记录:
 */
@Mapper
public interface SendMsgResMapper {

    int insert(@Param("code")int code, @Param("desc")String desc,@Param("from")String from,@Param("to")String to,@Param("data")String data);

}

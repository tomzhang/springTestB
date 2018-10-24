package com.jk51.modules.im.netease.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Async;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/4
 * 修改记录:
 */
@Mapper
public interface BatchSendMsgResMapper {


    int insert(@Param("success")String success, @Param("fail")String fail);
}

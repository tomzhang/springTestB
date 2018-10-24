package com.jk51.modules.member.mapper;

import com.jk51.model.login.BLogin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-21
 * 修改记录:
 */
@Mapper
public interface BWxLoginMapper {
    int insertLog(BLogin bLogin);
}

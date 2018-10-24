package com.jk51.modules.appInterface.mapper;

import com.jk51.model.AppH5Version;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/7
 * 修改记录:
 */
@Mapper
public interface AppH5VersionMapper {

    AppH5Version findNewestVersion();

    int insert(AppH5Version param);
}

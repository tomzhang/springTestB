package com.jk51.modules.persistence.mapper;

import com.jk51.model.theme.WxTheme;
import com.jk51.model.theme.WxThemeParm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/8/7
 * 修改记录:
 */
@Mapper
public interface WxThemeMapper {

    List<WxTheme> getLstBySiteId(WxThemeParm parm);

    Integer update(WxTheme theme);

    Integer add(WxTheme theme);

    Integer del(WxTheme theme);

    WxTheme getWxTheme(WxTheme theme);
}

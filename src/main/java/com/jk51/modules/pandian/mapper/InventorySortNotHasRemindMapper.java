package com.jk51.modules.pandian.mapper;

import com.jk51.model.InventorySortNotHasRemind;
import com.jk51.modules.pandian.dto.NotHasRemindCountRate;
import com.jk51.modules.pandian.param.StatusParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/2
 * 修改记录:
 */
@Mapper
public interface InventorySortNotHasRemindMapper {

    int insert(InventorySortNotHasRemind inventorySortNotHasRemind);

    NotHasRemindCountRate countRate(StatusParam param);

    int delete(InventorySortNotHasRemind inventorySortNotHasRemind);
}

package com.jk51.modules.pandian.param;

import com.jk51.modules.pandian.dto.PandianSortRedisDto;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/2
 * 修改记录:
 */
public class InventorySortRemindHitMassege {

    private PandianSortRedisDto dto;
    private String nextGoodsCode;
    private boolean hasRemind;

    public PandianSortRedisDto getDto() {
        return dto;
    }

    public void setDto(PandianSortRedisDto dto) {
        this.dto = dto;
    }

    public String getNextGoodsCode() {
        return nextGoodsCode;
    }

    public void setNextGoodsCode(String nextGoodsCode) {
        this.nextGoodsCode = nextGoodsCode;
    }

    public boolean isHasRemind() {
        return hasRemind;
    }

    public void setHasRemind(boolean hasRemind) {
        this.hasRemind = hasRemind;
    }
}

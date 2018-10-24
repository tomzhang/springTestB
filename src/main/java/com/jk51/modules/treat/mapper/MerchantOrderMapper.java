package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.BGoodsPrebook;
import com.jk51.model.treat.OrderPreQueryReqTreat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: baixiongfei
 * 创建日期: 2017/3/11
 * 修改记录:
 */
@Mapper
public interface MerchantOrderMapper {


    public List<BGoodsPrebook> getGoodsPrebookList(OrderPreQueryReqTreat orderPreQuery);
}

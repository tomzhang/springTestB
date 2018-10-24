package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.QueryGoodsDistribute;
import com.jk51.model.distribute.QueryTemplate;
import com.jk51.model.distribute.RewardTemplate;
import com.jk51.model.goods.PageData;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface RewardTemplateMapper {

    /**
     * 列表
     * @param
     * @return
     */
    RewardTemplate selectByTemplateId(Integer distributionTemplate);

    /**
     * 根据商家ID,获取门店下所有已确认和未确认的奖励总金额
     * @param rewardTemplate
     * @return
     */
    List<Map<String,Object>> queryTemplete(RewardTemplate rewardTemplate);

    /**
     * 根据siteId查询最小提现金额
     * @param rewardTemplate
     * @return
     */
    int insertSelective(RewardTemplate rewardTemplate);

    /**
     * 修改最小提现金额
     * @param rewardTemplate
     * @return
     */
    int updateByPrimaryKey(RewardTemplate rewardTemplate);

    List<PageData> queryTempleteList(QueryTemplate queryTemplate);

    int insertTemplate(RewardTemplate rewardTemplate);

    int updateByPrimaryKeySelective(RewardTemplate rewardTemplate);

    int editByPrimaryKeySelective(RewardTemplate rewardTemplate);

    List<PageData> queryTempleteUser(QueryTemplate queryTemplate);

    Map<String,Object> findById(Integer id,Integer owner);

    Map<String,Object> getTemplateByIdandShopPrice(QueryGoodsDistribute queryGoodsDistribute);

    List<Map<String,Object>> queryDiscountMax(QueryTemplate queryTemplate);
}
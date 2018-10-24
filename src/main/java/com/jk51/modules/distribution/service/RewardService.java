package com.jk51.modules.distribution.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.modules.distribution.mapper.RewardMapper;
import com.jk51.modules.distribution.util.ParamFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by admin on 2017/2/9.
 */
@Service
public class RewardService {

    @Autowired
    private RewardMapper rewardMapper;


    /**
     * 列表
     * @param
     * @return
     */
    public PageInfo<Map<String,Object>> queryRewardList(Integer page, Integer pageSize, Map<String,Object> param){

        Integer order_status = ParamFormat.formatToInteger(param.get("order_status"));
        Integer reward_status = ParamFormat.formatToInteger(param.get("reward_status"));

        List<Integer> orderStatusList = null;

        //    <select name="reward_status" id="reward_status" class="input-medium">
        //      <option value="3">全部</option>
        //      <option value="1">已确认</option>
        //      <option value="0">待确认</option>
        //      <option value="2">无奖励</option>
        //      <option value="4">待处理</option>
        //    </select>
        //
        //    <select name="order_status" id="order_status" class="input-medium">
        //      <option value="3">全部</option>
        //      <option value="110">未付款</option>
        //      <option value="0">交易失败（退款）</option>
        //      <option value="1">交易成功</option>
        //      <option value="2">交易结束</option>
        //    </select>

        List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();

        param.put("order_status",null);
        if(reward_status == null){
            if(order_status != null){
                orderStatusList = new ArrayList<Integer>();
                orderStatusList.add(order_status);
            }
            param.put("orderStatusList",orderStatusList);
            list = rewardMapper.getRewardList(param);
        }else if (reward_status == 1){
            if(order_status == null || order_status == 2){
                orderStatusList = new ArrayList<Integer>();
                orderStatusList.add(2);
                param.put("orderStatusList",orderStatusList);
                list = rewardMapper.getRewardList(param);
            }
        }else if(reward_status == 0){
            if(order_status == null || order_status == 1){
                orderStatusList = new ArrayList<Integer>();
                orderStatusList.add(1);
                param.put("orderStatusList",orderStatusList);
                list = rewardMapper.getRewardList(param);
            }
        }else if(reward_status == 2){
            reward_status = 0;
            if(order_status == null){
                orderStatusList = new ArrayList<Integer>();
                orderStatusList.add(110);
                orderStatusList.add(0);
                param.put("reward_status",reward_status);
                param.put("orderStatusList",orderStatusList);
                list = rewardMapper.getRewardList(param);
            }else if(order_status == 110 || order_status == 0){
                orderStatusList = new ArrayList<Integer>();
                orderStatusList.add(order_status);
                param.put("reward_status",reward_status);
                param.put("orderStatusList",orderStatusList);
                list = rewardMapper.getRewardList(param);
            }
        }else if(reward_status == 4){
            reward_status = 0;
            if(order_status == null || order_status == 2){
                orderStatusList = new ArrayList<Integer>();
                orderStatusList.add(2);
                param.put("reward_status",reward_status);
                param.put("orderStatusList",orderStatusList);
                list = rewardMapper.getRewardList(param);
            }
        }


        PageHelper.startPage(page, pageSize);//开启分页


        return new PageInfo<>(list);
    }

    /**
     * 根据商家ID,获取门店下所有已确认和未确认的奖励总金额
     * @param siteId
     * @return
     */
    public Map<String,Object> totalReward (Integer siteId){
        Map<String,Object> result = new HashMap<>();
        Integer receviedMoney = rewardMapper.receviedMoney(siteId);
        Integer payMoney = rewardMapper.payMoney(siteId);
        result.put("receviedMoney",receviedMoney);
        result.put("payMoney",payMoney);
        return result;
    }

//    /**
//     * 查询最小提现金额
//     * @param siteId
//     * @return
//     */
//    public Integer queryMinMoney (Integer siteId){
//        return rewardMapper.queryMinMoney(siteId);
//    }
//
//    /**
//     * 修改最小提现金额
//     * @param siteId
//     * @param minMoney
//     * @return
//     */
//    public Integer updateMinMoney (Integer siteId,Integer minMoney){
//        return rewardMapper.updateMinMoney(siteId,minMoney);
//    }
}

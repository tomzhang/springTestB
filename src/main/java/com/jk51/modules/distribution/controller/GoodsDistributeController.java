package com.jk51.modules.distribution.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.distribute.QueryGoodsDistribute;
import com.jk51.modules.distribution.service.GoodsDistributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by guosheng on 2017/4/14.
 */
@RestController
@RequestMapping("/goodsDistribute")
public class GoodsDistributeController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsDistributeController.class);

    @Autowired
    GoodsDistributeService goodsDistributeService;

    /**
     * 获取分销商品app端和后台
     *
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto  queryGoodsDistribute(QueryGoodsDistribute queryGoodsDistribute,String distributor){
        PageInfo<?> pageInfo;
        try {
            pageInfo = this.goodsDistributeService.queryGoodsDistribute(queryGoodsDistribute,distributor);

        } catch (Exception e) {
            logger.error("获取分销失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询分销出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 获取分销商品微信端
     *
     * @return
     */
    @RequestMapping(value = "/querywithWechat", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto  queryGoodsDistributewithWechat(QueryGoodsDistribute queryGoodsDistribute,String uid){
        PageInfo<?> pageInfo;
        try {
            pageInfo = this.goodsDistributeService.queryGoodsDistributeWithWechat(queryGoodsDistribute,uid);

        } catch (Exception e) {
            logger.error("获取分销失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询分销出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }


    /**
     * 获取分销商品
     *
     * @return
     */
    @RequestMapping(value = "/queryBytempId", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto  queryGoodsDistributeBytempId(QueryGoodsDistribute queryGoodsDistribute){
        PageInfo<?> pageInfo;
        try {
            pageInfo = this.goodsDistributeService.queryGoodsDistributeBytempId(queryGoodsDistribute);

        } catch (Exception e) {
            logger.error("获取分销失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询分销出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }



    /**
     * 根据模版id来更新商品使用的模版
     * @param
     * @return
     */
    @RequestMapping(value="/updateDistributionTemplate",method = RequestMethod.POST)
    @ResponseBody
    public Object updateDistributionTemplate(QueryGoodsDistribute queryGoodsDistribute){

        return goodsDistributeService.updateDistributionTemplate(queryGoodsDistribute);


    }

    /**
     * 根据模版id让商品成为推荐商品并且关联当前模版
     * @param
     * @return
     */
    @RequestMapping(value="/updateGoodsDistribute",method = RequestMethod.POST)
    @ResponseBody
    public Object updateGoodsDistribute(int tempid,int siteId,int[] goods_ids){
        return goodsDistributeService.updateGoodsDistribute(tempid,siteId,goods_ids);


    }
}

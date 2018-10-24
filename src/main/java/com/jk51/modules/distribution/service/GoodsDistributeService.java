package com.jk51.modules.distribution.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Goods;
import com.jk51.model.distribute.Distributor;
import com.jk51.model.distribute.GoodsDistribute;
import com.jk51.model.distribute.QueryGoodsDistribute;
import com.jk51.model.goods.PageData;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.distribution.mapper.DistributorMapper;
import com.jk51.modules.distribution.mapper.GoodsDistributeMapper;
import com.jk51.modules.distribution.mapper.RewardTemplateMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by guosheng on 2017/4/14.
 */
@Service
public class GoodsDistributeService {
    private static final Logger logger = LoggerFactory.getLogger(GoodsDistributeService.class);

    @Autowired
    private GoodsDistributeMapper goodsDistributeMapper;
    @Autowired
    private RewardTemplateMapper rewardTemplateMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private DistributorMapper distributorMapper;
    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;

    public PageInfo<?> queryGoodsDistribute(QueryGoodsDistribute queryGoodsDistribute,String distributor){
        //PageHelper.startPage(goodsDistribute.getPageNum(), goodsDistribute.getPageSize());
//        PageHelper.startPage(1,10);
        if (queryGoodsDistribute.getPageNum()!=null && queryGoodsDistribute.getPageSize()!=null){
            PageHelper.startPage(queryGoodsDistribute.getPageNum(), queryGoodsDistribute.getPageSize());
        }
        MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(queryGoodsDistribute.getSiteId());
        if ((merchantExt.getHas_erp_price().equals(1))) {
            queryGoodsDistribute.setHas_erp(1);
        }
        List<PageData> list = goodsDistributeMapper.findGoodsDistribute(queryGoodsDistribute);
        for(int i = 0 ; i < list.size() ; i++) {
//            System.out.println(list.get(i));\
            if(StringUtil.isNotEmpty(distributor))
            list.get(i).put("store",distributor);
        }
        return new PageInfo<>(list);
    }

    //微信端查询推荐商品
    public PageInfo<?> queryGoodsDistributeWithWechat(QueryGoodsDistribute queryGoodsDistribute,String uid) throws Exception {
        //PageHelper.startPage(goodsDistribute.getPageNum(), goodsDistribute.getPageSize());
//        PageHelper.startPage(1,10);
        if (queryGoodsDistribute.getPageNum()!=null && queryGoodsDistribute.getPageSize()!=null){
            PageHelper.startPage(queryGoodsDistribute.getPageNum(), queryGoodsDistribute.getPageSize());
        }
//

        Distributor distributor = distributorMapper.selectByUid(Integer.parseInt(uid), queryGoodsDistribute.getSiteId());
        List<PageData> list = goodsDistributeMapper.findGoodsDistribute(queryGoodsDistribute);
        for(int i = 0 ; i < list.size() ; i++) {
//            System.out.println(list.get(i));\
            if(distributor!=null)
                list.get(i).put("store",distributor.getLevel());
        }
        return new PageInfo<>(list);
    }


    public Object updateDistributionTemplate(QueryGoodsDistribute queryGoodsDistribute) {
        Map<String, Object> map;
        int i = goodsDistributeMapper.updateDistributionTemplate(queryGoodsDistribute);
        if(i!=0){
            map = rewardTemplateMapper.getTemplateByIdandShopPrice(queryGoodsDistribute);
            return map;
        }

        return "500";
    }

    public PageInfo<?> queryGoodsDistributeBytempId(QueryGoodsDistribute queryGoodsDistribute) {
//        PageHelper.startPage(queryGoodsDistribute.getPageNum(), queryGoodsDistribute.getPageSize());
        List<PageData> list = goodsDistributeMapper.queryGoodsDistributeBytempId(queryGoodsDistribute);

        return new PageInfo<>(list);

    }

    public Object updateGoodsDistribute(int tempid,int siteId,int[] goodsId) {
        if(tempid!=0){
            int a=goodsDistributeMapper.updategoodsDistributeBymodelid(tempid,siteId);

        }
        int c=0;
        int b=0;
        for (int i = 0; i <goodsId.length; i++) {
            Goods goods= goodsMapper.getBySiteIdAndGoodsId(goodsId[i],siteId);
            GoodsDistribute goodsDistribute=goodsDistributeMapper.queryByGoodsIdAndSiteId(siteId,goodsId[i]);
//            if (goodsDistribute d)

//            goodsDistribute=JacksonUtils.getInstance().convertValue(goods,GoodsDistribute.class);
            if (goodsDistribute!=null || goods==null){
                b=goodsDistributeMapper.updateDistributionTemplateBysiteIdAndGoodsId(tempid,siteId,goodsId[i]);
            }
            if (goods!=null &&goodsDistribute==null){
             c=goodsDistributeMapper.insertgoodsDistribute(goods,tempid);
            }
        }
        if(b!=0){
            return "200";
        }
        if(c!=0){
            return "200";
        }
        return "500";

    }
}

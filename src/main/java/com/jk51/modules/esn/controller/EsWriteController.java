package com.jk51.modules.esn.controller;

import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.esn.job.EsSearchSchedule;
import com.jk51.modules.esn.service.GoodsEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("es")
@RestController
public class EsWriteController {

    private static final Logger logger = LoggerFactory.getLogger(EsWriteController.class);

    @Autowired
    private GoodsEsService goodsEsService;

    /**
     * delete one record by goods_id and insert one which goods_id is same instead
     * @param shopid
     * @param id
     * @param has_erp_price
     * @return
     */
    @RequestMapping("update/{shopid}/{goodsid}")
    public ReturnDto updateGoods(@PathVariable("shopid")String shopid, @PathVariable("goodsid")String id, String has_erp_price) {
        try {
            logger.info("update brandid:{} ,goodsid:{}",shopid,id);
            goodsEsService.updateGoodsInxALlIndices(shopid, id, has_erp_price);
        }catch (RuntimeException e){
            logger.error("HomeController.updateGoods error , error Message:",e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }catch (Exception e){
            logger.error("HomeController.updateGoods error , error Message:",e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto();
    }

    /**
     * delete all already exist data then query list by site_id to insert into es
     * @param brandid
     * @param has_erp_price
     * @return
     */
    @RequestMapping("batchGoods/{siteId}")
    public ReturnDto batchGoods(@PathVariable("siteId")String brandid, String has_erp_price){
        try {
            goodsEsService.batchUpdateGoods(brandid, has_erp_price);
            goodsEsService.updateSuggestByBrandId(brandid);
        }catch (Exception e){
            logger.error("HomeController.batchGoods error , error Message:",e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto();
    }


    /**
     * update es list by cate_id
     * @param brandid
     * @param cateId     可能是-拼接的字符串
     * @return
     */
    @RequestMapping("batchUpdate/{siteId}/{cateId}")
    public ReturnDto updateGoods(@PathVariable("siteId")String brandid, @PathVariable("cateId")String cateId) {
        try {
            logger.info("update brandid:{} ,cateId:{}",brandid,cateId);
            goodsEsService.updateGoodsByCateIdNNew(brandid, cateId);
            //goodsEsService.updateGoodsByCateId(brandid, cateId);
            //goodsEsService.updateSuggestByBrandIdAndCateId(brandid,cateId);
//            goodsEsService.updateSuggestByBrandId(brandid);
        }catch (RuntimeException e){
            logger.error("HomeController.updateGoods error , error Message:",e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }catch (Exception e){
            logger.error("HomeController.updateGoods error , error Message:",e);
            return ReturnDto.buildSystemErrorReturnDto();
        }
        return ReturnDto.buildSuccessReturnDto();
    }
    @Autowired
    private EsSearchSchedule esSearchSchedule;

    @RequestMapping("autoUpdateESIndex")
    public void autoUpdateESIndex() {
        esSearchSchedule.autoUpdateESIndex();
    }
    @RequestMapping("insertUserStoresDistanceList")
    public void insertUserStoresDistanceList() {
        esSearchSchedule.insertUserStoresDistanceList();
    }

}

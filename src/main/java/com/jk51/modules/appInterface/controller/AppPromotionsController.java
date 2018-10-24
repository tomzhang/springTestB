package com.jk51.modules.appInterface.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.promotions.sequence.SequenceParam;
import com.jk51.model.promotions.sequence.SequenceResult;
import com.jk51.modules.promotions.sequence.app.AppSequenceHandlerImp;
import com.jk51.modules.promotions.sequence.app.AppSequenceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.util.Optional;

/**
 * Created by javen73 on 2018/5/16.
 */
@RequestMapping("/promotions")
@RestController
public class AppPromotionsController {
    private Logger logger = LoggerFactory.getLogger(AppPromotionsController.class);
    @Autowired
    private ServletContext sc;

    @RequestMapping("/showAdPromotionsGoods")
    public ReturnDto showAdPromotionsGoods(SequenceParam param){
        ReturnDto dto = null;
        try{
            AppSequenceImpl appSequence = new AppSequenceImpl(sc,param);
            appSequence.collection().processGoods();
            appSequence.processSequence(new AppSequenceHandlerImp());
            appSequence.processTags();
            Optional<? extends SequenceResult> result = appSequence.result;
            dto = result.map(ReturnDto::buildSuccessReturnDto).orElseGet(() -> ReturnDto.buildFailedReturnDto("查询推荐商品失败"));
        }catch (Exception e){
            logger.error("查询app活动广告推荐商品失败:{}",e);
            dto = ReturnDto.buildFailedReturnDto("查询推荐商品失败");
        }
        return dto;
    }

}

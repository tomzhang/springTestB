package com.jk51.modules.distribution.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.distribute.QueryTemplate;
import com.jk51.model.distribute.RewardTemplate;
import com.jk51.modules.distribution.mapper.RewardTemplateMapper;
import com.jk51.modules.distribution.service.GoodsDistributeService;
import com.jk51.modules.distribution.service.RewardTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guosheng on 2017/4/17.
 */
@RestController
@RequestMapping("/rewardtemplete")
public class RewardTemplateController {
    private static final Logger logger = LoggerFactory.getLogger(RewardTemplate.class);

    @Autowired
    private RewardTemplateService rewardTemplateService;
    @Autowired
    private RewardTemplateMapper rewardTemplateMapper;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ReturnDto query(QueryTemplate queryTemplate){
        PageInfo<?> pageInfo;
        try {
            pageInfo = this.rewardTemplateService.queryTemplete(queryTemplate);
        } catch (Exception e) {
            logger.error("获取模版失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询模版出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 奖励模版编辑增加
     * @param rewardTemplate
     * @return
     */
    @RequestMapping(value="/addTemplete",method = RequestMethod.POST)
    @ResponseBody
    public String addTemplate(RewardTemplate rewardTemplate){
        if (rewardTemplate.getId()==null){
            return rewardTemplateService.addRewardTemplate(rewardTemplate);
        }
        else
            return rewardTemplateService.updateRewardTemplate(rewardTemplate);
    }

//    @RequestMapping("/update")
//    public String updateRewardTemplate(@RequestBody RewardTemplate rewardTemplate ){
//        return rewardTemplateService.updateRewardTemplate(rewardTemplate);
//    }

    @RequestMapping(value = "/find/{id}/{owner}", method = RequestMethod.GET)
    @ResponseBody
    public Object accountDetailById(@PathVariable("id") Integer id, @PathVariable("owner") Integer owner) {

        Map<String, Object> map;
        try {
            map = rewardTemplateMapper.findById(id,owner);
        } catch (Exception e) {
            logger.error("根据id查询模版失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询账单明细详情失败");
        }

        return ReturnDto.buildSuccessReturnDto(map);
    }


    @RequestMapping(value="/editTemplate",method = RequestMethod.POST)
    @ResponseBody
    public String editRewardTemplate(RewardTemplate rewardTemplate ){

        //return rewardTemplateService.editRewardTemplate(rewardTemplate);
        return rewardTemplateService.editRewardTemplate(rewardTemplate);
    }

    @RequestMapping(value = "/queryUser")
    @ResponseBody
    public ReturnDto queryUser(QueryTemplate queryTemplate){
        PageInfo<?> pageInfo;
        try {
            pageInfo = this.rewardTemplateService.queryTempleteUser(queryTemplate);
        } catch (Exception e) {
            logger.error("获取可用模版失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询可用模版出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(value = "/changeById")
    @ResponseBody
    public String changeById(String id){
        return rewardTemplateService.changeById(id);
    }

    @RequestMapping(value = "/queryDiscountMax")
    @ResponseBody
    public List<Map<String,Object>> queryDiscountMax(QueryTemplate queryTemplate){
//        String  discountMax=null;
        List<Map<String,Object>> map=null;
        try {
            map= this.rewardTemplateService.queryDiscountMax(queryTemplate);
        } catch (Exception e) {
            logger.error("获取模版最大优惠失败,错误是" + e);

        }
        return map;
    }
}

package com.jk51.modules.im.service.clerkAnalyze;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.account.models.FinancesStatistic;
import com.jk51.modules.im.controller.request.ClerkAnalyzeParam;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.service.clerkAnalyze.response.ClerkAnalyze;
import com.jk51.modules.im.service.clerkAnalyze.response.Satisfactions;
import com.jk51.modules.im.service.indexCount.IMINdexCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:店员服务情况查询
 * 作者: gaojie
 * 创建日期: 2017-06-22
 * 修改记录:
 */
@Service
public class IMClerkAnalyzeService {


    @Autowired
    private IMINdexCountService indexCountService;
    @Autowired
    private BIMServiceMapper bimServiceMapper;


    /**
     * 查询店员服务情况
     *
     * @param param
     * */
    public ReturnDto queryClerkAnalyze(ClerkAnalyzeParam param){

        String endDay = param.getEnd_day() + " 23:59:59";
        String startDay = indexCountService.getStartDay(null,param.getEnd_day());
        param.setEndDay(endDay);
        param.setStartDay(startDay);

        PageHelper.startPage(param.getPageNum(), 10);//开启分页
        List<ClerkAnalyze> clerkAnalyzes = bimServiceMapper.finClerkAnalyzeBySiteIdAndStartTime(param);
        PageInfo<?> pageInfo = new PageInfo<>(clerkAnalyzes);

        if(StringUtil.isEmpty(clerkAnalyzes)){
            return ReturnDto.buildFailedReturnDto("查询结果为空");
        }

        return ReturnDto.buildSuccessReturnDto(pageInfo);


    }

    /**
     * 查询商家满意度前10名
     * @param site_id required
     * @param month required  "yyyy-MM"
     */
    public ReturnDto querySatisfactionsTop10(int site_id,String month){

        List<Satisfactions> satisfactionsList = bimServiceMapper.findEvaluateAVGByMonth(site_id,month);
        if(StringUtil.isEmpty(satisfactionsList)){
            return ReturnDto.buildFailedReturnDto("查询结果为空");
        }

        return ReturnDto.buildSuccessReturnDto(satisfactionsList);
    }

    /**
     * 查询商家满意度后10名
     * @param site_id required
     * @param month required  "yyyy-MM"
     */
    public ReturnDto querySatisfactionsAfter10(int site_id,String month){

        List<Satisfactions> satisfactionsList = bimServiceMapper.findEvaluateAVGByMonthAfter10(site_id,month);
        if(StringUtil.isEmpty(satisfactionsList)){
            return ReturnDto.buildFailedReturnDto("查询结果为空");
        }

        return ReturnDto.buildSuccessReturnDto(satisfactionsList);
    }

}

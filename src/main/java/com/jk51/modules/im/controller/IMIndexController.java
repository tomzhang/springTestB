package com.jk51.modules.im.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.distribute.OperationRecond;
import com.jk51.modules.im.controller.request.ClerkAnalyzeParam;
import com.jk51.modules.im.controller.request.IMRelationRequest;
import com.jk51.modules.im.service.clerkAnalyze.IMClerkAnalyzeService;
import com.jk51.modules.im.service.clerkAnalyze.response.ClerkAnalyze;
import com.jk51.modules.im.service.iMRecode.IMRecodeService;
import com.jk51.modules.im.service.indexCount.IMINdexCountService;
import com.jk51.modules.im.util.IMIndexRequestBody;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-26
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping("/im/count")
public class IMIndexController {

    @Autowired
    private IMINdexCountService indexCountService;
    @Autowired
    private IMClerkAnalyzeService imClerkAnalyzeService;
    @Autowired
    private IMRecodeService imRecodeService;

    /**
     * 昨日服务质量指标
     *
     * @param site_id
     * @param end_day  "2017-06-24"
     *
     * */
    @RequestMapping("queryServiceIndex")
    public ReturnDto queryServiceIndex(@Param("site_id")Integer site_id, @Param("end_day")String end_day){
        return indexCountService.queryServiceIndex(site_id,end_day);
    }

    /**
     * 7天或30天每天的指标查询
     *@param imIndexRequestBody
     *
     * */
    @RequestMapping("queryIndex")
    public ReturnDto queryIndex(@Valid IMIndexRequestBody imIndexRequestBody){
       return indexCountService.queryIndex(imIndexRequestBody);
    }

    /**
     * 店员服务情况查询
     * @param param
     * */
    @RequestMapping("queryClerkAnalyze")
    public ReturnDto queryClerkAnalyze(@RequestBody ClerkAnalyzeParam param){

        return imClerkAnalyzeService.queryClerkAnalyze(param);

    }

    /**
     *月份满意度Top10
     *@param site_id
     * @param month
     * */
    @RequestMapping("querySatisfactionsTop10")
    public ReturnDto querySatisfactionsTop10(@Param("site_id")Integer site_id,@Param("month")String month){
        return imClerkAnalyzeService.querySatisfactionsTop10(site_id,month);
    }

    /**
     *月份满意度after 10
     *@param site_id
     * @param month
     * */
    @RequestMapping("querySatisfactionsAfter10")
    public ReturnDto querySatisfactionsAfter10(@Param("site_id")Integer site_id,@Param("month")String month){
        return imClerkAnalyzeService.querySatisfactionsAfter10(site_id,month);
    }

    /**
     * 店员账号查询
     *@param site_id
     * @param name  店员名称
     *
     * */
    @RequestMapping("queryClerkList")
    public ReturnDto queryClerkList(@Param("site_id")Integer site_id,@Param("name")String name){
        return imRecodeService.queryClerkList(site_id,name);
    }

    /**
     * 会员账号查询
     *@param site_id
     * @param mobile  会员手机号
     *
     * */
    @RequestMapping("queryMemberList")
    public ReturnDto queryMemberList(@Param("site_id")Integer site_id,@Param("mobile")String mobile){
        return imRecodeService.queryMemberList(site_id,mobile);
    }

    /**
     * 查询聊天关系
     *@param imRelationRequest
     *
     * */
    @RequestMapping("queryIMRelation")
    public ReturnDto queryIMRelation(IMRelationRequest imRelationRequest){
        return imRecodeService.queryIMRelation(imRelationRequest);
    }

    /**
     * 查询聊天记录
     *@param site_id
     *@param store_admin_id
     * @param buyer_id
     * * */
    @RequestMapping("queryIMRecode")
    public ReturnDto queryIMRecode(Integer site_id,Integer store_admin_id,Integer buyer_id){
        return imRecodeService.queryIMRecode(site_id,store_admin_id,buyer_id);
    }

    /**
     * 查询10条聊天记录
     *@param site_id
     *@param store_admin_id
     * @param buyer_id
     * * */
    @RequestMapping("queryIMRecodeTop10")
    public ReturnDto queryIMRecodeTop10(Integer site_id,Integer store_admin_id,Integer buyer_id,String create_time){
        return imRecodeService.queryIMRecodeTop10(site_id,store_admin_id,buyer_id,create_time);
    }

    /**
     * 获取会员下单次数，总金额，客单价
     * @param siteId
     * @param buyerId
     * @return
     */
    @RequestMapping("queryMemberInfo")
    public ReturnDto queryMemberInfo(Integer siteId,Integer buyerId,Integer storeAdminId){

        return imRecodeService.queryMemberInfo(siteId,buyerId,storeAdminId);
    }


    /**
     * 查询商家的全部门店
     * @param site_id
     *
     * */
    @RequestMapping("queryStore")
    public ReturnDto queryStore(Integer site_id){

        return imRecodeService.queryStore(site_id);

    }



    /**
     * 查询商家店员数量和指定时间参与聊天的店员数量
     * */
    @RequestMapping("queryClerkAndJoinImClerkNum")
    public ReturnDto queryClerkAndJoinImClerkNum(Integer site_id,String end_day){

        return  imRecodeService.queryClerkAndJoinImClerkNum(site_id,end_day);

    }

}

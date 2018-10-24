package com.jk51.modules.merchant.controller;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.modules.merchant.service.LabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:会员标签
 * 作者:
 * 创建日期: 2017-06-06
 * 修改记录:
 */
@Controller
@RequestMapping("/label")
public class LabelController {
    public static final Logger log = LoggerFactory.getLogger(LabelController.class);
    @Autowired
    private LabelService labelService;

    /**
     * 添加会员标签
     * @param request
     * @return
     */
    @RequestMapping(value = "/insertLabel")
    @ResponseBody
    public Map<String,Object> insertLabel(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        MemberLabel memberLabel = null;
        try {
            memberLabel = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), MemberLabel.class);
            log.info("########WEB-memberLabel######:{}",JacksonUtils.mapToJson(param).toString());
        } catch (Exception e) {
            log.error("添加会员标签，类型转换异常：{}",e);
        }
        Map<String,Object> map = labelService.insertLabel(memberLabel);
        return map;
    }

    /**
     * 获取全部会员标签
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLabelAll")
    @ResponseBody
    public Map<String,Object> getLabelAll(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer crowdSort = null;
        if (param.containsKey("crowdSort") && !StringUtil.isEmpty(param.get("crowdSort"))){
            crowdSort = Integer.parseInt(String.valueOf(param.get("crowdSort")));
        }
        return labelService.getLabelAll(siteId,crowdSort);
    }
    /**
     * 根据Id查询会员
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLabelById")
    @ResponseBody
    public Map<String,Object> getLabelById(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer crowdSort = Integer.parseInt(String.valueOf(param.get("crowdSort")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        return labelService.getLabelById(siteId,id,crowdSort);
    }
    /**
     * 根据标签名称模糊查询
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLabelByName")
    @ResponseBody
    public Map<String,Object> getLabelByName(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer crowdSort = Integer.parseInt(String.valueOf(param.get("crowdSort")));
        String crowdName = String.valueOf(param.get("crowdName"));
        return labelService.getLabelByName(siteId,crowdName,crowdSort);
    }

    /**
     * 修改会员标签
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateLabel")
    @ResponseBody
    public Map<String,Object> updateLabel(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        MemberLabel memberLabel = null;
        try {
            memberLabel = JacksonUtils.json2pojo(JacksonUtils.mapToJson(param), MemberLabel.class);
        } catch (Exception e) {
            log.error("添加会员标签，类型转换异常:{}",e);
        }
        return labelService.updateLabel(memberLabel);
    }

    /**
     * 删除会员标签
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteLabel")
    @ResponseBody
    public Map<String,Object> deleteLabel(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer id = Integer.parseInt(String.valueOf(param.get("id")));
        Integer crowdSort = Integer.parseInt(String.valueOf(param.get("crowdSort")));
        return labelService.deleteLabel(siteId,id,crowdSort);
    }
    /**
     * 获取标签人数
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLabelCount")
    @ResponseBody
    public Map<String,Object> getLabelCount(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        try {
            return labelService.getLabelCount(params);
        } catch (Exception e) {
            log.error("获取标签人数异常:{}",e);
            return null;
        }

    }

    /**
     * 根据siteId获取区域标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getAreaLabelBySiteId")
    @ResponseBody
    public Object getAreaLabelBySiteId(HttpServletRequest request) throws Exception {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);

        //Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
       // Integer siteId=Integer.parseInt(request.getSession().getAttribute("siteId").toString());
        Integer siteId=Integer.parseInt(param.get("site_id").toString());
        Map<String, Object> result=labelService.getAreaLabelBySiteId(siteId);
        return result;
    }
    /**
     * 查询人群名称是否有重复
     * @param request
     * @return
     */
    @RequestMapping(value="/getBooleanByName")
    @ResponseBody
    public Object getBooleanByName(HttpServletRequest request) throws Exception {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer crowdSort = Integer.parseInt(String.valueOf(param.get("crowdSort")));
        String crowdName = String.valueOf(param.get("crowdName"));
        Map<String, Object> result=labelService.getBooleanByName(siteId,crowdSort,crowdName);
        return result;
    }

    /**
     * 添加自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/insertCustomLabel")
    @ResponseBody
    public Map<String,Object> insertCustomLabel(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.insertCustomLabel(param);
        return map;
    }

    /**
     * 查询商户下所有自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/selectCustomAll")
    @ResponseBody
    public Map<String,Object> selectCustomAll(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.selectCustomAll(param);
        return map;
    }

    /**
     * 根据Id查询自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/selectCustomById")
    @ResponseBody
    public Map<String,Object> selectCustomById(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.selectCustomById(param);
        return map;
    }

    /**
     * 修改自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/updateCustom")
    @ResponseBody
    public Map<String,Object> updateCustom(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.updateCustom(param);
        return map;
    }

    /**
     * 删除自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/deleteCustom")
    @ResponseBody
    public Map<String,Object> deleteCustom(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.deleteCustom(param);
        return map;
    }

    /**
     * 查询自定义标签名称是否存在
     * @param request
     * @return
     */
    @RequestMapping(value="/booleanCustom")
    @ResponseBody
    public Map<String,Object> booleanCustom(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.booleanCustom(param);
        return map;
    }

    /**
     * 获取自定义标签中所有的会员
     * @param request
     * @return
     */
    @RequestMapping(value="/getMemberAllByCustom")
    @ResponseBody
    public Map<String,Object> getMemberAllByCustom(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getMemberAllByCustom(param);
        return map;
    }

    /**
     * 模糊查询自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getDimByCustom")
    @ResponseBody
    public Map<String,Object> getDimByCustom(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getDimByCustom(param);
        return map;
    }

    /**
     * 会员修改——查询回显
     * @param request
     * @return
     */
    @RequestMapping(value="/memberLabelUpdateEcho")
    @ResponseBody
    public Map<String,Object> memberLabelUpdateEcho(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.memberLabelUpdateEcho(param);
        return map;
    }

    /**
     * 会员修改——修改
     * @param request
     * @return
     */
    @RequestMapping(value="/memberLabelUpdate")
    @ResponseBody
    public Map<String,Object> memberLabelUpdate(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.memberLabelUpdate(param);
        return map;
    }

    /**
     * 会员列表
     * @param request
     * @return
     */
    @RequestMapping(value="/getAllMember")
    @ResponseBody
    public Map<String,Object> getAllMember(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getAllMember(param);
        return map;
    }
    /**
     * APP--根据会员ID获取会员的慢病标签和自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getLabelSlowAndCustom")
    @ResponseBody
    public Map<String,Object> getLabelSlowAndCustom(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getLabelSlowAndCustom(param);
        return map;
    }

    /**
     * APP--根据site_id查询改商户下所有自定义标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getCustomLabelBySiteId")
    @ResponseBody
    public Map<String,Object> getCustomLabelBySiteId(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getCustomLabelBySiteId(param);
        return map;
    }

    /**
     * APP--修改用户标签
     * @param request
     * @return
     */
    @RequestMapping(value="/updateLabelByMemberId")
    @ResponseBody
    public Map<String,Object> updateLabelByMemberId(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.updateLabelByMemberId(param);
        return map;
    }

    /**
     * 初始化修改会员的首单时间
     * @param request
     * @return
     */
    @RequestMapping(value="/getFirstOrder")
    @ResponseBody
    public Map<String,Object> getFirstOrder(HttpServletRequest request) {
        Map<String,Object> map = labelService.getFirstOrder();
        return map;
    }

    /**
     * 修改会员的首单时间
     * @param request
     * @return
     */
    @PostMapping(value="/saveFirstOrderTime")
    @ResponseBody
    public void saveFirstOrderTime(HttpServletRequest request) {
        labelService.saveFirstOrderTime(100190,769613);
    }

    /**
     * 门店后台查询给该会员贴标签的所有店员
     * @param request
     * @return
     */
    @RequestMapping(value="/selectStoreadminForStore")
    @ResponseBody
    public Map<String,Object> selectStoreadminForStore(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.selectStoreadminForStore(param);
        return map;
    }

    /**
     * 门店后台删除给该会员贴标签的店员
     * @param request
     * @return
     */
    @RequestMapping(value="/updateStoreadminForStore")
    @ResponseBody
    public Map<String,Object> updateStoreadminForStore(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.updateStoreadminForStore(param);
        return map;
    }

    /**
     * 根据标签名称查询会员的open_id
     * @param request
     * @return
     */
    @RequestMapping(value="/getOpenIdByLabelName")
    @ResponseBody
    public Map<String,Object> getOpenIdByLabelName(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getOpenIdByLabelName(param);
        return map;
    }

    /**
     * 查询商户下所有的人群名称
     * @param request
     * @return
     */
    @RequestMapping(value="/getCrowdNameAll")
    @ResponseBody
    public Map<String,Object> getCrowdNameAll(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getCrowdNameAll(param);
        return map;
    }

    /**
     * 根据人群名称查询人群下所有的OpenId
     * @param request
     * @return
     */
    @RequestMapping(value="/getCrowdOpenIdAll")
    @ResponseBody
    public Map<String,Object> getCrowdOpenIdAll(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getCrowdOpenIdAll(param);
        return map;
    }

    /**
     * 查询商户下所有的标签名称
     * @param request
     * @return
     */
    @RequestMapping(value="/getCustomNameAll")
    @ResponseBody
    public Map<String,Object> getCustomNameAll(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getCustomNameAll(param);
        return map;
    }

    /**
     * 根据标签名称查询改标签下会员的openId
     * @param request
     * @return
     */
    @RequestMapping(value="/getCustomOpenIdAll")
    @ResponseBody
    public Map<String,Object> getCustomOpenIdAll(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getCustomOpenIdAll(param);
        return map;
    }

    /**
     * 获取商户下所有会员的openId
     * @param request
     * @return
     */
    @RequestMapping(value="/getAllMemberOpenId")
    @ResponseBody
    public Map<String,Object> getAllMemberOpenId(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        Map<String,Object> map = labelService.getAllMemberOpenId(param);
        return map;
    }

    /**
     * 初始化：会员慢病标签
     * @param request
     * @return
     */
    @RequestMapping(value="/initMemberSlow")
    @ResponseBody
    public Map<String, Object> initMemberSlow(HttpServletRequest request) {
        return labelService.initMemberSlow();
    }

    /**
     * 商户后台：会员标签批量导入会员
     * @param request
     * @return
     */
    @RequestMapping(value="/addInsertRelation")
    @ResponseBody
    public Map<String, Object> addInsertRelation(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return labelService.addInsertRelation(param);
    }

    /**
     * 获取该店员给指定会员添加的标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getLabelByStoreAsminIdAndMemberId")
    @ResponseBody
    public Map<String,Object> getLabelByStoreAsminIdAndMemberId(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return labelService.getLabelByStoreAsminIdAndMemberId(param);
    }

    /**
     * 获取该店员给指定会员添加的标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getAllLabelByCustom")
    @ResponseBody
    public Map<String,Object> getAllLabelByCustom(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return labelService.getAllLabelByCustom(param);
    }

    /**
     * 修改指定会员的标签
     * @param request
     * @return
     */
    @RequestMapping(value="/updateMemberLabelByStore")
    @ResponseBody
    public Map<String,Object> updateMemberLabelByStore(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return labelService.updateMemberLabelByStore(param);
    }

    /**
     * 初始化：给商户后台贴的标签打上标记“0”
     * @param request
     * @return
     */
    @RequestMapping(value="/initTag")
    @ResponseBody
    public Map<String,Object> initTag(HttpServletRequest request) {
        return labelService.initTag();
    }

    /**
     * 初始化：将慢病标签重新初始化到标签库
     * @param request
     * @return
     */
    @RequestMapping(value="/initSlowLabel")
    @ResponseBody
    public Map<String,Object> initSlowLabel(HttpServletRequest request) {
        return labelService.initSlowLabel();
    }
}

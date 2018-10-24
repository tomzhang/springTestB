package com.jk51.modules.merchant.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.merchant.service.LabelSecondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/17.
 */
@Controller
@RequestMapping("/labelsecond")
public class LabelSecondController {
    @Autowired
    private LabelSecondService labelSecondService;
    /**
     * 添加标签
     * @param request
     * @return
     */
    @RequestMapping(value="/insertAllLabel")
    @ResponseBody
    public Map<String,Object> insertAllLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.insertAllLabel(params);
    }

    /**
     * 查询指定类型下的标签是否有重复
     * @param request
     * @return
     */
    @RequestMapping(value="/getBooleanNameByClassType")
    @ResponseBody
    public Map<String,Object> getBooleanNameByClassType(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getBooleanNameByClassType(params);
    }

    /**
     * 修改标签
     * @param request
     * @return
     */
    @RequestMapping(value="/updateAllLabel")
    @ResponseBody
    public Map<String,Object> updateAllLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.updateAllLabel(params);
    }

    /**
     * 删除标签
     * @param request
     * @return
     */
    @RequestMapping(value="/deleteAllLabel")
    @ResponseBody
    public Map<String,Object> deleteAllLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.deleteAllLabel(params);
    }

    /**
     * 删除&修改 小标签(删除&修改 前查询)
     * @param request
     * @return
     */
    @RequestMapping(value="/deleteAndUpdateLabelToGetLabel")
    @ResponseBody
    public Map<String,Object> deleteAndUpdateLabelToGetLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.deleteAndUpdateLabelToGetLabel(params);
    }

    /**
     * 查询标签
     * @param request
     * @return
     */
    @RequestMapping(value="/seleteAllLabel")
    @ResponseBody
    public Map<String,Object> seleteAllLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.seleteAllLabel(params);
    }

    /**
     * 查询年龄
     * @param request
     * @return
     */
    @RequestMapping(value="/selectAge")
    @ResponseBody
    public Map<String,Object> selectAge(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectAge(params,"Y");
    }

    /**
     * 查询生日
     * @param request
     * @return
     */
    @RequestMapping(value="/selectBirthday")
    @ResponseBody
    public Map<String,Object> selectBirthday(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectBirthday(params,"Y");
    }

    /**
     * 查询注册时间
     * @param request
     * @return
     */
    @RequestMapping(value="/selectRegister")
    @ResponseBody
    public Map<String,Object> selectRegister(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectRegister(params,"Y");
    }

    /**
     * 查询成功交易金额
     * @param request
     * @return
     */
    @RequestMapping(value="/selectBargainMoney")
    @ResponseBody
    public Map<String,Object> selectBargainMoney(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectBargainMoney(params,"Y");
    }

    /**
     * 查询成功交易次数
     * @param request
     * @return
     */
    @RequestMapping(value="/selectBargainCount")
    @ResponseBody
    public Map<String,Object> selectBargainCount(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectBargainCount(params,"Y");
    }

    /**
     * 查询客单价
     * @param request
     * @return
     */
    @RequestMapping(value="/selectPreTransaction")
    @ResponseBody
    public Map<String,Object> selectPreTransaction(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectPreTransaction(params,"Y");
    }

    /**
     * 购买时段
     * @param request
     * @return
     */
    @RequestMapping(value="/selectEverBuy")
    @ResponseBody
    public Map<String,Object> selectEverBuy(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectEverBuy(params,"Y");
    }

    /**
     * 未购买时段
     * @param request
     * @return
     */
    @RequestMapping(value="/selectNotBuy")
    @ResponseBody
    public Map<String,Object> selectNotBuy(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectNotBuy(params,"Y");
    }

    /**
     * 退款率
     * @param request
     * @return
     */
    @RequestMapping(value="/selecRefundProbability")
    @ResponseBody
    public Map<String,Object> selecRefundProbability(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selecRefundProbability(params,"Y");
    }

    /**
     * 购买周期
     * @param request
     * @return
     */
    @RequestMapping(value="/selectBuyPeriod")
    @ResponseBody
    public Map<String,Object> selectBuyPeriod(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectBuyPeriod(params,"Y");
    }

    /**
     * 赚取积分
     * @param request
     * @return
     */
    @RequestMapping(value="/selectAddIntegral")
    @ResponseBody
    public Map<String,Object> selectAddIntegral(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectAddIntegral(params,"Y");
    }

    /**
     * 消费积分
     * @param request
     * @return
     */
    @RequestMapping(value="/selectConsumeIntegral")
    @ResponseBody
    public Map<String,Object> selectConsumeIntegral(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectConsumeIntegral(params,"Y");
    }

    /**
     * 剩余积分
     * @param request
     * @return
     */
    @RequestMapping(value="/selectResidueIntegral")
    @ResponseBody
    public Map<String,Object> selectResidueIntegral(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectResidueIntegral(params,"Y");
    }

    /**
     * 门店距离（活动）
     * @param request
     * @return
     */
    @RequestMapping(value="/selectDisStoreActivity")
    @ResponseBody
    public Map<String,Object> selectDisStoreActivity(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectDisStoreActivity(params,"Y");
    }

    /**
     * 竞店距离（活动）
     * @param request
     * @return
     */
    @RequestMapping(value="/selectDisContendStoreActivity")
    @ResponseBody
    public Map<String,Object> selectDisContendStoreActivity(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectDisContendStoreActivity(params,"Y");
    }

    /**
     * 竞店距离（收货地址）
     * @param request
     * @return
     */
    @RequestMapping(value="/selectDisContendStoreAddress")
    @ResponseBody
    public Map<String,Object> selectDisContendStoreAddress(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectDisContendStoreAddress(params,"Y");
    }

    /**
     * 门店距离（收货地址）
     * @param request
     * @return
     */
    @RequestMapping(value="/selectDisStoreAddress")
    @ResponseBody
    public Map<String,Object> selectDisStoreAddress(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectDisStoreAddress(params,"Y");
    }

    /**
     * 健康标签
     * @param request
     * @return
     */
    @RequestMapping(value="/selectHealth")
    @ResponseBody
    public Map<String,Object> selectHealth(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectHealth(params,"Y");
    }
    //----------------------------------------页面加载---------------------------------------
    /**
     * 基础标签查询（性别）
     * @param request
     * @return
     */
    @RequestMapping(value="/getBaseLabelBySex")
    @ResponseBody
    public Map<String,Object> getBaseLabelBySex(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getBaseLabelBySex(params);
    }

    /**
     * 基础标签年龄，生日，星座，生肖）
     * @param request
     * @return
     */
    @RequestMapping(value="/getBaseLabelByBirthday")
    @ResponseBody
    public Map<String,Object> getBaseLabelByBirthday(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getBaseLabelByBirthday(params);
    }

    /**
     * 基础标签（注册时间）
     * @param request
     * @return
     */
    @RequestMapping(value="/getBaseLabelByRegist")
    @ResponseBody
    public Map<String,Object> getBaseLabelByRegist(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getBaseLabelByRegist(params);
    }

    /**
     * 区域标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getAreaLabel")
    @ResponseBody
    public Map<String,Object> getAreaLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getAreaLabel3(params);
    }

    /**
     * 交易标签（成交金额）
     * @param request
     * @return
     */
    @RequestMapping(value="/getTradesLabelOne")
    @ResponseBody
    public Map<String,Object> getTradesLabelOne(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getTradesLabelOne(params);
    }

    /**
     * 交易标签（成交次数）
     * @param request
     * @return
     */
    @RequestMapping(value="/getTradesLabelTwo")
    @ResponseBody
    public Map<String,Object> getTradesLabelTwo(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getTradesLabelTwo(params);
    }

    /**
     * 交易标签（客单价）
     * @param request
     * @return
     */
    @RequestMapping(value="/getTradesLabelThree")
    @ResponseBody
    public Map<String,Object> getTradesLabelThree(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getTradesLabelThree(params);
    }

    /**
     * 交易标签（退款率、购买过）
     * @param request
     * @return
     */
    @RequestMapping(value="/getTradesLabelFore")
    @ResponseBody
    public Map<String,Object> getTradesLabelFore(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getTradesLabelFore(params);
    }

    /**
     * 交易标签（购买周期）
     * @param request
     * @return
     */
    @RequestMapping(value="/getTradesLabelFive")
    @ResponseBody
    public Map<String,Object> getTradesLabelFive(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getTradesLabelFive(params);
    }

    /**
     * 交易标签（赚取积分，消耗积分，剩余积分）
     * @param request
     * @return
     */
    @RequestMapping(value="/getTradesLabelSix")
    @ResponseBody
    public Map<String,Object> getTradesLabelSix(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getTradesLabelSix(params);
    }

    /**
     * 距离标签:查询门店距离(高频活动)
     * @param request
     * @return
     */
    @RequestMapping(value="/getDistanceLabelByDisStoreActivity")
    @ResponseBody
    public Map<String,Object> getDistanceLabelByDisStoreActivity(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getDistanceLabelByDisStoreActivity(params);
    }

    /**
     * 距离标签:查询门店距离(收货地址)
     * @param request
     * @return
     */
    @RequestMapping(value="/getDistanceLabelByDisStoreAddress")
    @ResponseBody
    public Map<String,Object> getDistanceLabelByDisStoreAddress(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getDistanceLabelByDisStoreAddress(params);
    }

    /**
     * 距离标签:查询竞店距离(高频活动)
     * @param request
     * @return
     */
    @RequestMapping(value="/getDistanceLabelByDisContendStoreActivity")
    @ResponseBody
    public Map<String,Object> getDistanceLabelByDisContendStoreActivity(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getDistanceLabelByDisContendStoreActivity(params);
    }

    /**
     * 距离标签:查询竞店距离(收货地址)
     * @param request
     * @return
     */
    @RequestMapping(value="/getDistanceLabelBydisContendStoreAddress")
    @ResponseBody
    public Map<String,Object> getDistanceLabelBydisContendStoreAddress(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getDistanceLabelBydisContendStoreAddress(params);
    }

    /**
     * 健康标签
     * @param request
     * @return
     */
    @RequestMapping(value="/getHealthLabelByGaoxueya")
    @ResponseBody
    public Map<String,Object> getHealthLabelByGaoxueya(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getHealthLabelByGaoxueya(params);
    }

    /**
     * 健康标签（高血压）
     * @param request
     * @return
     */
    @RequestMapping(value="/getHealthLabelByGaoxuezhi")
    @ResponseBody
    public Map<String,Object> getHealthLabelByGaoxuezhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getHealthLabelByGaoxuezhi(params);
    }

    /**
     * 健康标签（高血脂）
     * @param request
     * @return
     */
    @RequestMapping(value="/getHealthLabelByTangniaobing")
    @ResponseBody
    public Map<String,Object> getHealthLabelByTangniaobing(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getHealthLabelByTangniaobing(params);
    }

    /**
     * 自定义标签（糖尿病）
     * @param request
     * @return
     */
    @RequestMapping(value="/getCustomLabel")
    @ResponseBody
    public Map<String,Object> getCustomLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getCustomLabel(params);
    }

    /**
     * 查询人数
     * @param request
     * @return
     */
    @RequestMapping(value="/getLabelCount")
    @ResponseBody
    public Map<String,Object> getLabelCount(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getLabelCount(params);
    }

    /**
     * 查询会员ID
     * @param request
     * @return
     */
    @RequestMapping(value="/getMemberIdsToInsert")
    @ResponseBody
    public Map<String,Object> getMemberIdsToInsert(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getMemberIdsToInsert(params);
    }

    /**
     * 添加会员标签
     * @param request
     * @return
     */
    @RequestMapping(value="/insertMemberLabel")
    @ResponseBody
    public Map<String,Object> insertMemberLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.insertMemberLabel(params);
    }

    /**
     * 查询人群名称是否有重复
     * @param request
     * @return
     */
    @RequestMapping(value="/getBooleanByName")
    @ResponseBody
    public Map<String,Object> getBooleanByName(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getBooleanByName(params);
    }

    /**
     * 修改会员标签
     * @param request
     * @return
     */
    @RequestMapping(value="/updateMemberLabel")
    @ResponseBody
    public Map<String,Object> updateMemberLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.updateMemberLabel(params);
    }

    /**
     * 删除会员标签
     * @param request
     * @return
     */
    @RequestMapping(value="/deleteMemberLabel")
    @ResponseBody
    public Map<String,Object> deleteMemberLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.deleteMemberLabel(params);
    }

    /**
     * 查询全部会员标签 & 模糊查询  & 按ID查询
     * @param request
     * @return
     */
    @RequestMapping(value="/selectAllMemberLabel")
    @ResponseBody
    public Map<String,Object> selectAllMemberLabel(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.selectAllMemberLabel(params);
    }

    /**
     * 查询年龄：已知/未知
     * @param request
     * @return
     */
    @RequestMapping(value="/getAgeYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getAgeYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getAgeYizhiWeizhi(params);
    }

    /**
     * 查询成交金额：已知/未知
     * @param request
     * @return
     */
    @RequestMapping(value="/getBargainMoneyYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getBargainMoneyYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getBargainMoneyYizhiWeizhi(params);
    }

    /**
     * 查询赚取积分：已知/未知
     * @param request
     * @return
     */
    @RequestMapping(value="/getAddIntegrateYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getAddIntegrateYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getAddIntegrateYizhiWeizhi(params);
    }

    /**
     * 查询消耗积分：已知/未知
     * @param request
     * @return
     */
    @RequestMapping(value="/getConsumeIntegrateYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getConsumeIntegrateYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getConsumeIntegrateYizhiWeizhi(params);
    }

    /**
     * 查询剩余积分：已知/未知
     * @param request
     * @return
     */
    @RequestMapping(value="/getResidueIntegrateYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getResidueIntegrateYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getResidueIntegrateYizhiWeizhi(params);
    }

    /**
     * 门店距离（高频活动）：有活动/没有活动
     * @param request
     * @return
     */
    @RequestMapping(value="/getDisStoreActivityYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getDisStoreActivityYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getDisStoreActivityYizhiWeizhi(params);
    }

    /**
     * 区域：未知/已知
     * @param request
     * @return
     */
    @RequestMapping(value="/getAreaYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getAreaYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getAreaYizhiWeizhi(params);
    }

    /**
     * 门店距离（收货地址）：有地址/没有地址
     * @param request
     * @return
     */
    @RequestMapping(value="/getDisStoreAddressYizhiWeizhi")
    @ResponseBody
    public Map<String,Object> getDisStoreAddressYizhiWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getDisStoreAddressYizhiWeizhi(params);
    }

    /**
     * 零退款
     * @param request
     * @return
     */
    @RequestMapping(value="/getRefundMoneyWeizhi")
    @ResponseBody
    public Map<String,Object> getRefundMoneyWeizhi(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getRefundMoneyWeizhi(params);
    }

    /**
     * 基础标签查询（星座，生肖）
     * @param request
     * @return
     */
    @RequestMapping(value="/getBaseLabelByShengxiaoXingzuo")
    @ResponseBody
    public Map<String,Object> getBaseLabelByShengxiaoXingzuo(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return labelSecondService.getBaseLabelByShengxiaoXingzuo(params);
    }



















    /**
     * 初始化地址坐标
     * @param request
     * @return
     */
    @RequestMapping(value="/getPoint")
    @ResponseBody
    public String getPoint(HttpServletRequest request) {
        return labelSecondService.getPoint();
    }

    /**
     * 初始化地址坐标：查漏
     * @param request
     * @return
     */
    @RequestMapping(value="/getLou")
    @ResponseBody
    public String getLou(HttpServletRequest request) {
        return labelSecondService.getLou();
    }

    /**
     * 初始化最后一次下单时间
     * @param request
     * @return
     */
    @RequestMapping(value="/getEndOrderTime")
    @ResponseBody
    public String getEndOrderTime(HttpServletRequest request) {
        return labelSecondService.getEndOrderTime();
    }

    /**
     * 初始化商户后台标签
     * @param request
     * @return
     */
    @RequestMapping(value="/initLabelBySiteId")
    @ResponseBody
    public String initLabelBySiteId(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        List<Integer> siteIdList = new ArrayList<>();
        if (params.containsKey("siteId")){
            siteIdList.add(Integer.parseInt(String.valueOf(params.get("siteId"))));
        }
        return labelSecondService.initLabelBySiteId(siteIdList);
    }

    //--------------------------------回访接口-------------------------------------------------
    /**
     * 按分组选择:查询会员ID数量
     * @param request
     * @return
     */
    @RequestMapping(value="/getMemberIdForVisitByPeopleCount")
    @ResponseBody
    public Map<String, Object> getMemberIdForVisitByPeopleCount(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return labelSecondService.getMemberIdForVisitByPeopleCount(param);
    }

    /**
     * 按分组选择:查询会员ID
     * @param request
     * @return
     */
    @RequestMapping(value="/getMemberIdForVisitByPeopleIds")
    @ResponseBody
    public Map<String, Object> getMemberIdForVisitByPeopleIds(HttpServletRequest request) {
        Map<String,Object> param= ParameterUtil.getParameterMap(request);
        return labelSecondService.getMemberIdForVisitByPeopleIds(param);
    }

    //-------------------------------------------------------------------------------------------------


}

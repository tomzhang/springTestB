package com.jk51.modules.merchant.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.appInterface.util.Page;
import com.jk51.modules.persistence.mapper.LabelSecondMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/11/17.
 */
@Service
public class LabelSecondService {
    public static final int INT = 2;
    @Autowired
    private LabelSecondMapper labelSecondMapper;
    @Autowired
    private LabelService labelService;

    private static final Logger log = LoggerFactory.getLogger(LabelSecondService.class);

    /**
     * 添加小标签
     *
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> insertAllLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取标签描述
            String labelDescribe = getLabelDescribe(params);
            params.put("labelDescribe",labelDescribe);
            //执行添加
            Integer i = labelSecondMapper.insertAllLabel(params);
            if (i == 1){
                map.put("msg", "添加标签成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "添加标签失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            log.info("添加标签异常:{}", e);
            map.put("msg", "添加标签失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 获取标签描述
     *
     * @param params
     * @return
     */
    public String getLabelDescribe(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        String labelDescribe = "";
        try {
            String labelType = String.valueOf(params.get("labelType"));
            Map<String,Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
            //类型
            String type = "";
            if (labelAttribute.containsKey("type")){
                type = String.valueOf(labelAttribute.get("type"));
            }else if (labelAttribute.containsKey("day")){
                type = String.valueOf(labelAttribute.get("day"));
            }else if (labelAttribute.containsKey("time")){
                type = String.valueOf(labelAttribute.get("time"));
            }
            //范围
            String scope = "";
            if (labelAttribute.containsKey("scope")){
                scope = String.valueOf(labelAttribute.get("scope"));
            }

            if (labelType.equals("age")){
                if (type.equals("1")){
                    Integer age = Integer.parseInt(String.valueOf(labelAttribute.get("age")));
                    labelDescribe = "不足" + age + "岁";
                }else if (type.equals("2")){
                    Integer age = Integer.parseInt(String.valueOf(labelAttribute.get("age")));
                    labelDescribe = age + "岁以上";
                }else if (type.equals("3")){
                    Integer ageMin  = Integer.parseInt(String.valueOf(labelAttribute.get("ageMin")));
                    Integer ageMax  = Integer.parseInt(String.valueOf(labelAttribute.get("ageMax")));
                    labelDescribe = ageMin + "—" + ageMax+ "岁";
                }
            }else if (labelType.equals("birthday")){
                if (type.equals("1")){
                    Integer day  = Integer.parseInt(String.valueOf(labelAttribute.get("day")));
                    labelDescribe = "每月" + day + "号出生的人";
                }else if (type.equals("2")){
                    Integer month  = Integer.parseInt(String.valueOf(labelAttribute.get("month")));
                    labelDescribe = "每年" + month + "月出生的人";
                }else if (type.equals("3")){
                    String yearMin  = String.valueOf(labelAttribute.get("yearMin"));
                    String yearMax  = String.valueOf(labelAttribute.get("yearMax"));
                    labelDescribe = "出生日期为" + yearMin + "—" + yearMax;
                }
            }else if (labelType.equals("register")){
                if (type.equals("1")){
                    Integer day  = Integer.parseInt(String.valueOf(labelAttribute.get("day")));
                    labelDescribe = "距离当前近" + day + "天";
                }else if (type.equals("2")){
                    String registerMin  = String.valueOf(labelAttribute.get("registerMin"));
                    String registerMax  = String.valueOf(labelAttribute.get("registerMax"));
                    labelDescribe = "注册时间为" + registerMin + "—" + registerMax;
                }
            }else if (labelType.equals("bargain_money")){
                labelDescribe = getMothod(labelAttribute,type,scope,"成功交易金额");
            }else if (labelType.equals("bargain_count")){
                labelDescribe = getMothod(labelAttribute,type,scope,"成功交易次数");
            }else if (labelType.equals("pre_transaction")){
                labelDescribe = getMothod(labelAttribute,type,scope,"客单价");
            }else if (labelType.equals("ever_buy")){
                if (type.equals("1")){
                    Integer timeMin  = Integer.parseInt(String.valueOf(labelAttribute.get("timeMin")));
                    labelDescribe = "近" + timeMin + "天购买过";
                }else if (type.equals("2")){
                    String timeMin  = String.valueOf(labelAttribute.get("timeMin"));
                    String timeMax  = String.valueOf(labelAttribute.get("timeMax"));
                    labelDescribe = timeMin + "—" + timeMax + "购买过";
                }else if (type.equals("3")){
                    labelDescribe = "历史累计购买过";
                }
            }else if (labelType.equals("buy_period")){
                if (type.equals("1")){
                    Integer max  = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                    labelDescribe = "购买周期为" + max + "天";
                }else if (type.equals("2")){
                    Integer min  = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                    labelDescribe = "购买周期为" + min + "天以上";
                }else if (type.equals("3")){
                    String max  = String.valueOf(labelAttribute.get("max"));
                    String min  = String.valueOf(labelAttribute.get("min"));
                    labelDescribe = "购买周期为" + min + "到" + max;
                }
            }else if (labelType.equals("refund_probability")){
                labelDescribe = getMothod(labelAttribute,type,scope,"退款率");
                if (type.equals("1")){
                    Integer day  = Integer.parseInt(String.valueOf(labelAttribute.get("timeMin")));
                    if (scope.equals("1")){
                        Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                        labelDescribe = "近" + day + "天退款率小于等于" + max + "%";
                    }else if (scope.equals("2")){
                        Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                        labelDescribe = "近" + day + "天退款率大于等于" + min + "%";
                    }else if (scope.equals("3")){
                        Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                        Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                        labelDescribe = "近" + day + "天退款率介于" + min + "%" + "至" + max + "%";
                    }
                }else if (type.equals("2")){
                    String timeMin  = String.valueOf(labelAttribute.get("timeMin"));
                    String timeMax  = String.valueOf(labelAttribute.get("timeMax"));
                    if (scope.equals("1")){
                        Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                        labelDescribe = timeMin + "—" + timeMax + "退款率小于等于" + max + "%";
                    }else if (scope.equals("2")){
                        Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                        labelDescribe = timeMin + "—" + timeMax + "退款率大于等于" + min + "%";
                    }else if (scope.equals("3")){
                        Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                        Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                        labelDescribe = timeMin + "—" + timeMax + "退款率介于" + min + "%" + "至" + max + "%";
                    }
                }else if (type.equals("3")){
                    if (scope.equals("1")){
                        Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                        labelDescribe = "历史累计退款率小于等于" + max + "%";
                    }else if (scope.equals("2")){
                        Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                        labelDescribe = "历史累计退款率大于等于" + min + "%";
                    }else if (scope.equals("3")){
                        Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                        Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                        labelDescribe = "历史累计退款率介于" + min + "%" + "至" + max + "%";
                    }
                }
            }else if (labelType.equals("add_integral")){
                labelDescribe = getMothod(labelAttribute,type,scope,"赚取积分");
            }else if (labelType.equals("consume_integral")){
                labelDescribe = getMothod(labelAttribute,type,scope,"消耗积分");
            }else if (labelType.equals("residue_integral")){
                if (type.equals("1")){
                    Integer max  = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                    labelDescribe = "剩余积分小于等于"+max+"（且非零）";
                }else if (type.equals("2")){
                    Integer min  = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                    labelDescribe = "剩余积分大于等于"+min;
                }else if (type.equals("3")){
                    Integer max  = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                    Integer min  = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                    labelDescribe = "剩余积分介于"+min+"至"+max+"之间";
                }
            }else if (labelType.equals("dis_store_activity")){
                labelDescribe = getMothodStore(labelAttribute,type,scope,"活动距离离最近门店");
            }else if (labelType.equals("dis_contend_store_activity")){
                labelDescribe = getMothodStore(labelAttribute,type,scope,"活动距离离最近竞店");
            }else if (labelType.equals("dis_store_address")){
                labelDescribe = getMothodAddress(labelAttribute,type,"门店");
            }else if (labelType.equals("dis_contend_store_address")){
                labelDescribe = getMothodAddress(labelAttribute,type,"竞店");
            }else if (labelType.equals("health_gao_xue_ya")){
                labelDescribe = getMothodIll(labelAttribute,type,"高血压");
            }else if (labelType.equals("health_gao_xue_zhi")){
                labelDescribe = getMothodIll(labelAttribute,type,"高血脂");
            }else if (labelType.equals("health_tang_niao_bing")){
                labelDescribe = getMothodIll(labelAttribute,type,"糖尿病");
            }
            return labelDescribe;
        } catch (Exception e) {
            log.info("获取标签描述异常:{}", e);
            return labelDescribe;
        }
    }

    //描述抽方法（门店收货地址，竞店收货地址）
    public String getMothodIll(Map<String, Object> labelAttribute,String type,String state){
        String labelDescribe = "";
        if (type.equals("1")){
            Integer max  = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
            labelDescribe = state + "风险等级小于" + max + "级";
        }else if (type.equals("2")){
            Integer min  = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
            labelDescribe = state + "风险等级大于" + min + "级";
        }else if (type.equals("3")){
            String max  = String.valueOf(labelAttribute.get("max"));
            String min  = String.valueOf(labelAttribute.get("min"));
            labelDescribe = state + "风险等级在" + min + "到" + max + "级之间";
        }
        return labelDescribe;
    }

    //描述抽方法（门店收货地址，竞店收货地址）
    public String getMothodAddress(Map<String, Object> labelAttribute,String type,String state){
        String labelDescribe = "";
        if (type.equals("1")){
            Integer max  = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
            labelDescribe = "收货地址距离最近" + state + "在0到" + max + "米内";
        }else if (type.equals("2")){
            Integer min  = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
            labelDescribe = "收货地址距离最近" + state + "在" + min + "米以外";
        }else if (type.equals("3")){
            String max  = String.valueOf(labelAttribute.get("max"));
            String min  = String.valueOf(labelAttribute.get("min"));
            labelDescribe = "收货地址距离最近" + state + "在" + min + "到" + max + "米内";
        }
        return labelDescribe;
    }

    //描述抽方法（成交金额，成交次数，客单价，赚取积分，消耗积分，剩余积分）
    public String getMothod(Map<String, Object> labelAttribute,String type,String scope,String state){
        String labelDescribe = "";
        if (type.equals("1")){
            Integer day  = Integer.parseInt(String.valueOf(labelAttribute.get("timeMin")));
            if (scope.equals("1")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                labelDescribe = "近" + day + "天" + state + "小于等于" + max;
            }else if (scope.equals("2")){
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "近" + day + "天" + state + "大于等于" + min;
            }else if (scope.equals("3")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "近" + day + "天" + state + "介于" + min + "至" + max;
            }
        }else if (type.equals("2")){
            String timeMin  = String.valueOf(labelAttribute.get("timeMin"));
            String timeMax  = String.valueOf(labelAttribute.get("timeMax"));
            if (scope.equals("1")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                labelDescribe = timeMin + "—" + timeMax +  state + "小于等于" + max;
            }else if (scope.equals("2")){
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = timeMin + "—" + timeMax + state + "大于等于" + min;
            }else if (scope.equals("3")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = timeMin + "—" + timeMax + state + "介于" + min + "至" + max;
            }
        }else if (type.equals("3")){
            if (scope.equals("1")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                labelDescribe = "历史累计" + state + "小于等于" + max;
            }else if (scope.equals("2")){
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "历史累计" + state + "大于等于" + min;
            }else if (scope.equals("3")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "历史累计" + state + "介于" + min + "至" + max;
            }
        }
        return labelDescribe;
    }

    //描述抽方法（门店距离活动，竞店距离活动）
    public String getMothodStore(Map<String, Object> labelAttribute,String type,String scope,String state){
        String labelDescribe = "";
        if (type.equals("1")){
            Integer day  = Integer.parseInt(String.valueOf(labelAttribute.get("timeMin")));
            if (scope.equals("1")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                labelDescribe = "近" + day + "天" + state + "小于等于" + max + "米";
            }else if (scope.equals("2")){
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "近" + day + "天" + state + "大于等于" + min + "米";
            }else if (scope.equals("3")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "近" + day + "天" + state + "介于" + min + "至" + max + "米";
            }
        }else if (type.equals("2")){
            String timeMin  = String.valueOf(labelAttribute.get("timeMin"));
            String timeMax  = String.valueOf(labelAttribute.get("timeMax"));
            if (scope.equals("1")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                labelDescribe = timeMin + "—" + timeMax +  state + "小于等于" + max + "米";
            }else if (scope.equals("2")){
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = timeMin + "—" + timeMax + state + "大于等于" + min + "米";
            }else if (scope.equals("3")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = timeMin + "—" + timeMax + state + "介于" + min + "至" + max + "米";
            }
        }else if (type.equals("3")){
            if (scope.equals("1")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                labelDescribe = "历史累计" + state + "小于等于" + max + "米";
            }else if (scope.equals("2")){
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "历史累计" + state + "大于等于" + min + "米";
            }else if (scope.equals("3")){
                Integer max = Integer.parseInt(String.valueOf(labelAttribute.get("max")));
                Integer min = Integer.parseInt(String.valueOf(labelAttribute.get("min")));
                labelDescribe = "历史累计" + state + "介于" + min + "至" + max + "米";
            }
        }
        return labelDescribe;
    }

    /**
     * 查询指定类型下的标签是否有重复
     *
     * @param params
     * @return
     */
    public Map<String, Object> getBooleanNameByClassType(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            int j = getBooleanByTypeOrName(params);
            Integer i = labelSecondMapper.getBooleanNameByClassType(params);
            if (j == 1 || i > 0){
                map.put("msg", "标签名称已经存在，请更换名称...");
                map.put("status", 1);
                return map;
            }else {
                map.put("msg", "标签名称可以使用");
                map.put("status", 0);
                return map;
            }
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            map.put("msg", "查询异常");
            map.put("status", -1);
            return map;
        }
    }

    //抽方法（判断标签类型和标签名称）
    public int getBooleanByTypeOrName(Map<String, Object> params){
        String labelType = String.valueOf(params.get("labelType"));
        String labelName = String.valueOf(params.get("labelName"));
        if (labelType.equals("age") || labelType.equals("birthday")){
            if (labelName.equals("未知")){
                return 1;
            }
        }else if (labelType.equals("bargainMoney") && labelName.equals("无成交")){
            return 1;
        }else if (labelType.equals("bargainCount") && labelName.equals("零次")){
            return 1;
        }else if (labelType.equals("preTransaction") && labelName.equals("客单价为零")){
            return 1;
        }else if (labelType.equals("preTransaction") && labelName.equals("客单价为零")){
            return 1;
        }else if (labelType.equals("refundProbability") && labelName.equals("零退款")){
            return 1;
        }else if (labelType.equals("healthGaoXueYa") || labelType.equals("healthGaoXueZhi") || labelType.equals("healthTangNiaoBing")){
            if (labelName.equals("已确诊")){
                return 1;
            }
        }
        return 0;
    }

    /**
     * 修改小标签
     *
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> updateAllLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取标签描述
            String labelDescribe = getLabelDescribe(params);
            params.put("labelDescribe",labelDescribe);

            String name = String.valueOf(params.get("labelName"));
            String type = String.valueOf(params.get("labelType"));
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            //根据ID查询这个标签
            Map<String,Object> labelsOne = labelSecondMapper.seleteAllLabel(params);
            String labelName = String.valueOf(labelsOne.get("labelName"));
            String labelAttributeOne = String.valueOf(labelsOne.get("labelAttribute"));//查询出来的
            String labelAttributeTwo = String.valueOf(params.get("labelAttribute"));//获取修改参数传来的

            //执行修改
            Integer i = labelSecondMapper.updateAllLabel(params);

            //如果参数发生改变
            if (!labelAttributeOne.equals(labelAttributeTwo)){
                //获取要修改的会员分组的名称和数量
                if (params.containsKey("deleteList") && !StringUtil.isEmpty(params.get("deleteList"))){
                    Map<String, Object> deleteListMap = JacksonUtils.json2map(String.valueOf(params.get("deleteList")));
                    List<Map<String, Object>> deleteList = (List<Map<String, Object>>)deleteListMap.get("deleteList");
                    if (deleteList.size() > 0){
                        for (Map<String, Object> labelMap : deleteList){
                            String labelNameTwo = String.valueOf(labelMap.get("labelName"));
                            Integer labelCountTwo = Integer.parseInt(String.valueOf(labelMap.get("labelCount")));
                            params.put("labelNameTwo",labelNameTwo);
                            //根据分组名称查询标签组
                            Map<String,Object> labels = labelSecondMapper.getLabelGroupByName(params);
                            Map<String,Object> labelGroup = JacksonUtils.json2map(String.valueOf(labels.get("labelGroup")));

                            //根据名称修改数量
                            params.put("labelNameTwo",labelNameTwo);
                            params.put("labelCountTwo",labelCountTwo);

                            //获取会员分组的IDs
                            getShuXing(labelGroup,siteId);
                            Map<String,Object> chuanmap = new HashMap<>();
                            chuanmap.put("labelParams",JSON.toJSONString(labelGroup));
                            chuanmap.put("siteId",siteId);
                            Map<String, Object> idsMap = getMemberIdsToInsert(chuanmap);
                            String ids = String.valueOf(idsMap.get("memberIds"));
                            Map<String,Object> userIds = new HashMap<>();
                            userIds.put("userIds",ids);
                            params.put("memberIds",JSON.toJSONString(userIds));

                            //修改会员分组的会员数量 & 会员分组IDs
                            labelSecondMapper.updateCountByName(params);
                        }
                    }
                }
            }

            if (i == 1){
                updateName(name,labelName,type,params);
                map.put("msg", "修改标签成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "修改标签失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            log.info("修改标签异常:{}", e);
            map.put("msg", "修改标签失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 如果标签名称发生修改
     *
     * name 前端页面传来的标签名称
     * labelName 后台数据查询出来的，之前的标签名称
     * type 标签所属的类型
     *
     */

    public void updateName(String name, String labelName, String type, Map<String, Object> params){
        try{
            //如果修改了名称，就把会员标签labelGroup中，对应的标签名称改掉
            if (!name.equals(labelName)){
                String labelType = getType(type);
                List<Map<String,Object>> labelList = labelSecondMapper.getListByType(params);//查询所有的会员标签
                if (labelList.size() > 0){
                    for (Map<String,Object> labels : labelList){
                        Map<String,Object> labelsMap = JacksonUtils.json2map(String.valueOf(labels.get("label_group")));
                        List<String> labelStrList = (List<String>)labelsMap.get(labelType);
                        if (labelStrList.size() > 0){
                            for (int j = 0;j<labelStrList.size();j++){
                                String label = labelStrList.get(j);
                                if (label.equals(labelName)){
                                    labelStrList.remove(j);
                                    labelStrList.add(name);
                                    Integer id = Integer.parseInt(String.valueOf(labels.get("id")));
                                    //根据ID修改会员标签的名称
                                    String labelMap = JSON.toJSON(labelsMap).toString();
                                    labelSecondMapper.updateParamsNameById(id,labelMap);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            log.info("修改标签名称异常:{}", e);
        }
    }

    //处理type
    public String getType(String type){
        String labelType = "";
        if (type.equals("age")){
            labelType = "age";
        }else if (type.equals("register")){
            labelType = "register";
        }else if (type.equals("bargain_money")){
            labelType = "bargainMoney";
        }else if (type.equals("bargain_count")){
            labelType = "bargainCount";
        }else if (type.equals("pre_transaction")){
            labelType = "preTransaction";
        }else if (type.equals("refund_probability")){
            labelType = "refundProbability";
        }else if (type.equals("ever_buy")){
            labelType = "everBuy";
        }else if (type.equals("buy_period")){
            labelType = "buyPeriod";
        }else if (type.equals("add_integral")){
            labelType = "addIntegral";
        }else if (type.equals("consume_integral")){
            labelType = "consumeIntegral";
        }else if (type.equals("residue_integral")){
            labelType = "residueIntegral";
        }else if (type.equals("dis_store_activity")){
            labelType = "disStoreActivity";
        }else if (type.equals("dis_store_address")){
            labelType = "disStoreAddress";
        }else if (type.equals("dis_contend_store_activity")){
            labelType = "disContendStoreActivity";
        }else if (type.equals("dis_contend_store_address")){
            labelType = "disContendStoreAddress";
        }else if (type.equals("health_gao_xue_ya")){
            labelType = "healthGaoXueYa";
        }else if (type.equals("health_gao_xue_zhi")){
            labelType = "healthGaoXueZhi";
        }else if (type.equals("health_tang_niao_bing")){
            labelType = "healthTangNiaoBing";
        }else if (type.equals("custom")){
            labelType = "custom";
        }
        return labelType;
    }

    /**
     * 删除小标签
     *
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> deleteAllLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //根据ID查询这个标签
            Map<String,Object> label = labelSecondMapper.seleteAllLabel(params);
            String name = String.valueOf(label.get("labelName"));
            String labelType = String.valueOf(label.get("labelType"));

            labelType = getType(labelType);
            updateMemberLabeltoDelete(params,name,labelType);

            Integer i = labelSecondMapper.deleteAllLabel(params);
            if (i == 1){
                map.put("msg", "删除标签成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "删除标签失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            log.info("删除标签异常:{}", e);
            map.put("msg", "删除标签失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 执行删除：修改会员分组的会员数量和会员IDs
     *
     * @param params
     * @param name 后台数据查询得到
     * @param labelType 要修改数据的类型
     */
    public void updateMemberLabeltoDelete(Map<String, Object> params, String name,String labelType){
        try{
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            if (params.containsKey("deleteList") && !StringUtil.isEmpty(params.get("deleteList"))){
                Map<String, Object> deleteListMap = JacksonUtils.json2map(String.valueOf(params.get("deleteList")));
                List<Map<String, Object>> deleteList = (List<Map<String, Object>>)deleteListMap.get("deleteList");
                if (deleteList.size() > 0){
                    for (Map<String, Object> labelMap : deleteList){
                        String labelName = String.valueOf(labelMap.get("labelName"));
                        Integer labelCount = Integer.parseInt(String.valueOf(labelMap.get("labelCount")));
                        params.put("labelNameTwo",labelName);
                        //根据分组名称查询标签组
                        Map<String,Object> labels = labelSecondMapper.getLabelGroupByName(params);
                        Map<String,Object> labelGroup = JacksonUtils.json2map(String.valueOf(labels.get("labelGroup")));
                        List<String> labelList = (List<String>)labelGroup.get(labelType);
                        if (labelList.size() > 0 ){
                            for (String string : labelList){
                                if (string.equals(name)){
                                    labelList.remove(string);
                                    break;
                                }
                            }
                        }
                        //修改会员分组的会员数量
                        params.put("labelCount",labelCount);
                        params.put("labelGroup",JSON.toJSONString(labelGroup));

                        //获取会员分组的IDs
                        getShuXing(labelGroup,siteId);
                        Map<String,Object> chuanmap = new HashMap<>();
                        chuanmap.put("labelParams",JSON.toJSONString(labelGroup));
                        chuanmap.put("siteId",siteId);
                        Map<String, Object> idsMap = getMemberIdsToInsert(chuanmap);
                        String ids = String.valueOf(idsMap.get("memberIds"));
                        Map<String,Object> userIds = new HashMap<>();
                        userIds.put("userIds",ids);
                        params.put("memberIds",JSON.toJSONString(userIds));

                        //修改会员分组的会员数量 & 会员分组IDs
                        labelSecondMapper.updateLabelGroup(params);
                    }
                }
            }
        }catch (Exception e) {
            log.info("修改会员分组的会员数量和会员IDs:{}", e);
        }
    }


    /**
     * 删除&修改 小标签(删除&修改 前查询)
     *
     * @param params
     * @return
     */
    public Map<String, Object> deleteAndUpdateLabelToGetLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<Map<String, Object>> labelNamesList = new ArrayList<>();
            //获取要删除的标签的属性值
            String labelKey = String.valueOf(params.get("labelKey"));//标签类名称
            labelKey = getType(labelKey);
            String labelValue = String.valueOf(params.get("labelValue"));//标签名称
            Integer type = Integer.parseInt(String.valueOf(params.get("type")));
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));

            //根据标签名称查询含有此标签的会员分组有哪些
            List<Map<String,Object>> labelList = labelSecondMapper.getAllLabel(params);
            if (labelList.size() > 0){
                for (Map<String,Object> labels : labelList){
                    Map<String,Object> labelsMap = JacksonUtils.json2map(String.valueOf(labels.get("labelGroup")));
                    if (labelsMap.containsKey(labelKey)){
                        List<String> namesList = (List<String>)labelsMap.get(labelKey);
                        if (namesList.size() > 0){
                            if (namesList.contains(labelValue)){
                                Map<String, Object> returnMap = new HashMap<>();
                                String name = String.valueOf(labels.get("name"));
                                //根据名称查label_attribute
                                String label_attribute = labelSecondMapper.getLabelAttribute(siteId,labelKey,name);
                                //获取当前分组人数
                                Integer nowCount = Integer.parseInt(String.valueOf(labels.get("count")));
                                //迭代器删除集合中的标签
                                Iterator<String> it = namesList.iterator();
                                while (it.hasNext()){
                                    String str = it.next();
                                    if (str.equals(labelValue)){
                                        it.remove();
                                    }
                                }

                                //拿到每一个标签的属性这
                                getShuXing(labelsMap,siteId);

                                if (type == 2){//获取修改后的标签分组
                                    String labelAttribute = String.valueOf(params.get("labelAttribute"));
                                    for (String key : labelsMap.keySet()){
                                        if (key.equals(labelKey)){
                                            List<String> strList = (List<String>)labelsMap.get(key);
                                            for (String str : strList){
                                                if (str.equals(label_attribute)){
                                                    strList.remove(str);
                                                    break;
                                                }
                                            }
                                            strList.add(labelAttribute);
                                        }
                                    }
                                }

                                Map<String, Object> parMap = new HashMap<>();
                                parMap.put("labelParams",JSON.toJSONString(labelsMap));
                                parMap.put("siteId",siteId);
                                Map<String, Object> labelCountMap = getLabelCount(parMap);//获取会员分组人数
                                Integer houCount = Integer.parseInt(String.valueOf(labelCountMap.get("count")));
                                returnMap.put("name",name);
                                returnMap.put("nowCount",nowCount);
                                returnMap.put("houCount",houCount);
                                labelNamesList.add(returnMap);
                            }
                        }
                    }
                }
            }
            map.put("returnList", labelNamesList);
            map.put("msg", "查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询失败");
            map.put("status", -1);
            return map;
        }
    }

    public void getShuXing(Map<String,Object> labelsMap,Integer siteId){
        try{
            for (String key : labelsMap.keySet()){
                List<String> strList = (List<String>)labelsMap.get(key);
                if (key.equals("age") && strList.size() > 0){
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,key,strList);
                    if (strList.contains("未知")){
                        shuXingMap.add("{\"type\":4}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("register")&& strList.size() > 0){//注册时间
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,key,strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("birthday")&& strList.size() > 0){//生日
                    List<String> shuXingMap = new ArrayList<>();
                    for (String bir : strList){
                        if (bir.equals("未知")){
                            shuXingMap.add("00");
                        }else {
                            String s = bir.substring(0,bir.lastIndexOf("号"));
                            if (s.length() == 1){
                                s = "0" + s;
                            }
                            shuXingMap.add(s);
                        }
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("bargainMoney")&& strList.size() > 0){//成交金额
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"bargain_money",strList);
                    if (strList.contains("无成交")){
                        shuXingMap.add("{\"time\":4}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("bargainCount")&& strList.size() > 0){//成交次数
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"bargain_count",strList);
                    if (strList.contains("零次")){
                        shuXingMap.add("{\"time\":4}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("preTransaction")&& strList.size() > 0){//客单价为0
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"pre_transaction",strList);
                    if (strList.contains("客单价为零")){
                        shuXingMap.add("{\"time\":4}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("refundProbability")&& strList.size() > 0){//退款率为0
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"refund_probability",strList);
                    if (strList.contains("零退款")){
                        shuXingMap.add("{\"time\":4}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("healthGaoXueYa")&& strList.size() > 0){//高血压
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"health_gao_xue_ya",strList);
                    if (strList.contains("已确诊")){
                        shuXingMap.add("{\"ill\":\"health_gao_xue_ya_sure\"}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("healthGaoXueZhi")&& strList.size() > 0){//高血脂
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"health_gao_xue_zhi",strList);
                    if (strList.contains("已确诊")){
                        shuXingMap.add("{\"ill\":\"health_gao_xue_zhi_sure\"}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("healthTangNiaoBing")&& strList.size() > 0){//糖尿病
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"health_tang_niao_bing",strList);
                    if (strList.contains("已确诊")){
                        shuXingMap.add("{\"ill\":\"health_tang_niao_bing_sure\"}");
                    }
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("everBuy")&& strList.size() > 0){//购买过
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"ever_buy",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("buyPeriod")&& strList.size() > 0){//购买周期
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"buy_period",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("addIntegral")&& strList.size() > 0){//获取积分
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"add_integral",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("consumeIntegral")&& strList.size() > 0){//消耗积分
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"consume_integral",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("residueIntegral")&& strList.size() > 0){//剩余积分
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"residue_integral",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("disStoreActivity")&& strList.size() > 0){//门店距离（高频活动）
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"dis_store_activity",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("disStoreAddress")&& strList.size() > 0){//门店距离（收货地址）
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"dis_store_address",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("disContendStoreActivity")&& strList.size() > 0){//竞店距离（高频活动）
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"dis_contend_store_activity",strList);
                    labelsMap.put(key,shuXingMap);
                }else if (key.equals("disContendStoreAddress")&& strList.size() > 0){//竞店距离（收货地址）
                    List<String> shuXingMap = labelSecondMapper.getShuxing(siteId,"dis_contend_store_address",strList);
                    labelsMap.put(key,shuXingMap);
                }
            }
        }catch (Exception e) {
            log.info("查询异常:{}", e);
        }
    }

    /**
     * 查询小标签
     *
     * @param params
     * @return
     */
    public Map<String, Object> seleteAllLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            Map<String,Object> label = labelSecondMapper.seleteAllLabel(params);
            label.put("siteId",siteId);
            //回显人数与百分数
            Map<String, Object> map2 = JacksonUtils.json2map(String.valueOf(label.get("labelAttribute")));
            map2.put("siteId",siteId);
            String labelType = String.valueOf(label.get("labelType"));
            Map<String, Object> map1 = new HashMap<>();
            if (labelType.equals("age")){
                map1 = selectAge(map2,"Y");
            }else if (labelType.equals("birthday")){
                map1 = selectBirthday(label,"Y");
            }else if (labelType.equals("register")){
                map1 = selectRegister(label,"Y");
            }else if (labelType.equals("bargain_money")){
                map1 = selectBargainMoney(label,"Y");
            }else if (labelType.equals("bargain_count")){
                map1 = selectBargainCount(label,"Y");
            }else if (labelType.equals("pre_transaction")){
                map1 = selectPreTransaction(label,"Y");
            }else if (labelType.equals("ever_buy")){
                map1 = selectEverBuy(label,"Y");
            }else if (labelType.equals("buy_period")){
                map1 = selectBuyPeriod(label,"Y");
            }else if (labelType.equals("refund_probability")){
                map1 = selecRefundProbability(label,"Y");
            }else if (labelType.equals("add_integral")){
                map1 = selectAddIntegral(label,"Y");
            }else if (labelType.equals("consume_integral")){
                map1 = selectConsumeIntegral(label,"Y");
            }else if (labelType.equals("residue_integral")){
                map1 = selectResidueIntegral(label,"Y");
            }else if (labelType.equals("dis_store_activity")){
                map1 = selectDisStoreActivity(label,"Y");
            }else if (labelType.equals("dis_store_address")){
                map1 = selectDisStoreAddress(label,"Y");
            }else if (labelType.equals("dis_contend_store_activity")){
                map1 = selectDisContendStoreActivity(label,"Y");
            }else if (labelType.equals("dis_contend_store_address")){
                map1 = selectDisContendStoreAddress(label,"Y");
            }else if (labelType.equals("health_gao_xue_ya")){
                map1 = selectHealth(label,"Y");
            }else if (labelType.equals("health_gao_xue_zhi")){
                map1 = selectHealth(label,"Y");
            }else if (labelType.equals("health_tang_niao_bing")){
                map1 = selectHealth(label,"Y");
            }
            label = getCountAndRatio(map1, label);

            if (!StringUtil.isEmpty(label)){
                map.put("label", label);
                map.put("msg", "查询标签成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "查询标签失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            log.info("查询标签异常:{}", e);
            map.put("msg", "查询标签失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询小标签
     * @return
     */
    public Map<String, Object> getCountAndRatio(Map<String, Object> map1,Map<String, Object> label) {
        try {
            Map<String, Object> map2 = (Map<String, Object>)map1.get("returnMap");
            Integer count = Integer.parseInt(String.valueOf(map2.get("count")));
            String ratio = String.valueOf(map2.get("ratio"));
            label.put("count",count);
            label.put("ratio",ratio);
            return label;
        } catch (Exception e) {
            log.info("查询标签抽方法异常:{}", e);
            return label;
        }
    }

    /**
     * 添加会员标签
     *
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> insertMemberLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, Object> memberIdsToInsert = getMemberIdsToInsert(params);
            if (!StringUtil.isEmpty(memberIdsToInsert.get("memberIds"))){
                String memberIds = String.valueOf(memberIdsToInsert.get("memberIds"));

                if (!StringUtil.isEmpty(memberIds)){
                    Map<String,String> ids = new HashMap<>();
                    ids.put("userIds",memberIds);
                    params.put("scene",JacksonUtils.mapToJson(ids));
                }
            }
            Integer i = labelSecondMapper.insertMemberLabel(params);
            if (i == 1){
                map.put("msg", "添加会员标签成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "添加会员标签失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            log.info("添加会员标签异常:{}", e);
            map.put("msg", "添加会员标签失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询人群名称是否有重复
     *
     * @param params
     * @return
     */
    public Map<String,Object> getBooleanByName(Map<String, Object> params) {
        Map<String, Object> map = new HashedMap();
        try{
            Integer i = labelSecondMapper.getBooleanByName(params);
            if (i > 0){
                map.put("code",-1);
                map.put("msg","标签人群名称已经存在，请重新输入");
                return map;
            }else {
                map.put("code",0);
                map.put("msg","success");
                return map;
            }
        }catch (Exception e){
            log.error("查询失败:{}",e);
            return null;
        }
    }

    /**
     * 修改会员标签
     *
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> updateMemberLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, Object> memberIdsToInsert = getMemberIdsToInsert(params);
            String memberIds = String.valueOf(memberIdsToInsert.get("memberIds"));
            Map<String,String> ids = new HashMap<>();
            ids.put("userIds",memberIds);
            params.put("scene",JacksonUtils.mapToJson(ids));

            Integer i = labelSecondMapper.updateMemberLabel(params);
            if (i == 1){
                map.put("msg", "修改会员标签成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "修改会员标签失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            log.info("添加会员标签异常:{}", e);
            map.put("msg", "添加会员标签失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 删除会员标签
     *
     * @param params
     * @return
     */
    public Map<String, Object> deleteMemberLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer i = labelSecondMapper.deleteMemberLabel(params);
            if (i == 1){
                map.put("msg", "删除会员标签成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "删除会员标签失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            log.info("删除会员标签异常:{}", e);
            map.put("msg", "删除会员标签失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询全部会员标签 & 模糊查询  & 按ID查询
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectAllMemberLabel(Map<String, Object> params) {

        Map<String, Object> map = new HashMap<>();
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        try {
            //按ID查询
            if (params.containsKey("id")){
                List<Map<String, Object>> allLabel = labelSecondMapper.selectAllMemberLabel(params);
                //区域反编译
                if (allLabel.size() > 0){
                    chuliAreaList(allLabel,siteId);
                }
                map.put("allLabel", allLabel);
            }else {//查询全部会员标签 & 模糊查询--->列表展示
                Integer pageNum = Integer.parseInt(String.valueOf(params.get("page")));
                Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
                PageHelper.startPage(pageNum,pageSize);//开启分页
                List<Map<String, Object>> allLabelList = labelSecondMapper.selectAllMemberLabel(params);
                PageInfo<Map<String, Object>> allLabel = new PageInfo<>(allLabelList);
                //区域反编译
                if (allLabelList.size() > 0){
                    List<Map<String, Object>> list = allLabel.getList();
                    chuliAreaList(list,siteId);
                }
                map.put("allLabel", allLabel);
            }
            map.put("msg", "查询全部会员标签成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询全部会员标签异常:{}", e);
            map.put("msg", "查询全部会员标签失败");
            map.put("status", -1);
            return map;
        }
    }

    public void chuliAreaList(List<Map<String, Object>> list,Integer siteId){
        try{
            for (Map<String, Object> labelMap : list){
                Map<String, Object> labelGroup = JacksonUtils.json2map(String.valueOf(labelMap.get("labelGroup")));
                List<String> areaList = (List<String>)labelGroup.get("area");
                if (areaList.size() > 0){
                    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");//判断传来的参数是数字还是汉字，用来区分新老数据
                    String str = areaList.get(0);
                    if (pattern.matcher(str).matches()){

                        //处理list，将同一城市的区ID放一起（已选择）
                        Set<String> qvchSet = new HashSet();
                        for (String areaStr : areaList){
                            qvchSet.add(areaStr.substring(0,4));
                        }
                        List<List<String>> heList = new ArrayList<>();//创建一个list，存放不同城市区ID的list
                        for (String aStr : qvchSet){
                            List<String> cunList = new ArrayList<>();//用来盛放区ID
                            for (String areaStr : areaList){
                                if (areaStr.substring(0,4).equals(aStr)){
                                    cunList.add(areaStr);
                                }
                            }
                            if (cunList.size() > 0){
                                heList.add(cunList);
                            }
                        }
                        //获取商户下所有区ID，进行分类
                        List<String> areaListThree = new ArrayList<>();//用来接收区域名称
                        //根据区域ID查区域名称,判断是否全部选中市级下的所有的区
                        Map<String,Object> params = new HashMap<>();
                        params.put("siteId",siteId);
                        Map<String, Object> areaLabelMap = getAreaLabel2(params);//获取商户下的所有区域
                        List<Map<String, Object>> areaListMap = (List<Map<String, Object>>)areaLabelMap.get("areaList");

                        List<List<String>> newList = new ArrayList<>();
                        for (Map<String, Object> areamap : areaListMap) {
                            List<String> nowList = new ArrayList<>();//创建一个list,已经存在的区ID
                            List<Map<String, Object>> countriesList = (List<Map<String, Object>>) areamap.get("countries");
                            for (Map<String, Object> countriesMap : countriesList) {
                                String cityId = String.valueOf(countriesMap.get("city"));
                                nowList.add(cityId);
                            }
                            newList.add(nowList);
                        }

                        List<String> areaAllList = new ArrayList<>();
                        //比较区ID：筛选
                        for (List<String> areaListTwo : heList){
                            String areaListTwoNumOne = areaListTwo.get(0).substring(0,4);
                            for (List<String> areaListFore : newList){
                                String areaListTwoNumTwo = areaListFore.get(0).substring(0,4);
                                if (areaListTwoNumOne.equals(areaListTwoNumTwo)){
                                    if (areaListTwo.containsAll(areaListFore) && areaListFore.containsAll(areaListTwo)){
                                        //获取市级ID
                                        String shiID = areaListTwoNumOne + "00";
                                        areaAllList.clear();
                                        areaAllList.add(shiID);
                                    }else {
                                        areaAllList.clear();
                                        areaAllList.addAll(areaListTwo);
                                    }
                                    break;
                                }
                            }
                            List<String> area = labelSecondMapper.getAreaName(areaAllList);
                            areaListThree.addAll(area);
                        }
                        labelGroup.put("area",areaListThree);
                        labelMap.put("labelGroup",JSON.toJSONString(labelGroup));



                    }
                }
            }
        }catch (Exception e) {
            log.info("查询全部会员标签异常:{}", e);
        }
    }

    /**
     * 查询年龄
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectAge(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = getAgeTime(params);
            Integer ageCount = labelSecondMapper.selectAge(params);
            Map<String, Object> returnMap = formatRatio(ageCount,params,str);
            map.put("returnMap", returnMap);
            map.put("msg", "查询年龄成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询年龄异常:{}", e);
            map.put("msg", "查询年龄失败");
            map.put("status", -1);
            return map;
        }
    }

    public Map<String, Object> getAgeTime(Map<String, Object> params){
        Integer type = Integer.parseInt(String.valueOf(params.get("type")));
        if (type == 1){
            Integer age = Integer.parseInt(String.valueOf(params.get("age")));
            if (age > 200){age = 200;}
            String max = formatTime(age,1);
            params.put("max",max);
            params.put("min",getToday()+" 23:59:59");
        }else if (type == 2){
            Integer age = Integer.parseInt(String.valueOf(params.get("age")));
            if (age > 200){age = 200;}
            String max = formatTime(age,1);
            params.put("max","0000-00-01");
            params.put("min",max);
        }else if (type == 3){
            Integer ageMin = Integer.parseInt(String.valueOf(params.get("ageMin")));
            Integer ageMax = Integer.parseInt(String.valueOf(params.get("ageMax")));
            if (ageMin > 200){ageMin = 200;}
            if (ageMax > 200){ageMax = 200;}
            String min = formatTime(ageMin,1);
            String max = formatTime(ageMax,1);
            params.put("max",max);
            params.put("min",min);
        }
        return params;
    }

    /**
     * 查询生日
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectBirthday(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            Integer type = Integer.parseInt(String.valueOf(params.get("type")));
            Integer birthdayCount = 0;
            if (type == 1){
                birthdayCount = labelSecondMapper.selectBirthdayByDay(params);
            }else if (type == 2){
                birthdayCount = labelSecondMapper.selectBirthdayByMonth(params);
            }else if (type == 3){
                birthdayCount = labelSecondMapper.selectBirthdayBySection(params);
            }

            Map<String, Object> returnMap = formatRatio(birthdayCount,params,str);
            map.put("returnMap", returnMap);
            map.put("msg", "查询生日成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询生日异常:{}", e);
            map.put("msg", "查询生日失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询注册时间
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectRegister(Map<String, Object> params,String s) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            Integer regiterCount = 0;
            Integer type = Integer.parseInt(String.valueOf(params.get("type")));
            if (type == 1){
                //日期转换
                Integer day = Integer.parseInt(String.valueOf(params.get("day")));
                String str = formatTime(day, 2);
                params.put("day",str);
                regiterCount = labelSecondMapper.selectRegisterOne(params);
            }else if (type == 2){
                String registerMax = String.valueOf(params.get("registerMax"))+" 23:59:59";
                params.put("registerMax",registerMax);
                regiterCount = labelSecondMapper.selectRegisterTwo(params);
            }
            Map<String, Object> returnMap = formatRatio(regiterCount,params,s);
            map.put("returnMap", returnMap);
            map.put("msg", "查询注册时间成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询注册时间异常:{}", e);
            map.put("msg", "查询注册时间失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询成功交易金额
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectBargainMoney(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            Integer moneyCount = labelSecondMapper.selectBargainMoney(params);
            Map<String, Object> returnMap = formatRatio(moneyCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询成功交易金额成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询成功交易金额异常:{}", e);
            map.put("msg", "查询成功交易金额失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询成功交易次数（SQL 需要初始化表数据后重新写）
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectBargainCount(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            Integer bargainCount = labelSecondMapper.selectBargainCount(params);
            Map<String, Object> returnMap = formatRatio(bargainCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询成功交易次数成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询成功交易次数异常:{}", e);
            map.put("msg", "查询成功交易次数失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询客单价（SQL 需要初始化表数据后重新写）
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectPreTransaction(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            Integer bargainCount = labelSecondMapper.selectPreTransaction(params);
            Map<String, Object> returnMap = formatRatio(bargainCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询客单价成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询客单价异常:{}", e);
            map.put("msg", "查询客单价失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 购买时段
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectEverBuy(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAllMethod(params);
            Integer everBuyCount = labelSecondMapper.selectEverBuy(params);
            Map<String, Object> returnMap = formatRatio(everBuyCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询购买时段成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询购买时段异常:{}", e);
            map.put("msg", "查询购买时段失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 未购买时段(初始化后修改)
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectNotBuy(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAllMethod(params);
            Integer notBuyCount = labelSecondMapper.selectNotBuy(params);//已购买的会员
            Map<String, Object> returnMap = formatRatio(notBuyCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询未购买时段成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询未购买时段异常:{}", e);
            map.put("msg", "查询未购买时段失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 退款率
     *
     * @param params
     * @return
     */
    public Map<String, Object> selecRefundProbability(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            Integer refundProbabilityCount = labelSecondMapper.selecRefundProbability(params);
            Map<String, Object> returnMap = formatRatio(refundProbabilityCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询退款率成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询退款率异常:{}", e);
            map.put("msg", "查询退款率失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 购买周期
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectBuyPeriod(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = scopeAllMethod(params);
            Integer addIntegralCount = labelSecondMapper.selectBuyPeriod(params);
            Map<String, Object> returnMap = formatRatio(addIntegralCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询购买周期成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询购买周期异常:{}", e);
            map.put("msg", "查询购买周期失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 赚取积分
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectAddIntegral(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            Integer addIntegralCount = 0;
            String time = String.valueOf(params.get("time"));
            if (time.equals("3")){
                addIntegralCount = labelSecondMapper.selectAddIntegralTotal(params);
            }else {
                addIntegralCount = labelSecondMapper.selectAddIntegral(params);
            }
            Map<String, Object> returnMap = formatRatio(addIntegralCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询赚取积分成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询赚取积分异常:{}", e);
            map.put("msg", "查询赚取积分失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 消费积分
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectConsumeIntegral(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            Integer consumeIntegralCount = 0;
            String time = String.valueOf(params.get("time"));
            if (time.equals("3")){
                consumeIntegralCount = labelSecondMapper.selectConsumeIntegralTotal(params);
            }else {
                consumeIntegralCount = labelSecondMapper.selectConsumeIntegral(params);
            }
            Map<String, Object> returnMap = formatRatio(consumeIntegralCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询购买周期成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询购买周期异常:{}", e);
            map.put("msg", "查询购买周期失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 剩余积分
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectResidueIntegral(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = scopeAllMethod(params);
            Integer residueIntegralCount = labelSecondMapper.selectResidueIntegral(params);
            Map<String, Object> returnMap = formatRatio(residueIntegralCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询剩余积分成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询剩余积分异常:{}", e);
            map.put("msg", "查询剩余积分失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 门店距离（活动）
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectDisStoreActivity(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            Integer disStoreActivityCount = labelSecondMapper.selectDisStoreActivity(params);
            Map<String, Object> returnMap = formatRatio(disStoreActivityCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询门店距离成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            map.put("msg", "查询门店距离失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 门店距离（收货地址）
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectDisStoreAddress(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = scopeAllMethodToAddress(params);
            //查询每个门店的坐标
            Integer disStoreAddressCount = 0;
            List<Map<String,Object>> storeList = labelSecondMapper.getStorePoint(params);
            if (storeList.size() > 0){
                for (Map<String,Object> storeMap : storeList){
                    storeMap.putAll(params);
                }

                disStoreAddressCount = labelSecondMapper.selectDisStoreAddress(params);
            }
            Map<String, Object> returnMap = formatRatio(disStoreAddressCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询门店距离成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            map.put("msg", "查询门店距离失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 竞店距离（活动）
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectDisContendStoreActivity(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = timeAndScopeMethod(params);
            //查询每一个竞店的坐标
            List<Map<String,Object>> storeList = labelSecondMapper.getStoreCoodinate(params);
            Integer disContendStoreActivityCount = 0;
            if (storeList.size() > 0){
                for (Map<String,Object> storeMap : storeList){
                    storeMap.putAll(params);
                }
//                String timeMax = null;
//                String timeMin = null;
//                if (params.containsKey("timeMin") && params.containsKey("timeMax")){
//                    timeMax = String.valueOf(params.get("timeMax"));
//                    timeMin = String.valueOf(params.get("timeMin"));
//                }
                Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
                disContendStoreActivityCount = labelSecondMapper.selectDisContendStoreActivity(params);
            }
            Map<String, Object> returnMap = formatRatio(disContendStoreActivityCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询竞店距离成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询竞店距离异常:{}", e);
            map.put("msg", "查询竞店距离失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 竞店距离（收货地址）
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectDisContendStoreAddress(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            params = scopeAllMethod(params);
            //查询每一个竞店的坐标
            Integer disContendStoreAddressCount = 0;
            List<Map<String,Object>> storeList = labelSecondMapper.getStoreCoodinate(params);
            if (storeList.size() > 0){
                for (Map<String,Object> storeMap : storeList){
                    storeMap.putAll(params);
                }

                disContendStoreAddressCount = labelSecondMapper.selectDisContendStoreAddress(params);
            }
            Map<String, Object> returnMap = formatRatio(disContendStoreAddressCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询竞店距离成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询竞店距离异常:{}", e);
            map.put("msg", "查询竞店距离失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 健康标签
     *
     * @param params
     * @return
     */
    public Map<String, Object> selectHealth(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (params.containsKey("labelAttribute")){
                Map<String, Object> labelAttribute = JacksonUtils.json2map(String.valueOf(params.get("labelAttribute")));
                params.putAll(labelAttribute);
            }
            params = scopeAllMethod(params);
            Integer residueIntegralCount = labelSecondMapper.selectHealthIll(params);
            Map<String, Object> returnMap = formatRatio(residueIntegralCount,params,str);

            map.put("returnMap", returnMap);
            map.put("msg", "查询健康标签成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询健康标签异常:{}", e);
            map.put("msg", "查询健康标签失败");
            map.put("status", -1);
            return map;
        }
    }

    //---------------------------------页面加载查询-----------------------------------------

    /**
     * 基础标签查询（性别）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getBaseLabelBySex(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> sexMap = new HashMap<>();//性别
        try {
            Integer man = labelSecondMapper.getSexByMan(params);//男
            Integer woman = labelSecondMapper.getSexByWoman(params);//女
            Integer unknow = labelSecondMapper.getSexByUnknow(params);//未知
            sexMap.put("man",man);
            sexMap.put("woman",woman);
            sexMap.put("unknow",unknow);
            returnMap.put("sexMap",sexMap);

            map.put("baseLabelMap", returnMap);
            map.put("msg", "基础标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("基础标签查询异常:{}", e);
            map.put("msg", "基础标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 基础标签查询（年龄，生日）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getBaseLabelByBirthday(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            //查询年龄标签
            List<Map<String,Object>> ageListMap = labelSecondMapper.getAgeLabel(params);
            ageListMap = getBaseList(ageListMap,params,"age");
            ageListMap = getYingyongList(ageListMap,params,"age");
            returnMap.put("ageListMap",ageListMap);

            //查询生日
            Map<String,Object> birthdayMap = labelSecondMapper.getBithdayLabelByDay(params);
            returnMap.put("birthdayListMap",birthdayMap);

            map.put("baseLabelMap", returnMap);
            map.put("msg", "基础标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("基础标签查询异常:{}", e);
            map.put("msg", "基础标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 基础标签查询（星座，生肖）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getBaseLabelByShengxiaoXingzuo(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            //查询星座
            Map<String,Object> xingzuoMap = labelSecondMapper.getXingzuoLabel(params);
            returnMap.put("xingzuoMap",xingzuoMap);

            //查询生肖
            Map<String,Object> shengxiaoMap = labelSecondMapper.getShengxiaoLabel(params);
            returnMap.put("shengxiaoMap",shengxiaoMap);

            map.put("baseLabelMap", returnMap);
            map.put("msg", "基础标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("基础标签查询异常:{}", e);
            map.put("msg", "基础标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 基础标签查询（注册时间）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getBaseLabelByRegist(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            //查询注册时间
            List<Map<String,Object>> registerMap = labelSecondMapper.getRegisterLabel(params);
            registerMap = getBaseList(registerMap,params,"register");
            registerMap = getYingyongList(registerMap,params,"register");
            returnMap.put("registerMap",registerMap);

            map.put("baseLabelMap", returnMap);
            map.put("msg", "基础标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("基础标签查询异常:{}", e);
            map.put("msg", "基础标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    public Map<String,Object> getRedistTime(Map<String, Object> params){
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer type = Integer.parseInt(String.valueOf(params.get("type")));
        if (type == 1){
            Integer str = Integer.parseInt(String.valueOf(params.get("day")));
            String registerMin = formatTime(str,2);
            String registerMax = sdf2.format(new Date());
            params.put("registerMin",registerMin);
            params.put("registerMax",registerMax);
        }else if (type == 2){
            String registerMin = String.valueOf(params.get("registerMin"));
            String registerMax = String.valueOf(params.get("registerMax"))+" 23:59:59";
            params.put("registerMin",registerMin);
            params.put("registerMax",registerMax);
        }
        return params;
    }

    //抽方法（未知）
    public Map<String,Object> getWeizhiAge(Integer count,String labelDescribe,String labelName){
        Map<String,Object> reMap = new HashMap<>();
        Map<String,Object> laMap = new HashMap<>();
        reMap.put("label",labelName + "(" + count +"人" + ")");
        reMap.put("labelName",labelName);
        reMap.put("count",count);
        reMap.put("labelDescribe",labelDescribe);
        reMap.put("state","true");
        laMap.put("type",4);
        reMap.put("labelAttribute",JSON.toJSONString(laMap));
        return reMap;
    }

    //抽方法（已知）
    public Map<String,Object> getYizhiAge(Integer count,String labelDescribe,String labelName){
        Map<String,Object> reMap = new HashMap<>();
        Map<String,Object> laMap = new HashMap<>();
        reMap.put("label",labelName + "(" + count +"人" + ")");
        reMap.put("labelName",labelName);
        reMap.put("count",count);
        reMap.put("labelDescribe",labelDescribe);
        reMap.put("state","true");
        laMap.put("type",5);
        reMap.put("labelAttribute",JSON.toJSONString(laMap));
        return reMap;
    }

    /**
     * 区域查询
     *
     * @param params
     * @return
     */
    public Map<String, Object> getAreaLabel2(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Map<String, Object>> cityListMap = new ArrayList<>();
        try {
            //先获取所有的会员注册时写的区域
            List<Map<String, Object>> infoListMap = labelSecondMapper.getInfoListMap2(params);
            //获取会员填写的收货地址的区域
            List<Map<String, Object>> addrListMap = labelSecondMapper.getAddrListMap2(params);
            Map<String, Object> kMap = areaChou2(infoListMap, addrListMap);
            returnList = (List<Map<String, Object>>)kMap.get("aList");
            cityListMap = (List<Map<String, Object>>)kMap.get("cityListMap");


            for (Map<String, Object> retMap : returnList){
                String city = String.valueOf(retMap.get("city"));
                List<Map<String, Object>> ceMapList = new ArrayList<>();
                //将相同“市”的所有会员放在一起
                for (Map<String, Object> cityMap : cityListMap){
                    String area = String.valueOf(cityMap.get("city"));
                    if (city.equals(area)){
                        ceMapList.add(cityMap);
                    }
                }
                //将相同“区”的会员放在一起
                Map<String,String> hMap = new HashMap<>();
                for (Map<String, Object> areaMap : ceMapList){//将所有的区去重，将区ID和区名称封装在一个MAP中
                    String area = String.valueOf(areaMap.get("area"));
                    String areaname = String.valueOf(areaMap.get("areaname"));
                    hMap.put(area,areaname);
                }

                List<Map<String, Object>> lList = new ArrayList<>();
                for (String key : hMap.keySet()){
                    Map<String,Object> lMap = new HashMap<>();
                    Integer count = 0;
                    for (Map<String, Object> areaMap : ceMapList){
                        String area = String.valueOf(areaMap.get("area"));
                        if (key.equals(area)){
                            count++;
                        }
                    }
                    lMap.put("city",key);
                    lMap.put("count",count);
                    lMap.put("name",hMap.get(key));
                    lMap.put("label",hMap.get(key) +"("+ count +"人" +")");
                    lList.add(lMap);
                }
                retMap.put("countries",lList);
            }
            map.put("areaList", returnList);
            map.put("msg", "区域标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            map.put("areaList", returnList);
            map.put("msg", "区域标签查询失败");
            map.put("status", -1);
            return map;
        }
    }


    /**
     * 区域查询
     *
     * @param params
     * @return
     */
    public Map<String, Object> getAreaLabel3(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> returnList = new ArrayList<>();
        try {
            //先获取所有的会员注册时写的区域
            List<Map<String, Object>> infoListMap = labelSecondMapper.getInfoListMap2(params);
            //获取会员填写的收货地址的区域
            List<Map<String, Object>> addrListMap = labelSecondMapper.getAddrListMap2(params);

            //先获取infoListMap所有的buyerId,用来和addrListMap中的buyerId比较
            List<Integer> infoBuyerIdList = new ArrayList<>();//用来盛放info表中的会员ID
            Set<String> shiSet = new HashSet();//获取所有市的ID
            for (Map<String, Object> infoMap : infoListMap){
                Integer buyerId = Integer.parseInt(String.valueOf(infoMap.get("buyerId")));//会员ID
                infoBuyerIdList.add(buyerId);
                String shiId = String.valueOf(infoMap.get("city"));//市ID
                shiSet.add(shiId);
            }

            //将两个集合合并，将收获地址集合合并到会员配置信息集合
            for (Map<String, Object> addrMap : addrListMap){
                Integer buyerId = Integer.parseInt(String.valueOf(addrMap.get("buyerId")));
                if (!infoBuyerIdList.contains(buyerId)){
                    String shiId = String.valueOf(addrMap.get("city"));//市ID
                    infoListMap.add(addrMap);
                    shiSet.add(shiId);
                }
            }

            //将所有的市分开存放
            List<Map<String,Object>> allList = new ArrayList<>();//总集合
            Set<String> areaSet = new HashSet();//获取指定市下的区域的ID
            String cityName = "";//市名称
            for (String shi : shiSet){
                Map<String,Object> shiMap = new HashMap<>();
                List<Map<String,Object>> shiList = new ArrayList<>();
                areaSet.clear();
                for (Map<String, Object> infoMap : infoListMap){
                    String shiId = String.valueOf(infoMap.get("city"));//市ID
                    if (shi.equals(shiId)){
                        if (!StringUtil.isEmpty(infoMap.get("cityname"))){
                            cityName = String.valueOf(infoMap.get("cityname"));
                        }
                        shiList.add(infoMap);
                    }
                }

                //遍历shiList，获取区域ID
                for (Map<String,Object> shiqvMap : shiList){
                    String areaId = String.valueOf(shiqvMap.get("area"));//区ID
                    areaSet.add(areaId);
                }
                List<Map<String,Object>> allAreaList = new ArrayList<>();//区域集合
                String areaName = "";//区名称
                for (String area : areaSet){
                    Map<String,Object> areaMap = new HashMap<>();
                    List<Map<String,Object>> areaList = new ArrayList<>();
                    for (Map<String, Object> shiqvMap : shiList){
                        String areaId = String.valueOf(shiqvMap.get("area"));//区ID
                        if (area.equals(areaId)){
                            if (!StringUtil.isEmpty(shiqvMap.get("areaname"))){
                                areaName = String.valueOf(shiqvMap.get("areaname"));
                            }
                            areaList.add(shiqvMap);
                        }
                    }
                    if (StringUtil.isEmpty(areaName) || areaName.equals("null")){//如果区名称为null,显示未知
                        areaName = "未知";
                    }
                    areaMap.put("name",areaName);
                    areaMap.put("count",areaList.size());
                    areaMap.put("city",area);
                    areaMap.put("label",areaName +"("+ areaList.size() +"人" +")");
                    allAreaList.add(areaMap);
                }

                shiMap.put("name",cityName);
                shiMap.put("count",shiList.size());
                shiMap.put("city",shi);
                shiMap.put("countries",allAreaList);
                allList.add(shiMap);
            }

            map.put("areaList", allList);
            map.put("msg", "区域标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            map.put("areaList", returnList);
            map.put("msg", "区域标签查询失败");
            map.put("status", -1);
            return map;
        }
    }
//    public Map<String, Object> getAreaLabel(Map<String, Object> params) {
//        Map<String, Object> map = new HashMap<>();
//        List<Map<String, Object>> returnList = new ArrayList<>();
//        try {
//            //先获取所有的会员注册时写的区域
//            List<Map<String, Object>> infoListMap = labelSecondMapper.getInfoListMap(params);
//            //获取会员填写的收货地址的区域
//            List<Map<String, Object>> addrListMap = labelSecondMapper.getAddrListMap(params);
//            returnList = areaChou(infoListMap, addrListMap);
//
//            //查区域
//            for (Map<String, Object> retMap : returnList){
//                String city = String.valueOf(retMap.get("city"));
//                params.put("city",city);
//                List<Map<String, Object>> infoListMapArea = labelSecondMapper.getInfoListMapArea(params);
//                List<Map<String, Object>> addrListMapAddr = labelSecondMapper.getAddrListMapAddr(params);
//                List<Map<String, Object>> returnListArea = areaChou(infoListMapArea, addrListMapAddr);
//                for (Map<String, Object> laMap : returnListArea){
//                    String name = String.valueOf(laMap.get("name"));
//                    String count = String.valueOf(laMap.get("count"));
//                    String label = name+"("+ count +")";
//                    laMap.put("label",label);
//                }
//                retMap.put("countries",returnListArea);
//            }
//
//            map.put("areaList", returnList);
//            map.put("msg", "区域标签查询成功");
//            map.put("status", 0);
//            return map;
//        } catch (Exception e) {
//            log.info("查询门店距离异常:{}", e);
//            map.put("areaList", returnList);
//            map.put("msg", "区域标签查询失败");
//            map.put("status", -1);
//            return map;
//        }
//    }

//    public Map<String, Object> getAreaLabel(Map<String, Object> params) {
//        Map<String,Object> map=new HashMap<String,Object>();
//        List<Map<Object, Object>> result = new ArrayList<>();
//        try {
//            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
//            result = labelSecondMapper.getCityLabelBySiteId(siteId);//根据siteId 将省市取出
//            if (result.size() > 0){
//                //遍历省市，再根据市的ID，取出市包含的区，并合并到结果中
//                for (Map<Object, Object> item : result) {
//                    List<Map<Object, Object>> country = new ArrayList<>();
//                    Integer city_id = Integer.parseInt(String.valueOf(item.get("city_id"))) ;
//                    if (city_id != null) {
//                        country = labelSecondMapper.getAreaLabelByCityId(city_id,siteId);
//                        if (country.size() > 0){
//                            for (Map<Object, Object> countryMap : country){
//                                String name = String.valueOf(countryMap.get("country"));
//                                Integer count = Integer.parseInt(String.valueOf(countryMap.get("count")));
//                                String label = name+"("+ count +")";
//                                countryMap.put("label",label);
//                            }
//                        }
//                    }
//                    item.put("countries", country);
//                }
//            }
//            map.put("areaList", result);
//            map.put("msg", "区域标签查询成功");
//            map.put("status", 0);
//            return map;
//        }catch (Exception e){
//            log.info("区域标签查询失败:{}",e);
//            map.put("code",-1);
//            map.put("msg", "区域标签查询失败");
//            map.put("areaList",result);
//            return map;
//        }
//    }

//    private Integer bargainMoneyCount = 0;//定义“无成交”的会员数为0，使客单价少查询一次
    /**
     * 交易标签查询（交易金额）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getTradesLabelOne(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            List<Map<String,Object>> listTime = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotal = new ArrayList<>();//累计的
            //查询成交金额
            List<Map<String,Object>> bargainMoneyList = labelSecondMapper.getBargainMoneyLabel(params);
            for (Map<String,Object> eMap : bargainMoneyList){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") || aMap.containsKey("timeMax")){
                    listTime.add(eMap);
                }else {
                    listTotal.add(eMap);
                }
            }

            List<Map<String,Object>> chaList = new ArrayList<>();
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> totalList = new ArrayList<>();
            for (int i = 0;i< listTotal.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotal.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaList.add(map1);
                Map<String,Object> zMap= labelSecondMapper.getBargainMoneyList(siteId,chaList);//查询
                totalList = getJianList(listTotal,zMap);
            }

            listTime = getBaseList(listTime,params,"bargain_money");
            listTotal = getYingyongList(totalList,params,"bargain_money");
            listTime = getYingyongList(listTime,params,"bargain_money");
            listTime.addAll(listTotal);
            returnMap.put("bargainMoneyListMap",listTime);

            map.put("tradesLabelMap", returnMap);
            map.put("msg", "交易标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("交易标签查询异常:{}", e);
            map.put("msg", "交易标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 交易标签查询（交易次数）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getTradesLabelTwo(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            List<Map<String,Object>> listTime = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotal = new ArrayList<>();//累计的
            //查询成交次数
            List<Map<String,Object>> bargainCountList = labelSecondMapper.getBargainCountLabel(params);
            for (Map<String,Object> eMap : bargainCountList){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") || aMap.containsKey("timeMax")){
                    listTime.add(eMap);
                }else {
                    listTotal.add(eMap);
                }
            }

            List<Map<String,Object>> chaList = new ArrayList<>();
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> totalList = new ArrayList<>();
            for (int i = 0;i< listTotal.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotal.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaList.add(map1);
                Map<String,Object> zMap= labelSecondMapper.getBargainCountList(siteId,chaList);//查询
                totalList = getJianList(listTotal,zMap);
            }

            listTime = getBaseList(listTime,params,"bargain_count");

            listTotal = getYingyongList(totalList,params,"bargain_count");
            listTime = getYingyongList(listTime,params,"bargain_count");
            listTime.addAll(listTotal);
            returnMap.put("bargainCountListMap",listTime);

            map.put("tradesLabelMap", returnMap);
            map.put("msg", "交易标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("交易标签查询异常:{}", e);
            map.put("msg", "交易标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 交易标签查询（客单价）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getTradesLabelThree(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            List<Map<String,Object>> listTime = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotal = new ArrayList<>();//累计的
            //查询客单价
            List<Map<String,Object>> preTransactionList = labelSecondMapper.getPreTransactionLabel(params);
            for (Map<String,Object> eMap : preTransactionList){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") || aMap.containsKey("timeMax")){
                    listTime.add(eMap);
                }else {
                    listTotal.add(eMap);
                }
            }

            List<Map<String,Object>> chaList = new ArrayList<>();
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> totalList = new ArrayList<>();
            for (int i = 0;i< listTotal.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotal.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaList.add(map1);
                Map<String,Object> zMap= labelSecondMapper.getTransactionList(siteId,chaList);//查询
                totalList = getJianList(listTotal,zMap);
            }

            listTime = getBaseList(listTime,params,"pre_transaction");

            listTotal = getYingyongList(totalList,params,"pre_transaction");
            listTime = getYingyongList(listTime,params,"pre_transaction");
            listTime.addAll(listTotal);
            returnMap.put("preTransactionListMap",listTime);

            map.put("tradesLabelMap", returnMap);
            map.put("msg", "交易标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("交易标签查询异常:{}", e);
            map.put("msg", "交易标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 交易标签查询（退款率、购买过）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getTradesLabelFore(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> listTime = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotal = new ArrayList<>();//累计的
            //查询退款率
            List<Map<String,Object>> refundProbabilityList = labelSecondMapper.getRefundProbabilityLabel(params);
            for (Map<String,Object> eMap : refundProbabilityList){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") || aMap.containsKey("timeMax")){
                    listTime.add(eMap);
                }else {
                    listTotal.add(eMap);
                }
            }

            List<Map<String,Object>> chaList = new ArrayList<>();
            List<Map<String,Object>> totalList = new ArrayList<>();
            for (int i = 0;i< listTotal.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotal.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaList.add(map1);
                Map<String,Object> zMap= labelSecondMapper.getRefundProbabilityList(siteId,chaList);//查询
                totalList = getJianList(listTotal,zMap);
            }

            listTime = getBaseList(listTime,params,"refund_probability");

            //查询零退款
            Integer zeroRefund = labelSecondMapper.getZeroRefundCount(params);
            Map<String,Object> zeroRefundMap = getWeizhi(zeroRefund,"历史累计无退款","零退款","time",4);
            listTime.add(zeroRefundMap);
            listTotal = getYingyongList(totalList,params,"refund_probability");
            listTime = getYingyongList(listTime,params,"refund_probability");
            listTime.addAll(listTotal);
            returnMap.put("refundProbabilityListMap",listTime);

            //查询购买过
            List<Map<String,Object>> EverBuyListMap = labelSecondMapper.getEverBuyLabel(params);
            List<Map<String,Object>> chaXunList = new ArrayList<>();
            for (int i = 0;i< EverBuyListMap.size();i++){
                Map<String, Object> map1 = timeAllMethod(JacksonUtils.json2map(String.valueOf(EverBuyListMap.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaXunList.add(map1);
            }
            Map<String,Object> sMap= labelSecondMapper.getEverBuyList(siteId,chaXunList);//查询
            List<Map<String,Object>> cunList = getJianList(EverBuyListMap,sMap);

            EverBuyListMap = getYingyongList(cunList,params,"ever_buy");
            returnMap.put("EverBuyListMap",EverBuyListMap);

            map.put("tradesLabelMap", returnMap);
            map.put("msg", "交易标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("交易标签查询异常:{}", e);
            map.put("msg", "交易标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 交易标签查询（购买周期）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getTradesLabelFive(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> chaList = new ArrayList<>();
            //查询购买周期
            List<Map<String,Object>> buyPeriodListMap = labelSecondMapper.getBuyPeriodLabel(params);
            for (int i = 0;i< buyPeriodListMap.size();i++){
                Map<String, Object> map1 = scopeAllMethod(JacksonUtils.json2map(String.valueOf(buyPeriodListMap.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaList.add(map1);
            }
            Map<String,Object> zMap= labelSecondMapper.getPeriodList(siteId,chaList);//查询
            List<Map<String,Object>> cunList = getJianList(buyPeriodListMap,zMap);
            buyPeriodListMap = getYingyongList(cunList,params,"buy_period");
            returnMap.put("buyPeriodListMap",buyPeriodListMap);

            map.put("tradesLabelMap", returnMap);
            map.put("msg", "交易标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("交易标签查询异常:{}", e);
            map.put("msg", "交易标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    public List<Map<String,Object>> getJianList(List<Map<String, Object>> buyPeriodListMap, Map<String, Object> zMap){
        List<Map<String,Object>> cunList = new ArrayList<>();
        try{
            for (int i = 0;i< buyPeriodListMap.size();i++){
                String type = "type" + i;
                Integer count = StringUtil.isEmpty(zMap.get(type))?0:Integer.parseInt(String.valueOf(zMap.get(type)));
//                Integer count = Integer.parseInt(String.valueOf(zMap.get(type)));
                Map<String, Object> map1 = buyPeriodListMap.get(i);
                Map<String, Object> rMap = formatRatio(count,map1,"Y");
                String label = String.valueOf(map1.get("labelName"))+"("+count+"人"+")";
                map1.put("label",label);
                map1.putAll(rMap);
                cunList.add(map1);
            }
            return cunList;
        }catch (Exception e) {
            log.info("交易标签查询异常:{}", e);
            return cunList;
        }
    }

    /**
     * 交易标签查询（赚取积分，消耗积分，剩余积分）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getTradesLabelSix(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> listTimeAdd = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotalAdd = new ArrayList<>();//累计的
            List<Map<String,Object>> chaListAdd = new ArrayList<>();
            List<Map<String,Object>> totalListAdd = new ArrayList<>();
            //查询赚取积分
            List<Map<String,Object>> addIntegralListMap = labelSecondMapper.getaddIntegralLabel(params);
            for (Map<String,Object> eMap : addIntegralListMap){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") || aMap.containsKey("timeMax")){
                    listTimeAdd.add(eMap);
                }else {
                    listTotalAdd.add(eMap);
                }
            }

            for (int i = 0;i< listTotalAdd.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotalAdd.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaListAdd.add(map1);
                Map<String,Object> addMap= labelSecondMapper.getaddIntegraList(siteId,chaListAdd);//查询
                totalListAdd = getJianList(listTotalAdd,addMap);
            }
            listTimeAdd = getBaseList(listTimeAdd,params,"add_integral");

            listTotalAdd = getYingyongList(totalListAdd,params,"add_integral");
            listTimeAdd = getYingyongList(listTimeAdd,params,"add_integral");
            listTimeAdd.addAll(listTotalAdd);
            returnMap.put("addIntegralListMap",listTimeAdd);


            //查询消耗积分
            List<Map<String,Object>> listTimeCos = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotalCos = new ArrayList<>();//累计的
            List<Map<String,Object>> chaListCos = new ArrayList<>();
            List<Map<String,Object>> totalListCos = new ArrayList<>();
            List<Map<String,Object>> consumeIntegralListMap = labelSecondMapper.getConsumeIntegralLabel(params);
            for (Map<String,Object> eMap : consumeIntegralListMap){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") || aMap.containsKey("timeMax")){
                    listTimeCos.add(eMap);
                }else {
                    listTotalCos.add(eMap);
                }
            }
            for (int i = 0;i< listTotalCos.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotalCos.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaListCos.add(map1);
                Map<String,Object> conMap= labelSecondMapper.getconsumeIntegralList(siteId,chaListCos);//查询
                totalListCos = getJianList(listTotalCos,conMap);
            }
            listTimeCos = getBaseList(listTimeCos,params,"consume_integral");

            listTotalCos = getYingyongList(totalListCos,params,"consume_integral");
            listTimeCos = getYingyongList(listTimeCos,params,"consume_integral");
            listTimeCos.addAll(listTotalCos);
            returnMap.put("consumeIntegralListMap",listTimeCos);

            //查询剩余积分
            List<Map<String,Object>> residueIntegralListMap = labelSecondMapper.getResidueIntegralLabel(params);
            List<Map<String,Object>> chaXunList = new ArrayList<>();
            for (int i = 0;i< residueIntegralListMap.size();i++){
                Map<String, Object> map1 = scopeAllMethod(JacksonUtils.json2map(String.valueOf(residueIntegralListMap.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaXunList.add(map1);
            }
            Map<String,Object> sMap= labelSecondMapper.getresidueIntegralList(siteId,chaXunList);//查询

            List<Map<String,Object>> cunList = getJianList(residueIntegralListMap,sMap);

            residueIntegralListMap = getYingyongList(cunList,params,"residue_integral");
            returnMap.put("residueIntegralListMap",residueIntegralListMap);

            map.put("tradesLabelMap", returnMap);
            map.put("msg", "交易标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("交易标签查询异常:{}", e);
            map.put("msg", "交易标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 距离标签查询:查询门店距离(高频活动)
     *
     * @param params
     * @return
     */
    public Map<String, Object> getDistanceLabelByDisStoreActivity(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            //获取90天前的时间
            String timeNineten = formatTime(90, 2);
            //获取当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String curentTime = df.format(new Date());
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> listTimeAdd = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotalAdd = new ArrayList<>();//累计的
            List<Map<String,Object>> chaListAdd = new ArrayList<>();
            //查询门店距离(高频活动)
            List<Map<String,Object>> disStoreActivityListMap = labelSecondMapper.getDisStoreActivityLabel(params);
            for (Map<String,Object> eMap : disStoreActivityListMap){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") && aMap.containsKey("time") && "1".equals(String.valueOf(aMap.get("time"))) && "90".equals(String.valueOf(aMap.get("timeMin")))){
                    listTotalAdd.add(eMap);
                }else {
                    listTimeAdd.add(eMap);
                }
            }
            List<Map<String,Object>> totalListAdd = new ArrayList<>();
            for (int i = 0;i< listTotalAdd.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotalAdd.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaListAdd.add(map1);
                Map<String,Object> addMap= labelSecondMapper.getDisStoreActivityList(siteId,chaListAdd,timeNineten,curentTime);//查询
                totalListAdd = getJianList(listTotalAdd,addMap);
            }
            listTimeAdd = getBaseList(listTimeAdd,params,"dis_store_activity");
            listTotalAdd = getYingyongList(totalListAdd,params,"dis_store_activity");
            listTimeAdd = getYingyongList(listTimeAdd,params,"dis_store_activity");
            listTimeAdd.addAll(listTotalAdd);
            returnMap.put("disStoreActivityListMap",listTimeAdd);

            map.put("distanceLabelMap", returnMap);
            map.put("msg", "距离标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("距离标签查询异常:{}", e);
            map.put("msg", "距离标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 距离标签查询:查询门店距离(收货地址)
     *
     * @param params
     * @return
     */
    public Map<String, Object> getDistanceLabelByDisStoreAddress(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> chaList = new ArrayList<>();
            //查询门店距离(收货地址)
            List<Map<String,Object>> disStoreAddressListMap = labelSecondMapper.getDisStoreAddressLabel(params);
            for (int i = 0;i< disStoreAddressListMap.size();i++){
                Map<String, Object> map1 = scopeAllMethodToAddress(JacksonUtils.json2map(String.valueOf(disStoreAddressListMap.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaList.add(map1);
            }

            Map<String,Object> zMap= labelSecondMapper.getdDisStoreAddressList(siteId,chaList);//查询
            List<Map<String,Object>> cunList = getJianList(disStoreAddressListMap,zMap);
            disStoreAddressListMap = getYingyongList(cunList,params,"buy_period");
            returnMap.put("disStoreAddressListMap",disStoreAddressListMap);

            map.put("distanceLabelMap", returnMap);
            map.put("msg", "距离标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("距离标签查询异常:{}", e);
            map.put("msg", "距离标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 距离标签查询:查询竞店距离(高频活动)
     *
     * @param params
     * @return
     */
    public Map<String, Object> getDistanceLabelByDisContendStoreActivity(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            //获取90天前的时间
            String timeNineten = formatTime(90, 2);
            //获取当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String curentTime = df.format(new Date());

            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> listTimeAdd = new ArrayList<>();//含时间
            List<Map<String,Object>> listTotalAdd = new ArrayList<>();//累计的
            List<Map<String,Object>> chaListAdd = new ArrayList<>();
            //查询竞店距离(高频活动)
            List<Map<String,Object>> disContendStoreActivityListMap = labelSecondMapper.getDisContendStoreActivityLabel(params);
            for (Map<String,Object> eMap : disContendStoreActivityListMap){
                Map<String,Object> aMap = JacksonUtils.json2map(String.valueOf(eMap.get("labelAttribute")));
                if (aMap.containsKey("timeMin") && aMap.containsKey("time") && "1".equals(String.valueOf(aMap.get("time"))) && "90".equals(String.valueOf(aMap.get("timeMin")))){
                    listTotalAdd.add(eMap);
                }else {
                    listTimeAdd.add(eMap);
                }
            }
            List<Map<String,Object>> totalListAdd = new ArrayList<>();
            for (int i = 0;i< listTotalAdd.size();i++){
                Map<String, Object> map1 = timeAndScopeMethod(JacksonUtils.json2map(String.valueOf(listTotalAdd.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaListAdd.add(map1);
                Map<String,Object> addMap= labelSecondMapper.getDisContendStoreActivityList(siteId,chaListAdd,timeNineten,curentTime);//查询
                totalListAdd = getJianList(listTotalAdd,addMap);
            }
            listTimeAdd = getBaseList(listTimeAdd,params,"dis_contend_store_activity");



            listTotalAdd = getYingyongList(totalListAdd,params,"dis_contend_store_activity");
            listTimeAdd = getYingyongList(listTimeAdd,params,"dis_contend_store_activity");
            listTimeAdd.addAll(listTotalAdd);
            returnMap.put("disContendStoreActivityListMap",listTimeAdd);

            map.put("distanceLabelMap", returnMap);
            map.put("msg", "距离标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("距离标签查询异常:{}", e);
            map.put("msg", "距离标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 距离标签查询:查询竞店距离(收货地址)
     *
     * @param params
     * @return
     */
    public Map<String, Object> getDistanceLabelBydisContendStoreAddress(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            List<Map<String,Object>> chaList = new ArrayList<>();
            //查询竞店距离(收货地址)
            List<Map<String,Object>> disContendStoreAddressListMap = labelSecondMapper.getDisContendStoreAddressLabel(params);
            for (int i = 0;i< disContendStoreAddressListMap.size();i++){
                Map<String, Object> map1 = scopeAllMethodToAddress(JacksonUtils.json2map(String.valueOf(disContendStoreAddressListMap.get(i).get("labelAttribute"))));
                map1.put("typeCoude","type" + i);
                chaList.add(map1);
            }
            Map<String,Object> zMap= labelSecondMapper.getdDsContendStoreAddressList(siteId,chaList);//查询
            List<Map<String,Object>> cunList = getJianList(disContendStoreAddressListMap,zMap);
            disContendStoreAddressListMap = getYingyongList(cunList,params,"buy_period");
            returnMap.put("disContendStoreAddressListMap",disContendStoreAddressListMap);

            map.put("distanceLabelMap", returnMap);
            map.put("msg", "距离标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("距离标签查询异常:{}", e);
            map.put("msg", "距离标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 健康签查询(高血压)
     *
     * @param params
     * @return
     */
    public Map<String, Object> getHealthLabelByGaoxueya(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            params.put("healthName","高血压");
            Integer gaoxueya = labelSecondMapper.getHealthLabel(params);
//            Integer gaoxueya = Integer.parseInt(String.valueOf(healthMap.get("gao_xue_ya")));
            Map<String, Object> gaoxueyaMap = getIllMap(gaoxueya,"高血压");

            //高血压
            List<Map<String,Object>>healIllByGaoxueyaListMap = labelSecondMapper.getHealIllLabelByGaoxueya(params);
            healIllByGaoxueyaListMap = getBaseList(healIllByGaoxueyaListMap,params,"health_gao_xue_ya");
            healIllByGaoxueyaListMap.add(0,gaoxueyaMap);
            healIllByGaoxueyaListMap = getYingyongList(healIllByGaoxueyaListMap,params,"health_gao_xue_ya");
            returnMap.put("healIllByGaoxueyaListMap",healIllByGaoxueyaListMap);

            map.put("healthLabelMap", returnMap);
            map.put("msg", "健康标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("健康标签查询异常:{}", e);
            map.put("msg", "健康标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 健康签查询(高血脂)
     *
     * @param params
     * @return
     */
    public Map<String, Object> getHealthLabelByGaoxuezhi(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            params.put("healthName","高血脂");
            Integer gaoxuezhi = labelSecondMapper.getHealthLabel(params);
//            Integer gaoxuezhi = Integer.parseInt(String.valueOf(healthMap.get("gao_xue_zhi")));
            Map<String, Object> gaoxuezhiMap = getIllMap(gaoxuezhi,"高血脂");

            //高血脂
            List<Map<String,Object>>healIllByGaoxuezhiListMap = labelSecondMapper.getHealIllLabelByGaoxuezhi(params);
            healIllByGaoxuezhiListMap = getBaseList(healIllByGaoxuezhiListMap,params,"health_gao_xue_zhi");
            healIllByGaoxuezhiListMap.add(0,gaoxuezhiMap);
            healIllByGaoxuezhiListMap = getYingyongList(healIllByGaoxuezhiListMap,params,"health_gao_xue_zhi");
            returnMap.put("healIllByGaoxuezhiListMap",healIllByGaoxuezhiListMap);

            map.put("healthLabelMap", returnMap);
            map.put("msg", "健康标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("健康标签查询异常:{}", e);
            map.put("msg", "健康标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 健康签查询（糖尿病）
     *
     * @param params
     * @return
     */
    public Map<String, Object> getHealthLabelByTangniaobing(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        try {
            params.put("healthName","糖尿病");
            Integer tangniaobing = labelSecondMapper.getHealthLabel(params);
//            Integer tangniaobing = Integer.parseInt(String.valueOf(healthMap.get("tang_niao_bing")));
            Map<String, Object> tangniaobingMap = getIllMap(tangniaobing,"糖尿病");
            //糖尿病
            List<Map<String,Object>>healIllByTangniaobingListMap = labelSecondMapper.getHealIllLabelByTangniaobing(params);
            healIllByTangniaobingListMap = getBaseList(healIllByTangniaobingListMap,params,"health_tang_niao_bing");
            healIllByTangniaobingListMap.add(0,tangniaobingMap);
            healIllByTangniaobingListMap = getYingyongList(healIllByTangniaobingListMap,params,"health_tang_niao_bing");
            returnMap.put("healIllByTangniaobingListMap",healIllByTangniaobingListMap);

            map.put("healthLabelMap", returnMap);
            map.put("msg", "健康标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("健康标签查询异常:{}", e);
            map.put("msg", "健康标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 自定义标签查询
     *
     * @param params
     * @return
     */
    public Map<String, Object> getCustomLabel(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<Map<String,Object>> customLabelListMap = labelSecondMapper.getCustomLabel(params);
            if(customLabelListMap.size() > 0){
                for (Map<String, Object> customMap : customLabelListMap){
                    String name = String.valueOf(customMap.get("name"));
                    Integer count = Integer.parseInt(String.valueOf(customMap.get("count")));
                    String label = name +"("+count+"人"+")";
                    customMap.put("label",label);
                }
            }
            map.put("customLabelListMap", customLabelListMap);
            map.put("msg", "自定义标签查询成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("自定义标签查询异常:{}", e);
            map.put("msg", "自定义标签查询失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询人数
     *
     * @param params
     * @return
     */
    public Map<String, Object> getLabelCount(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer count = 0;
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            if (!StringUtil.isEmpty(params.get("labelParams"))){
                Map<String, Object> labelParams = JacksonUtils.json2map(params.get("labelParams").toString());
                List<String> sex = (List<String>)labelParams.get("sex");
                List<String> age = ( List<String>)labelParams.get("age");
                List<String> birthday = ( List<String>)labelParams.get("birthday");
                List<String> xingzuo = (List<String>)labelParams.get("xingzuo");
                List<String> shengxiao = (List<String>)labelParams.get("shengxiao");
                List<String> register = ( List<String>)labelParams.get("register");
                List<String> area = (List<String>)labelParams.get("area");
                List<String> bargainMoney = (List<String>)labelParams.get("bargainMoney");
                List<String> bargainCount = (List<String>)labelParams.get("bargainCount");
                List<String> preTransaction = (List<String>)labelParams.get("preTransaction");
                List<String> refundProbability = (List<String>)labelParams.get("refundProbability");
                List<String> everBuy = (List<String>)labelParams.get("everBuy");
                List<String> buyPeriod = (List<String>)labelParams.get("buyPeriod");
                List<String> addIntegral = (List<String>)labelParams.get("addIntegral");
                List<String> consumeIntegral = (List<String>)labelParams.get("consumeIntegral");
                List<String> residueIntegral = (List<String>)labelParams.get("residueIntegral");
                List<String> disStoreActivity = (List<String>)labelParams.get("disStoreActivity");
                List<String> disStoreAddress = (List<String>)labelParams.get("disStoreAddress");
                List<String> disContendStoreActivity = (List<String>)labelParams.get("disContendStoreActivity");
                List<String> disContendStoreAddress = (List<String>)labelParams.get("disContendStoreAddress");
                List<String> healthGaoXueYa = (List<String>)labelParams.get("healthGaoXueYa");
                List<String> healthGaoXueZhi = (List<String>)labelParams.get("healthGaoXueZhi");
                List<String> healthTangNiaoBing = (List<String>)labelParams.get("healthTangNiaoBing");
                List<String> custom = (List<String>)labelParams.get("custom");

                if (sex.size() > 0 || age.size() > 0 || birthday.size() > 0 || xingzuo.size() > 0 || shengxiao.size() > 0 || register.size() > 0
                    || area.size() > 0 || bargainMoney.size() > 0 || bargainCount.size() > 0 || preTransaction.size() > 0 || refundProbability.size() > 0
                    || everBuy.size() > 0 || buyPeriod.size() > 0 || addIntegral.size() > 0 || consumeIntegral.size() > 0 || residueIntegral.size() > 0
                    || disStoreActivity.size() > 0 || disStoreAddress.size() > 0 || disContendStoreActivity.size() > 0 || disContendStoreAddress.size() > 0
                    || healthGaoXueYa.size() > 0 || healthGaoXueZhi.size() > 0 || healthTangNiaoBing.size() > 0 || custom.size() > 0
                    ){
                    Map<String, Object> args = getLabelCountAndId(labelParams,siteId);
                    //执行查询
                    args.put("states","memberCount");
                    List<Integer> countList = labelSecondMapper.getMemberLabelCount(args);
                    if (countList.size() == 1){
                        count = countList.get(0);
                    }else {
                        count=0;
                    }
                }
            }
            map.put("count", count);
            map.put("status", 0);
            map.put("msg", "查询人数成功");
            return map;
        } catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("count", 0);
            map.put("msg", "查询人数失败");
            map.put("status", -1);
            return map;
        }
    }

    /**
     * 查询会员ID
     *
     * @param params
     * @return
     */
    public Map<String, Object> getMemberIdsToInsert(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            String memberIds = null;
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            if (!StringUtil.isEmpty(params.get("labelParams"))){
                Map<String, Object> labelParams = JacksonUtils.json2map(params.get("labelParams").toString());
                Map<String, Object> args = getLabelCountAndId(labelParams,siteId);
                //执行查询
                args.put("states","memberIds");
                List<Integer> memberIdsList = labelSecondMapper.getMemberLabelCount(args);
                if (memberIdsList.size() > 0){
                    memberIds = StringUtils.join(memberIdsList.toArray(),',');
                }
            }
            map.put("memberIds", memberIds);
            map.put("status", 0);
            map.put("msg", "查询会员ID成功");
            return map;
        } catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("memberIds", null);
            map.put("msg", "查询会员ID失败");
            map.put("status", -1);
            return map;
        }
    }

    //抽方法：计算成交金额，成交次数，客单价，退款率，获取积分，消耗积分
    public List<List<Integer>> getBufenList(Map<String, Object> labelParams, Integer siteId){
        List<List<Integer>> allList = new ArrayList<>();
        try{





            //高血压
            if (labelParams.containsKey("healthGaoXueYa")){
                Map<String,Object> xMap = new HashMap<>();
                List<String> list = (List<String>)labelParams.get("healthGaoXueYa");
                if (list.size() > 0){
                    for (int i = 0;i < list.size(); i++){
                        Map<String,Object> illMap = JacksonUtils.json2map(list.get(i));
                        String ill = String.valueOf(illMap.get("ill"));
                        if (ill.equals("health_gao_xue_ya_sure")){
                            xMap.put("gaoxueya","gaoxueya");
                            list.remove(i);
                        }
                    }
                    List<Map<String, Object>> healthGaoXueYaList = getMemberCountByTradesThree(list);
                    if (healthGaoXueYaList.size() > 0 || !StringUtil.isEmpty(xMap)){
                        if (!StringUtil.isEmpty(xMap)){
                            healthGaoXueYaList.add(xMap);
                        }
                        List<Integer> healthGaoXueYaBuyerIdList = labelSecondMapper.getHealthGaoXueYaBuyerIdList(siteId,healthGaoXueYaList);
                        allList.add(healthGaoXueYaBuyerIdList);
                    }
                }
            }
            //高血脂
            if (labelParams.containsKey("healthGaoXueZhi")){
                Map<String,Object> xMap = new HashMap<>();
                List<String> list = (List<String>)labelParams.get("healthGaoXueZhi");
                if (list.size() > 0){
                    for (int i = 0;i < list.size(); i++){
                        Map<String,Object> illMap = JacksonUtils.json2map(list.get(i));
                        String ill = String.valueOf(illMap.get("ill"));
                        if (ill.equals("health_gao_xue_zhi_sure")){
                            xMap.put("gaoxuezhi","gaoxuezhi");
                            list.remove(i);
                        }
                    }
                    List<Map<String, Object>> healthGaoXueZhiList = getMemberCountByTradesThree(list);
                    if (healthGaoXueZhiList.size() > 0 || !StringUtil.isEmpty(xMap)){
                        if (!StringUtil.isEmpty(xMap)){
                            healthGaoXueZhiList.add(xMap);
                        }
                        List<Integer> healthGaoXuezhiBuyerIdList = labelSecondMapper.getHealthGaoXueZhiBuyerIdList(siteId,healthGaoXueZhiList);
                        allList.add(healthGaoXuezhiBuyerIdList);
                    }
                }
            }
            //糖尿病
            if (labelParams.containsKey("healthTangNiaoBing")){
                Map<String,Object> xMap = new HashMap<>();
                List<String> list = (List<String>)labelParams.get("healthTangNiaoBing");
                if (list.size() > 0){
                    for (int i = 0;i < list.size(); i++){
                        Map<String,Object> illMap = JacksonUtils.json2map(list.get(i));
                        String ill = String.valueOf(illMap.get("ill"));
                        if (ill.equals("health_tang_niao_bing_sure")){
                            xMap.put("tangniaobing","tangniaobing");
                            list.remove(i);
                        }
                    }
                    List<Map<String, Object>> healthTangNiaoBingList = getMemberCountByTradesThree(list);
                    if (healthTangNiaoBingList.size() > 0 || !StringUtil.isEmpty(xMap)){
                        if (!StringUtil.isEmpty(xMap)){
                            healthTangNiaoBingList.add(xMap);
                        }
                        List<Integer> healthGaoXuezhiBuyerIdList = labelSecondMapper.getHealthTangniaobingBuyerIdList(siteId,healthTangNiaoBingList);
                        allList.add(healthGaoXuezhiBuyerIdList);
                    }
                }
            }

            return allList;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            return allList;
        }
    }

    //抽方法：查会员人数和会员ID
    public Map<String, Object> getLabelCountAndId(Map<String, Object> labelParams,Integer siteId){
        Map<String, Object> args = new HashMap<>();
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("siteId",siteId);//用于查询商户下所有的门店
            args.put("siteId",siteId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //性别
            if (labelParams.containsKey("sex")){
                List<String> list = (List<String>)labelParams.get("sex");
                if (list.size() > 0){
                    List<Map<String, Object>> sexList = new ArrayList<>();
                    Map<String, Object> sexMap = new HashMap<>();
                    for (String str :list){
                        if (str.equals("男")){
                            sexMap.put("sex1",1);
                        }else if (str.equals("女")){
                            sexMap.put("sex0",0);
                        }else if (str.equals("未知")){
                            sexMap.put("sex3",3);
                        }
                    }
                    sexList.add(sexMap);
                    if (sexList.size() > 0){
                        args.put("sexList",sexList);
                    }
                }
            }
            //年龄
            if (labelParams.containsKey("age")){
                List<String> list = (List<String>)labelParams.get("age");
                if (list.size() > 0){
                    List<Map<String, Object>> ageList = new ArrayList<>();
                    for (String ageStr : list){
                        Map<String, Object> ageMap = JacksonUtils.json2map(ageStr);
                        Map<String, Object> aMap = new HashMap<>();
                        Integer type = Integer.parseInt(String.valueOf(ageMap.get("type")));
                        if (type == 1){
                            Integer max = Integer.parseInt(String.valueOf(ageMap.get("age")));
                            String ageMin = formatTime(max,1);
                            String ageMax = sdf.format(new Date());
                            aMap.put("ageMin",ageMin);
                            aMap.put("ageMax",ageMax);
                        }else if (type == 2){
                            Integer min = Integer.parseInt(String.valueOf(ageMap.get("age")));
                            String ageMax = formatTime(min,1);
                            String ageMin = formatTime(200,1);
                            aMap.put("ageMin",ageMin);
                            aMap.put("ageMax",ageMax);
                        }else if (type == 3){
                            Integer max = Integer.parseInt(String.valueOf(ageMap.get("ageMax")));
                            Integer min = Integer.parseInt(String.valueOf(ageMap.get("ageMin")));
                            String ageMax = formatTime(min,1);
                            String ageMin = formatTime(max,1);
                            aMap.put("ageMin",ageMin);
                            aMap.put("ageMax",ageMax);
                        }else if (type == 4){
                            args.put("weizhiAge","0000-00-00");
                        }else if (type == 5){
                            args.put("yizhiAge","0000-00-01");
                        }
                        ageList.add(aMap);
                    }
                    if (ageList.size() > 0){
                        args.put("ageList",ageList);
                    }
                }
            }
            //生日
            if (labelParams.containsKey("birthday")){
                List<String> list = (List<String>)labelParams.get("birthday");
                if (list.contains("32") && list.contains("00")){
                    args.put("birthdAll","00");
                }else if (list.contains("32") && !list.contains("00")){
                    args.put("birthdayyizhi","0000-00-01");
                }else if (!list.contains("32") && list.contains("00") && list.size()==1){
                    args.put("birthdayweizhi","0000-00-01");
                }else if (!list.contains("32") && list.size() > 0){
                    args.put("birthdayList",list);
                    if (list.contains("00")){
                        args.put("birthdayListWeizhi","birthdayListWeizhi");
                    }
                }
            }
            //星座
            if (labelParams.containsKey("xingzuo")){
                List<String> list = (List<String>)labelParams.get("xingzuo");
                if (list.size() > 0){
                    args.put("xingzuoList",list);
                }
            }
            //生肖
            if (labelParams.containsKey("shengxiao")){
                List<String> list = (List<String>)labelParams.get("shengxiao");
                if (list.size() > 0){
                    args.put("shengxiaoList",list);
                }
            }
            //注册时间
            if (labelParams.containsKey("register")){
                List<String> list = (List<String>)labelParams.get("register");
                if (list.size() > 0){
                    List<Map<String, Object>> registList = new ArrayList<>();
                    for (String strreg : list){
                        Map<String, Object> regMap = JacksonUtils.json2map(strreg);
                        Map<String, Object> registMap = new HashMap<>();
                        Integer type = Integer.parseInt(String.valueOf(regMap.get("type")));
                        if (type == 1){
                            Integer str = Integer.parseInt(String.valueOf(regMap.get("day")));
                            String registerMin = formatTime(str,2);
                            String registerMax = sdf2.format(new Date());
                            registMap.put("registerMin",registerMin);
                            registMap.put("registerMax",registerMax);
                            registList.add(registMap);
                        }else if (type == 2){
                            String registerMin = String.valueOf(regMap.get("registerMin"));
                            String registerMax = String.valueOf(regMap.get("registerMax"))+" 23:59:59";
                            registMap.put("registerMin",registerMin);
                            registMap.put("registerMax",registerMax);
                            registList.add(registMap);
                        }
                    }
                    if (registList.size() > 0){
                        args.put("registList",registList);
                    }
                }
            }
            //区域
            if (labelParams.containsKey("area")){
                List<String> list = (List<String>)labelParams.get("area");
                if (list.contains("已知区域") && !list.contains("未知区域")){
                    args.put("areaYizhiList","areaYizhiList");
                    for (String area :list){
                        if(area.equals("已知区域")){
                            list.remove(area);
                            break;
                        }
                    }
                }else if (list.contains("已知区域") && list.contains("未知区域")){
                    args.put("areaAll","areaAll");
                }else if (!list.contains("已知区域") && list.contains("未知区域") && list.size()==1){
                    args.put("areaWeiizhiList","areaWeiizhiList");
                }else if (list.size() > 0){
                    if (list.contains("未知区域")){
                        args.put("areaWeiizhiArea","areaWeiizhiArea");
                        for (String area : list){
                            if (area.equals("未知区域")){
                                list.remove(area);
                                break;
                            }
                        }
                    }
                    //先获取所有的会员注册时写的区域
                    List<Map<String, Object>> infoListMap = labelSecondMapper.getInfoListMap2(params);
                    //获取会员填写的收货地址的区域
                    List<Map<String, Object>> addrListMap = labelSecondMapper.getAddrListMap2(params);
                    Map<String, Object> kMap = areaChou2(infoListMap, addrListMap);
                    List<Map<String, Object>> cityListMap = (List<Map<String, Object>>)kMap.get("cityListMap");
                    List<Integer> buyerIdList = new ArrayList<>();
                    for (String area : list){
                        for (Map<String, Object> areaMap : cityListMap){
                            String areaid = String.valueOf(areaMap.get("area"));
                            if (area.equals(areaid)){
                                Integer buyerId = Integer.parseInt(String.valueOf(areaMap.get("buyerId")));
                                buyerIdList.add(buyerId);
                            }
                        }
                    }
                    args.put("areaList",buyerIdList);
                }
            }

            //成交金额
            if (labelParams.containsKey("bargainMoney")){
                List<String> list = (List<String>)labelParams.get("bargainMoney");
                if (list.size() > 0) {
                    List<Map<String, Object>> bargainMoneyList = getMemberCountByTradesOne(list);
                    for(Map<String, Object> rMap : bargainMoneyList){
                        if (rMap.containsKey("weizhi")){
                            args.put("bargainWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("bargainYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : bargainMoneyList){
                        if (rMap.containsKey("yizhi")){
                            bargainMoneyList.remove(rMap);
                            break;
                        }
                    }
                    if (bargainMoneyList.size() > 0){
                        args.put("bargainMoneyList",bargainMoneyList);
                    }
                }
            }

            //成交次数
            if (labelParams.containsKey("bargainCount")){
                List<String> list = (List<String>)labelParams.get("bargainCount");
                if (list.size() > 0) {
                    List<Map<String, Object>> bargainCountList = getMemberCountByTradesOne(list);
                    if (bargainCountList.size() > 0){
                        args.put("bargainCountList",bargainCountList);
                    }
                }
            }
            //客单价
            if (labelParams.containsKey("preTransaction")){
                List<String> list = (List<String>)labelParams.get("preTransaction");
                if (list.size() > 0) {
                    List<Map<String, Object>> preTransactionList = getMemberCountByTradesOne(list);
                    if (preTransactionList.size() > 0){
                        args.put("preTransactionList",preTransactionList);
                    }
                }
            }

            //退款率
            if (labelParams.containsKey("refundProbability")){
                List<String> list = (List<String>)labelParams.get("refundProbability");
                if (list.size() > 0) {
                    List<Map<String, Object>> refundProbabilityList = getMemberCountByTradesOne(list);
                    if (refundProbabilityList.size() > 0){
                        args.put("refundProbabilityList",refundProbabilityList);
                    }
                }
            }

            //购买过
            if (labelParams.containsKey("everBuy")){
                List<String> list = (List<String>)labelParams.get("everBuy");
                List<Map<String, Object>> everBuyList = new ArrayList<>();
                if (list.size() > 0) {
                    for (String ebStr : list){
                        Map<String, Object> ebMap = JacksonUtils.json2map(ebStr);
                        Map<String, Object> memberCountByTradesTwo = getMemberCountByTradesTwo(ebMap);
                        if (!StringUtil.isEmpty(memberCountByTradesTwo)){
                            everBuyList.add(memberCountByTradesTwo);
                        }
                    }
                }
                if (everBuyList.size() > 0){
                    args.put("everBuyList",everBuyList);
                }
            }

            //购买周期
            if (labelParams.containsKey("buyPeriod")){
                List<String> list = (List<String>)labelParams.get("buyPeriod");
                if (list.size() > 0){
                    List<Map<String, Object>> buyPeriodList = getMemberCountByTradesThree(list);
                    if (buyPeriodList.size() > 0){
                        args.put("buyPeriodList",buyPeriodList);
                    }
                }
            }

            //获取积分
            if (labelParams.containsKey("addIntegral")){
                List<String> list = (List<String>)labelParams.get("addIntegral");
                if (list.size() > 0) {
                    List<Map<String, Object>> addIntegralList = getMemberCountByTradesOne(list);
                    for(Map<String, Object> rMap : addIntegralList){
                        if (rMap.containsKey("weizhi")){
                            args.put("addIntegralWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("addIntegralYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : addIntegralList){
                        if (rMap.containsKey("yizhi")){
                            addIntegralList.remove(rMap);
                            break;
                        }
                    }
                    for(Map<String, Object> rMap : addIntegralList){
                        if (rMap.containsKey("weizhi")){
                            addIntegralList.remove(rMap);
                            break;
                        }
                    }
                    if (addIntegralList.size() > 0){
                        args.put("addIntegralList",addIntegralList);
                    }
                }
            }
            //消耗积分
            if (labelParams.containsKey("consumeIntegral")){
                List<String> list = (List<String>)labelParams.get("consumeIntegral");
                if (list.size() > 0) {
                    List<Map<String, Object>> consumeIntegralList = getMemberCountByTradesOne(list);
                    for(Map<String, Object> rMap : consumeIntegralList){
                        if (rMap.containsKey("weizhi")){
                            args.put("consumeIntegralWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("consumeIntegralYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : consumeIntegralList){
                        if (rMap.containsKey("yizhi")){
                            consumeIntegralList.remove(rMap);
                            break;
                        }
                    }
                    for(Map<String, Object> rMap : consumeIntegralList){
                        if (rMap.containsKey("weizhi")){
                            consumeIntegralList.remove(rMap);
                            break;
                        }
                    }
                    if (consumeIntegralList.size() > 0){
                        args.put("consumeIntegralList",consumeIntegralList);
                    }
                }
            }

            //剩余积分
            if (labelParams.containsKey("residueIntegral")){
                List<String> list = (List<String>)labelParams.get("residueIntegral");
                if (list.size() > 0){
                    List<Map<String, Object>> residueIntegralList = getMemberCountByTradesThree(list);
                    for(Map<String, Object> rMap : residueIntegralList){
                        if (rMap.containsKey("weizhi")){
                            args.put("residueIntegralWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("residueIntegralYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : residueIntegralList){
                        if (rMap.containsKey("yizhi")){
                            residueIntegralList.remove(rMap);
                            break;
                        }
                    }
                    for(Map<String, Object> rMap : residueIntegralList){
                        if (rMap.containsKey("weizhi")){
                            residueIntegralList.remove(rMap);
                            break;
                        }
                    }
                    if (residueIntegralList.size() > 0){
                        args.put("residueIntegralList",residueIntegralList);
                    }
                }
            }

            //门店距离（高频活动）
            if (labelParams.containsKey("disStoreActivity")){
                List<String> list = (List<String>)labelParams.get("disStoreActivity");
                if (list.size() > 0) {
                    List<Map<String, Object>> disStoreActivityList = getMemberCountByTradesOne(list);
                    for(Map<String, Object> rMap : disStoreActivityList){
                        if (rMap.containsKey("weizhi")){
                            args.put("disStoreActivityWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("disStoreActivityYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : disStoreActivityList){
                        if (rMap.containsKey("yizhi")){
                            disStoreActivityList.remove(rMap);
                            break;
                        }
                    }
                    for(Map<String, Object> rMap : disStoreActivityList){
                        if (rMap.containsKey("weizhi")){
                            disStoreActivityList.remove(rMap);
                            break;
                        }
                    }
                    if (disStoreActivityList.size() > 0){
                        args.put("disStoreActivityList",disStoreActivityList);
                    }
                }
            }

            //门店距离（收货地址）
            if (labelParams.containsKey("disStoreAddress")){
                List<String> list = (List<String>)labelParams.get("disStoreAddress");
                if (list.size() > 0){
                    List<Map<String, Object>> disStoreAddressList = getMemberCountByTradesThreeToAddress(list);
                    for(Map<String, Object> rMap : disStoreAddressList){
                        if (rMap.containsKey("weizhi")){
                            args.put("disStoreAddressWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("disStoreAddressYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : disStoreAddressList){
                        if (rMap.containsKey("yizhi")){
                            disStoreAddressList.remove(rMap);
                            break;
                        }
                    }
                    for(Map<String, Object> rMap : disStoreAddressList){
                        if (rMap.containsKey("weizhi")){
                            disStoreAddressList.remove(rMap);
                            break;
                        }
                    }
                    if (disStoreAddressList.size() > 0){
                        args.put("disStoreAddressList",disStoreAddressList);
                    }
                }
            }

            //竞店距离（高频活动）
            if (labelParams.containsKey("disContendStoreActivity")){
                List<String> list = (List<String>)labelParams.get("disContendStoreActivity");
                if (list.size() > 0) {
                    List<Map<String, Object>> disContendStoreActivityList = getMemberCountByTradesOne(list);
                    for(Map<String, Object> rMap : disContendStoreActivityList){
                        if (rMap.containsKey("weizhi")){
                            args.put("disContendStoreActivityWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("disContendStoreActivityYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : disContendStoreActivityList){
                        if (rMap.containsKey("yizhi")){
                            disContendStoreActivityList.remove(rMap);
                            break;
                        }
                    }
                    for(Map<String, Object> rMap : disContendStoreActivityList){
                        if (rMap.containsKey("weizhi")){
                            disContendStoreActivityList.remove(rMap);
                            break;
                        }
                    }
                    if (disContendStoreActivityList.size() > 0){
                        args.put("disContendStoreActivityList",disContendStoreActivityList);
                    }
                }
            }

            //竞店距离（收货地址）
            if (labelParams.containsKey("disContendStoreAddress")){
                List<String> list = (List<String>)labelParams.get("disContendStoreAddress");
                if (list.size() > 0){
                    List<Map<String, Object>> disContendStoreAddressList = getMemberCountByTradesThreeToAddress(list);
                    for(Map<String, Object> rMap : disContendStoreAddressList){
                        if (rMap.containsKey("weizhi")){
                            args.put("disContendStoreAddressWeizhi","weizhi");
                        }
                        if (rMap.containsKey("yizhi")){
                            args.put("disContendStoreAddressYizhi","yizhi");
                        }
                    }
                    for(Map<String, Object> rMap : disContendStoreAddressList){
                        if (rMap.containsKey("yizhi")){
                            disContendStoreAddressList.remove(rMap);
                            break;
                        }
                    }
                    for(Map<String, Object> rMap : disContendStoreAddressList){
                        if (rMap.containsKey("weizhi")){
                            disContendStoreAddressList.remove(rMap);
                            break;
                        }
                    }
                    if (disContendStoreAddressList.size() > 0){
                        args.put("disContendStoreAddressList",disContendStoreAddressList);
                    }
                }
            }

            //高血压
            if (labelParams.containsKey("healthGaoXueYa")){
                List<String> list = (List<String>)labelParams.get("healthGaoXueYa");
                Integer type = 0;
                if (list.size() > 0){
                    for (int i = 0;i < list.size(); i++){
                        Map<String,Object> illMap = JacksonUtils.json2map(list.get(i));
                        String ill = String.valueOf(illMap.get("ill"));
                        if (ill.equals("health_gao_xue_ya_sure")){
                            args.put("gaoxueya","gaoxueya");
                            list.remove(i);
                            type = 1;
                        }
                    }
                    List<Map<String, Object>> healthGaoXueYaList = getMemberCountByTradesThree(list);
                    if (type == 1){
                        Map<String,Object> xy = new HashMap<>();
                        xy.put("gaoxueya","gaoxueya");
                        healthGaoXueYaList.add(xy);
                        args.put("healthGaoXueYaList",healthGaoXueYaList);
                    }else if (type == 0 && healthGaoXueYaList.size() > 0){
                        args.put("healthGaoXueYaList",healthGaoXueYaList);
                    }
                }
            }
            //高血脂
            if (labelParams.containsKey("healthGaoXueZhi")){
                List<String> list = (List<String>)labelParams.get("healthGaoXueZhi");
                Integer type = 0;
                if (list.size() > 0){
                    for (int i = 0;i < list.size(); i++){
                        Map<String,Object> illMap = JacksonUtils.json2map(list.get(i));
                        String ill = String.valueOf(illMap.get("ill"));
                        if (ill.equals("health_gao_xue_zhi_sure")){
                            args.put("gaoxuezhi","gaoxuezhi");
                            list.remove(i);
                            type = 1;
                        }
                    }
                    List<Map<String, Object>> healthGaoXueZhiList = getMemberCountByTradesThree(list);
                    if (type == 1){
                        Map<String,Object> xy = new HashMap<>();
                        xy.put("gaoxuezhi","gaoxuezhi");
                        healthGaoXueZhiList.add(xy);
                        args.put("healthGaoXueZhiList",healthGaoXueZhiList);
                    }else if (type == 0 && healthGaoXueZhiList.size() > 0){
                        args.put("healthGaoXueZhiList",healthGaoXueZhiList);
                    }
                }
            }
            //糖尿病
            if (labelParams.containsKey("healthTangNiaoBing")){
                List<String> list = (List<String>)labelParams.get("healthTangNiaoBing");
                Integer type = 0;
                if (list.size() > 0){
                    for (int i = 0;i < list.size(); i++){
                        Map<String,Object> illMap = JacksonUtils.json2map(list.get(i));
                        String ill = String.valueOf(illMap.get("ill"));
                        if (ill.equals("health_tang_niao_bing_sure")){
                            args.put("tangniaobing","tangniaobing");
                            list.remove(i);
                            type = 1;
                        }
                    }
                    List<Map<String, Object>> healthTangNiaoBingList = getMemberCountByTradesThree(list);
                    if (type == 1){
                        Map<String,Object> xy = new HashMap<>();
                        xy.put("tangniaobing","tangniaobing");
                        healthTangNiaoBingList.add(xy);
                        args.put("healthTangNiaoBingList",healthTangNiaoBingList);
                    }else if (type == 0 && healthTangNiaoBingList.size() > 0){
                        args.put("healthTangNiaoBingList",healthTangNiaoBingList);
                    }
                }
            }

            //自定义标签
            if (labelParams.containsKey("custom")){
                List<String> list = (List<String>)labelParams.get("custom");
                if (list.size() > 0){
                    args.put("customList",list);
                }
            }
            return args;
        } catch (Exception e) {
            log.info("抽方法：查询人数:{}", e);
            return args;
        }
    }

    //应用分组
    public List<Map<String,Object>> getYingyongList(List<Map<String,Object>> allList,Map<String,Object> params,String type){
        try{
            type = getType(type);
            if (allList.size() > 0){
                for (Map<String,Object> zuMap : allList){
                    Integer count = 0;
                    String xiaoName = "";
                    if (zuMap.containsKey("labelName")){
                        xiaoName = String.valueOf(zuMap.get("labelName"));
                    }else if (zuMap.containsKey("name")){
                        xiaoName = String.valueOf(zuMap.get("name"));
                    }

                    //查询所有的会员分组
                    List<Map<String,Object>> labelsList = labelSecondMapper.getAllLabel(params);
                    if (labelsList.size() > 0){
                        for (Map<String,Object> daMap : labelsList){
                            Map<String,Object> labelGroupMap = JacksonUtils.json2map(String.valueOf(daMap.get("labelGroup")));
                            List<String> labels = (List<String>)labelGroupMap.get(type);
                            if (labels.contains(xiaoName)){
                                count++;
                            }
                        }
                    }
                    zuMap.put("yingyongCount",count);
                }
            }
            return allList;
        }catch (Exception e){
            log.info("基础标签查询异常:{}", e);
            return allList;
        }
    }

    //查询年龄：已知/未知
    public Map<String, Object> getAgeYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            List<Map<String,Object>> ageYizhiWeizhiListMap = new ArrayList<>();
            //已知年龄
            Integer yizhiAgeCount = labelSecondMapper.getYizhiAge(params);
            Map<String,Object> ageYizhiMap = getYizhiAge(yizhiAgeCount,"有年龄属性的人数","已知");

            //未知年龄
            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数
            Integer weizhiAgeCount = allCount - yizhiAgeCount;
//            Integer weizhiAgeCount = labelSecondMapper.getWeizhiAge(params);
            Map<String,Object> ageWeizhiMap = getWeizhiAge(weizhiAgeCount,"无法识别年龄属性的人数","未知");
            ageYizhiWeizhiListMap.add(ageWeizhiMap);
//            map.put("ageWeizhiMap",ageWeizhiMap);

            ageYizhiWeizhiListMap.add(ageYizhiMap);
            ageYizhiWeizhiListMap = getYingyongList(ageYizhiWeizhiListMap,params,"age");
            map.put("ageYizhiWeizhiListMap",ageYizhiWeizhiListMap);
            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询年龄已知未知失败");
            map.put("status", -1);
            return map;
        }
    }

    //查询成交金额：已知/未知
    public Map<String, Object> getBargainMoneyYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //已知成交金额
            Integer yizhiBargainMoneyCount = labelSecondMapper.getyizhiBargainMoneyCount(params);
            Map<String,Object> yizhiBargainMoneyMap = getWeizhi(yizhiBargainMoneyCount,"历史累计有成功交易的会员数","已成交","time",5);
            map.put("yizhiBargainMoneyMap",yizhiBargainMoneyMap);

            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数

            //未知成交金额
//            Integer bargainMoneyBufenMap = labelSecondMapper.getBufenWeizhiBargainMoney(params);//成交金额为0（成交总金额为0，下的全部订单均为退款）
            Integer weizhiBargainMoneyCount = allCount - yizhiBargainMoneyCount;
            Map<String,Object> weizhiBargainMoneyMap = getWeizhi(weizhiBargainMoneyCount,"历史累计没有成功交易的会员数","无成交","time",4);
            map.put("weizhiBargainMoneyMap",weizhiBargainMoneyMap);

            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询金额已知未知失败");
            map.put("status", -1);
            return map;
        }
    }

    //查询赚取积分：已知/未知
    public Map<String, Object> getAddIntegrateYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //已知有赚取积分
            Integer yizhiAddIntegrateCount = labelSecondMapper.getyizhiAddIntegrateCount(params);
            Map<String,Object> yizhiAddIntegrateMap = getWeizhi(yizhiAddIntegrateCount,"历史累计有赚取积分的会员数","有赚取","time",5);
            map.put("yizhiAddIntegrateMap",yizhiAddIntegrateMap);

            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数

            //已知没有赚取积分
            Integer weizhiAddIntegrateCount = allCount - yizhiAddIntegrateCount;
            Map<String,Object> weiizhiAddIntegrateMap = getWeizhi(weizhiAddIntegrateCount,"历史累计没有赚取积分的会员数","无赚取","time",4);
            map.put("weiizhiAddIntegrateMap",weiizhiAddIntegrateMap);
            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询赚取积分已知未知失败");
            map.put("status", -1);
            return map;
        }
    }

    //查询消耗积分：已知/未知
    public Map<String, Object> getConsumeIntegrateYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //已知有消耗积分
            Integer yizhiConsumeIntegrateCount = labelSecondMapper.getyizhiConsumeIntegrateCount(params);
            Map<String,Object> yizhiConsumeIntegrateMap = getWeizhi(yizhiConsumeIntegrateCount,"历史累计有消耗积分的会员数","有消耗","time",5);
            map.put("yizhiConsumeIntegrateMap",yizhiConsumeIntegrateMap);

            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数

            //已知没有消耗积分
            Integer weizhiConsumeIntegrateCount = allCount - yizhiConsumeIntegrateCount;
            Map<String,Object> weiizhiConsumeIntegrateMap = getWeizhi(weizhiConsumeIntegrateCount,"历史累计没有消耗积分的会员数","无消耗","time",4);
            map.put("weiizhiConsumeIntegrateMap",weiizhiConsumeIntegrateMap);
            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询消耗积分已知未知失败");
            map.put("status", -1);
            return map;
        }
    }

    //查询剩余积分：已知/未知
    public Map<String, Object> getResidueIntegrateYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //已知有剩余积分
            Integer yizhiResidueIntegrateCount = labelSecondMapper.getyizhiResidueIntegrateCount(params);
            Map<String,Object> yizhiResidueIntegrateMap = getWeizhi(yizhiResidueIntegrateCount,"历史累计有消耗积分的会员数","有剩余","day",5);
            map.put("yizhiResidueIntegrateMap",yizhiResidueIntegrateMap);

            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数

            //已知没有剩余积分
            Integer weizhiResidueIntegrateCount = allCount - yizhiResidueIntegrateCount;
            Map<String,Object> weiizhiResidueIntegrateMap = getWeizhi(weizhiResidueIntegrateCount,"历史累计没有消耗积分的会员数","无剩余","day",4);
            map.put("weiizhiResidueIntegrateMap",weiizhiResidueIntegrateMap);

            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询剩余积分已知未知失败");
            map.put("status", -1);
            return map;
        }
    }

    //门店距离（高频活动）：有活动/没有活动
    public Map<String, Object> getDisStoreActivityYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //已知有活动
            Integer yizhiDisStoreActivityCount = labelSecondMapper.getyizhiDisStoreActivityCount(params);
            Map<String,Object> yizhiDisStoreActivityMap = getWeizhi(yizhiDisStoreActivityCount,"有过活动坐标的会员数","有活动","time",5);
            map.put("yizhiDisStoreActivityMap",yizhiDisStoreActivityMap);

            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数

            //已知没有活动
            Integer weizhiDisStoreActivityCount = allCount - yizhiDisStoreActivityCount;
            Map<String,Object> weiizhiDisStoreActivityMap = getWeizhi(weizhiDisStoreActivityCount,"没有任何活动坐标的会员数","无活动","time",4);
            map.put("weiizhiDisStoreActivityMap",weiizhiDisStoreActivityMap);
            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询门店距离（高频活动）已知未知失败");
            map.put("status", -1);
            return map;
        }
    }

    //门店距离（收货地址）：有地址/没有地址
    public Map<String, Object> getDisStoreAddressYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //有地址
            Integer yizhiDisStoreAddressCount = labelSecondMapper.getyizhiDisStoreAddressCount(params);
            Map<String,Object> yizhiDisStoreAddressMap = getWeizhi(yizhiDisStoreAddressCount,"有收货地址信息的会员数","有地址","day",5);
            map.put("yizhiDisStoreAddressMap",yizhiDisStoreAddressMap);

            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数

            //没有地址
            Integer weizhiDisStoreAddressCount = allCount - yizhiDisStoreAddressCount;
            Map<String,Object> weiizhiDisStoreAddressMap = getWeizhi(weizhiDisStoreAddressCount,"没有任何收货地址的信息的会员数","无地址","day",4);
            map.put("weiizhiDisStoreAddressMap",weiizhiDisStoreAddressMap);
            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询门店距离（收货地址）已知未知失败");
            map.put("status", -1);
            return map;
        }
    }

    //零退款
    public Map<String, Object> getRefundMoneyWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //查询零退款
            Integer zeroRefund = labelSecondMapper.getZeroRefundCount(params);
            Map<String,Object> zeroRefundMap = getWeizhi(zeroRefund,"历史累计无退款","零退款","time",4);
            map.put("zeroRefundMap",zeroRefundMap);
            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询零退款失败");
            map.put("status", -1);
            return map;
        }
    }

    //区域已知/未知
    public Map<String, Object> getAreaYizhiWeizhi(Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try{
            //已知区域
            Integer yizhiArea = labelSecondMapper.getAreaYizhi(params);
            Map<String,Object> areaYizhiMap = getYizhiAge(yizhiArea,"有区域属性的人数","已知");
            map.put("areaYizhiMap",areaYizhiMap);
            //未知区域
            Integer allCount = labelSecondMapper.getallCount(params);//商户下所有的会员数
//            Integer weizhiArea = labelSecondMapper.getAreaWeizhi(params);
            Integer weizhiArea = allCount - yizhiArea;
            Map<String,Object> areaWeizhiMap = getWeizhiAge(weizhiArea,"无法识别区域属性的人数","未知");
            map.put("areaWeizhiMap",areaWeizhiMap);
            return map;
        }catch (Exception e) {
            log.info("查询人数异常:{}", e);
            map.put("msg", "查询零退款失败");
            map.put("status", -1);
            return map;
        }
    }

    //抽方法（未知）
    public Map<String,Object> getWeizhi(Integer count,String labelDescribe,String labelName,String type,Integer typeCount){
        Map<String,Object> reMap = new HashMap<>();
        Map<String,Object> laMap = new HashMap<>();
        reMap.put("label",labelName + "(" + count + "人" + ")");
        reMap.put("labelName",labelName);
        reMap.put("count",count);
        reMap.put("labelDescribe",labelDescribe);
        reMap.put("state","true");
        laMap.put(type,typeCount);
        reMap.put("labelAttribute",JSON.toJSONString(laMap));
        return reMap;
    }





















    //抽方法：查询人数
    public List<Map<String,Object>> getMemberCountByTradesOne(List<String> list){
        List<Map<String, Object>> bargainMoneyList = new ArrayList<>();
        try {
            for (String bmStr : list) {
                Map<String, Object> bmMap = JacksonUtils.json2map(bmStr);
                Map<String, Object> bargainMoneyMap = getMemberCountByTradesTwo(bmMap);//调用方法处理时间
                if (!StringUtil.isEmpty(bmMap.get("scope"))){
                    Integer scope = Integer.parseInt(String.valueOf(bmMap.get("scope")));
                    if (scope == 1){
                        String max = String.valueOf(bmMap.get("max"));
                        String min = "0.01";
                        bargainMoneyMap.put("max",max);
                        bargainMoneyMap.put("min",min);
                    }else if (scope == 2){
                        Integer max = 99999999;
                        String min = String.valueOf(bmMap.get("min"));
                        bargainMoneyMap.put("max",max);
                        bargainMoneyMap.put("min",min);
                    }else if (scope == 3){
                        String max = String.valueOf(bmMap.get("max"));
                        String min = String.valueOf(bmMap.get("min"));
                        bargainMoneyMap.put("max",max);
                        bargainMoneyMap.put("min",min);
                    }
                }
                if (!StringUtil.isEmpty(bargainMoneyMap)){
                    bargainMoneyList.add(bargainMoneyMap);
                }
            }
            return bargainMoneyList;
        } catch (Exception e) {
            log.info("抽方法：查询人数:{}", e);
            return bargainMoneyList;
        }
    }

    //抽方法：查询人数
    public List<Map<String,Object>> getMemberCountByTradesThree(List<String> list){
        List<Map<String, Object>> bargainMoneyList = new ArrayList<>();
        try {
            for (String bmStr : list) {
                Map<String,Object> bmMap = JacksonUtils.json2map(bmStr);
                Map<String,Object> params = new HashMap<>();
                Integer time = Integer.parseInt(String.valueOf(bmMap.get("day")));
                if (time == 1){
                    String max = String.valueOf(bmMap.get("max"));
                    params.put("max",max);
                    params.put("min","0.01");
                }else if (time == 2){
                    String min = String.valueOf(bmMap.get("min"));
                    params.put("max",99999999);
                    params.put("min",min);
                }else if (time == 3){
                    String min = String.valueOf(bmMap.get("min"));
                    String max = String.valueOf(bmMap.get("max"));
                    params.put("min",min);
                    params.put("max",max);
                }else if (time == 4){
                    params.put("weizhi","weizhi");
                }else if (time == 5){
                    params.put("yizhi","yizhi");
                }
                if (!StringUtil.isEmpty(params)){
                    bargainMoneyList.add(params);
                }
            }
            return bargainMoneyList;
        } catch (Exception e) {
            log.info("抽方法：查询人数:{}", e);
            return bargainMoneyList;
        }
    }

    //抽方法：查询人数(收货地址专用)
    public List<Map<String,Object>> getMemberCountByTradesThreeToAddress(List<String> list){
        List<Map<String, Object>> bargainMoneyList = new ArrayList<>();
        try {
            for (String bmStr : list) {
                Map<String,Object> bmMap = JacksonUtils.json2map(bmStr);
                Map<String,Object> params = new HashMap<>();
                Integer time = Integer.parseInt(String.valueOf(bmMap.get("day")));
                if (time == 1){
                    String max = String.valueOf(bmMap.get("max"));
                    params.put("max",max);
                    params.put("min","0");
                }else if (time == 2){
                    String min = String.valueOf(bmMap.get("min"));
                    params.put("max",99999999);
                    params.put("min",min);
                }else if (time == 3){
                    String min = String.valueOf(bmMap.get("min"));
                    String max = String.valueOf(bmMap.get("max"));
                    params.put("min",min);
                    params.put("max",max);
                }else if (time == 4){
                    params.put("weizhi","weizhi");
                }else if (time == 5){
                    params.put("yizhi","yizhi");
                }
                if (!StringUtil.isEmpty(params)){
                    bargainMoneyList.add(params);
                }
            }
            return bargainMoneyList;
        } catch (Exception e) {
            log.info("抽方法：查询人数:{}", e);
            return bargainMoneyList;
        }
    }

    public Map<String,Object> getMemberCountByTradesTwo(Map<String,Object> bmMap){
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> bargainMoneyMap = new HashMap<>();
        try {
            Integer time = Integer.parseInt(String.valueOf(bmMap.get("time")));
            if (time == 1){
                Integer day = Integer.parseInt(String.valueOf(bmMap.get("timeMin")));
                String timeMin = formatTime(day,2);
                String timeMax = sdf2.format(new Date());
                bargainMoneyMap.put("timeMin",timeMin);
                bargainMoneyMap.put("timeMax",timeMax);
            }else if (time == 2){
                String timeMin = String.valueOf(bmMap.get("timeMin"));
                String timeMax = String.valueOf(bmMap.get("timeMax"))+" 23:59:59";
                bargainMoneyMap.put("timeMin",timeMin);
                bargainMoneyMap.put("timeMax",timeMax);
            }else if (time == 3){
                bargainMoneyMap.put("timeMin","2015-01-01");
                bargainMoneyMap.put("timeMax",sdf2.format(new Date()));
            }else if (time == 4){
                bargainMoneyMap.put("weizhi","weizhi");
            }else if (time == 5){
                bargainMoneyMap.put("yizhi","yizhi");
            }
            return bargainMoneyMap;
        } catch (Exception e) {
            log.info("抽方法：查询人数:{}", e);
            return bargainMoneyMap;
        }
    }

    //抽方法：基础查询：可自定义
    public List<Map<String,Object>> getBaseList(List<Map<String,Object>> baseList,Map<String,Object> params,String type){
        try {
            if (baseList.size() > 0){
                for (Map<String,Object> birthdayLabelMap : baseList){
                    String str = String.valueOf(birthdayLabelMap.get("labelAttribute"));
                    Map<String, Object> labelAttributeMap = JacksonUtils.json2map(str);
                    labelAttributeMap.putAll(params);
                    Map<String, Object> map1 = new HashMap<>();
                    if (type.equals("age")){
                        map1 = selectAge(labelAttributeMap,"N");//年龄
                    }else if (type.equals("birthday")){
                        map1 = selectBirthday(labelAttributeMap,"N");//生日
                    }else if (type.equals("register")){
                        map1 = selectRegister(labelAttributeMap,"N");//注册时间
                    }else if (type.equals("bargain_money")){
                        map1 = selectBargainMoney(labelAttributeMap,"N");//成交金额
                    }else if (type.equals("bargain_count")){
                        map1 = selectBargainCount(labelAttributeMap,"N");//成交次数
                    }else if (type.equals("pre_transaction")){
                        map1 = selectPreTransaction(labelAttributeMap,"N");//客单价
                    }else if (type.equals("refund_probability")){
                        map1 = selecRefundProbability(labelAttributeMap,"N");//退款率
                    }else if (type.equals("ever_buy")){
                        map1 = selectEverBuy(labelAttributeMap,"N");//购买过
                    }else if (type.equals("buy_period")){
                        map1 = selectBuyPeriod(labelAttributeMap,"N");//购买周期
                    }else if (type.equals("add_integral")){
                        map1 = selectAddIntegral(labelAttributeMap,"N");//赚取积分
                    }else if (type.equals("consume_integral")){
                        map1 = selectConsumeIntegral(labelAttributeMap,"N");//消耗积分
                    }else if (type.equals("residue_integral")){
                        map1 = selectResidueIntegral(labelAttributeMap,"N");//剩余积分
                    }else if (type.equals("dis_store_activity")){
                        map1 = selectDisStoreActivity(labelAttributeMap,"N");//门店距离(高频活动)
                    }else if (type.equals("dis_store_address")){
                        map1 = selectDisStoreAddress(labelAttributeMap,"N");//门店距离(收货地址)
                    }else if (type.equals("dis_contend_store_activity")){
                        map1 = selectDisContendStoreActivity(labelAttributeMap,"N");//竞店距离(高频活动)
                    }else if (type.equals("dis_contend_store_address")){
                        map1 = selectDisContendStoreAddress(labelAttributeMap,"N");//竞店距离(收货地址)
                    }else if (type.equals("health_gao_xue_ya")){
                        map1 = selectHealth(labelAttributeMap,"N");//高血压
                    }else if (type.equals("health_gao_xue_zhi")){
                        map1 = selectHealth(labelAttributeMap,"N");//高血脂
                    }else if (type.equals("health_tang_niao_bing")){
                        map1 = selectHealth(labelAttributeMap,"N");//糖尿病
                    }
                    Map<String, Object> reMap = (Map<String, Object>)map1.get("returnMap");
                    Integer count = Integer.parseInt(String.valueOf(reMap.get("count")));
                    String labenName = String.valueOf(birthdayLabelMap.get("labelName"));
                    String label = labenName+"("+count+"人"+")";
                    birthdayLabelMap.put("count",count);
                    birthdayLabelMap.put("label",label);
                }
            }
            return baseList;
        } catch (Exception e) {
            log.info("抽方法：基础查询：可自定义--异常:{}", e);
            return baseList;
        }
    }


    //抽方法（成交金额、成交次数、客单价、赚取积分、消耗积分、退款率）
    public Map<String, Object> timeAndScopeMethod(Map<String, Object> params){
        Integer scope = Integer.parseInt(String.valueOf(params.get("scope")));
        params = timeAllMethod(params);//调公用方法
        if (scope == 1){
            String scopeMin = "0.01";
            String scopeMax = String.valueOf(params.get("max"));
            params.put("min",scopeMin);
            params.put("max",scopeMax);
        }else if (scope == 2){
            String scopeMin = String.valueOf(params.get("min"));
            Integer scopeMax = 99999999;
            params.put("min",scopeMin);
            params.put("max",scopeMax);
        }else if (scope == 3){
            String scopeMin = String.valueOf(params.get("min"));
            String scopeMax = String.valueOf(params.get("max"));
            params.put("min",scopeMin);
            params.put("max",scopeMax);
        }
        return params;
    }

    //抽方法（成交金额、成交次数、客单价、购买时段、未购买时段）
    public Map<String, Object> timeAllMethod(Map<String, Object> params){
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer time = Integer.parseInt(String.valueOf(params.get("time")));
        if (time == 1){
            Integer day = Integer.parseInt(String.valueOf(params.get("timeMin")));
            String timeMin = formatTime(day,2);
            String timeMax = getToday();
            params.put("timeMin",timeMin);
            params.put("timeMax",sdf2.format(new Date()));
        }else if (time == 2){
            String timeMin = String.valueOf(params.get("timeMin"));
            String timeMax = String.valueOf(params.get("timeMax"))+" 23:59:59";
            params.put("timeMin",timeMin);
            params.put("timeMax",timeMax);
        }else if (time == 3){
            params.put("timeMin","2015-01-01");
            params.put("timeMax",sdf2.format(new Date()));
        }

        return params;
    }

    //抽方法（购买周期、剩余积分）
    public Map<String, Object> scopeAllMethod(Map<String, Object> params){
        Integer time = Integer.parseInt(String.valueOf(params.get("day")));
        if (time == 1){
            Integer max = Integer.parseInt(String.valueOf(params.get("max")));
            params.put("max",max);
            params.put("min",0.01);
        }else if (time == 2){
            Integer min = Integer.parseInt(String.valueOf(params.get("min")));
            params.put("max",99999999);
            params.put("min",min);
        }else if (time == 3){
            String min = String.valueOf(params.get("min"));
            String max = String.valueOf(params.get("max"));
            params.put("min",min);
            params.put("max",max);
        }
        return params;
    }

    //抽方法（收货地址专用）
    public Map<String, Object> scopeAllMethodToAddress(Map<String, Object> params){
        Integer time = Integer.parseInt(String.valueOf(params.get("day")));
        if (time == 1){
            Integer max = Integer.parseInt(String.valueOf(params.get("max")));
            params.put("max",max);
            params.put("min",0);
        }else if (time == 2){
            Integer min = Integer.parseInt(String.valueOf(params.get("min")));
            params.put("max",99999999);
            params.put("min",min);
        }else if (time == 3){
            String min = String.valueOf(params.get("min"));
            String max = String.valueOf(params.get("max"));
            params.put("min",min);
            params.put("max",max);
        }
        return params;
    }

    //健康标签返回MAP
    public Map<String, Object> getIllMap(Integer count,String str){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> labelAttribute = new HashMap<>();
        if (str.equals("高血压")){
            labelAttribute.put("ill","health_gao_xue_ya_sure");
        }else if (str.equals("高血脂")){
            labelAttribute.put("ill","health_gao_xue_zhi_sure");
        }else if (str.equals("糖尿病")){
            labelAttribute.put("ill","health_tang_niao_bing_sure");
        }
        map.put("count",count);
        map.put("labelType","sure");
        map.put("labelAttribute", JSON.toJSONString(labelAttribute));
        return map;
    }


    //计算比例
    public Map<String, Object> formatRatio(Integer min,Map<String, Object> params,String s){
        Map<String, Object> map = new HashMap<>();
        if (s.equals("Y")){
            Integer allMemberCount = getMemberCount(params);
            Double d1 = Double.valueOf(min);
            Double d2 = Double.valueOf(allMemberCount);
            String ratio = "0.0";
            if (d2 != 0.0){
                DecimalFormat df = new DecimalFormat("#0.00");
                ratio = df.format(d1*100/d2);
            }
            map.put("ratio",ratio+" %");
            map.put("allMemberCount",allMemberCount);
        }
        map.put("count",min);
        return map;
    }

    //查询改商家下所有的会员数
    public Integer getMemberCount(Map<String, Object> params){
        return labelSecondMapper.getMemberCount(params);
    }

    //范围方法
    public Map<String, Object> scopeMethod(Map<String, Object> params,String str) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer max = null;
            Integer min = null;
            String scope = String.valueOf(params.get("scope"));
            if (scope.contains("大于")){
                String s = scope.substring(2,scope.lastIndexOf(str));
                min = Integer.parseInt(s);
            }else if (scope.contains("小于")){
                String s = scope.substring(2,scope.lastIndexOf(str));
                max = Integer.parseInt(s);
            }else if (scope.contains("—")){
                String[] split = scope.split("—");
                min = Integer.parseInt(split[0]);
                max = Integer.parseInt(split[1]);
            }
            map.put("max",max);
            map.put("min",min);
            return map;
        } catch (Exception e) {
            log.info("范围方法异常:{}", e);
            map.put("max",0);
            map.put("min",0);
            return map;
        }
    }

    //年龄转日期(年份)
    public String formatTime(Integer num,Integer type){
        Calendar calendar = Calendar.getInstance();
        Date d = new Date();
        if (type == 1){
            calendar.add(Calendar.YEAR, -num);
            d = calendar.getTime();
        }else if (type == 2){
            calendar.add(Calendar.DAY_OF_YEAR, -num);
            d = calendar.getTime();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(d);
        return date;
    }

    //获取今年的日期
    public String getToday(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    //初始化：给每个收货地址匹配坐标
    public String getPoint() {
        try {
            //查询所有的siteId
            List<Integer> siteIdList = labelSecondMapper.getSiteIdAll();
            for (Integer siteId : siteIdList){
                //根据siteId查询所有的地址
                List<Map<String,Object>> addressList = labelSecondMapper.getAddressList(siteId);//map:siteId,buyerId,address,
                if (addressList.size() > 0){
                    for (Map<String,Object> addressMap : addressList){
                        String address = String.valueOf(addressMap.get("address"));
                        Map<String, Object> returnMap = uodateAddressForGaode(address, addressMap);
                        if (!StringUtil.isEmpty(returnMap)){
                            addressMap.putAll(returnMap);
                            labelSecondMapper.updateAddressPoint(addressMap);
                        }
                    }
                }
            }
            return "Ok";
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            return "Error";
        }
    }
    public Map<String,Object> uodateAddressForGaode(String address,Map<String, Object> addressMap){
        Map<String,Object> map = new HashMap<>();
        try{
            if (!StringUtil.isEmpty(address)){
                //再地址前加上省市区
                String str = labelSecondMapper.getAdd(addressMap);
                str = str.replaceAll(",","");
                address = str +" "+ address;
                String s = OkHttpUtil.get("http://restapi.amap.com/v3/geocode/geo?key=137816363e19388eae9c693ebe9281ce&address=" + address);
                if (!StringUtil.isEmpty(s)){
                    Object parse = JSONObject.parse(s);
                    Map resultMap = JacksonUtils.json2map(JacksonUtils.obj2json(parse));
                    List list = (List)(resultMap.get("geocodes"));
                    if (list.size() > 0){
                        Map<String, Object> map1 = (Map<String, Object>)list.get(0);
                        String si = String.valueOf(map1.get("location"));
                        String[] split = si.split(",");
                        String lng = split[0];
                        String lat = split[1];
                        map.put("lng",lng);
                        map.put("lat",lat);
                    }
                }
            }
            return map;
        }catch (Exception e) {
            log.info("调用高德接口异常:{}", e);
            return map;
        }
    }

    //初始化地址表：查漏补缺
    public String getLou(){
        try{
            //查询所有没有经纬度的地址
            List<Map<String,Object>> addressList = labelSecondMapper.getLou();
            for (Map<String,Object> addressMap : addressList){
                String address = String.valueOf(addressMap.get("addr"));
                Map<String, Object> returnMap = uodateAddressForGaode(address, addressMap);
                if (!StringUtil.isEmpty(returnMap)){
                    addressMap.putAll(returnMap);
                    labelSecondMapper.updateAddressPointByLou(addressMap);
                }
            }
            return "OK";
        }catch (Exception e) {
            log.info("地址查漏补缺异常:{}", e);
            return "Error";
        }
    }

    //初始化：最后一次下单时间
    public String getEndOrderTime() {
        Map<String, Object> map = new HashMap<>();
        try {
            List<Integer> siteIdList = labelSecondMapper.getSiteIdAll();
            for (Integer siteId : siteIdList){
                //根据siteId查询所有下过单的会员
                List<Integer> buyerIdList = labelSecondMapper.getBuyerIdAll(siteId);
                if (buyerIdList.size() > 0){
                    for (Integer buyerId : buyerIdList){
                        //查询出每个会员的最后一次下单时间
                        String createTime = labelSecondMapper.getCreateTime(siteId,buyerId);
                        labelSecondMapper.updateEndOrderTime(siteId,buyerId,createTime);
                    }
                }
            }
            return "Ok";
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            return "Error";
        }
    }

    //初始化：将会员分组的新老数据兼容
    public String newCompatibleOld() {
        Map<String, Object> map = new HashMap<>();
        try {
            //从标签表中查询所有的siteId
            List<Integer> siteIdList = labelSecondMapper.getAllSiteId();
            for (Integer siteId : siteIdList){
                //查询商户下所有的会员标签
                List<String> strList = labelSecondMapper.getStrList(siteId);
                if (strList.size() > 0){
                    for (String strLabel : strList){
                        Map<String, Object> labelMap = JacksonUtils.json2map(strLabel);
                        List<String> sexList = (List<String>)labelMap.get("sex");//性别
                        if (sexList.size() > 0){
                            map.put("sex",sexList);
                        }else {
                            map.put("sex",new ArrayList<>());
                        }
                        List<String> ageList = (List<String>)labelMap.get("age");//年龄
                        if (ageList.size() > 0){
                            List<Map<String, Object>> age = new ArrayList<>();
                            for (String aget : ageList){
                                Map<String, Object> ageMap = new HashMap<>();
                                if ("不足18岁（少年）".equals(aget)) {
                                    ageMap.put("age",18);
                                    ageMap.put("type",1);
                                    age.add(ageMap);
                                }
                                if ("18-24岁（青少年）".equals(aget)) {
                                    ageMap.put("ageMin",18);
                                    ageMap.put("ageMax",24);
                                    ageMap.put("type",3);
                                    age.add(ageMap);
                                }
                                if ("25-34岁（青年）".equals(aget)) {
                                    ageMap.put("ageMin",25);
                                    ageMap.put("ageMax",34);
                                    ageMap.put("type",3);
                                    age.add(ageMap);
                                }
                                if ("35-49岁（中年）".equals(aget)) {
                                    ageMap.put("ageMin",35);
                                    ageMap.put("ageMax",49);
                                    ageMap.put("type",3);
                                    age.add(ageMap);
                                }
                                if ("50-59岁（中老年）".equals(aget)) {
                                    ageMap.put("ageMin",50);
                                    ageMap.put("ageMax",59);
                                    ageMap.put("type",3);
                                    age.add(ageMap);
                                }
                                if ("60岁以上（老年）".equals(aget)) {
                                    ageMap.put("age",60);
                                    ageMap.put("type",2);
                                    age.add(ageMap);
                                }
                            }
                            map.put("age",age);
                        }else {
                            map.put("age",new ArrayList<>());
                        }
                        map.put("birthday",new ArrayList<>());//生日
                        map.put("xingzuo",new ArrayList<>());//星座
                        map.put("shengxiao",new ArrayList<>());//生肖
                        map.put("register",new ArrayList<>());//注册时间
                        map.put("area",new ArrayList<>());//区域

                        //成交金额
                        if (!StringUtil.isEmpty(labelMap.get("successMoney"))){
                            String str = String.valueOf(labelMap.get("successMoney"));
                            List<Map<String,Object>> mapForOld = getMapForOld(str);
                            map.put("bargainMoney",mapForOld);//成交金额
                        }else {
                            map.put("bargainMoney",new ArrayList<>());
                        }
                        //成交次数
                        if (!StringUtil.isEmpty(labelMap.get("successCount"))){
                            String str = String.valueOf(labelMap.get("successCount"));
                            List<Map<String,Object>> mapForOld = getMapForOld(str);
                            map.put("bargainCount",mapForOld);//成交金额
                        }else {
                            map.put("bargainCount",new ArrayList<>());
                        }
                        //客单价
                        if (!StringUtil.isEmpty(labelMap.get("ticket"))){
                            String str = String.valueOf(labelMap.get("ticket"));
                            List<Map<String,Object>> mapForOld = getMapForOld(str);
                            map.put("preTransaction",mapForOld);//成交金额
                        }else {
                            map.put("preTransaction",new ArrayList<>());
                        }
                        //购买过
                        if (!StringUtil.isEmpty(labelMap.get("datePay"))){
                            List<Map<String,Object>> datePayListMap = new ArrayList<>();
                            Map<String,Object> smMap = new HashMap<>();
                            String str = String.valueOf(labelMap.get("datePay"));
                            String s = str.substring(1,str.length()-1);
                            Integer day = Integer.parseInt(s);
                            smMap.put("time",1);
                            smMap.put("timeMin",day);
                            datePayListMap.add(smMap);
                            map.put("everBuy",datePayListMap);
                        }else {
                            map.put("everBuy",new ArrayList<>());
                        }

                        map.put("refundProbability",new ArrayList<>());//退款率
                        map.put("buyPeriod",new ArrayList<>());//购买周期
                        map.put("addIntegral",new ArrayList<>());//获取积分
                        map.put("consumeIntegral",new ArrayList<>());//消耗积分
                        map.put("residueIntegral",new ArrayList<>());//剩余积分

                        map.put("disStoreActivity",new ArrayList<>());//门店距离（高频活动）
                        map.put("disStoreAddress",new ArrayList<>());//门店距离（收货地址）
                        map.put("disContendStoreActivity",new ArrayList<>());//竞店距离（高频活动）
                        map.put("disContendStoreAddress",new ArrayList<>());//竞店距离（收货地址）

                        //自定义标签
                        if (labelMap.containsKey("customLabel")){
                            if (!StringUtil.isEmpty(labelMap.get("customLabel"))){
                                List<String> customLabelList = (List<String>)labelMap.get("customLabel");
                                map.put("custom",customLabelList);
                            }
                        }

                    }
                }
            }
            return "OK";
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            return "Error";
        }
    }

    public List<Map<String,Object>> getMapForOld(String str){
        List<Map<String,Object>> smListMap = new ArrayList<>();
        Map<String,Object> smMap = new HashMap<>();
        String strPay = str.substring(0,4);
        if ("大于等于".equals(strPay)){
            String s = str.substring(4,str.length());
            smMap.put("time",3);
            smMap.put("scope",2);
            smMap.put("min",s);
            smListMap.add(smMap);
        }else if("小于等于".equals(strPay)){
            String s = str.substring(4,str.length());
            smMap.put("time",3);
            smMap.put("scope",1);
            smMap.put("max",s);
            smListMap.add(smMap);
        }else {
            String s = str.substring(2,str.length());
            String[] sPay = s.split("—");
            smMap.put("time",3);
            smMap.put("scope",3);
            smMap.put("min",sPay[0]);
            smMap.put("max",sPay[1]);
            smListMap.add(smMap);
        }
        return smListMap;
    }

    //抽方法（初始化：区域查询）
//    public List<Map<String, Object>> areaChou(List<Map<String, Object>> infoListMap,List<Map<String, Object>> addrListMap) {
//        List<Map<String, Object>> aList = new ArrayList<>();
//        try {
//            List<Map<String, Object>> cityListMap = new ArrayList<>();
//            if (infoListMap.size() > 0){
//                if (addrListMap.size() > 0){
//                    Set<Integer> intList = new HashSet<>();
//                    Set<Integer> infoList = new HashSet<>();
//                    List<Map<String, Object>> newList = new ArrayList<>();
//                    cityListMap.addAll(infoListMap);
//
//                    for (Map<String, Object> inMap : infoListMap){
//                        Integer bui = Integer.parseInt(String.valueOf(inMap.get("buyerId")));
//                        infoList.add(bui);
//                    }
//
//                    for (Integer buyerId : infoList){
//                        for (int j = 0; j < addrListMap.size(); j++){
//                            Integer o = 0;
//                            Integer addrBuyerId = Integer.parseInt(String.valueOf(addrListMap.get(j).get("buyerId")));
//                            if (buyerId.equals(addrBuyerId)){
////                                infoListMap.remove(j);
//                                intList.add(j);
////                                i--;
////                                break;
//                            }
//                        }
//                    }
//                    for (int j = 0 ;j<addrListMap.size() ;j++){
//                        if (!intList.contains(j)){
//                            newList.add(addrListMap.get(j));
//                        }
//                    }
//                    cityListMap.addAll(newList);
//                }else {
//                    cityListMap = infoListMap;
//                }
//            }else {
//                cityListMap = addrListMap;
//            }
//
//            //去重
//            Map<String, Object> smap = new HashMap<>();
//            if (cityListMap.size() > 0){
//                for (Map<String, Object> aMap : cityListMap){
//                    String city = String.valueOf(aMap.get("city"));
//                    smap.put(city,city);
//                }
//            }
//            for (String key : smap.keySet()){
//                Map<String, Object> dmap = new HashMap<>();
//                Integer count = 0;
//                String name = "";
//                for (Map<String, Object> cMap : cityListMap){
//                    String city1 = String.valueOf(cMap.get("city"));
//                    if (key.equals(city1)){
//                        name = String.valueOf(cMap.get("name"));
//                        count++;
//                    }
//                }
//                dmap.put("count",count);
//                dmap.put("city",key);
//                dmap.put("name",name);
//                aList.add(dmap);
//            }
//            return aList;
//        } catch (Exception e) {
//            log.info("查询门店距离异常:{}", e);
//            return aList;
//        }
//    }


    //抽方法（初始化：区域查询）
    public Map<String, Object> areaChou2(List<Map<String, Object>> infoListMap,List<Map<String, Object>> addrListMap) {
        List<Map<String, Object>> aList = new ArrayList<>();
        Map<String, Object> rMap = new HashMap<>();
        try {
            List<Map<String, Object>> cityListMap = new ArrayList<>();
            if (infoListMap.size() > 0){
                if (addrListMap.size() > 0){
                    Set<Integer> intList = new HashSet<>();
                    Set<Integer> infoList = new HashSet<>();
                    List<Map<String, Object>> newList = new ArrayList<>();
                    cityListMap.addAll(infoListMap);

                    for (Map<String, Object> inMap : infoListMap){
                        Integer bui = Integer.parseInt(String.valueOf(inMap.get("buyerId")));
                        infoList.add(bui);
                    }

                    for (Integer buyerId : infoList){
                        for (int j = 0; j < addrListMap.size(); j++){
                            Integer o = 0;
                            Integer addrBuyerId = Integer.parseInt(String.valueOf(addrListMap.get(j).get("buyerId")));
                            if (buyerId.equals(addrBuyerId)){
//                                infoListMap.remove(j);
                                intList.add(j);
//                                i--;
//                                break;
                            }
                        }
                    }
                    for (int j = 0 ;j<addrListMap.size() ;j++){
                        if (!intList.contains(j)){
                            newList.add(addrListMap.get(j));
                        }
                    }
                    cityListMap.addAll(newList);
                }else {
                    cityListMap = infoListMap;
                }
            }else {
                cityListMap = addrListMap;
            }

            //去重
            Map<String, Object> smap = new HashMap<>();
            if (cityListMap.size() > 0){
                for (Map<String, Object> aMap : cityListMap){
                    String city = String.valueOf(aMap.get("city"));
                    smap.put(city,city);
                }
            }
            for (String key : smap.keySet()){
                Map<String, Object> dmap = new HashMap<>();
                Integer count = 0;
                String name = "";
                for (Map<String, Object> cMap : cityListMap){
                    String city1 = String.valueOf(cMap.get("city"));
                    if (key.equals(city1)){
                        name = String.valueOf(cMap.get("cityname"));
                        count++;
                    }
                }
                dmap.put("count",count+"人");
                dmap.put("city",key);
                dmap.put("name",name);
                aList.add(dmap);
            }
            rMap.put("aList",aList);
            rMap.put("cityListMap",cityListMap);
            return rMap;
        } catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            return rMap;
        }
    }

    //初始化会员标签：给每个商户添加默认标签
    public String initLabelBySiteId(List<Integer> sList){

        try{
            List<Integer> siteIdList = new ArrayList<>();
            if (sList.size() == 0){
                //查询所有的siteId
                siteIdList = labelSecondMapper.getSiteIdAll();
            }else {
                siteIdList = sList;
            }

            List<Map<String,Object>> initList = new ArrayList<>();
            for (Integer siteId : siteIdList){
                initList.clear();
//            Integer siteId = 100190;
                //年龄标签:
                initList.add(getInitLabelByAge("1", "18", null, null, "少年", "age",siteId));
                initList.add(getInitLabelByAge("3", null, "18", "24", "青少年", "age",siteId));
                initList.add(getInitLabelByAge("3", null, "25", "34", "青年", "age",siteId));
                initList.add(getInitLabelByAge("3", null, "35", "49", "中年", "age",siteId));
                initList.add(getInitLabelByAge("3", null, "50", "59", "中老年", "age",siteId));
                initList.add(getInitLabelByAge("2", "60", null, null, "老年","age",siteId));

                //注册标签：
                initList.add(getInitLabelByRegister("1", "7", null, null, "近7天", "register",siteId));
                initList.add(getInitLabelByRegister("1", "30", null, null, "近30天", "register",siteId));
                initList.add(getInitLabelByRegister("1", "90", null, null, "近90天", "register",siteId));
                initList.add(getInitLabelByRegister("1", "180", null, null, "近180天", "register",siteId));
                initList.add(getInitLabelByRegister("1", "360", null, null, "近360天", "register",siteId));

                //成交金额
                initList.add(getInitLabelByBargainMoney("3", "1", null, "100", "低成交", "bargain_money",siteId));
                initList.add(getInitLabelByBargainMoney("3", "3", "100", "500", "中成交", "bargain_money",siteId));
                initList.add(getInitLabelByBargainMoney("3", "2", "500", null, "高成交", "bargain_money",siteId));

                //成交次数
                initList.add(getInitLabelByBargainMoney("3", "1", null, "3", "次数少", "bargain_count",siteId));
                initList.add(getInitLabelByBargainMoney("3", "3", "3", "10", "次数中等", "bargain_count",siteId));
                initList.add(getInitLabelByBargainMoney("3", "2", "10", null, "次数多", "bargain_count",siteId));

                //客单价
                initList.add(getInitLabelByBargainMoney("3", "1", null, "30", "低客单价", "pre_transaction",siteId));
                initList.add(getInitLabelByBargainMoney("3", "3", "30", "100", "中客单价", "pre_transaction",siteId));
                initList.add(getInitLabelByBargainMoney("3", "2", "100", null, "高客单价", "pre_transaction",siteId));

                //退款率
                initList.add(getInitLabelByBargainMoney("3", "1", null, "10", "低退款率", "refund_probability",siteId));
                initList.add(getInitLabelByBargainMoney("3", "3", "10", "20", "中退款率", "refund_probability",siteId));
                initList.add(getInitLabelByBargainMoney("3", "2", "30", null, "高退款率", "refund_probability",siteId));

                //购买时段
                initList.add(getInitLabelByEverBuy("1",  "7", null, "近7天", "ever_buy",siteId));
                initList.add(getInitLabelByEverBuy("1",  "30", null, "近30天", "ever_buy",siteId));
                initList.add(getInitLabelByEverBuy("1",  "90", null, "近90天", "ever_buy",siteId));
                initList.add(getInitLabelByEverBuy("1",  "180", null, "近180天", "ever_buy",siteId));
                initList.add(getInitLabelByEverBuy("1",  "360", null, "近360天", "ever_buy",siteId));

                //购买周期
                initList.add(getInitLabelByBuyPeriod("1",  null, "7", "购买周期短", "buy_period",siteId));
                initList.add(getInitLabelByBuyPeriod("3",  "7", "90", "购买周期中", "buy_period",siteId));
                initList.add(getInitLabelByBuyPeriod("2",  "90", null, "购买周期长", "buy_period",siteId));

                //赚取积分
                initList.add(getInitLabelByBargainMoney("3", "1", null, "100", "赚取积分少", "add_integral",siteId));
                initList.add(getInitLabelByBargainMoney("3", "3", "100", "500", "赚取积分中", "add_integral",siteId));
                initList.add(getInitLabelByBargainMoney("3", "2", "500", null, "赚取积分多", "add_integral",siteId));

                //消耗积分
                initList.add(getInitLabelByBargainMoney("3", "1", null, "100", "消耗积分少", "consume_integral",siteId));
                initList.add(getInitLabelByBargainMoney("3", "3", "100", "500", "消耗积分中", "consume_integral",siteId));
                initList.add(getInitLabelByBargainMoney("3", "2", "500", null, "消耗积分多", "consume_integral",siteId));

                //剩余积分
                initList.add(getInitLabelByBuyPeriod("1",  null, "100", "剩余积分少", "residue_integral",siteId));
                initList.add(getInitLabelByBuyPeriod("3",  "100", "500", "剩余积分中", "residue_integral",siteId));
                initList.add(getInitLabelByBuyPeriod("2",  "500", null, "剩余积分多", "residue_integral",siteId));

                //门店距离（高频活动）
                initList.add(getInitLabelByDisStoreActivity("1", "1", null, "1000", "90","近距离", "dis_store_activity",siteId));
                initList.add(getInitLabelByDisStoreActivity("1", "3", "1000", "6000", "90","中距离", "dis_store_activity",siteId));
                initList.add(getInitLabelByDisStoreActivity("1", "2", "6000", null, "90","远距离", "dis_store_activity",siteId));

                //门店距离（收货地址）
                initList.add(getInitLabelByBuyPeriod("1",  null, "1000", "近距离", "dis_store_address",siteId));
                initList.add(getInitLabelByBuyPeriod("3",  "1000", "6000", "中距离", "dis_store_address",siteId));
                initList.add(getInitLabelByBuyPeriod("2",  "6000", null, "远距离", "dis_store_address",siteId));

                //竞店距离（高频活动）
                initList.add(getInitLabelByDisStoreActivity("1", "1", null, "1000", "90","近距离", "dis_contend_store_activity",siteId));
                initList.add(getInitLabelByDisStoreActivity("1", "3", "1000", "6000", "90","中距离", "dis_contend_store_activity",siteId));
                initList.add(getInitLabelByDisStoreActivity("1", "2", "6000", null, "90","远距离", "dis_contend_store_activity",siteId));

                //竞店距离（收货地址）
                initList.add(getInitLabelByBuyPeriod("1",  null, "1000", "近距离", "dis_contend_store_address",siteId));
                initList.add(getInitLabelByBuyPeriod("3",  "1000", "6000", "中距离", "dis_contend_store_address",siteId));
                initList.add(getInitLabelByBuyPeriod("2",  "6000", null, "远距离", "dis_contend_store_address",siteId));

                //高血压
                initList.add(getInitLabelByIll("3",  "1", "2","health_gao_xue_ya", "低疑似", "health_gao_xue_ya",siteId));
                initList.add(getInitLabelByIll("3",  "3", "4","health_gao_xue_ya", "中疑似", "health_gao_xue_ya",siteId));
                initList.add(getInitLabelByIll("2",  "5", null,"health_gao_xue_ya", "高疑似", "health_gao_xue_ya",siteId));

                //高血脂
                initList.add(getInitLabelByIll("3",  "1", "2","health_gao_xue_zhi", "低疑似", "health_gao_xue_zhi",siteId));
                initList.add(getInitLabelByIll("3",  "3", "4","health_gao_xue_zhi", "中疑似", "health_gao_xue_zhi",siteId));
                initList.add(getInitLabelByIll("2",  "5", null,"health_gao_xue_zhi", "高疑似", "health_gao_xue_zhi",siteId));

                //糖尿病
                initList.add(getInitLabelByIll("3",  "1", "2","health_tang_niao_bing", "低疑似", "health_tang_niao_bing",siteId));
                initList.add(getInitLabelByIll("3",  "3", "4","health_tang_niao_bing", "中疑似", "health_tang_niao_bing",siteId));
                initList.add(getInitLabelByIll("2",  "5", null,"health_tang_niao_bing", "高疑似", "health_tang_niao_bing",siteId));

                //执行添加
                labelSecondMapper.initInsertLabel(initList);
            }

            return "OK";
        }catch (Exception e) {
            log.info("查询门店距离异常:{}", e);
            return "Error";
        }

    }

    //生成初始化标签:年龄
    public Map<String,Object> getInitLabelByAge(String type,String age,String ageMin,String ageMax,String labelName,String labelType,Integer siteId){
        Map<String,Object> map = new HashMap<>();
        map.put("type",type);
        if (!StringUtil.isEmpty(age)){
            map.put("age",age);
        }
        if (!StringUtil.isEmpty(ageMin)){
            map.put("ageMin",ageMin);
        }
        if (!StringUtil.isEmpty(ageMax)){
            map.put("ageMax",ageMax);
        }
        String labelAttribute = JSON.toJSONString(map);
        return getGroupLabel(labelAttribute,labelName,labelType,siteId);//将标签组合完整后返回
    }

    //生成初始化标签:注册时间
    public Map<String,Object> getInitLabelByRegister(String type,String day,String registerMin,String registerMax,String labelName,String labelType,Integer siteId){
        Map<String,Object> map = new HashMap<>();
        map.put("type",type);
        if (!StringUtil.isEmpty(day)){
            map.put("day",day);
        }
        if (!StringUtil.isEmpty(registerMin)){
            map.put("registerMin",registerMin);
        }
        if (!StringUtil.isEmpty(registerMax)){
            map.put("registerMax",registerMax);
        }
        String labelAttribute = JSON.toJSONString(map);
        return getGroupLabel(labelAttribute,labelName,labelType,siteId);
    }

    //生成初始化标签:成交金额，成交次数
    public Map<String,Object> getInitLabelByBargainMoney(String time,String scope,String min,String max,String labelName,String labelType,Integer siteId){
        Map<String,Object> map = new HashMap<>();
        map.put("time",time);
        map.put("scope",scope);
        if (!StringUtil.isEmpty(min)){
            map.put("min",min);
        }
        if (!StringUtil.isEmpty(max)){
            map.put("max",max);
        }
        String labelAttribute = JSON.toJSONString(map);
        return getGroupLabel(labelAttribute,labelName,labelType,siteId);
    }

    //生成初始化标签:购买时段
    public Map<String,Object> getInitLabelByEverBuy(String time,String timeMin,String timeMax,String labelName,String labelType,Integer siteId){
        Map<String,Object> map = new HashMap<>();
        map.put("time",time);
        if (!StringUtil.isEmpty(timeMin)){
            map.put("timeMin",timeMin);
        }
        if (!StringUtil.isEmpty(timeMax)){
            map.put("timeMax",timeMax);
        }
        String labelAttribute = JSON.toJSONString(map);
        return getGroupLabel(labelAttribute,labelName,labelType,siteId);
    }

    //生成初始化标签:购买周期
    public Map<String,Object> getInitLabelByBuyPeriod(String day,String min,String max,String labelName,String labelType,Integer siteId){
        Map<String,Object> map = new HashMap<>();
        map.put("day",day);
        if (!StringUtil.isEmpty(min)){
            map.put("min",min);
        }
        if (!StringUtil.isEmpty(max)){
            map.put("max",max);
        }
        String labelAttribute = JSON.toJSONString(map);
        return getGroupLabel(labelAttribute,labelName,labelType,siteId);
    }

    //生成初始化标签:门店距离
    public Map<String,Object> getInitLabelByDisStoreActivity(String time,String scope,String min,String max,String timeMin,String labelName,String labelType,Integer siteId){
        Map<String,Object> map = new HashMap<>();
        map.put("time",time);
        map.put("scope",scope);
        if (!StringUtil.isEmpty(min)){
            map.put("min",min);
        }
        if (!StringUtil.isEmpty(max)){
            map.put("max",max);
        }
        map.put("timeMin",timeMin);
        String labelAttribute = JSON.toJSONString(map);
        return getGroupLabel(labelAttribute,labelName,labelType,siteId);
    }

    //生成初始化标签:将康标签
    public Map<String,Object> getInitLabelByIll(String day,String min,String max,String ill,String labelName,String labelType,Integer siteId){
        Map<String,Object> map = new HashMap<>();
        map.put("day",day);
        if (!StringUtil.isEmpty(min)){
            map.put("min",min);
        }
        if (!StringUtil.isEmpty(max)){
            map.put("max",max);
        }
        map.put("ill",ill);
        String labelAttribute = JSON.toJSONString(map);
        return getGroupLabel(labelAttribute,labelName,labelType,siteId);
    }


    //组合标签
    public Map<String,Object> getGroupLabel(String labelAttribute,String labelName,String labelType,Integer siteId){
        Map<String,Object> groupLabelMap = new HashMap<>();
        groupLabelMap.put("labelAttribute",labelAttribute);
        groupLabelMap.put("labelName",labelName);
        groupLabelMap.put("labelType",labelType);
        groupLabelMap.put("siteId",siteId);
        //获取标签描述
        String labelDescribe = getLabelDescribe(groupLabelMap);
        groupLabelMap.put("labelDescribe",labelDescribe);
        return groupLabelMap;
    }


    //--------------------------------回访接口-------------------------------------------------

    /**
     * 按分组选择:查询会员ID数量
     * @param params
     * @return
     */

    public Map<String, Object> getMemberIdForVisitByPeopleCount(Map<String,Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            Set<String> memberIdForVisitByPeople = getMemberIdForVisitByPeople(params);
            Integer count = memberIdForVisitByPeople.size();
            map.put("count", count);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }

    /**
     * 按分组选择:查询会员ID
     * @param params
     * @return
     */

    public Map<String, Object> getMemberIdForVisitByPeopleIds(Map<String,Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            Set<String> memberIdForVisitByPeople = getMemberIdForVisitByPeople(params);
            String memberIds = org.apache.shiro.util.StringUtils.join(memberIdForVisitByPeople.iterator(),",");

            map.put("memberIds", memberIds);
            map.put("msg", "查询成功");
            map.put("status",0);
            return map;
        } catch (Exception e) {
            log.info("查询异常:{}", e);
            map.put("msg", "查询异常");
            map.put("status",-1);
            return map;
        }
    }


    //按分组选择:查询会员ID
    public Set<String> getMemberIdForVisitByPeople(Map<String,Object> params){
        Set<String> set = new HashSet<>();
        try {
            if (!StringUtil.isEmpty(String.valueOf(params.get("labelNames")))){
                String labelNames = String.valueOf(params.get("labelNames"));
                Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
                List<String> labelNameList = labelService.stringToList(labelNames);
                for (String labelName : labelNameList){
                    String memberIds = labelSecondMapper.memberIdsByNameAndSiteId(siteId,labelName);
                    if (!StringUtil.isEmpty(memberIds)){
                        Map<String,Object> memberIdsMap = JacksonUtils.json2map(memberIds);
                        String memberIdsStr = String.valueOf(memberIdsMap.get("userIds"));
                        if (!StringUtil.isEmpty(memberIdsStr) && !"null".equals(memberIdsStr)){
                            Set<String> memberIdsSet = getSet(memberIdsStr);
                            set.addAll(memberIdsSet);
                        }
                    }
                }
                return set;
            }else {
                return set;
            }
        } catch (Exception e) {
            return set;
        }
    }

    //String转Set
    public Set<String> getSet(String string){
        Set<String> set = new HashSet<>();
        if (!StringUtil.isEmpty(string)){
            String[] strings = string.split(",");
            set = new HashSet<>(Arrays.asList(strings));
            return set;
        }else {
            return set;
        }
    }



    //------------------------------------------------------------------------------------------




}

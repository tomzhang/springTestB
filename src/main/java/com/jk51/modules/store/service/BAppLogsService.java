package com.jk51.modules.store.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.SBAppLogs;
import com.jk51.modules.goods.service.SelectGoodsService;
import com.jk51.modules.persistence.mapper.BAppLogsMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 门店后台日志
 * 作者: zhangkuncheng
 * 创建日期: 2017/5/23
 * 修改记录:
 */
@Service
public class BAppLogsService {

    public static final Logger logger = LoggerFactory.getLogger(SRefundService.class);

    @Autowired
    private BAppLogsMapper bAppLogsMapper;
    @Autowired
    private SelectGoodsService service;

    /**
     * 查询门店后日志
     *
     * @return
     */
    public Map<String, Object> selective(Integer siteId, Integer store_id, String operatorName, Integer num, Integer size,String action,String startTime,String endTime,String content) {
        try {
            List<Map> logList = new ArrayList<>();
            Integer count;
            if (num == 0) {
                num = 1;
            }
            if (store_id == 0) {//查询商户后台操作日志
                count = bAppLogsMapper.selectMerchantCount(siteId, operatorName,action,startTime,endTime,content);
                logList = bAppLogsMapper.selectMerchantLog(siteId, operatorName,action,startTime,endTime, (num - 1) * size, size,content);
            } else {
                count = bAppLogsMapper.selectStoreLogCount(siteId, store_id, operatorName,action,startTime,endTime,content);
                logList = bAppLogsMapper.selectStoreLog(siteId, store_id, operatorName,action,startTime,endTime, (num - 1) * size, size,content);
            }
            if(CollectionUtils.isNotEmpty(logList)){
                String remark;
                StringBuilder remark2 = new StringBuilder();//重组后的备注信息
                String methodParm;
                String beforeParm;
                Map methodParmMap = new HashMap();
                Map beforeParmMap = new HashMap();
                Map goodsInfo = new HashMap();
                Map parms = new HashMap();
                for (Map map : logList) {
                    remark2.delete( 0, remark2.length() );
                    parms.put("siteId",siteId);

                    if("商品价格快速修改".equals(map.get("action"))){
                        remark = map.get("remark")+"";
                        if(remark.contains("修改前商品数据")){
                            methodParm = remark.split("修改前商品数据:")[0];
                            methodParm = methodParm.substring(methodParm.indexOf("{"),methodParm.indexOf("}")+1);
                            beforeParm = remark.split("修改前商品数据:")[1].split("请求方法")[0];

                            methodParmMap = JacksonUtils.json2map(methodParm);//修改的数据
                            beforeParmMap = JacksonUtils.json2map(beforeParm);//修改前的数据

                            parms.put("goodsId", methodParmMap.get("goodId"));
                            goodsInfo = (Map) service.getGoodsD(parms).get("goods");//获取商品信息
                            if (goodsInfo==null){
                                map.put("remark","该商品已被删除");
                                continue;
                            }

                            remark2.append(StringUtil.isEmpty(goodsInfo.get("comName"))?goodsInfo.get("drugName"):goodsInfo.get("comName"));
                            remark2.append("：修改前:市场价="+beforeParmMap.get("marketPrice")+" 现价="+beforeParmMap.get("shopPrice"));
                            remark2.append("--修改后:市场价="+methodParmMap.get("marketPrice")+" 现价="+methodParmMap.get("price"));

                            map.put("remark",remark2.toString());
                        }else {
                            methodParm = remark.split("方法参数:")[1];
                            methodParm = methodParm.substring(methodParm.indexOf("{"),methodParm.indexOf("}")+1);
                            methodParmMap = JacksonUtils.json2map(methodParm);//修改的数据
                            parms.put("goodsId", methodParmMap.get("goodId"));
                            goodsInfo = (Map) service.getGoodsD(parms).get("goods");//获取商品信息
                            if (goodsInfo==null){
                                map.put("remark","该商品已被删除");
                                continue;
                            }

                            remark2.append(StringUtil.isEmpty(goodsInfo.get("comName"))?goodsInfo.get("drugName"):goodsInfo.get("comName"));
                            remark2.append("修改:市场价="+methodParmMap.get("marketPrice")+" 现价="+methodParmMap.get("price"));
                            map.put("remark",remark2.toString());
                        }
                    }else if("商品修改".equals(map.get("action"))){
                        remark = map.get("remark")+"";
                        if(remark.contains("修改前商品数据")){
                            methodParm = remark.split("修改前商品数据:")[0];
                            methodParm = methodParm.substring(methodParm.indexOf("{"),methodParm.indexOf("}")+1);
                            beforeParm = remark.split("修改前商品数据:")[1].split("请求方法")[0];

                            methodParmMap = JacksonUtils.json2map(methodParm);//修改后的数据
                            beforeParmMap = JacksonUtils.json2map(beforeParm);//修改前的数据

                            parms.put("goodsId", StringUtil.isEmpty(methodParmMap.get("goodsId"))?
                                StringUtil.isEmpty(beforeParmMap.get("goodsId"))?""
                                    :beforeParmMap.get("goodsId"):methodParmMap.get("goodsId"));
                            goodsInfo = (Map) service.getGoodsD(parms).get("goods");//获取商品信息
                            if (goodsInfo==null){
                                map.put("remark","商品修改");
                                continue;
                            }

                            remark2.append("修改了商品：");
                            remark2.append(StringUtil.isEmpty(goodsInfo.get("comName"))?goodsInfo.get("drugName"):goodsInfo.get("comName"));

                            map.put("remark",remark2.toString());
                        }else {
                            methodParm = remark.split("方法参数:")[1];
                            if(!methodParm.endsWith("}")){
                                methodParm = methodParm.substring(0,methodParm.lastIndexOf(","));
                                if (methodParm.endsWith("\"")){
                                    methodParm+=" } ";
                                }else{
                                    methodParm+=" \"} ";
                                }
                            }
                            methodParm = methodParm.substring(methodParm.indexOf("{"),methodParm.indexOf("}")+1);
                            methodParmMap = JacksonUtils.json2map(methodParm);//修改的数据
                            parms.put("goodsId", methodParmMap.containsKey("goodsId")?methodParmMap.get("goodsId"):"");
                            goodsInfo = (Map) service.getGoodsD(parms).get("goods");//获取商品信息
                            if (goodsInfo==null){
                                map.put("remark","商品修改");
                                continue;
                            }

                            remark2.append("修改了商品：");
                            remark2.append(StringUtil.isEmpty(goodsInfo.get("comName"))?goodsInfo.get("drugName"):goodsInfo.get("comName"));
                            map.put("remark",remark2.toString());
                        }
                    }/*else if("主题数据更新".equals(map.get("action"))){
                        map.put("remark","更新了主题数据");
                        continue;
                    }*/else {
                        remark = map.get("remark")+"";
                        if(!remark.contains("方法参数:")){
                            map.put("remark",remark);
                            continue;
                        }
                        methodParm = remark.split("方法参数:")[1];
                        if(methodParm.contains(";IP")){
                            methodParm = methodParm.split(";IP")[0];
                        }
                        if(!methodParm.endsWith("}")){
                            methodParm = methodParm.substring(0,methodParm.lastIndexOf(","));
                            if (methodParm.endsWith("\"")){
                                methodParm+=" } ";
                            }else{
                                methodParm+=" \"} ";
                            }
                        }
                        methodParm = methodParm.substring(methodParm.indexOf("{"),methodParm.indexOf("}")+1);
                        if(methodParm.length()<=2){
                            remark2.append(map.get("action"));
                        }else {
                            remark2.append(methodParm);
                        }

                        map.put("remark",remark2.toString());
                    }
                }
            }
            return pageHelper(count, logList, size);
        }catch (Exception e){
            logger.error("解析商品日志错误{}",e);
            return null;
        }

    }

    public int insertSelective(SBAppLogs bAppLogs) {
        return bAppLogsMapper.insertSelective(bAppLogs);
    }

    public Map<String, Object> pageHelper(Integer count, List<Map> logList, Integer pageSize) {
        Map<String, Object> response = new HashMap<>();
        response.put("total", count);
        if (count % pageSize == 0) {
            response.put("pages", count / pageSize);
        } else {
            response.put("pages", count / pageSize + 1);
        }
        response.put("list", logList);
        response.put("pageSize", pageSize);
        return response;
    }
}

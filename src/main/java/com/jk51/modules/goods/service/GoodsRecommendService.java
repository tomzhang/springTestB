package com.jk51.modules.goods.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.modules.goods.mapper.GoodsRecommendMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

/**
 * Created by Administrator on 2018/6/29.
 */
@Service
public class GoodsRecommendService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private GoodsRecommendMapper goodsRecommendMapper;
    public Map<String,Object> getGoodsRecommendList(Map<String, Object> paramMap) {
        Object goodsNameObj = paramMap.get("goodsName");
        if (Objects.nonNull(goodsNameObj) && StringUtils.isNotBlank(goodsNameObj.toString())){
            String goodsName = goodsNameObj.toString().trim();
            paramMap.put("goodsName", "%" + goodsName + "%");
        }
        Object codeObj = paramMap.get("code");
        if (Objects.nonNull(codeObj) && StringUtils.isNotBlank(codeObj.toString())){
            String code = codeObj.toString().trim();
            paramMap.put("code", "%" + code + "%");
        }
        Object timeStartObj = paramMap.get("timeStart");
        if (Objects.nonNull(timeStartObj) && StringUtils.isNotBlank(timeStartObj.toString())){
            String timeStart =timeStartObj.toString().trim() + " 00:00:00";
            paramMap.put("timeStart", timeStart);
        }
        Object timeEndObj = paramMap.get("timeEnd");
        if (Objects.nonNull(timeEndObj) && StringUtils.isNotBlank(timeEndObj.toString())){
            String timeEnd =timeEndObj.toString().trim() + " 23:59:59";
            paramMap.put("timeEnd", timeEnd);
        }
        Integer page = Integer.parseInt(paramMap.get("page").toString());
        Integer pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
        PageHelper.startPage(page, pageSize);
        List<Map<String, Object>> goodsRecommendList = goodsRecommendMapper.getGoodsRecommendList(paramMap);
        PageInfo<Map<String, Object>> info = new PageInfo<>(goodsRecommendList);
        List<Map<String, Object>> list = info.getList();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("value", list);
        //总记录数
        resultMap.put("total", info.getTotal());
        resultMap.put("pages", info.getPages());
        resultMap.put("page", info.getPageNum());
        return resultMap;
    }
    /**
     * 推荐商品详情修改保存操作
     *
     * 入参原始状态（即修改之前的状态）statusC 0 1
     * 表中statusB 0 1
     *
     * statusC 0 -> statusB 0 允许修改
     * statusC 0 -> statusB 1 (若表中无该推荐商品或该推荐商品的状态为0，则允许修改；若表中有该推荐商品且状态为1，则不允许修改)
     * statusC 1 -> statusB 0 允许修改
     * statusC 1 -> statusB 1 允许修改
     *
     * @param paramMap
     * @return
     */
    public Map<String, Object> updateGoodsRecommendDetail(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //1.更新推荐商品详情之前，先判断该推荐商品状态是否是从关闭->启用，
        Object statusObj = paramMap.get("status");
        //原始状态，即没有修改之前的状态
        Object statusOriginalObj = paramMap.get("statusOriginal");
        //根据原始状态 和 表中的状态 进行比较并判断，是否可以修改
        if (Objects.isNull(statusOriginalObj) || StringUtils.isBlank(statusOriginalObj.toString())){
            resultMap.put("flag", 0);
            resultMap.put("message", "推荐商品修改之前的状态status为空！");
            return resultMap;
        }
        //修改----------根据原始状态statusOriginal和修改后的状态status决定是否更新推荐商品 ------start
        //若statusOriginal和status相等，则进行更新操作
        if (Objects.nonNull(statusOriginalObj) && Objects.nonNull(statusObj) && Integer.parseInt(statusOriginalObj.toString()) == Integer.parseInt(statusObj.toString())){
            Integer flag = goodsRecommendMapper.updateAgainGoodsRecommend(paramMap);
            resultMap.put("flag", flag);
            if (flag > 0)
                resultMap.put("message", "操作成功！");
            else
                resultMap.put("message", "操作失败！");
        }else if (Objects.nonNull(statusOriginalObj) && Objects.nonNull(statusObj) && Integer.parseInt(statusOriginalObj.toString()) != Integer.parseInt(statusObj.toString())){
            //若statusOriginal和status不相等，则根据情况决定是否进行更新操作
            if (1 == Integer.parseInt(statusOriginalObj.toString()) && 0 == Integer.parseInt(statusObj.toString())){
                //status : 0 关闭
                //2.若为关闭状态，则可以直接进行更新
                Integer flag = goodsRecommendMapper.updateAgainGoodsRecommend(paramMap);
                resultMap.put("flag", flag);
                if (flag > 0)
                    resultMap.put("message", "操作成功！");
                else
                    resultMap.put("message", "操作失败！");
            }else if (0 == Integer.parseInt(statusOriginalObj.toString()) && 1 == Integer.parseInt(statusObj.toString())){
                //status : 1 启用
                //3.若为开启状态，则查找并判断是否已经再次新增该推荐商品且状态为开启的状态，
                Map<String, Object> countMap = goodsRecommendMapper.getGoodsRecommendByGoodsId(paramMap);
                Object countNumObj = countMap.get("countNum");
                if (0 < Integer.parseInt(countNumObj.toString())){
                    //记录数为 ：1 表中已有该推荐商品，且状态为启用状态
                    //4.若查找有，但是，入参状态和表中的状态一致，则允许修改；
                    Object statusOb = countMap.get("status");
                    if (Objects.nonNull(statusOb) && Integer.parseInt(statusOb.toString()) == Integer.parseInt(statusOriginalObj.toString())){
                        Integer flag = goodsRecommendMapper.updateAgainGoodsRecommend(paramMap);
                        resultMap.put("flag", flag);
                        if (flag > 0)
                            resultMap.put("message", "操作成功！");
                        else
                            resultMap.put("message", "操作失败！");
                    }else {
                        // 4.1若不一致，则不允许更新详情，
                        resultMap.put("flag", 0);//flag ： 0 表该推荐商品已存在，且状态为启用状态
                        resultMap.put("message", "该推荐商品已存在，且状态为启用状态！请推荐其他商品！");
                    }
                }else {
                    //5.若查找没有，则允许更新详情
                    Integer flag = goodsRecommendMapper.updateAgainGoodsRecommend(paramMap);
                    resultMap.put("flag", flag);
                    if (flag > 0)
                        resultMap.put("message", "操作成功！");
                    else
                        resultMap.put("message", "操作失败！");
                }
            }else {
                resultMap.put("flag", 0);
                resultMap.put("message", "该推荐商品状态异常！");
            }
        }else {
            resultMap.put("flag", 0);
            resultMap.put("message", "该推荐商品状态异常！");
        }
        //修改----------根据原始状态statusOriginal和修改后的状态status决定是否更新推荐商品 ------end
        return resultMap;
    }
    //APP下单页，关联商品推荐
    // 增加需求（8-16），若关联下单商品下，存在关联的推荐商品，则优先推荐该下单商品下的推荐商品
    public ReturnDto getStartOfGoodsRecommendList2(Map<String, Object> paramMap) {
        Integer siteId = Integer.parseInt(paramMap.get("siteId").toString());
        //门店id
        Object storeIdObj = paramMap.get("storeId");
        //根据siteId和门店id获取推荐商品信息
        List<Map<String, Object>> containtNullGoodsRecommendInfoList = null;
        Integer flagTip = 0;
        containtNullGoodsRecommendInfoList = goodsRecommendMapper.getStartOfGoodsRecommendList(siteId, storeIdObj);
        if (CollectionUtils.isEmpty(containtNullGoodsRecommendInfoList)){
            flagTip = 1;
            containtNullGoodsRecommendInfoList = goodsRecommendMapper.getStartOfGoodsRecommendList2(siteId);
        }
        if (CollectionUtils.isEmpty(containtNullGoodsRecommendInfoList)){
            return ReturnDto.buildSuccessReturnDtoByMsg("暂无关联推荐商品！");
        }
        //过滤掉关联下单商品id为null的记录
        List<Map<String, Object>> notContaintNullGoodsRecommendInfoList = containtNullGoodsRecommendInfoList.stream().filter(gr -> {
            Object orderGoodsIdsObj = gr.get("orderGoodsIds");
            if (Objects.isNull(orderGoodsIdsObj) || StringUtils.isBlank(orderGoodsIdsObj.toString()))
                return false;
            return true;
        }).collect(Collectors.toList());
        //若过滤后的结果为null，表示，关联下单商品下无关联的推荐商品，则走之前的逻辑
        if (CollectionUtils.isEmpty(notContaintNullGoodsRecommendInfoList)){
            return getStartOfGoodsRecommendList(paramMap);
        }
        //存放关联推荐商品的集合
        List<Map<String, Object>> goodsRecommendList = new ArrayList<>();
        //遍历下单商品中的商品id，判断过滤后的集合中orderGoodsIds字段中是否包含该下单商品中的商品id，若包含，则存储该集合中的该条数据；若不包含，则不存储
        Object goodsIdsObj = paramMap.get("goodsIds");
        List<String> goodsIdsArrList = Arrays.asList(goodsIdsObj.toString().split(","));
        List<String> goodsIdsList = new ArrayList<>(goodsIdsArrList);
        goodsIdsList.stream().forEach(ogId -> {
            notContaintNullGoodsRecommendInfoList.stream().forEach(map -> {
                String orderGoodsIds = (String)map.get("orderGoodsIds");
                String[] goodsIdsList2 = orderGoodsIds.split(",");
                List<String> ogoodsIdsList = new ArrayList<>(Arrays.asList(goodsIdsList2));
                if (ogoodsIdsList.contains(ogId)){
                    goodsRecommendList.add(map);
                }
            });
        });
        //判断存储之后的集合是否为空，若为空，则走之前的逻辑
        if (CollectionUtils.isEmpty(goodsRecommendList)){
            return getStartOfGoodsRecommendList(paramMap);
        }
        //先去除推荐商品中包含，准备提交订单中的商品的id
        List<Map<String, Object>> goodsRecommendList2 = removeRepeatGoodsIds(goodsIdsList, goodsRecommendList);
        //若去除推荐商品和下单商品相同的商品id后，结果为空，则走之前的逻辑
        if (CollectionUtils.isEmpty(goodsRecommendList2)){
            return getStartOfGoodsRecommendList(paramMap);
        }
        //若结果不为空，则根据存储之后的集合判断推荐商品的权重是否相同，若相同，则随机推荐一个商品
        //若不相同，则根据权重进行分组，若分组后权重优先级最高的推荐商品只有一个，则直接推荐
        //若分组后权重优先级最高的推荐商品有多个，则随机推荐一个商品

        Boolean flag = isEqual(goodsRecommendList2);
        return judgeWeight(siteId, storeIdObj, goodsRecommendList2, flagTip, flag);

    }

    ////跟据b_goods_recommend表中的weight权重字段进行排序，并推荐weight权重字段值最高的商品进行推荐，若权重值相等则随机推荐
    /**
     * APP下单页 -- 根据权重大小，进行商品的推荐 Integer siteId, Object goodsIdsObj
     * @param paramMap
     * @return
     */
    public ReturnDto getStartOfGoodsRecommendList(Map<String, Object> paramMap) {
        Integer siteId = Integer.parseInt(paramMap.get("siteId").toString());
        //门店id
        Object storeIdObj = paramMap.get("storeId");
        //APP下单中，即将要下单的商品的id
        Object goodsIdsObj = paramMap.get("goodsIds");
        //准备提交订单的商品ids
        List<String> goodsIdsArrList = Arrays.asList(goodsIdsObj.toString().split(","));
        List<String> goodsIdsList = new ArrayList<>(goodsIdsArrList);
        LocalDate localDate = LocalDate.now();
        paramMap.put("localDate", localDate.toString());
        //一、首先，获取status为1的推荐商品集合，（优先获取指定门店的推荐商品信息，添加查询参数storeId，若storeId不为空，则查询该指定门店下的推荐商品，若为空，则查询该商户下的所有推荐商品）
        List<Map<String, Object>> startGoodsRecommendList = null;
        Integer tip = 0;//添加标识tip，tip:0表示是从指定门店下查询的推荐商品的结果
        startGoodsRecommendList = goodsRecommendMapper.getStartOfGoodsRecommendList(siteId, storeIdObj);
        if (CollectionUtils.isEmpty(startGoodsRecommendList)){
            tip = 1;//tip:1表示是从该商户下查询的推荐商品的结果
            startGoodsRecommendList = goodsRecommendMapper.getStartOfGoodsRecommendList2(siteId);
        }
        if (CollectionUtils.isEmpty(startGoodsRecommendList)) {
            return ReturnDto.buildSuccessReturnDtoByMsg("暂无关联推荐商品！");
        }
        //------添加逻辑，若推荐商品只有一件，且同一天同一个用户两次在该门店购买药品，第二次购买时也推荐------start
        if (1 == startGoodsRecommendList.size()){
            Map<String, Object> goodsInfo = getGoodsInfo(siteId, storeIdObj, startGoodsRecommendList.get(0), tip);
            return ReturnDto.buildSuccessReturnDto(goodsInfo);
        }
        //------添加逻辑，若推荐商品只有一件，且同一天同一个用户两次在该门店购买药品，第二次购买时也推荐------end
        /*推过之后，不去除，随机推//该会员同一天推荐了哪些关联推荐商品
        List<Map<String, Object>> goodsRecommendRecordsMap = goodsRecommendMapper.getGoodsRecommendRecords(paramMap);
        //若已经推荐了某个商品，则将该推荐过的商品加入到将要下单的商品id集合中，在接下的代码中进行过滤
        if (CollectionUtils.isNotEmpty(goodsRecommendRecordsMap)){
            goodsRecommendRecordsMap.stream().forEach(map -> {
                Object goodsId = map.get("goodsId");
                goodsIdsList.add(goodsId.toString());
            });
        }*/
        //先去除推荐商品中包含，准备提交订单中的商品的id
        startGoodsRecommendList = removeRepeatGoodsIds(goodsIdsList, startGoodsRecommendList);
        //对过滤处理后的推荐商品，进行二次判断是否非空
        if (CollectionUtils.isEmpty(startGoodsRecommendList))
            return ReturnDto.buildSuccessReturnDtoByMsg("暂无关联推荐商品！");
        //二、其次，判断是否权重都相同
        Boolean flag = isEqual(startGoodsRecommendList);
        return judgeWeight(siteId, storeIdObj, startGoodsRecommendList, tip, flag);
    }

    private ReturnDto judgeWeight(Integer siteId, Object storeIdObj, List<Map<String, Object>> startGoodsRecommendList, Integer tip, Boolean flag) {
        //根据权重排序后的集合
        List<Map<String, Object>> sortList = new ArrayList<Map<String, Object>>();
        if (flag){
            //三、若权重都相同，则随机选择一个商品进行推荐
            return getRandomGoodsRecommendInfo(siteId, storeIdObj, startGoodsRecommendList, tip);
        }else {
            //四、若权重不都相同，则按权重进行分组
            sortList = sortOfRailyBrosing(startGoodsRecommendList);
            if (1 == sortList.size()){
                //五、若最高的一组中只有一个商品，则直接推荐该商品
                Map<String, Object> goodsInfo = getGoodsInfo(siteId, storeIdObj, sortList.get(0), tip);
                return ReturnDto.buildSuccessReturnDto(goodsInfo);
            }else if (1 < sortList.size()){
                //六、若最高的一组中有多个商品，则随机选择一个商品进行推荐
                return getRandomGoodsRecommendInfo(siteId, storeIdObj, sortList, tip);
            }else {
                return ReturnDto.buildFailedReturnDto("关联推荐商品数据异常！");
            }
        }
    }

    //获取商品信息，map最终为，商品信息和推荐商品信息的集合
    private Map<String, Object> getGoodsInfo(Integer siteId, Object storeIdObj, Map<String, Object> map, Integer tip) {
        Map<String, Object> goodsMap = new HashMap<>();
        //获取商品的商品名称
        if (1 == tip){
            goodsMap = goodsRecommendMapper.getGoodsNameBySiteIdAndGoodsId(siteId, Integer.parseInt(map.get("goodsId").toString()));
        }else {
            goodsMap = goodsRecommendMapper.getGoodsNameBySiteIdAndGoodsIdAndStoreId
                (siteId, Integer.parseInt(map.get("goodsId").toString()), Integer.parseInt(storeIdObj.toString()));
        }
        //添加逻辑，若指定门店的商品中没有推荐商品，就返回该商品下的推荐商品------start
        if ((Objects.isNull(goodsMap) || goodsMap.size() <= 0) && tip == 0){
            goodsMap = goodsRecommendMapper.getGoodsNameBySiteIdAndGoodsId(siteId, Integer.parseInt(map.get("goodsId").toString()));
        }
        //添加逻辑，若指定门店的商品中没有推荐商品，就返回该商品下的推荐商品------end
        if (Objects.nonNull(goodsMap) && goodsMap.size() > 0){
            map.put("goodsName", goodsMap.get("goodsName"));
            map.put("goodsName2", goodsMap.get("goodsName2"));
            Object companyPriceObj = goodsMap.get("companyPrice");
            if (Objects.nonNull(companyPriceObj)){
                Integer companyPrice = (Integer)companyPriceObj;
                map.put("companyPrice", String.format("%.2f", companyPrice / 100f));
            }else {
                map.put("companyPrice", companyPriceObj);
            }
            Object storePriceObj = goodsMap.get("storePrice");
            if (Objects.nonNull(storePriceObj)){
                if (storePriceObj instanceof Integer){
                    Integer storePrice = (Integer)storePriceObj;
                    map.put("storePrice", String.format("%.2f", storePrice / 100f));
                }else if (storePriceObj instanceof Long){
                    Long storePrice = (Long)storePriceObj;
                    map.put("storePrice", String.format("%.2f", storePrice / 100f));
                }
            }else {
                map.put("storePrice", storePriceObj);
            }
            map.put("flag", 1);//1：表示该指定门店或该商户下，存在该推荐商品
        }else {
            map.put("goodsName", "");
            map.put("goodsName2", "");
            map.put("companyPrice", "");
            map.put("storePrice", "");
            map.put("flag", 0);//0：表示该指定门店或该商户下，不存在该推荐商品
        }
        return map;
    }

    ////先去除推荐商品中包含，准备提交订单中的商品的id
    private List<Map<String, Object>> removeRepeatGoodsIds(List<String> goodsIdsList, List<Map<String, Object>> startGoodsRecommendList) {
//        List<Map<String, Object>> goodsRecommendList = new ArrayList<>();
        //遍历推荐商品集合，并去除和goodsIdsList集合中相同的goodsIds
        List<Map<String, Object>> goodsRecommendList = startGoodsRecommendList.stream().filter(map -> {
            Object goodsIdObj = map.get("goodsId");
            if (Objects.nonNull(goodsIdObj) && StringUtils.isNotBlank(goodsIdObj.toString()))
                if (goodsIdsList.contains(goodsIdObj.toString()))
                    return false;
                else
                    return true;
            else
                return false;
        }).collect(Collectors.toList());
        return goodsRecommendList;
    }

    //权重相同的情况下，随机选择一个商品进行推荐
    private ReturnDto getRandomGoodsRecommendInfo(Integer siteId, Object storeIdObj, List<Map<String, Object>> goodsRecommendList, Integer tip) {
        Random random = new Random();
        int randomNum = random.nextInt(goodsRecommendList.size());
        Map<String, Object> randomMap = goodsRecommendList.get(randomNum);
        //goodsInfo最终为，商品信息和推荐商品信息的集合
        Map<String, Object> goodsInfo = getGoodsInfo(siteId, storeIdObj, randomMap, tip);
        return ReturnDto.buildSuccessReturnDto(goodsInfo);
    }
    //根据权重进行降序排序
    private List<Map<String,Object>> sortOfRailyBrosing(List<Map<String, Object>> startGoodsRecommendList) {
        //1.先 分组
        Map<Integer, List<Map<String, Object>>> collectMap = startGoodsRecommendList.stream().collect(Collectors.groupingBy(GoodsRecommendService::comparingByWeight));
        //存放分组后的map的key
        List<Integer> sortList = new ArrayList<Integer>();
        //将分组后的key，放入sortList集合中
        collectMap.entrySet().stream().forEach(e -> sortList.add(e.getKey()));
        //2.对 分组后的序号key进行降序排序
        List<Integer> afterSortList = sortList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        //3.返回，分组降序排序后的，第一个map值,（即 权重值最高的一个组）
        List<Map<String, Object>> afterSort = collectMap.get(afterSortList.get(0));
        return afterSort;
    }
    //获取推荐商品权重
    private static Integer comparingByWeight(Map<String, Object> map){
        return (Integer)map.get("weight");
    }
    //判断集合中的权重是否相等
    private Boolean isEqual(List<Map<String, Object>> startGoodsRecommendList) {
        Boolean flagSuccess = null;
        Boolean flagFail = null;
        for (Map map :startGoodsRecommendList) {
            Object dailyBrowsingObj = map.get("weight");
            for (Map m :startGoodsRecommendList) {
                Object oObj = m.get("weight");
                if (Integer.parseInt(dailyBrowsingObj.toString()) == Integer.parseInt(oObj.toString()))
                    flagSuccess = true;
                else{
                    flagFail = true;//只要有一个不相等即为不相等
                    return false;
                }
            }
        }
        if ((null != flagSuccess && flagSuccess && null == flagFail))
            return true;
        return false;
    }
}

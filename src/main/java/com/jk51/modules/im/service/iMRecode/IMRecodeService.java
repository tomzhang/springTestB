package com.jk51.modules.im.service.iMRecode;

import com.github.binarywang.java.emoji.EmojiConverter;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BIMService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.mapper.YbGoodsMapper;
import com.jk51.modules.im.service.wechatUtil.WechatInfo;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.im.controller.request.IMRelationRequest;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.mapper.ImRecodeMapper;
import com.jk51.modules.im.service.iMRecode.response.*;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.merchant.service.LabelService;
import com.jk51.modules.persistence.mapper.BTradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;


import static com.jk51.modules.appInterface.util.RLMsgType.EVALUATR;
import static com.jk51.modules.im.util.MsgType.SEND_GOODS;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-23
 * 修改记录:
 */
@Service
public class IMRecodeService {


    @Autowired
    private ImRecodeMapper imRecodeMapper;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private BMemberMapper bMemberMapper;
    @Autowired
    private BTradesMapper bTradesMapper;
    @Autowired
    private BStoresMapper bStoresMapper;
    @Autowired
    private BIMServiceMapper bimServiceMapper;
    @Autowired
    private WechatUtil wechatUtil;
    @Autowired
    private LabelService labelService;
    @Autowired
    private GoodsMapper goodsMapper;

    private Logger logger = LoggerFactory.getLogger(IMRecodeService.class);

    private static EmojiConverter emojiConverter = EmojiConverter.getInstance();

    /**
     * 查询聊天记录
     * @param site_id required
     *@param store_admin_id required
     * @param buyer_id required
     *
     */
    public ReturnDto queryIMRecode(int site_id,int store_admin_id, int buyer_id){

        List<IMRecode> imRecodes =  imRecodeMapper.findByStoreAdminIdAndBuyerId(site_id,store_admin_id,buyer_id);
        if(StringUtil.isEmpty(imRecodes)){
            return ReturnDto.buildFailedReturnDto("没有数据");
        }

        return ReturnDto.buildSuccessReturnDto(imRecodes);
    }

    /**
     * 查询店员
     * @param site_id required
     * @param name 模糊查询
     * */
    public ReturnDto queryClerkList(int site_id, String name){

        List<Clerk> clerks = storeAdminMapper.findBySiteIdAndName(site_id,name);
        if(StringUtil.isEmpty(clerks)){
            return ReturnDto.buildFailedReturnDto("没有数据");
        }

        return ReturnDto.buildSuccessReturnDto(clerks);
    }


    /**
     * 查询会员
     * @param site_id required
     * @param mobile 模糊查询
     * */
    public ReturnDto queryMemberList(int site_id, String mobile){

        List<Member> members = bMemberMapper.findBySiteIdAndMobile(site_id,mobile);
        if(StringUtil.isEmpty(members)){
            return ReturnDto.buildFailedReturnDto("没有数据");
        }

        return ReturnDto.buildSuccessReturnDto(members);
    }


    /**
     *查询聊天关系
     * @param imRelationRequest
     * */
    public ReturnDto queryIMRelation(IMRelationRequest imRelationRequest){

        List<Clerk> clerks = imRecodeMapper.findClerkList(imRelationRequest);
        List<Member> members = imRecodeMapper.findMemberList(imRelationRequest);

        if(StringUtil.isEmpty(clerks)||StringUtil.isEmpty(members)){
            return ReturnDto.buildFailedReturnDto("没有数据");
        }

        IMRelation imRelation = new IMRelation();
        imRelation.setClerkList(clerks);
        imRelation.setMemberList(members);

        return ReturnDto.buildSuccessReturnDto(imRelation);
    }


    /**
     * 会员、店员每次查询10天记录
     *
     * @param site_id required
     * @param buyer_id required
     * @param store_admin_id
     * @param create_time  格式 "2017-06-26 10:43:43"
     * */
    public ReturnDto queryIMRecodeTop10(Integer site_id,Integer store_admin_id, Integer buyer_id,String create_time){

        List<IMRecode> imRecodes =  imRecodeMapper.queryIMRecodeTop10(site_id,store_admin_id,buyer_id,create_time);
        if(StringUtil.isEmpty(imRecodes)){
            return ReturnDto.buildFailedReturnDto("没有数据");
        }

        return ReturnDto.buildSuccessReturnDto(parseGoodsInfo(imRecodes));
    }

    //将聊天信息中的商品id转换为商品信息
    public List<IMRecode> parseGoodsInfo(List<IMRecode> imRecodes){

        for(IMRecode im:imRecodes){

            if(im.getMsg_type()==SEND_GOODS.getIndex()&&im.getSender().equals("app")){

                //消息类型为推荐商品时，解析商品信息，添加到msg中
                parseGoodInfo(im);

            }else if(im.getMsg_type()==EVALUATR || im.getMsg_type()== 23){

                //消息类型为评价时，解析评价信息，添加到msg中
                parseEvaluatr(im);

            }

            im.setMsg(emojiConverter.toUnicode(StringUtil.isEmpty(im.getMsg())?"":im.getMsg()));  //emoji 存储的别名转成Unicode
        }

        return imRecodes;
    }

    private void parseEvaluatr(IMRecode im){


        boolean msgIsNotEmpry = !StringUtil.isEmpty(im.getMsg());
        if(msgIsNotEmpry && StringUtil.isNumber(im.getMsg())){
            BIMService bimService = bimServiceMapper.selectByPrimaryKey(Integer.parseInt(im.getMsg()));

            Evaluatr evaluatr = new Evaluatr();
            evaluatr.setEvaluate(bimService.getEvaluate());
            evaluatr.setImServiceId(Integer.parseInt(im.getMsg()));

            try {
                im.setMsg(JacksonUtils.obj2json(evaluatr));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("evaluatr 转Jason失败，evaluatr：{},报错信息，{}",evaluatr,e);
            }
        }
    }


    private void parseGoodInfo(IMRecode im){

        Set<Integer> ids = parseGoodsId(im.getMsg());
        if(!StringUtil.isEmpty(ids)){

            List<GoodsInfo> goodsInfos = goodsMapper.findGoodsInfo(ids,im.getSiteId());
            try {
                im.setMsg(JacksonUtils.obj2json(goodsInfos));
            } catch (Exception e) {
                logger.error("goodsInfos 转Jason失败，goodsInfos：{}",goodsInfos);
                im.setMsg("[]");
            }
        }else {
            im.setMsg("[]");
        }
    }

    private Set<Integer> parseGoodsId(String idStr){

       Set<Integer> result = new HashSet<>();
       String[] strs =  idStr.split(",");
       for(String str:strs){

           if(StringUtil.isNumber(str)){
               result.add(Integer.parseInt(str));
           }
       }

       return result;

    }

    public ReturnDto queryMemberInfo(Integer siteId, Integer buyerId,Integer storeadminId) {

        StatMemberTrades stat = bTradesMapper.statMemberTrades(siteId,buyerId);
        WechatInfo wechatInfo = wechatUtil.getWechatInfo(siteId,buyerId);
        stat.setWechatInfo(wechatInfo);

        Map<String,Object> labels = labelService.getLable(siteId,buyerId,storeadminId);
        stat.setLabels(labels);

        if(stat != null){
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            if(Double.parseDouble(stat.getTotalFee())!=0){
                stat.setTotalFee(df.format(Double.parseDouble(stat.getTotalFee())));
            }
            if(Double.valueOf(stat.getAvgFee())!=0){
                stat.setAvgFee(df.format(Double.valueOf(stat.getAvgFee())));
            }
        }

        return ReturnDto.buildSuccessReturnDto(stat);
    }


    public ReturnDto queryStore(Integer site_id) {

        List<Map<String,String>> bStores =  bStoresMapper.findBySiteId(site_id);
        if(StringUtil.isEmpty(bStores)){
            return ReturnDto.buildFailedReturnDto("查询数据为空");
        }

        return ReturnDto.buildSuccessReturnDto(bStores);
    }

    public ReturnDto queryClerkAndJoinImClerkNum(Integer site_id, String end_day) {

        Integer clerkNum = storeAdminMapper.findBySiteNum(site_id);
        Integer joinIMClerkNum = bimServiceMapper.findJoinIMClerkNum(site_id,end_day);
        Map<String,Integer> map = new HashMap<>();
        map.put("clerkNum",clerkNum);
        map.put("joinIMClerkNum",joinIMClerkNum);
        return ReturnDto.buildSuccessReturnDto(map);

    }




}

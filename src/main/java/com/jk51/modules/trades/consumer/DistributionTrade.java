package com.jk51.modules.trades.consumer;

import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Goods;
import com.jk51.model.distribute.*;
import com.jk51.model.order.Member;
import com.jk51.model.order.Orders;
import com.jk51.model.order.Trades;
import com.jk51.modules.distribution.mapper.*;
import com.jk51.modules.distribution.result.RefereeList;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RunMsgWorker(queueName = "OrderTopic")
public class DistributionTrade implements MessageWorker{
    public static final Logger logger = LoggerFactory.getLogger(DistributionTrade.class);
    public static final String topicName = "OrderTopic";
    public static final  Integer rootId=Integer.MAX_VALUE-1; //顶级分销商

    @Autowired
    TradesMapper tradesMapper;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    GoodsDistributeMapper goodsDistributeMapper;
    @Autowired
    RewardTemplateMapper rewardTemplateMapper;
    @Autowired
    RewardMapper rewardMapper;
    @Autowired
    DistributeOrderService distributeOrderService;
    @Autowired
    RecruitMapper recruitMapper;
    @Autowired
    DistributorMapper distributorMapper;
    @Autowired
    DistributorExtMapper distributorExtMapper;
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    RefereeListMapper refereeListMapper;
    @Autowired
    DFundsMapper fundsMapper;

    @Override
    public void consume(Message message) throws Exception {
        try {
            // 处理消息
            String content = message.getMessageBodyAsString();
            logger.info("开始处理分销订单 {}", content);

            Map<String, Object> tradesMsg = JacksonUtils.json2map(content);
            logger.info(tradesMsg.get("type").toString());
            if (TradeMsgType.TRADES_CREATE.toString().equals(tradesMsg.get("type"))) {
                this.handleCreateOrderMsg(tradesMsg);
            } else if (TradeMsgType.TRADES_REFUND.toString().equals(tradesMsg.get("type"))) {
                this.handleRefundSuccess(tradesMsg);
            } else if (TradeMsgType.TRADES_FINISH.toString().equals(tradesMsg.get("type"))) {
                this.handleTradeEnd(tradesMsg);
            }
        } catch (Exception e) {
            logger.error("处理分销消息异常 {} {}", message, e);
            throw e;
        }
    }

    /**
      * 订单结束处理
      *
      * @param tradesMsg
      */
    private void handleTradeEnd(Map<String, Object> tradesMsg) {
        List<Long> ids = (List<Long>) tradesMsg.get("tradesIds");
        List<Trades> trades = new ArrayList<>();
        ids.stream().forEach(tradesId -> {
            trades.add(this.tradesMapper.getTradesByTradesId(tradesId));
        });
        trades.stream().filter(trade -> null != trade.getId() && 0 != trade.getDistributorId())
                .forEach(trade -> this.rewardMapper.updateOrderStatus(trade.getTradesId(), trade.getSiteId(),
                                                                      trade.getDistributorId(), 2));

    }

    /**
     * 获取能够达到的等级
     *
     * @param currentLevel 当前等级
     * @param order_price  分销商品总金额
     * @param rule         招募规则
     * @return Integer 能达到的最高等级
     */
    private Integer getReachedLevel(Integer currentLevel, long order_price, Map rule) {
        if (null != rule.get("level" + (currentLevel + 1)) && order_price >= (Integer) rule.get("level" + (currentLevel + 1))) {
            //存在比现在更高的等级,并且达到这一等级需要购买的金额
            return  this.getReachedLevel(currentLevel + 1, order_price, rule);
        }
        return currentLevel;
    }

    /**
     * 生成邀请码方法
     * @return FE:ab1234
     */
    public String createVerificationCode() {
        String[] verificationNumCodeArrary = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        String[] verificationLetterCodeArrary = {
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
        };
        String verificationCode = "";
        Random random = new Random();
        for(int i = 0; i < 2; i++){
            verificationCode += verificationLetterCodeArrary[random.nextInt(verificationLetterCodeArrary.length)];
        }
        for (int i = 0; i < 4; i++) {
            verificationCode += verificationNumCodeArrary[random.nextInt(verificationNumCodeArrary.length)];
        }
        return verificationCode;
    }

    //生成唯一邀请码
    public String getCode(String siteId){
        String validateCode = "";
        List<String> codeList=distributorExtMapper.selectCode(siteId);
        boolean continueFlag = true;
        String code=createVerificationCode();
        while (continueFlag){
            if(!codeList.contains(code)){
                validateCode=code;
                continueFlag=false;
            }else {
                code=createVerificationCode();
            }
        }
        return validateCode;
    }

    /**
     * 订单退款成功处理分佣状态
     *
     * @param tradesMsg
     */
    private void handleRefundSuccess(Map<String, Object> tradesMsg) {
        Long tradesId = (Long) tradesMsg.get("tradesId");//获取交易号
        Trades trades = this.tradesMapper.getTradesByTradesId(tradesId);//获取交易信息
        if (Integer.MAX_VALUE != trades.getDistributorId()){
            this.rewardMapper.updateOrderStatus(
                    trades.getTradesId(), trades.getSiteId(), trades.getDistributorId(), 0);
        }
    }

    /**
     * 订单支付成功处理分佣状态
     */
    public void handlePaySuccess(@NotNull Trades trades) throws Exception {
        Objects.requireNonNull(trades);
        Distributor distributor = this.distributorMapper.selectByUid(trades.getBuyerId(),trades.getSiteId());
        logger.info("{} 处理支付成功 分销商信息:{}", trades.getTradesId(), distributor);
        if (Integer.MAX_VALUE != trades.getDistributorId()){
            this.rewardMapper.updateOrderStatus(trades.getTradesId(), trades.getSiteId(), trades.getDistributorId(), 1);
        }

        if (null != distributor) {
            List<RefereeList> refereeLists = refereeListMapper.selectRefereeList(trades.getSiteId(),Long.valueOf(distributor.getId()));
            if(refereeLists == null || refereeLists.size() == 0){
                activeDistributor(trades, distributor);
            }

            try {
                dFundsInsert(trades, distributor);
            } catch (Exception e) {
                logger.error("支付成功处理分佣错误 {} {}", e, e.getMessage());
            }
            updateAccountChange(distributor.getId(),trades.getRealPay());
            upLevel(trades, distributor);
        }
    }

    private void dFundsInsert(Trades trades,Distributor distributor){
        DFunds funds = fundsMapper.selectByDistributorId(trades.getDistributorId());
        DFunds dFunds = new DFunds();
        int amount = (funds == null)?trades.getRealPay():(int)funds.getAmount()+(int)trades.getRealPay();

        dFunds.setOwner(trades.getSiteId());
        dFunds.setDistributorId(distributor.getId());
        dFunds.setTradesId(String.valueOf(trades.getTradesId()));
        dFunds.setAmount(amount);
        dFunds.setMoney(trades.getRealPay());
        switch (trades.getPayStyle()){
            case "ali":
                dFunds.setPayType("0");
                break;
            case "wx":
                dFunds.setPayType("1");
                break;
            case "bil":
                dFunds.setPayType("3");
                break;
            case "unionPay":
                dFunds.setPayType("2");
                break;
            case "health_insurance":
                dFunds.setPayType("4");
                break;
            default:
                dFunds.setPayType("5");
                break;
        }
        dFunds.setType(Byte.valueOf("3"));
        dFunds.setStatus(trades.getIsPayment());
        dFunds.setStoreAmount(0);

        dFunds.setId(null);
        fundsMapper.insert(dFunds);
    }

    private void updateAccountChange(int distributorId,int money){
        distributorExtMapper.updateAccountChange(distributorId,money);
    }

    /**
     * 提升推荐人等级
     */
    public void upLevel(Trades trades, Distributor distributor) throws Exception {
        Long tradesId = trades.getTradesId();
        int distributeTotalPrice = 0;
        Double distributePrice = 0.0 ;
        Double discountRate = 1.0;
        List<Orders> orders = this.ordersMapper.getOrdersByTradesId(tradesId);
        for (Orders order : orders) {
            Integer goodsPrice = order.getGoodsPrice();
            Integer goodsId = order.getGoodsId();
            Integer goodsNum = order.getGoodsNum();
            GoodsDistribute goodsDistribute = this.goodsDistributeMapper.selectByGoodsIdAndSiteId(trades.getSiteId(), goodsId);
            RewardTemplate rewardTemplate = this.rewardTemplateMapper.selectByTemplateId(goodsDistribute.getDistributionTemplate());


            if (null != goodsDistribute && null != rewardTemplate) {

                if (null != rewardTemplate && StringUtil.isNotBlank(rewardTemplate.getDiscount())) {
                    Map<String,Object> discounts = new TreeMap<>();
                    try {
                        discounts = JacksonUtils.json2map(rewardTemplate.getDiscount());
                        double max = 100.0;
                        System.out.println(discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).count());
                        long totalLevel = discounts.values().stream().filter(x -> Integer.valueOf(x.toString()) > 0).count();
                        Stream maxLevel = discounts.values().stream().filter(x->Integer.valueOf(x.toString())>0);
                        List tmpList = (List) maxLevel.collect(Collectors.toList());
                        if(tmpList.size() > 0){
                            max = discounts.values().stream().filter(x->Integer.valueOf(x.toString())>0).mapToDouble(x->Integer.valueOf(x.toString())).summaryStatistics().getMin();
                        }
                        //折扣
                        if (discounts.containsKey("level" + distributor.getLevel()) && Integer.valueOf(discounts.get("level" + distributor.getLevel()).toString())>0) {
                            discountRate = Double.parseDouble(discounts.get("level" + distributor.getLevel()).toString()) * 0.01;//获得折扣率
                        }else if((int)totalLevel < distributor.getLevel()){
                            discountRate = max * 0.01;//获得折扣率
                        }
                        //是分销商品
                        distributePrice += goodsNum * goodsPrice *  discountRate;
                        distributeTotalPrice = new BigDecimal(distributePrice).setScale(0,RoundingMode.HALF_UP).intValue();

                    } catch (Exception e) {
                        logger.error("分佣模板的推荐优惠比例解析失败", e);
                    }
                }

            }
        }

        List<Recruit> recruitListByOwner = this.recruitMapper.getRecruitListByOwner(String.valueOf(trades.getSiteId()));
        Recruit recruit = recruitListByOwner.get(0);
        String ruleStr = recruit.getRule();
        try {
            Map rule = JacksonUtils.json2map(ruleStr);
            String level = distributor.getLevel().toString();
            if(StringUtil.equals(level, "0")){
                level = "1";
            }
            String levelKey = "level" + level;

            if (rule.containsKey(levelKey) && distributeTotalPrice >= (Integer) rule.get(levelKey)) {
                Integer reachedLevel=getReachedLevel(Integer.parseInt(level),distributeTotalPrice,rule);
                distributorMapper.updateDistributorLevel(reachedLevel,distributor.getId(),trades.getSiteId());
                logger.info("======fenxiao 订单编号:{} 提升分销商{}等级至 {}", tradesId, distributor.getUserName(), reachedLevel);
            }
        } catch (Exception e) {
            logger.debug("=======fenxiao 处理等级错误{}", e);
            throw e;
        }

    }

    /**
     * 激活分销商
     */
    public void activeDistributor(Trades trades, Distributor distributor) {
        //将冻结状态改为待审核状态  //'状态：0-待审核，1-通过，2-拒绝，3-冻结，'
        //Integer status = this.distributorMapper.updateDistributor(distributor.getId(),trades.getSiteId(),0,null);

        //if(status > 0){
        RefereeList refereeList = new RefereeList();
        refereeList.setRefereeId(Long.valueOf(distributor.getId()));
        refereeList.setRefereeName(distributor.getUserName());
        refereeList.setTotalIncomeAmount(0l);
        refereeList.setTotalExpenditure(0l);
        refereeList.setAccountBalance(0l);
        refereeListMapper.insertRefereeList(refereeList);
        //}
    }

    /**
     * 订单创建时计算分佣金额
     *
     * @param tradesMsg
     */
    //@Transactional(isolation = Isolation.READ_UNCOMMITTED,propagation = Propagation.REQUIRES_NEW)
    public void handleCreateOrderMsg(Map<String, Object> tradesMsg) {
        Long tradesId = (Long) tradesMsg.get("tradesId");//获取交易号
        Trades trades = this.tradesMapper.getTradesByTradesId(tradesId);//获取交易信息
        if (Objects.isNull(trades)) {
            logger.warn("{} 订单号不存在", tradesId);
            return;
        }

        Integer siteId = trades.getSiteId();
        Integer buyerId = trades.getBuyerId();//购买人 b_member的buyer_id
        Member member = this.memberMapper.getMember(siteId, buyerId);
        // 分佣对象
        //下单人
        Distributor distributor=this.distributorMapper.selectByUid(buyerId,siteId);
        //推荐人
        Distributor distributorReferrer=this.distributorMapper.selectDistributorInfo(trades.getDistributorId(),siteId);

        //根据tradeId和siteId获取Orders信息
        List<Orders> orders = this.ordersMapper.getOrdersByTradesId(tradesId);
        Double level1Total = 0d;
        Double level2Total = 0d;
        Double level3Total = 0d;
        Integer distributeTotalPrice = 0;
        // 没有分销商品就不是分销订单
        for (Orders order : orders) {
            Integer goodsPrice = order.getGoodsPrice();
            Integer goodsId = order.getGoodsId();
            Integer goodsNum = Optional.ofNullable(order.getGoodsNum()).orElse(1);
            GoodsDistribute goodsDistribute = this.goodsDistributeMapper.selectByGoodsIdAndSiteId(siteId, goodsId);
            Goods good = this.goodsMapper.getBySiteIdAndGoodsId(goodsId, siteId);
            if (null != goodsDistribute) {
                logger.info("{}是分销订单，开始处理。。。。。。",tradesId);
                //是分销商品
                distributeTotalPrice += goodsPrice * goodsNum;//计算分销商品总金额
                RewardTemplate rewardTemplate = this.rewardTemplateMapper.selectByTemplateId(goodsDistribute.getDistributionTemplate());
                if(null != rewardTemplate && StringUtil.isNotBlank(rewardTemplate.getReward())){
                    Byte type = rewardTemplate.getType();
                    //Byte accordingType = rewardTemplate.getAccordingType();
                    String rewardStr = rewardTemplate.getReward();
                    Map<String, Object> reward = null;
                    Map<String,Object> discount=null;
                    try {
                        reward = JacksonUtils.json2map(rewardStr);//'分佣比例'
                        discount=JacksonUtils.json2map(rewardTemplate.getDiscount());
                        logger.info("{} 的分销比例json 固定奖励:{}, 百分比奖励:{}", tradesId, reward, discount);
                    } catch (Exception e) {
                        logger.error("解析分销比例失败", e.getMessage());
                    }
                    if (0 == type) {//'分佣类型：0-按比例，1-按固定金额'
                        float dis = 1;
                        if(null != distributor){
                            //获取分销商当前等级折扣
                            if(discount.containsKey("level"+distributor.getLevel())){
                                if(Integer.parseInt(discount.get("level"+distributor.getLevel()).toString()) >0){
                                    dis = Float.parseFloat(discount.get("level"+distributor.getLevel()).toString()) / 100;
                                }
                            }else{//获取奖励模板中的最高等级Level的折扣
                                Set<String> set=discount.keySet();
                                Object [] obj=set.toArray();
                                Arrays.sort(obj);
                                int maxLevelValue = discount.values().stream().mapToInt(x->Integer.parseInt(x.toString())).summaryStatistics().getMax();
                                dis = Float.parseFloat(String.valueOf(maxLevelValue)) / 100;
                            }

                        }
                        logger.info("{} 分销商品原价{}, 等级折扣{}", tradesId, goodsPrice, dis);
                    /*if(discount.containsKey("level"+distributor.getLevel())){
                        int leveldis = Integer.parseInt(discount.get("level"+distributor.getLevel()).toString());
                        logger.info("{} 分销商等级折扣{}", tradesId, leveldis);
                        if (leveldis > 0) {
                            goodsPrice = goodsPrice * leveldis;
                        }
                    }*/
                        if (dis * 100 < 1) {
                            dis = 1;
                        }
                        logger.info("{} 计算分佣: 使用{}计算 商品数量{}", tradesId, goodsPrice, goodsNum);
                        level1Total += new BigDecimal(reward.get("level1").toString()).doubleValue() * goodsNum * 0.01 * goodsPrice * dis;
                        level2Total += new BigDecimal(reward.get("level2").toString()).doubleValue() * goodsNum * 0.01 * goodsPrice * dis;
                        level3Total += new BigDecimal(reward.get("level3").toString()).doubleValue() * goodsNum * 0.01 * goodsPrice * dis;
                    } else {
                        level1Total += new BigDecimal(reward.get("level1").toString()).doubleValue() * goodsNum;
                        level2Total += new BigDecimal(reward.get("level2").toString()).doubleValue() * goodsNum;
                        level3Total += new BigDecimal(reward.get("level3").toString()).doubleValue() * goodsNum;
                    }
                }else{
                    logger.info("{} 未找到匹配的奖励模板", tradesId);
                }
            } else {
                //不是分销商品 暂不处理
                logger.info("商品编号:{}不是分销商品:{}",goodsId,tradesId);
            }
        }
        //存在推荐人并且当前用户已是分销商未激活
        if (null != distributor && distributor.getStatus()==0 && null != distributorReferrer){
            Map<String,Object> param=new HashMap<>();
            param.put("parentId",distributorReferrer.getId());
            param.put("did",distributor.getId());
            distributorExtMapper.updateByDisSelective(param);//更替上级
        }

        //Integer.MAX_VALUE == trades.getDistributorId()
        if (trades.getDistributorId()==Integer.MAX_VALUE || trades.getDistributorId() ==Integer.MAX_VALUE -1 || distributor ==null){
            //判断是否需要处理佣金
            logger.info("{} 购买人{}还未成为分销商，需要增加分销商信息 状态设置为未审核", tradesId, trades.getBuyerNick());

            List<Recruit> recruitListByOwner = this.recruitMapper.getRecruitListByOwner(String.valueOf(trades.getSiteId()));
            if(recruitListByOwner.size() > 0){
                Recruit recruit = recruitListByOwner.get(0);
                String ruleStr = recruit.getRule();
                Map rule = null;
                try {
                    rule = JacksonUtils.json2map(ruleStr);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                logger.info("{} 分销商品总金额:{} 分销商信息:{} 等级规则:{}", tradesId, distributeTotalPrice, distributor, rule);
                if (distributor ==null && distributeTotalPrice >= (Integer) rule.get("level1")) {//可以成为分销商,插入新的分销商数据 此分销商设置为冻结状态
                    Integer reachedLevel = this.getReachedLevel(1, distributeTotalPrice, rule);//购买分销商品达到的等级

                    //插入新的分销商数据
                    String phone =member.getMobile().toString();
                    Map<String, Object> distributorMap = new HashMap<>();
                    //distributorMap.put("status", (byte) 3);//'状态：0-待审核，1-通过，2-拒绝，3-冻结，'
                    //distributorMap.put("level", reachedLevel.toString());
                    distributorMap.put("uid", trades.getBuyerId().toString());
                    distributorMap.put("owner", trades.getSiteId().toString());
                    distributorMap.put("user_name",phone);
                    if (StringUtil.isNotEmpty(member.getName())) {
                        distributorMap.put("real_name", member.getName());
                    } else {
                        distributorMap.put("real_name", member.getBuyerNick());
                    }
                    logger.info("订单ID{} 创建分销商 data:{}", tradesId, distributorMap);
                    this.distributorMapper.createDistributor(distributorMap);
                    Integer id =Integer.parseInt(distributorMap.get("id").toString()) ;//生成的分销商主键
                    //插入新的分销商扩展数据
                    Map<String, Object> distributorExtMap = new HashMap<>();
                    distributorExtMap.put("did", id);
                    distributorExtMap.put("uid", trades.getBuyerId().toString());
                    //不存在推荐人，上级编号给预设值：Integer.MAX_VALUE
                    distributorExtMap.put("parentId", trades.getDistributorId().toString());

                    distributorExtMap.put("mobile", phone);
                    distributorExtMap.put("invitationCode", getCode(trades.getSiteId().toString()));
//                distributorExtMap.put("chargeAccount", trades);//'账户消费金(分)'
                    distributorExtMap.put("status", 1);//'状态：0-未激活账户，1-激活，2-冻结'
                    this.distributorExtMapper.insertSelective(distributorExtMap);

                    //将此订单中的分销id更新为生成的
//                this.tradesMapper.updateDistributorId(trades.getTradesId(),id);
                }
                //存在推荐人
                if(null != distributorReferrer && distributorReferrer.getStatus() ==1){
                    Reward reward = new Reward();
                    reward.setOrder_id(tradesId);//订单id
                    reward.setDistributor_id(trades.getDistributorId());//分销商id
                    reward.setBuyer_id(trades.getBuyerId());//买家会员id
                    reward.setOrder_price(distributeTotalPrice);//订单中分销商品总金额价格
                    reward.setOrder_status(110);//'订单状态 0：交易失败（退款） 1：交易成功  2：交易结束  110：初始状态'
                    reward.setOwner(siteId);//门店id
                    reward.setReal_pay(trades.getRealPay());//订单实际支付价格
                    reward.setReward_status(0);//'奖励状态：0-待确认 1-已确认'
                    reward.setLevel_1_award(0);
                    reward.setLevel_2_award(0);
                    reward.setLevel_3_award(0);
                    //顶级分销商 0 给自己分佣，没有上级
                    if(trades.getDistributorId() == Integer.MAX_VALUE-1){
                        reward.setLevel_1_award(new BigDecimal(level1Total).setScale(0, RoundingMode.HALF_UP).longValue());//'下单推荐分佣'
                    }else{
                        reward.setLevel_1_award(new BigDecimal(level1Total).setScale(0, RoundingMode.HALF_UP).longValue());//'下单推荐分佣'
                        DistributorExt distributorExt = this.distributorExtMapper.selectByDid(trades.getDistributorId());

                        if (null != distributorExt && notRoot(distributorExt.getParentId())) {
                            reward.setDistributor_father_id(distributorExt.getParentId());
                            reward.setLevel_2_award(new BigDecimal(level2Total).setScale(0, RoundingMode.HALF_UP).longValue());//'上级推荐分佣'
                            DistributorExt distributorFatherExt = this.distributorExtMapper.selectByDid(distributorExt.getParentId());
                            if (null !=distributorFatherExt && notRoot(distributorFatherExt.getParentId())){
                                DistributorExt distributorGrandFatherExt = this.distributorExtMapper.selectByDid(distributorExt.getParentId());
                                if (null != distributorGrandFatherExt) {
                                    reward.setDistributor_grandfather_id(distributorGrandFatherExt.getParentId());
                                    reward.setLevel_3_award(new BigDecimal(level3Total).setScale(0, RoundingMode.HALF_UP).longValue());// '上上级推荐分佣'
                                }
                            }
                        }
                    }
                    long total_award = (reward.getLevel_1_award()) + reward.getLevel_2_award() + reward.getLevel_3_award();
                    reward.setTotal_award(total_award);//'总分佣'
                    logger.info("===========fenxiao 写入分佣记录:{}", reward);
                    this.rewardMapper.insertSelective(reward);
                }
                //金额未达到招募书中成为分销商的最低金额，不需要处理
            }else{
                logger.info("{}d_recruit null",trades.getSiteId());
            }
        }else{//需要处理分佣
            //下单人是分销商并且状态已激活 OR 推荐人是分销商并且状态已激活
            if(distributor.getStatus() ==1 ||(null != distributorReferrer && distributorReferrer.getStatus() ==1)){
                Reward reward = new Reward();
                reward.setOrder_id(tradesId);//订单id
                reward.setDistributor_id(trades.getDistributorId());//分销商id
                reward.setBuyer_id(trades.getBuyerId());//买家会员id
                reward.setOrder_price(distributeTotalPrice);//订单中分销商品总金额价格
                reward.setOrder_status(110);//'订单状态 0：交易失败（退款） 1：交易成功  2：交易结束  110：初始状态'
                reward.setOwner(siteId);//门店id
                reward.setReal_pay(trades.getRealPay());//订单实际支付价格
                reward.setReward_status(0);//'奖励状态：0-待确认 1-已确认'
                reward.setLevel_1_award(0);
                reward.setLevel_2_award(0);
                reward.setLevel_3_award(0);
                //顶级分销商 0 给自己分佣，没有上级
                if(trades.getDistributorId() == Integer.MAX_VALUE-1){
                    reward.setLevel_1_award(new BigDecimal(level1Total).setScale(0, RoundingMode.HALF_UP).longValue());//'下单推荐分佣'
                }else{
                    reward.setLevel_1_award(new BigDecimal(level1Total).setScale(0, RoundingMode.HALF_UP).longValue());//'下单推荐分佣'
                    DistributorExt distributorExt = this.distributorExtMapper.selectByDid(trades.getDistributorId());

                    if (null != distributorExt && notRoot(distributorExt.getParentId())) {
                        reward.setDistributor_father_id(distributorExt.getParentId());
                        reward.setLevel_2_award(new BigDecimal(level2Total).setScale(0, RoundingMode.HALF_UP).longValue());//'上级推荐分佣'
                        DistributorExt distributorFatherExt = this.distributorExtMapper.selectByDid(distributorExt.getParentId());
                        if (null !=distributorFatherExt && notRoot(distributorFatherExt.getParentId())){
                            DistributorExt distributorGrandFatherExt = this.distributorExtMapper.selectByDid(distributorExt.getParentId());
                            if (null != distributorGrandFatherExt) {
                                reward.setDistributor_grandfather_id(distributorGrandFatherExt.getParentId());
                                reward.setLevel_3_award(new BigDecimal(level3Total).setScale(0, RoundingMode.HALF_UP).longValue());// '上上级推荐分佣'
                            }
                        }
                    }
                }
                long total_award = (reward.getLevel_1_award()) + reward.getLevel_2_award() + reward.getLevel_3_award();
                reward.setTotal_award(total_award);//'总分佣'
                logger.info("===========fenxiao 写入分佣记录:{}", reward);
                this.rewardMapper.insertSelective(reward);
            }else{
                logger.info("===========fenxiao 下单人或推荐人不存在或状态未激活:{}",tradesId);
            }

        }
    }

    public boolean isRoot(int parentId) {
        return (rootId - parentId) == 0 || parentId - rootId == 1;
    }

    public boolean notRoot(int parentId) {
        return !isRoot(parentId);
    }
}
//@MsgConsumer(
//        topicName = DistributionTrade.topicName,
//        tagName = DistributionTrade.tagName,
//        consumeType = MsgConsumer.ConsumeType.Orderly,
//        consumerGroup = "TradesConsumerGroupTOTO"
//)
//public class DistributionTrade implements Consumer {
//    public static final Logger logger = LoggerFactory.getLogger(DistributionTrade.class);
//    public static final String topicName = "OrderTopic";
//    public static final String tagName = "distribution_tradesTOTO";
//    public static final  Integer rootId=Integer.MAX_VALUE-1; //顶级分销商
//
//    @Autowired
//    TradesMapper tradesMapper;
//    @Autowired
//    OrdersMapper ordersMapper;
//    @Autowired
//    GoodsMapper goodsMapper;
//    @Autowired
//    GoodsDistributeMapper goodsDistributeMapper;
//    @Autowired
//    RewardTemplateMapper rewardTemplateMapper;
//    @Autowired
//    RewardMapper rewardMapper;
//    @Autowired
//    DistributeOrderService distributeOrderService;
//    @Autowired
//    RecruitMapper recruitMapper;
//    @Autowired
//    DistributorMapper distributorMapper;
//    @Autowired
//    DistributorExtMapper distributorExtMapper;
//    @Autowired
//    MemberMapper memberMapper;
//    @Autowired
//    RefereeListMapper refereeListMapper;
//
//    @Autowired
//    DFundsMapper fundsMapper;
//
//    @Override
//    public void consume(List<MessageExt> msgs) throws MsgConsumeException {
//        try {
//            MessageExt msg = msgs.get(0);
//            // 处理消息
//            String content = new String(msg.getBody());
//            logger.info("开始处理分销订单 {}", content);
//
//            Map<String, Object> tradesMsg = JacksonUtils.json2map(content);
//            if (TradeMsgType.TRADES_CREATE.toString().equals(tradesMsg.get("type"))) {
//                this.handleCreateOrderMsg(tradesMsg);
//            } else if (TradeMsgType.TRADES_PAY_SUCCESS.toString().equals(tradesMsg.get("type"))) {
//                this.handlePaySuccess(tradesMsg);
//            } else if (TradeMsgType.TRADES_REFUND.toString().equals(tradesMsg.get("type"))) {
//                this.handleRefundSuccess(tradesMsg);
//            } else if (TradeMsgType.TRADES_FINISH.toString().equals(tradesMsg.get("type"))) {
//                this.handleTradeEnd(tradesMsg);
//            }
//        } catch (Exception e) {
//            logger.error("错啦", e);
//            throw new MsgConsumeException(e.toString());
//        }
//    }
//
//}

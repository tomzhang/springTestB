package com.jk51.modules.comment.service;

import com.jk51.model.Comments;
import com.jk51.model.Goods;
import com.jk51.model.order.Trades;
import com.jk51.modules.comment.mapper.CommentMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/2/24.
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private TradesMapper tradesMapper;

    @Autowired
    private IntegerRuleService integralRuleService;

    public List<Comments> findCommentsList(Map<String,Object> paramsMap){return commentMapper.findCommentsList(paramsMap);}
    @Transactional
    public void updateState(Map<String, Object> params) {
        Comments comments = new Comments();
        Object O_isShow = params.get("isShow");
        String S_isShow = O_isShow.toString();
        int I_isShow = Integer.parseInt(S_isShow);
        comments.setIsShow(I_isShow);
        List<String> list = new ArrayList<String>();
        String ids = (String) params.get("commentIds");
        String[] commentIds = ids.split(",");
        for (int i = 0; i < commentIds.length; i++) {
            list.add(commentIds[i]);
        }
        Object siteId = params.get("siteId");
        String S_siteId = siteId.toString();
        int I_siteId = Integer.parseInt(S_siteId);
        comments.setSiteId(I_siteId);
        comments.setCommentIds(list);
        commentMapper.updateState(comments);
    }

    @Transactional
    public void updateStateAll(Map<String, Object> params) {
        Comments comments = new Comments();
        comments.setIsShow(0);
        List<String> list = new ArrayList<String>();
        String ids = (String) params.get("commentIds");
        String[] commentIds = ids.split(",");
        for (int i = 0; i < commentIds.length; i++) {
            list.add(commentIds[i]);
        }
        Object siteId = params.get("siteId");
        String S_siteId = siteId.toString();
        int I_siteId = Integer.parseInt(S_siteId);
        comments.setSiteId(I_siteId);
        comments.setCommentIds(list);
        commentMapper.updateState(comments);
    }



    public List<Map<String, Object>> findItemComments(String siteId, String goodsId){
        return commentMapper.findItemComments(siteId,goodsId);
    }

    public Goods findGoodsById(String goodsId){
        return goodsMapper.queryGoods(goodsId);
    }

    @Transactional
    public List<Comments> findOrderComments(String  tradesId){
        List<Comments> comments =  commentMapper.findOrderComments(tradesId);
/*
        comments.stream().forEach(comments1 -> {
            System.out.print("dsfdsf");
            Goods goods = goodsMapper.getBySiteIdAndGoodsId(comments1.getGoodsId(),comments1.getSiteId());
            comments1.setGoods(goods);
        });*/
        comments.forEach(comments1 -> {
            System.out.println("~~~~");
            Goods goods = goodsMapper.getBySiteIdAndGoodsId(comments1.getGoodsId(),comments1.getSiteId());
            comments1.setGoods(goods);
        });

        return comments;
    }
    @Transactional
    public void addServiceComment(Map<String,Object> paramterMap){commentMapper.addServiceComment(paramterMap);}

    @Transactional
    public String addComment(Map<String,Object> param){
//        printMap(param);

        List<Comments> commentss = new ArrayList<>();
        Comments comments = null;

        Integer siteId = (Integer) param.get("siteId");
        String tradesId = (String) param.get("tradesId");
        String byerNick = (String) param.get("buyerNick");

        comments = new Comments();
        comments.setSiteId(siteId);
        comments.setTradesId(tradesId);
        comments.setBuyerNick(byerNick);
        comments.setCommentRank(Integer.parseInt((String)param.get("tradesRank")));

        commentss.add(comments);

        int index=1;
        String goods = (String) param.get("goodss");
        String [] goodss = goods.split("\\|");
        for(int i = 0;i<goodss.length;i++){
            comments = new Comments();
            String[] comment = goodss[i].split(",");
            comments.setSiteId(siteId);
            comments.setTradesId(tradesId);
            comments.setBuyerNick(byerNick);
            comments.setCommentRank(Integer.parseInt(comment[0]));
            comments.setCommentContent(comment[1]);
            comments.setGoodsId(Integer.parseInt(comment[2]));
            comments.setIsShow(1);
            commentss.add(comments);
            index++;

        }

        int i = commentMapper.addTradeComment(commentss);
        if(i==index){

            Trades trades=tradesMapper.getTradesByTradesId(Long.valueOf((String) param.get("tradesId")));

            integralRuleService.integralByOrderMulti(trades);

            if(trades!=null){
                Integer rank = Integer.parseInt((String)param.get("tradesRank"));
                int m = tradesMapper.updateTradeRank(rank,trades.getTradesId());
                if(m!=0)return "ok";
            }
            return "ok";
        }

        throw new RuntimeException("faild");
    }

    private void printMap(Map<String,Object> param){
        Set<String> set = param.keySet();
        System.out.println("*************************************");

        for (String s:set
                ) {
            System.out.println(param.get(s));
        }
        System.out.println("*************************************");
    }

    public void updateComments(Map param){
        commentMapper.updateComments(param);
    }
}

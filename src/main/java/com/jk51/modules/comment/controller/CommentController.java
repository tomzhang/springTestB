package com.jk51.modules.comment.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.Comments;
import com.jk51.model.Goods;
import com.jk51.modules.comment.service.CommentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by wangcheng on 2017/2/24.
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    /**
     *根据条件查詢评价列表
     *param commentContent 评价内容
     * param commentRank 评价等级（1-5）
     * param isShow 是否显示 1为显示 0为隐藏
     * param startTime 开始时间
     * param endTime 结束时间
     * param pageNum 页数
     * param pageSize 每页的数量
     * */
    @RequestMapping("/commentlist" )
    @ResponseBody
    public PageInfo findCommentsList(HttpServletRequest request,@RequestParam(required =true,defaultValue = "1")int pageNum,@RequestParam(required = false,defaultValue = "15")int pageSize){
        Map<String,Object> paramsMap = ParameterUtil.getParameterMap(request);
        PageHelper.startPage(pageNum,pageSize);//开启分页
        List<Comments> commentsMap = commentService.findCommentsList(paramsMap);
        return new PageInfo<>(commentsMap);
    }

    /**
     *评价的隐藏/显示
     *param siteId 商家id
     * param commentIds 评价ids 是一个数组
     * */
    @RequestMapping("/updateState")
    @ResponseBody
    public String comment2Hide(HttpServletRequest request){
        Map<String,Object> params=ParameterUtil.getParameterMap(request);
        try {
            commentService.updateState(params);
            return "ok";
        }catch (Exception e){
            return "error" + e.getMessage();
        }

    }

    /**
     *评价的隐藏/显示
     *param siteId 商家id
     * param commentIds 评价ids 是一个数组
     * */
    @RequestMapping("/updateStateAll")
    @ResponseBody
    public String comment2hideAll(HttpServletRequest request){
        Map<String,Object> params=ParameterUtil.getParameterMap(request);
        try {
            commentService.updateStateAll(params);
            return "ok";
        }catch (Exception e){
            return "error" + e.getMessage();
        }

    }
    /**
     *商品评价详情
     *param goodsId 商品id
     * param pageNum 页数
     * param pageSize 每页数量
     * */
    @RequestMapping("/goodscommentdetail")
    @ResponseBody
    public PageInfo findItemComments(String goodsId,String siteId, @RequestParam(required =true,defaultValue = "1")int pageNum, @RequestParam(required = false,defaultValue = "10")int pageSize){

        PageHelper.startPage(pageNum,pageSize);//开启分页

        List<Map<String, Object>> commentsList = commentService.findItemComments(siteId,goodsId);
        return new PageInfo<>(commentsList);
    }
    /**
     *订单评价详情
     *param tid  订单id
     * */
    @RequestMapping("/ordercommentdetail")
    @ResponseBody
    public ReturnDto findOrderComments(String tradesId){
        List<Comments> commentsList = null;
        if(StringUtils.isNotBlank(tradesId)){
            commentsList = commentService.findOrderComments(tradesId);
        }
        return ReturnDto.buildSuccessReturnDto(commentsList);

    }

    /**
     *评价的隐藏/显示
     *param siteId 商家id
     * param commentIds 评价ids 是一个数组
     * */

    /**
     *查询打分等级
     *1 非常满意
     * 2 一般
     * 3 不满意
     * */
    @RequestMapping("/selectcomment")
    @ResponseBody
    public Map<String,String> selectCommentGrade(){
        Map<String,String> map = new HashMap<>();
        map.put("1","非常满意");
        map.put("2","一般");
        map.put("3","不满意");
        return map;
    }
    /**
     *保存聊天时的评价信息
     *siteId 商家id
     * commentRank 评价等价
     * commentContext 评价内容
     * chatEnter 评价入口 （1 为订单入口）
     * chatTerr 聊天平台（1为微信）
     * clientName 客户姓名
     * clientPhone 客户手机
     * serviceName 客服姓名
     * servicePhone 客服手机
     * */
    @RequestMapping("/saveclientcommentservice")
    @ResponseBody
    public String addServiceComment(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Content-type", "text/html;charset=utf-8");
        Map<String,Object> paramterMap = ParameterUtil.getParameterMap(request);
        try {
            commentService.addServiceComment(paramterMap);
            return "ok";
        }catch (Exception e){
            return "error"+ e.getMessage();
        }
    }

    @ResponseBody
    @RequestMapping("/addcomment")
    public String addComment(@RequestBody Map<String,Object> param){
//        Integer siteId = Integer.parseInt((String)request.getSession().getAttribute("siteId"));
//        Map<String,Object> param = ParameterUtil.getParameterMap(request);
//        param.put("siteId",siteId);
//        param.put("isShow",1);
        return commentService.addComment(param);
    }

    @RequestMapping("/queryGoods")
    @ResponseBody
    public Goods findgoodsById(HttpServletRequest request){
        Map<String,Object> map=(Map)request.getParameterMap();
        String goodsId = map.get("goodsId").toString();
        return commentService.findGoodsById(goodsId);
    }

    @RequestMapping("/updateComments")
    @ResponseBody
    public Map updateComments(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        commentService.updateComments(param);
        Map result = new HashMap();
        result.put("status", "success");
        return result;
    }

}

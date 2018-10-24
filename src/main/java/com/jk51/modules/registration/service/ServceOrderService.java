package com.jk51.modules.registration.service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.requestParams.QueryStatement;
import com.jk51.model.goods.PageData;
import com.jk51.model.registration.models.MyMingYiYuYueDetail;
import com.jk51.model.registration.models.ServceOrder;
import com.jk51.model.registration.requestParams.ServerOrderParams;
import com.jk51.modules.registration.mapper.ServceOrderMapper;
import com.jk51.modules.registration.request.ServceOrderRequestParam;
import com.jk51.modules.registration.request.SubscribeDetailRequestParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.registration.goodsService.
 * author   :mqq
 * date     :2017/4/7
 * Update   :
 */
@Service
@Transactional
public class ServceOrderService {

    @Autowired
    ServceOrderMapper servceOrderMapper;


    public List<ServceOrder> queryAllServceOrder(ServceOrder servceOrder){
        return  this.servceOrderMapper.queryAllServceOrder(servceOrder);
    }

    public int  queryAllServceOrderMapper(ServceOrder servceOrder){
        return  this.servceOrderMapper.updateByPrimaryKey(servceOrder);
    }

    /**
     *
     * @param memberId
     * @return
     */
    public int queryMyMingYiYuYueCount(Integer memberId){
        return  this.servceOrderMapper.queryMyMingYiYuYueCount(memberId);
    }




   public List<MyMingYiYuYueDetail>  queryMyMingYiYuYueDetailList (Map<String,Object> paramMap){
        return this.servceOrderMapper.queryMyMingYiYuYueDetailList(paramMap);
   }

    //查询商品下面为待就诊的预约单
    public List<ServceOrder> queryAllDaiJiuZhenServceOrder(ServerOrderParams orderParams){
        return  this.servceOrderMapper.queryAllDaiJiuZhenServceOrder(orderParams);
    }

    //商品下架或是停诊 的话 修改商品关联下的预约单的状态
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int  updateServderOrderServerStatus(ServerOrderParams orderParams){
        return  this.servceOrderMapper.updateServderOrderServerStatus(orderParams);
    }

    //商品查询并且且修改状态
    public ReturnDto queryAndUpdateServerOrderForGoodStatus(ServerOrderParams orderParams){
          List<ServceOrder> list=queryAllDaiJiuZhenServceOrder(orderParams);
          Map<String,Object> map= new HashMap<String,Object>();
        if(!list.isEmpty()){
              map.put("list",list);
              if(StringUtils.equalsIgnoreCase(orderParams.getType(),"1")){
                  orderParams.setServeStatus(1);
              }else if(StringUtils.equalsIgnoreCase(orderParams.getType(),"2")){
                  orderParams.setServeStatus(2);
              }
              int size=updateServderOrderServerStatus(orderParams);
              map.put("size" ,size);
            return ReturnDto.buildSuccessReturnDto(map);
          }


          return ReturnDto.buildSuccessReturnDto("没有相关数据");
    }

    //取消预约单
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int  deleteServderOrderServerStatus(ServceOrder servceOrder){
        return  this.servceOrderMapper.updateByPrimaryKeySelective(servceOrder);
    }

    public Boolean insert(ServceOrder servceOrder) {
        //向预约表中插入一条数据
        servceOrderMapper.insert(servceOrder);
        return true;
    }


    //服务列表分页查询
    public PageInfo<?> queryServceOrderDetail(ServceOrderRequestParam queryServceOrder) {
        PageHelper.startPage(queryServceOrder.getPageNum(), queryServceOrder.getPageSize());

        List<PageData> list = servceOrderMapper.findServceOrderDetailPage(queryServceOrder);
        return new PageInfo<>(list);
    }



    //服务列表分页查询
    public PageInfo<?> queryAllDoctorDetailByStore(SubscribeDetailRequestParam subscribeDetailRequestParam) {
        PageHelper.startPage(subscribeDetailRequestParam.getPageNum(), subscribeDetailRequestParam.getPageSize());

        List<PageData> list = servceOrderMapper.queryAllDoctorDetailByStorePage(subscribeDetailRequestParam);
        return new PageInfo<>(list);
    }


    public List<PageData> queryServceOrderDetailInfo(ServceOrderRequestParam queryServceOrder){
        return  this.servceOrderMapper.findServceOrderDetailPage(queryServceOrder);
    }

    public int updateservceorder(ServceOrder servceOrder){
        return this.servceOrderMapper.updateByPrimaryKeySelective(servceOrder);
    }

}

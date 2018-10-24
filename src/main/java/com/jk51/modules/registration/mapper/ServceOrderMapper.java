package com.jk51.modules.registration.mapper;

import com.jk51.model.account.requestParams.QueryStatement;
import com.jk51.model.goods.PageData;
import com.jk51.model.registration.models.MyMingYiYuYueDetail;
import com.jk51.model.registration.models.ServceOrder;
import com.jk51.model.registration.requestParams.ServerOrderParams;
import com.jk51.modules.registration.request.ServceOrderRequestParam;
import com.jk51.modules.registration.request.SubscribeDetailRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServceOrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_order
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_order
     *
     * @mbg.generated
     */
    int insert(ServceOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_order
     *
     * @mbg.generated
     */
    int insertSelective(ServceOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_order
     *
     * @mbg.generated
     */
    ServceOrder selectByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ServceOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(ServceOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ServceOrder record);

    /**
     * @param  record
     * return list
     */

    List<ServceOrder> queryAllServceOrder(ServceOrder record);

    /**
     *
     * @param orderParams
     * @return
     */
    List<ServceOrder> queryAllDaiJiuZhenServceOrder(ServerOrderParams orderParams);

    /**
     *
     * @param orderParams
     * @return
     */
    int updateServderOrderServerStatus(ServerOrderParams orderParams);

    /**
     *
     * @param status
     * @param siteId
     * @param goodsId
     * @return
     */
    int updateByGoodsId(@Param("status") Integer status, @Param("siteId")Integer siteId,@Param("goodsId")Integer goodsId);

    /**
     *
     * @param memberId
     * @return
     */
   int queryMyMingYiYuYueCount(@Param("memberId")Integer memberId);

    /**
     *
     * @param paramMap
     * @return
     */
   List<MyMingYiYuYueDetail>  queryMyMingYiYuYueDetailList(Map<String,Object> paramMap);

    /**
     *
     * @param queryServceOrder
     * @return
     */
    List<PageData> findServceOrderDetailPage(ServceOrderRequestParam queryServceOrder);

    /**
     *
     * @param subscribeDetailRequestParam
     * @return
     */
    List<PageData> queryAllDoctorDetailByStorePage(SubscribeDetailRequestParam subscribeDetailRequestParam);

    Map<String, Object> selectByTradesId(Long tradesId);

    List<Map<String,Object>> getTradesServceOrdersList(Map<String, Object> params);
}
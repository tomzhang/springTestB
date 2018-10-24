package com.jk51.modules.grouppurchase.mapper;

import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.modules.grouppurchase.request.GroupPurchaseParam;
import com.jk51.modules.promotions.job.StatusTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by mqq on 2017/11/17.
 */
@Mapper
public interface GroupPurChaseMapper {

    int create(@Param("grouppurchase") GroupPurchase groupPurchase);

    GroupPurchase findInfo(@Param("site_id") Integer site_id, @Param("id") Integer id);

    GroupPurchase findByTradesId(@Param("siteId") Integer siteId, @Param("tradesId") String tradesId);

    int updateStatusByIdAndSiteId(@Param("id") Integer id, @Param("siteId") Integer siteId, @Param("status") int status);

    int updateMainGroupPurchaseStatus(@Param("groupPurchaseParam") GroupPurchaseParam groupPurchaseParam);

    List<GroupPurchase> getGroupPurchaseList(GroupPurchaseParam groupPurchaseParam);

    /**
     * 用于定时任务{@link StatusTask#groupPurchaseStatus()}的方法
     *
     * @return
     * @param headGroupPurchaseId
     */
    List<GroupPurchase> getGroupPurchaseListForTask(@Param("headGroupPurchaseId") Integer headGroupPurchaseId);

    /**
     * 通过团长的团id找到该团所有团员的信息（包括团长）
     *
     * @param parentId 父id
     * @return
     */
    List<GroupPurchase> findGroupPurchasesByParentId(@Param("parentId") int parentId, @Param("siteId") int siteId);

    GroupPurchase getOneGroupPurchase(GroupPurchaseParam groupPurchaseParam);

    GroupPurchase getMainForGroupPurchase(GroupPurchaseParam groupPurchaseParam);

    int selectCountForGroupPurchase(GroupPurchaseParam groupPurchaseParam);

    List<GroupPurchase> childRenNoPayList(GroupPurchaseParam groupPurchaseParam);

    int selectCountForJoinTheGroupPurchase(GroupPurchaseParam groupPurchaseParam);

    List<GroupPurchase> mainGroupPurchaseList(GroupPurchaseParam groupPurchaseParam);

    List<GroupPurchase> childrenFrommainGroupPurchaseList(GroupPurchaseParam groupPurchaseParam);

    GroupPurchase getGroupPurchaseFromTradsesId(@Param("tradesId") String tradesId);

    GroupPurchase getGoruoLeaderStatus1(@Param("siteId") int siteId, @Param("tradesId") String tradesId);

    GroupPurchase getGoruoLeaderStatus2(@Param("siteId") int siteId, @Param("id") int id);

    List<GroupPurchase> getGroupPurcharseParentIdPurcharse(@Param("siteId") Integer siteId,@Param("activityId") Integer activityId,@Param("goodsId") Integer goodsId);

}

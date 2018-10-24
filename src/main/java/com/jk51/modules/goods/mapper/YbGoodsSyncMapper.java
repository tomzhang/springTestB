package com.jk51.modules.goods.mapper;

import com.jk51.model.goods.PageData;
import com.jk51.model.goods.YbGoodsSyncGrid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@Mapper
public interface YbGoodsSyncMapper {
    public List<PageData> querySyncGoodsList(YbGoodsSyncGrid ybGoodsSyncGrid);

    public void batchDelSyncGoods(String[] good_ids);//软删除

    public List<PageData> queryGoodsConfigList(@Param("detail_tpl") Integer detail_tpl);

    public void setGoodConfig(PageData pageData);

    public PageData querySyncGoodBySyncDraftId(Integer sync_draft_id);

    public void ignoreUpdate(Integer good_sync_id);

    void handleUpdateImgStatus(Object id);

    HashMap queryIyfImageByMd5(String hash);

    void setStatus(Object syncGood_id);

    void updatePic(@Param("yb_goods_id") Integer goods_id,@Param("id") Integer syncGood_id);

    String queryIyfImageByHash(String hash);

    List<PageData> goodsSyncQueryList(Map<String, Object> parameterMap);

    void batchSyncDel(String[] ids);
    
}

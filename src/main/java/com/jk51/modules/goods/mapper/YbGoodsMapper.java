package com.jk51.modules.goods.mapper;

import com.jk51.model.goods.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Mapper
public interface YbGoodsMapper {
    List getList(YbGoodsGrid ybGoodsGrid);

    void batchDel(String[] ids);

    void batchUpdateImg(String[] ids);

    YbBarnd queryBarnd(String brand_name);

    YbCategory queryCate(String cate_id);

    List<YbImagesAttr> queryImgsByGoodId(Integer goodId);

    void delSinglePic(@Param("goodId") Integer goodId, @Param("hashId") String hashId);

    Map<String, Object> querySingleGoodDetail(Integer goodId);

    void updateItem(PageData pageData);

    void updateGoodExtd(PageData pageData);

    void saveGood(PageData pageData);

    void saveGoodExtd(PageData pageData);

    void saveBarnd(String brand_name);

    void batchDelPic(String[] ids);

    YbImagesAttr queryDefaultImg(Integer goodId);

    void saveSinglePic(YbImagesAttr ybImagesAttr);

    YbImagesAttr queryImgLimitOne(Integer goods_id);

    void updateGoodDefaultImg(@Param(value = "goodId") Integer goodId, @Param(value = "hashId") String hashId);

    PageData queryYbGoodByPzwhAndGg(@Param(value = "approval_number") String approval_number,
                                    @Param(value = "specif_cation") String specif_cation,
                                    @Param(value="detail_tpl") String detail_tpl);
    PageData queryYbGoodByPzwh(@Param(value = "approval_number") String approval_number,
                               @Param(value="detail_tpl") String detail_tpl);

    PageData queryYbGoodByGoodCode(@Param(value = "good_code") String good_code,
                                   @Param(value="detail_tpl") String detail_tpl);

    //把图片数也查出来
    List<PageData> queryYbGoodByBarCodeORApprovalNumber(@Param(value = "bar_code") String bar_code,
                                                        @Param(value = "approval_number") String approval_number,
                                                        @Param(value="detail_tpl") String detail_tpl);

    YbImagesAttr queryImgById(Integer id);

    void updateImgToNotDefault(Integer id);

    Integer saveGoodOfMerchant(PageData pageData);

    void saveGoodExtdOfMerchant(PageData pageData);

    void updateGoodOfMerchant(PageData pageData);

    void updateGoodExtdOfMerchant(PageData pageData);

    boolean restore(@Param("goodsId") int goodsId);

    List getListIgnoreImgExists(YbGoodsGrid ybGoodsGrid);

    void setGoodImgStatusToUnhandle(Integer goods_id);

    int updateGoodsCate(@Param("goodsId")String gls,@Param("cate_code") String cate_code);

    YbImagesAttr queryImgsByGoodIdAndHash(@Param("goods_id") Integer goods_id,@Param("hash")  String hash);

    Map<String,Object> findYbGoodsByBarCode(String bar_code);

    List<String> queryYbGoodsColumns();

    List getMerchantList(YbGoodsGrid ybGoodsGrid);

    PageData queryYbGoodByPzwhBarCode(@Param(value = "approval_number") String approval_number,
                                    @Param(value = "bar_code") String barcode,
                                    @Param(value="detail_tpl") String detail_tpl);

    List getList2(YbGoodsGrid ybGoodsGrid);
}

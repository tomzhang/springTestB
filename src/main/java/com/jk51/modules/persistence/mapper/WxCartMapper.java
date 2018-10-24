package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.WxCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/31
 */
@Mapper
public interface WxCartMapper {

    public List<WxCart> getList();

    public WxCart get(@Param("goods_id") Integer goodId,
                      @Param("site_id") Integer siteId,
                      @Param("user_id") Integer userId);


    void insert(Map<String, Object> param);

    void update(Map<String, Object> param);

    //软删除，修改状态
    void delete(@Param("good_ids") String[] good_ids,
                @Param("site_id") Integer siteId,
                @Param("user_id") Integer userId);


}

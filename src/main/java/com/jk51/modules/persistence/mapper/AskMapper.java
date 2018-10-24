package com.jk51.modules.persistence.mapper;

import com.jk51.model.merchant.Ask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
@Mapper
public interface AskMapper {
    Integer insertAsk(Ask ask);

    List<Ask> getAskAll(@Param("siteId") Integer siteId);

    List<Ask> getAskById(@Param("siteId") Integer siteId, @Param("id") Integer id);

    Integer updateAsk(Ask ask);

    Integer deleteAsk(@Param("siteId") Integer siteId, @Param("id") Integer id);

    List<Ask> getAskByName(@Param("siteId") Integer siteId, @Param("dimName") String dimName);

}

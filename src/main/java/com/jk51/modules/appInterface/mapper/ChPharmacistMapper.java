package com.jk51.modules.appInterface.mapper;

import com.jk51.model.ChPharmacist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChPharmacistMapper {
    int deleteByPrimaryKey(@Param("pharmacist_id")Integer id,@Param("siteId")Integer siteId);

    int insert(ChPharmacist record);

    int insertSelective(ChPharmacist record);

    ChPharmacist selectByPrimaryKey(@Param("pharmacist_id")Integer id,@Param("siteId")Integer siteId);

    int updateByPrimaryKeySelective(ChPharmacist record);

    int updateByPrimaryKey(ChPharmacist record);

    List<ChPharmacist> findChPharmacistByUserId(int user_id);

    int updateOnlineByPharmacistId(@Param("num") int num, @Param("pharmacist_id") int pharmacist_id,@Param("siteId")Integer siteId);

    ChPharmacist queryPharmacistByUserId(Integer user_id);

    int updateRemind(@Param("pharmacistId") int pharmacistId, @Param("siteId") int siteId, @Param("is_remind") int is_remind);
}

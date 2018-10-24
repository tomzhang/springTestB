package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SChPharmacist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SChPharmacistMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SChPharmacist record);

    int insertSelective(SChPharmacist record);

    SChPharmacist selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SChPharmacist record);

    int updateByPrimaryKey(SChPharmacist record);

    List<SChPharmacist> findChPharmacistByUserId(int user_id);

    int updateOnlineByPharmacistId(@Param("num") int num, @Param("pharmacist_id") int pharmacist_id);

    SChPharmacist queryPharmacistByUserId(Integer user_id);

    int updateRemind(@Param("pharmacistId") int pharmacistId, @Param("siteId") int siteId, @Param("is_remind") int is_remind);
}

package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.OldAdvertisement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OldAdvertisementMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_adverisement
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer advId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_adverisement
     *
     * @mbg.generated
     */
    int insert(OldAdvertisement record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_adverisement
     *
     * @mbg.generated
     */
    int insertSelective(OldAdvertisement record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_adverisement
     *
     * @mbg.generated
     */
    OldAdvertisement selectByPrimaryKey(Integer advId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_adverisement
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(OldAdvertisement record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_adverisement
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(OldAdvertisement record);

    List<OldAdvertisement> getWxAdvertiseListBySiteId(@Param("siteId") Integer siteId);
}

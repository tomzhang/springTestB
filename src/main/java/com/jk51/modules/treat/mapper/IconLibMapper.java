package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.IconLib;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IconLibMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_iconlib
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("iconId") Integer iconId, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_iconlib
     *
     * @mbg.generated
     */
    int insert(IconLib record);

    int insertAndGet(IconLib record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_iconlib
     *
     * @mbg.generated
     */
    int insertSelective(IconLib record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_iconlib
     *
     * @mbg.generated
     */
    IconLib selectByPrimaryKey(@Param("iconId") Integer iconId, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_iconlib
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(IconLib record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_iconlib
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(IconLib record);

    /**
     * 获取图标库
     *
     * @return
     */
    List<IconLib> getList(@Param("siteId") Integer siteId);
}
package com.jk51.modules.registration.mapper;

import com.jk51.model.registration.models.ServceTemplate;
import com.jk51.model.registration.models.ServiceTemplateFormatDate;
import com.jk51.modules.registration.request.TemplateRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ServceTemplateMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_template
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_template
     *
     * @mbg.generated
     */
    int insert(ServceTemplate record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_template
     *
     * @mbg.generated
     */
    int insertSelective(ServceTemplate record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_template
     *
     * @mbg.generated
     */
    ServceTemplate selectByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_template
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ServceTemplate record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_servce_template
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ServceTemplate record);

    /**
     * 查询一个排班
     * @param templateNo
     * @param templateId
     * @return
     */
    ServceTemplate selectByNoAndId(@Param("templateNo") String templateNo, @Param("templateId") Integer templateId);

    /**
     *
     * @param storeId
     * @return
     */
    List<ServceTemplate> queryTemplateStoreId(@Param("store_id") Integer storeId);


    List<ServceTemplate> queryTemplateNo(@Param("store_id") Integer storeId,@Param("template_no")String templateNo );



    /**
     *
     * @param
     * @return
     */
    List<ServceTemplate> queryAllTemplateByStore(TemplateRequestParam templateRequestParam);
}
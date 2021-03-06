package com.jk51.modules.registration.mapper;

import com.jk51.model.registration.models.ContactPerson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ContactPersonMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_contact_person
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_contact_person
     *
     * @mbg.generated
     */
    int insert(ContactPerson record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_contact_person
     *
     * @mbg.generated
     */
    int insertSelective(ContactPerson record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_contact_person
     *
     * @mbg.generated
     */
    ContactPerson selectByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_contact_person
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ContactPerson record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_contact_person
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ContactPerson record);

    /**
     * @param   record
     * return list
     */

    List<ContactPerson>  queryAllContactPersonByMemberId(ContactPerson record);


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_contact_person
     *
     * @mbg.generated
     */
    int updateByMemberId(ContactPerson record);
}
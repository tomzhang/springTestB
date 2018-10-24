package com.jk51.modules.logistics.mapper;

import com.jk51.model.Address;
import com.jk51.modules.logistics.requestData.AddressRequire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {
    Address findDefault(@Param("buyerId") int buyerId, @Param("siteId") int siteId);

    int add(Address address);

    int userCount(@Param("buyerId")int buyerId, @Param("siteId")int siteId);

    boolean update(@Param("addressId")int addressId, @Param("siteId")int siteId, @Param("address") Address address);

    boolean setDefault(@Param("addressId")int addressId, @Param("siteId")int siteId);

    boolean cancelDefault(@Param("buyerId") int buyerId, @Param("siteId") int siteId);

    boolean deleteNotDefault(@Param("addressId")int addressId, @Param("siteId")int siteId);

    List<Address> findUserAddress(@Param("buyerId") int buyerId, @Param("siteId") int siteId);

    Address findOne(AddressRequire addressRequire);

    Address findAddressForProRule(@Param("addressId")int addressId, @Param("siteId")int siteId);
}
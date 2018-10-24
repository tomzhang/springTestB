package com.jk51.modules.logistics.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.Address;
import com.jk51.modules.logistics.mapper.AddressMapper;
import com.jk51.modules.logistics.requestData.AddressRequire;
import com.jk51.modules.logistics.requestData.ModifyAddress;
import com.jk51.modules.merchant.service.LabelSecondService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AddressService {
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    private LabelSecondService labelSecondService;

    public Address findDefault(int buyerId, int siteId) {
        return addressMapper.findDefault(buyerId, siteId);
    }

    public List<Address> find() {
        return new ArrayList<>();
    }

    @Transactional
    public boolean update(ModifyAddress modifyAddress) {

        //获取经纬度
        Address address = modifyAddress.getAddress();
        address = getLngAndLat(address);
        modifyAddress.setAddress(address);

        Boolean isUpdate = addressMapper.update(modifyAddress.getAddressId(), modifyAddress.getSiteId(), modifyAddress.getAddress());
        if (modifyAddress.getAddress().getIsDefault() == 1) {
            // 将其他的默认地址取消然后设置新增的为默认
            AddressRequire adr = modifyAddress;
            //setDefault(adr);
            addressMapper.cancelDefault(modifyAddress.getBuyerId(), modifyAddress.getSiteId());
            addressMapper.setDefault(modifyAddress.getAddressId(), modifyAddress.getSiteId());
        }

        return isUpdate;
    }

    /**
     * 设置默认地址
     * @return
     */
    @Transactional
    public boolean setDefault(AddressRequire addressRequire) {
        int buyerId = addressRequire.getBuyerId();
        int addressId = addressRequire.getAddressId();
        int siteId = addressRequire.getSiteId();

        addressMapper.cancelDefault(buyerId, siteId);
        return addressMapper.setDefault(addressId, siteId);
    }

    /**
     * 获取用户总共有多少条地址
     * @param buyerId
     * @param siteId
     * @return
     */
    public int userCount(int buyerId, int siteId) {
        return addressMapper.userCount(buyerId, siteId);
    }

    @Transactional
    public int add(Address address) {
        boolean lazySetDef = false;
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        int haveRecordNum = userCount(address.getBuyerId(), address.getSiteId());
        if (haveRecordNum == 0) {
            // 第一条地址设置为默认的
            address.setIsDefault(1);
        } else if (address.getIsDefault() == 1) {
            lazySetDef = true;
        }
        //获取经纬度
        address = getLngAndLat(address);
        addressMapper.add(address);

        if (lazySetDef) {
            // 将其他的默认地址取消然后设置新增的为默认
            AddressRequire adr = new AddressRequire();
            adr.setBuyerId(address.getBuyerId());
            adr.setSiteId(address.getSiteId());
            adr.setAddressId(address.getAddressId());
            setDefault(adr);
        }
//        setDefault(address);
        return address.getAddressId();
    }

    //获取经纬度
    public Address getLngAndLat(Address address){
        String addressTwo = address.getAddr();
        Map<String,Object> addrMap = new HashedMap();
        addrMap.put("province",address.getProvince());
        addrMap.put("city",address.getCity());
        addrMap.put("country",address.getCountry());
        Map<String, Object> map = labelSecondService.uodateAddressForGaode(addressTwo, addrMap);
        if (!StringUtil.isEmpty(map)) {
            String lng = String.valueOf(map.get("lng"));
            String lat = String.valueOf(map.get("lat"));
            address.setAddrLng(lng);
            address.setAddrLat(lat);
        }
        return address;
    }

    public boolean deleteNotDefault(AddressRequire addressRequire) {
        return addressMapper.deleteNotDefault(addressRequire.getAddressId(), addressRequire.getSiteId());
    }

    public List<Address> list(int buyerId, int siteId) {
        return addressMapper.findUserAddress(buyerId, siteId);
    }

    public Address findOne(AddressRequire addressRequire) {
        return addressMapper.findOne(addressRequire);
    }
}

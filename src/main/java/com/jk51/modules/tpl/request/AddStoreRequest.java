package com.jk51.modules.tpl.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jk51.commons.json.JacksonUtils;

/**
 * 取消订单对应的 字段
 */
public class AddStoreRequest extends AbstractRequest {
    private AddStoreRequestData data ;

    public static class AddStoreRequestData {

        //private String name;
//        @JsonProperty("contactPhone")
//        private String contactPhone;

        @JsonProperty("contact_phone")
        private String contact_phone;
        private String address;
        private String longitude;
        private String latitude;

        private String chain_store_code;
        private String chain_store_name;
        private Integer position_source;
        private Integer service_code;

//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }

//        public String getContactPhone() {
//            return contactPhone;
//        }
//
//        public void setContactPhone(String contactPhone) {
//            this.contactPhone = contactPhone;
//        }


        public String getContact_phone() {
            return contact_phone;
        }

        public void setContact_phone(String contact_phone) {
            this.contact_phone = contact_phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getChain_store_code() {
            return chain_store_code;
        }

        public void setChain_store_code(String chain_store_code) {
            this.chain_store_code = chain_store_code;
        }

        public String getChain_store_name() {
            return chain_store_name;
        }

        public void setChain_store_name(String chain_store_name) {
            this.chain_store_name = chain_store_name;
        }

        public Integer getPosition_source() {
            return position_source;
        }

        public void setPosition_source(Integer position_source) {
            this.position_source = position_source;
        }

        public Integer getService_code() {
            return service_code;
        }

        public void setService_code(Integer service_code) {
            this.service_code = service_code;
        }
    }


    public String getData() throws Exception {
        return JacksonUtils.obj2json(data);
    }

    public void setData(AddStoreRequestData data) {
        this.data = data;
    }
}

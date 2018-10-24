package com.jk51.modules.pandian.dto;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018-03-30
 * 修改记录:
 */
public class PandianTimeDto {

    private String mobile_type;
    private Date click_scaner_time;
    private Date send_request_time;
    private Date get_request_time;
    private Date response_time;

    public PandianTimeDto(){

    }

    PandianTimeDto(Builder builder) {
        this.mobile_type = builder.mobile_type;
        this.click_scaner_time = builder.click_scaner_time;
        this.send_request_time = builder.send_request_time;
        this.get_request_time = builder.get_request_time;
        this.response_time = builder.response_time;
    }

    public String getMobile_type() {
        return mobile_type;
    }

    public Date getClick_scaner_time() {
        return click_scaner_time;
    }

    public Date getSend_request_time() {
        return send_request_time;
    }

    public Date getGet_request_time() {
        return get_request_time;
    }

    public Date getResponse_time() {
        return response_time;
    }


    public static class Builder{

          String mobile_type;
          Date click_scaner_time;
          Date send_request_time;
          Date get_request_time;
          Date response_time;

          public Builder(){

          }

          public Builder mobileType(String mobile_type){
              if(mobile_type==null){
                  throw new IllegalArgumentException("mobile_type 不能为空");
              }
              this.mobile_type = mobile_type;
              return this;
          }

        public Builder clickScanerTime(Date click_scaner_time){
            if(click_scaner_time==null){
                throw new IllegalArgumentException("click_scaner_time 不能为空");
            }
            this.click_scaner_time = click_scaner_time;
            return this;
        }

        public Builder sendRequestTime(Date send_request_time){
            if(send_request_time==null){
                throw new IllegalArgumentException("send_request_time 不能为空");
            }
            this.send_request_time = send_request_time;
            return this;
        }

        public Builder getRequestTime(Date get_request_time){
            if(get_request_time==null){
                throw new IllegalArgumentException("get_request_time 不能为空");
            }
            this.get_request_time = get_request_time;
            return this;
        }

        public Builder responseTime(Date response_time){
            if(response_time==null){
                throw new IllegalArgumentException("response_time 不能为空");
            }
            this.response_time = response_time;
            return this;
        }

          public PandianTimeDto build(){

              if(response_time==null){
                  throw new IllegalArgumentException("response_time 不能为空");
              }
              if(get_request_time==null){
                  throw new IllegalArgumentException("get_request_time 不能为空");
              }
              if(send_request_time==null){
                  throw new IllegalArgumentException("send_request_time 不能为空");
              }
              if(click_scaner_time==null){
                  throw new IllegalArgumentException("click_scaner_time 不能为空");
              }
              if(mobile_type==null){
                  throw new IllegalArgumentException("mobile_type 不能为空");
              }

              return new PandianTimeDto(this);
          }
    }
}

package com.jk51.model.account.requestParams;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/10-03-10
 * 修改记录 :
 */

public class PayDataImportParams {
      private  String trades_id;
      private  Integer  status;//结算状态
      private  Integer  invoice;//开票状态
      private  String   pay_style;//支付类型

      public String getTrades_id() {
            return trades_id;
      }

      public void setTrades_id(String trades_id) {
            this.trades_id = trades_id;
      }

      public Integer getStatus() {
            return status;
      }

      public void setStatus(Integer status) {
            this.status = status;
      }

      public Integer getInvoice() {
            return invoice;
      }

      public void setInvoice(Integer invoice) {
            this.invoice = invoice;
      }

      public String getPay_style() {
            return pay_style;
      }

      public void setPay_style(String pay_style) {
            this.pay_style = pay_style;
      }
}

# === begin create table ===
CREATE TABLE IF NOT EXISTS `b_prebook_new` (
  `site_id` int(16) NOT NULL COMMENT '商户ID',
  `prebook_number` varchar(64) NOT NULL COMMENT '预约编号',
  `prebook_trades` varchar(32) NOT NULL DEFAULT '' COMMENT '订单交易号',
  `prebook_phone` varchar(16) NOT NULL DEFAULT '' COMMENT '用户手机号码',
  `prebook_goods_id` int(16) NOT NULL COMMENT '商品 ID',
  `prebook_goods_name` varchar(32) NOT NULL DEFAULT '' COMMENT '商品名称',
  `prebook_goods_num` int(16) NOT NULL DEFAULT '1' COMMENT '预约商品的购买数量',
  `prebook_style` int(8) NOT NULL DEFAULT '0' COMMENT '预约方式(0 门店自提, 1 送货上门)',
  `prebook_storeId` int(8) DEFAULT NULL COMMENT '预约的门店自提ID(只有门店自提方式才会有此门店ID)',
  `prebook_addressId` int(8) DEFAULT NULL COMMENT '预约收货地址的ID',
  `prebook_address` varchar(128) NOT NULL DEFAULT '' COMMENT '根据预约方式判断是客户地址还是门店地址',
  `prebook_status` int(8) NOT NULL DEFAULT '0' COMMENT '0 未关闭 1 已关闭',
  `receiver_name` varchar(16) DEFAULT '' COMMENT '名称',
  `receiver_city_code` varchar(8) DEFAULT '' COMMENT '城市编码',
  `receiver_province_code` varchar(8) DEFAULT '' COMMENT '省份编码',
  `receiver_country_code` varchar(8) DEFAULT '' COMMENT '乡镇编码',
  `receiver_address` varchar(64) DEFAULT '' COMMENT '地址',
  `receiver_mobile` varchar(16) DEFAULT '' COMMENT '电话',
  `receiver_phone` varchar(16) DEFAULT '' COMMENT '手机',
  `receiver_zip` varchar(32) DEFAULT '',
  `prebook_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`prebook_number`)
) COMMENT = '预约单详情';
TRUNCATE TABLE `b_prebook_new`;
# === end create table ===

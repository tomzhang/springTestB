CREATE TABLE IF NOT EXISTS `b_goods_erp` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL,
  `goods_id` int UNSIGNED NOT NULL DEFAULT 0,
  `goods_code` varchar(50) NOT NULL COMMENT '商品编码',
  `price` int UNSIGNED NOT NULL,
  `store_id` int NOT NULL,
  `store_number` varchar(30) NOT NULL,
  `store_name` varchar(200) NOT NULL,
  `province` varchar(60) NOT NULL,
  `city` varchar(60) NOT NULL,
  `country` varchar(60) NOT NULL,
  `status` tinyint NOT NULL DEFAULT 10 COMMENT '10 有效  20 删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) ,
  INDEX `idx_site_store_goods_id` (`site_id` ASC, `goods_id` ASC, `store_id` ASC)
)
COMMENT = '门店ERP价格';
TRUNCATE TABLE `b_goods_erp`;

CREATE TABLE IF NOT EXISTS `erp_price_setting` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL,
  `type` tinyint NOT NULL DEFAULT 10 COMMENT '10 总部基础价 20 市级价 30 区级价 40 门店价',
  `store_id` int NOT NULL COMMENT '基准价的门店ID',
  `area_code` mediumint UNSIGNED NOT NULL COMMENT '根据type记录行政区划代码 40 不需要记录',
  `priority` tinyint UNSIGNED NOT NULL DEFAULT 100 COMMENT '优先级  ',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 10 COMMENT '状态值 10 有效 20无效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
COMMENT = '商户erp价格设置';
TRUNCATE TABLE `erp_price_setting`;

CREATE TABLE IF NOT EXISTS `b_erp_setting` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL,
  `type` tinyint NOT NULL DEFAULT 10 COMMENT '10 总部基础价 20 市级价 30 区级价 40 门店价',
  `is_joint` tinyint UNSIGNED NOT NULL DEFAULT 10 COMMENT '状态值 10 对接 20 不对接',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `uk_site` (`site_id` ASC)
)
COMMENT = '商户erp设置';
TRUNCATE TABLE `b_erp_setting`;

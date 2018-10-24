CREATE TABLE `b_concern` (
`id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关注表',
`site_id` MEDIUMINT UNSIGNED NOT NULL COMMENT '商家id',
`type_pk` INT UNSIGNED NOT NULL COMMENT '店员id对应b_store_admin.id/门店id对应b_stores.id/商户site_id',
`type` TINYINT NOT NULL COMMENT '扫码类型 1店员 2门店 3商户',
`open_id` VARCHAR(32) NOT NULL COMMENT '用户帐号',
`scene_str` VARCHAR(255) COMMENT '二维码场景值',
`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
PRIMARY KEY (`id`)
)
COMMENT = '关注表';
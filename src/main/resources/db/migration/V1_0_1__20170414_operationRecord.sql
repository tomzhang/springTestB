ALTER TABLE `d_operation_recond`
ADD COLUMN `remark`  varchar(255) NULL DEFAULT '' COMMENT '操作备注' AFTER `autding_status`,
ADD COLUMN `snapshot`  varchar(255) NULL DEFAULT '' COMMENT '修改之前的记录快照  存放json串' AFTER `remark`;
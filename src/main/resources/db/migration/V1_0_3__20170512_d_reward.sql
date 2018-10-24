ALTER TABLE `d_reward`
ADD COLUMN `distributor_father_id`  int(11) NOT NULL DEFAULT 0 COMMENT '分销商上级id（无上级为0）' AFTER `distributor_id`,
ADD COLUMN `distributor_grandfather_id`  int(11) NOT NULL DEFAULT 0 COMMENT '分销商上上级id（无上上级为0）' AFTER `distributor_father_id`;
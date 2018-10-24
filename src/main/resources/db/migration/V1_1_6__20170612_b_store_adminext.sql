ALTER TABLE `b_store_adminext`
ADD COLUMN `qrcode_url`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT '店员推荐公众号二维码url' AFTER `clerk_invitation_code`;
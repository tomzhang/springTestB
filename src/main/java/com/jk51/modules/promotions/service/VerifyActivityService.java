package com.jk51.modules.promotions.service;

import com.jk51.model.promotions.PromotionsActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * filename :com.jk51.modules.promotions.service.
 * author   :zw
 * date     :2017/8/11
 * Update   :
 */
@Service
public class VerifyActivityService {
    @Autowired
    private PromotionsActivityService service;

    /**
     * 判断活动发放时间是否过期
     *
     * @param promotionsActivity
     * @return
     */
    public boolean verifyEndtime(PromotionsActivity promotionsActivity) {
        LocalDateTime endTime = promotionsActivity.getEndTime();
        return endTime.isBefore(LocalDateTime.now());
    }

    /**
     * @param promotionsActivity
     * @return true 已经超过发布时间， false 代表还未到发布时间
     */
    public boolean verifyStarTime(PromotionsActivity promotionsActivity) {
        LocalDateTime startTime = promotionsActivity.getStartTime();
        return startTime.isBefore(LocalDateTime.now());
    }

    /**
     * 检验状态
     *
     * @param promotionsActivity
     * @return
     */
    public boolean verifyStatus(PromotionsActivity promotionsActivity) {
        return service.autoChangeStatus(promotionsActivity.getSiteId(), promotionsActivity.getId());
    }
}

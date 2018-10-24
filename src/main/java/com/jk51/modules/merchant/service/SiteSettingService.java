package com.jk51.modules.merchant.service;

import com.jk51.modules.merchant.mapper.SiteSettingMapper;
import com.jk51.modules.merchant.request.QRcodeTips;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SiteSettingService {
    @Autowired
    SiteSettingMapper siteSettingMapper;

    public boolean setQRcodeTips(QRcodeTips qRcodeTips) {
        return siteSettingMapper.setQRcodeTips(qRcodeTips);
    }

    public Map getQRcodeTips(QRcodeTips qRcodeTips) {
        return siteSettingMapper.getQRcodeTips(qRcodeTips);
    }
}

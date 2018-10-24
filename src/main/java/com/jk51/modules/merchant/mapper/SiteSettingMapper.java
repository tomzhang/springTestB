package com.jk51.modules.merchant.mapper;

import com.jk51.modules.merchant.request.QRcodeTips;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SiteSettingMapper {

    boolean setQRcodeTips(QRcodeTips qRcodeTips);

    Map getQRcodeTips(QRcodeTips qRcodeTips);
}

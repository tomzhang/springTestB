package com.jk51.modules.merchant.service;

import com.jk51.model.YbMeta;
import com.jk51.modules.merchant.mapper.YbMetaMapper;
import com.jk51.modules.merchant.request.GoodsIconStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class YbMetaService {
    @Autowired
    YbMetaMapper ybMetaMapper;

    @Transactional
    public boolean setGoodsIconStatus(GoodsIconStatus gis) {
        for (String metaKey : getCategoryIconsKeys(gis.getPlatform())) {
            YbMeta record = ybMetaMapper.findFirst(gis.getSiteId(), metaKey);
            if (record == null) {
                record = new YbMeta();
                record.setSiteId(gis.getSiteId());
                record.setMetaKey(metaKey);
                record.setMetaVal(gis.getStatus() + "");
                ybMetaMapper.save(record);
            } else {
                record.setMetaVal(gis.getStatus() + "");
                ybMetaMapper.update(record);
            }
        }

        return true;
    }

    public List<Map> getGoodsIconStatus(GoodsIconStatus gis) {
        List<String> categoryIconsKeys = getCategoryIconsKeys(gis.getPlatform());
        return ybMetaMapper.findMetaKey(categoryIconsKeys, gis.getSiteId());
    }

    private List<String> getCategoryIconsKeys(int platform) {
        List<String> categoryIconsKeys = new ArrayList<>();
        switch (platform) {
            case 110:
                categoryIconsKeys.add("pcShowCategoryIcons");
                break;
            case 120:
                categoryIconsKeys.add("wxShowCategoryIcons");
                break;
            case 130:
                categoryIconsKeys.add("appShowCategoryIcons");
                break;
            case 9999:
                categoryIconsKeys.add("pcShowCategoryIcons");
                categoryIconsKeys.add("wxShowCategoryIcons");
                categoryIconsKeys.add("appShowCategoryIcons");
                break;
        }

        return categoryIconsKeys;
    }
}

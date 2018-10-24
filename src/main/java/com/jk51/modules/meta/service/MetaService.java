package com.jk51.modules.meta.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.model.order.SMeta;
import com.jk51.model.theme.WxTheme;
import com.jk51.modules.persistence.mapper.StmpMetaMapper;
import com.jk51.modules.persistence.mapper.WxThemeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-27
 * 修改记录:
 */
@Service
public class MetaService {
    @Autowired
    private StmpMetaMapper sMetaMapper;
    @Autowired
    private WxThemeMapper wxThemeMapper;

    public Integer addmeta(SMeta sMeta) {
        return sMetaMapper.addMeta(sMeta);
    }

    @Transactional
    public Integer addmeta2(SMeta sMeta) {
        Integer x = 0;
        if(sMeta.getMetaVal() == null || "".equals(sMeta.getMetaVal())){
            SMeta meta1 = sMetaMapper.selectByMetaTypeAndKey(sMeta.getSiteId(), "merchant_page", "merchant_context", sMeta.getThemeId());
            if(meta1 != null){
                sMeta.setMetaVal(meta1.getMetaVal());
            }else{
                SMeta meta2 = sMetaMapper.selectByMetaTypeAndKey(sMeta.getSiteId(), "merchant_draft_page", "merchant_draft_context", sMeta.getThemeId());
                if(meta2 != null){
                    sMeta.setMetaVal(meta2.getMetaVal());
                }
            }
        }

        x = sMetaMapper.addMeta(sMeta);
        if(x==1){
            WxTheme theme = new WxTheme();
            theme.setThemeId(sMeta.getThemeId());
            theme.setSiteId(sMeta.getSiteId());
            theme.setCreateTime(DateUtils.getNowTimestamp());
            wxThemeMapper.update(theme);
        }

        return x;
    }

    public SMeta selectByMetaTypeAndKey(Integer siteId, String metaType, String metaKey, Integer themeId) {
        return sMetaMapper.selectByMetaTypeAndKey(siteId, metaType, metaKey, themeId);
    }
    public List<SMeta> selectMetesTypeAndKey(Integer siteId, String metaType, String metaKey) {
        return sMetaMapper.selectMetesTypeAndKey(siteId, metaType, metaKey);
    }
    public Integer updateMetaByMetaTypeMetaKey(SMeta meta) {
        return sMetaMapper.updateMetaByMetaTypeMetaKey(meta);
    }

    @Transactional
    public Integer updateMetaByMetaTypeMetaKey2(SMeta meta) {
        Integer x = 0;
        if(meta.getMetaVal() == null){
            SMeta meta2 = sMetaMapper.selectByMetaTypeAndKey(meta.getSiteId(), "merchant_draft_page", "merchant_draft_context", meta.getThemeId());
            if(meta2 != null){
                meta.setMetaVal(meta2.getMetaVal());
            }
        }

        x = sMetaMapper.updateMetaByMetaTypeMetaKey(meta);

        WxTheme theme = new WxTheme();
        theme.setThemeId(meta.getThemeId());
        theme.setSiteId(meta.getSiteId());

        //直接点击发布则需要更新草稿
        if("merchant_context".equals(meta.getMetaKey())){
            SMeta meta2 = sMetaMapper.selectByMetaTypeAndKey(meta.getSiteId(), "merchant_draft_page", "merchant_draft_context", meta.getThemeId());
            if(meta2 != null){
                meta2.setMetaVal(meta.getMetaVal());
                x = sMetaMapper.updateMetaByMetaTypeMetaKey(meta2);
            }
            theme.setUpdateTime(DateUtils.getNowTimestamp());
            theme.setCreateTime(DateUtils.getNowTimestamp());
        }else{
            theme.setUpdateTime(DateUtils.getNowTimestamp());
        }

        if(theme != null){
            wxThemeMapper.update(theme);
        }


        return x;
    }

    public Integer updateByPrimaryKeys(SMeta meta) {
        return sMetaMapper.updateByPrimaryKeys(meta);
    }

    public SMeta queryMeta(int siteId,String metaType){ return sMetaMapper.selectBysiteIdAndMetaType(siteId, metaType);}

    public SMeta findBySiteIdAndMetaId(int siteId, int metaId){ return sMetaMapper.findBySiteIdAndMetaId(siteId, metaId);}

    public int updateMeta(SMeta meta){ return sMetaMapper.updateMeta(meta);}
}

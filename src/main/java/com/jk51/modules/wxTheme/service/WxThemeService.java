package com.jk51.modules.wxTheme.service;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.SMeta;
import com.jk51.model.theme.WxTheme;
import com.jk51.model.theme.WxThemeParm;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.meta.service.MetaService;
import com.jk51.modules.persistence.mapper.WxThemeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/8/7
 * 修改记录:
 */
@Service
public class WxThemeService {

    @Resource private WxThemeMapper themeMapper;
    @Resource private MetaService metaService;
    @Autowired private YbMerchantMapper merchantMapper;

    public List<WxTheme> getLstBySiteId(WxThemeParm parm) {
        List<WxTheme> lst = themeMapper.getLstBySiteId(parm);
        return lst;
    }

    public Integer update(WxTheme theme) {

        return themeMapper.update(theme);
    }

    @Transactional
    public Integer add(WxTheme theme) {
        Integer x = 0;
        //如果theme.getThemeId()存在则说明是  先保存草稿再进行发布  否则走else 直接点击发布然后自动生成草稿数据
        if(theme.getThemeId() != null){
            SMeta meta = new SMeta();
            meta.setMetaType("merchant_page");
            meta.setMetaKey("merchant_context");
            meta.setSiteId(theme.getSiteId());
            meta.setMetaVal(theme.getMetaVal());
            meta.setMetaStatus(1);
            meta.setThemeId(theme.getThemeId());
            metaService.addmeta(meta);

            WxTheme theme2 = new WxTheme();

            SMeta meta2 = metaService.selectByMetaTypeAndKey(meta.getSiteId(), "merchant_draft_page", "merchant_draft_context", meta.getThemeId());
            if(meta2 != null){
                meta2.setMetaVal(meta.getMetaVal());
                theme2.setUpdateTime(DateUtils.getNowTimestamp());
                x = metaService.updateMetaByMetaTypeMetaKey(meta2);
            }


            theme2.setSiteId(theme.getSiteId());
            theme2.setThemeId(theme.getThemeId());
            theme2.setCreateTime(DateUtils.getNowTimestamp());
            themeMapper.update(theme2);

        }else {
            theme.setUpdateTime(DateUtils.getNowTimestamp());
            theme.setCreateTime(DateUtils.getNowTimestamp());
            x = themeMapper.add(theme);//保存主题
            //保存主题数据
            if(x>0){
                SMeta meta = new SMeta();
                meta.setMetaType("merchant_page");
                meta.setMetaKey("merchant_context");
                meta.setSiteId(theme.getSiteId());
                meta.setMetaVal(theme.getMetaVal());
                meta.setMetaStatus(1);
                meta.setThemeId(theme.getThemeId());
                x = metaService.addmeta(meta);
                //保存主题草稿数据
                if(x==1){
                    SMeta meta2 = new SMeta();
                    meta2.setMetaType("merchant_draft_page");
                    meta2.setMetaKey("merchant_draft_context");
                    meta2.setSiteId(theme.getSiteId());
                    meta2.setMetaVal(theme.getMetaVal());
                    meta2.setMetaStatus(1);
                    meta2.setThemeId(theme.getThemeId());
                    metaService.addmeta(meta2);
                }

            }
        }

        return theme.getThemeId();
    }

    @Transactional
    public Integer addAndDraft(WxTheme theme) {
        theme.setUpdateTime(DateUtils.getNowTimestamp());
        Integer x = themeMapper.add(theme);
        if(x>0){
            SMeta meta = new SMeta();
            meta.setMetaType("merchant_draft_page");
            meta.setMetaKey("merchant_draft_context");
            meta.setSiteId(theme.getSiteId());
            meta.setMetaVal(theme.getMetaVal());
            meta.setMetaStatus(1);
            meta.setThemeId(theme.getThemeId());
            metaService.addmeta(meta);
        }

        return theme.getThemeId();
    }

    @Transactional
    public Integer del(WxTheme theme) {

        Integer x = themeMapper.del(theme);//软删除

//        if(x==1){
//            // 删除成功后 再删除b_meta表的数据以及b_wx_ads数据
//            x = metaService.delMetaBySiteidAndThemeid(theme.getSiteId(),theme.getThemeId());
//            if(x>0){
//
//            }
//        }

        return x;
    }

    public WxTheme getWxTheme(WxTheme theme) {

        return themeMapper.getWxTheme(theme);
    }

    public String getWechatUrl(Integer siteId) {
        YbMerchant ybMerchant = merchantMapper.selectBySiteId(siteId);
        if(ybMerchant != null){
            return ybMerchant.getShopwx_url();
        }
        return null;
    }
}

package com.jk51.modules.merchant.service;

import com.jk51.modules.merchant.mapper.MerchantAliTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/6/20
 * 修改记录:
 */
@Service
public class MerchantAliTemplateService {

    @Autowired
    private MerchantAliTemplateMapper merchantAliTemplateMapper;


    public Integer add(Integer siteId, String templateName, String templateId) {

        return merchantAliTemplateMapper.add(siteId, templateName, templateId);
    }

    public Integer upd(Integer siteId, Integer id, String templateId) {
        return merchantAliTemplateMapper.upd(siteId,id,templateId);
    }

    public Integer del(Integer siteId, Integer id, Integer isdel) {
        return merchantAliTemplateMapper.del(id,isdel);
    }

    public List<Map> getAliTemplateLst(Integer siteId) {
        return merchantAliTemplateMapper.getAliTemplateLst(siteId);
    }

    /**
     * 根据名称获取模板消息
     * @param siteId
     * @param templateName
     * @return
     */
    public String getAliTemplateIdBySiteIdAndName(Integer siteId, String templateName){
        return merchantAliTemplateMapper.getAliTemplateIdBySiteIdAndName(siteId, templateName);
    }

}

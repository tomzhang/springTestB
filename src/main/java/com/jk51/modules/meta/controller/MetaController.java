package com.jk51.modules.meta.controller;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.order.SMeta;
import com.jk51.modules.meta.service.MetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-27
 * 修改记录:
 */
@Controller
@RequestMapping("meta")
public class MetaController {

    public static final Logger logger = LoggerFactory.getLogger(MetaController.class);

    @Autowired
    private MetaService metaService;

    @ResponseBody
    @PostMapping("saveIndexPage")
    public Integer saveIndexPage(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        logger.info("####saveIndexPage####{}", objectMap);
        logger.info("####saveIndexPage####{}", objectMap.get("meta").toString());
        SMeta meta = new SMeta();
        try {
            meta = JacksonUtils.json2pojo(objectMap.get("meta").toString(), SMeta.class);
        } catch (Exception e) {
            return 0;
        }
        return metaService.addmeta(meta);
    }


    @PostMapping("saveIndexPage2")
    public @ResponseBody Integer saveIndexPage2(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        logger.info("####saveIndexPage####{}", objectMap);
        logger.info("####saveIndexPage####{}", objectMap.get("meta").toString());
        SMeta meta = new SMeta();
        try {
            meta = JacksonUtils.json2pojo(objectMap.get("meta").toString(), SMeta.class);
        } catch (Exception e) {
            return 0;
        }
        return metaService.addmeta2(meta);
    }

    @ResponseBody
    @PostMapping("selectByMetaType")
    public SMeta selectByMetaType(Integer siteId, String metaType, String metaKey, Integer themeId) {
        return metaService.selectByMetaTypeAndKey(siteId, metaType, metaKey, themeId);
    }

    @ResponseBody
    @PostMapping("selectByMetaId")
    public SMeta selectByMetaId(Integer siteId, Integer metaId) {
        return metaService.findBySiteIdAndMetaId(siteId, metaId);
    }

    @ResponseBody
    @PostMapping("selectMetesTypeAndKey")
    public List<SMeta> selectMetesTypeAndKey(Integer siteId, String metaType, String metaKey) {
        return metaService.selectMetesTypeAndKey(siteId, metaType, metaKey);
    }

    @ResponseBody
    @PostMapping("updateByMetaTypeAndMetaKey")
    public Integer updateByMetaTypeAndMetaKey(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        SMeta meta = new SMeta();
        try {
            meta = JacksonUtils.json2pojo(objectMap.get("meta").toString(), SMeta.class);
        } catch (Exception e) {
            return 0;
        }

        return metaService.updateMetaByMetaTypeMetaKey(meta);
    }

    /**
     * 新版微信主题修改操作
     * @param request
     * @return
     */
    @PostMapping("updateByMetaTypeAndMetaKey2")
    public @ResponseBody Integer updateByMetaTypeAndMetaKey2(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        SMeta meta = new SMeta();
        try {
            meta = JacksonUtils.json2pojo(objectMap.get("meta").toString(), SMeta.class);
        } catch (Exception e) {
            return 0;
        }

        return metaService.updateMetaByMetaTypeMetaKey2(meta);
    }

    @ResponseBody
    @PostMapping("updateByPrimaryKeys")
    public Integer updateByPrimaryKeys(HttpServletRequest request) {
        Map<String, Object> objectMap = ParameterUtil.getParameterMap(request);
        SMeta meta = new SMeta();
        try {
            meta = JacksonUtils.json2pojo(objectMap.get("meta").toString(), SMeta.class);
        } catch (Exception e) {
            return 0;
        }
        return metaService.updateByPrimaryKeys(meta);
    }
}

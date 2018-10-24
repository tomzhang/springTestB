package com.jk51.modules.merchant.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.merchant.Ask;
import com.jk51.modules.persistence.mapper.AskMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/19.
 */
@Service
public class AskService {

    public static final Logger log = LoggerFactory.getLogger(AskService.class);
    @Autowired
    private AskMapper askMapper;

    public Map<String,Object> insertAsk(Ask ask) {

        Map<String,Object> map = new HashedMap();
        Integer siteId = ask.getSiteId();
        getSiteId(siteId);  //判断siteId是否有值
        try{
            if (!StringUtil.isEmpty(replaceBlank(ask.getQuestions()))){
                //执行添加
                Integer i = askMapper.insertAsk(ask);
                if (1 == i){
                    map.put("code",200);
                    map.put("msg","success");
                    return map;
                }
                map.put("code",500);
                map.put("msg","问答插入失败");
                return map;
            }else {
                map.put("code",400);
                map.put("msg","没有输入问题");
                return map;
            }

        }catch (Exception e){
            log.error("问答插入失败:{}",e);
        }
        return map;
    }

    /**
     * 查询全部标签
     * @return
     */
    public Map<String,Object> getAskAll(Integer siteId){
        Map<String,Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            List<Ask> askList = askMapper.getAskAll(siteId);
            if (!StringUtil.isEmpty(askList) && 0 != askList.size()){
                map.put("askList",askList);
                map.put("code",200);
                map.put("msg","success");
                return map;
            }
            map.put("code",500);
            map.put("msg","查询失败");
            return map;
        }catch (Exception e){
            log.error("问答查询失败:{}",e);
        }
        return map;
    }

    /**
     * 根据ID查询单个标签
     * @return
     */
    public Map<String, Object> getAskById(Integer siteId,Integer id){
        Map<String, Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            List<Ask> askList = askMapper.getAskById(siteId,id);
            if (!StringUtil.isEmpty(askList)){
                map.put("code",200);
                map.put("msg","success");
                map.put("askList",askList);
                return map;
            }
            map.put("code",500);
            map.put("msg","查询失败");
            return map;
        }catch (Exception e){
            log.error("问答查询失败:{}",e);
            return null;
        }
    }

    /**
     * 修改标签
     * @return
     */
    @Transactional
    public Map<String,Object> updateAsk(Ask ask){
        Map<String,Object> map = new HashedMap();
        Integer siteId = ask.getSiteId();
        getSiteId(siteId);
        try{
            if (!StringUtil.isEmpty(replaceBlank(ask.getQuestions()))){
                Integer i = askMapper.updateAsk(ask);
                if(1 == i) {
                    map.put("code", 200);
                    map.put("msg", "修改成功");
                    return map;
                }else {
                    map.put("code",500);
                    map.put("msg","修改失败");
                }
            }else {
                map.put("code", 400);
                map.put("msg", "没有输入问题");
                return map;
            }
        }catch (Exception e){
            log.error("问答修改失败:{}",e);
            map.put("code",500);
            map.put("msg","修改失败");
        }
        return map;
    }

    /**
     * 删除标签
     * @return
     */
    public Map<String,Object> deleteAsk(Integer siteId,Integer id){
        Map<String,Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            askMapper.deleteAsk(siteId,id);
            map.put("code",200);
            map.put("msg","删除成功");
            return map;
        }catch (Exception e){
            log.error("问答删除失败:{}",e);
        }
        map.put("code",500);
        map.put("msg","删除失败");
        return map;

    }

    /**
     * 按标签名称模糊查询
     * @return
     */
    public Map<String,Object> getAskByName(Integer siteId,String dimName){
        Map<String,Object> map = new HashedMap();
        getSiteId(siteId);
        try{
            List<Ask> askList = askMapper.getAskByName(siteId,dimName);
            if (!StringUtil.isEmpty(askList)  && 0 != askList.size()){
                map.put("askList",askList);
                map.put("code",200);
                map.put("msg","success");
                return map;
            }
            map.put("code",500);
            map.put("msg","查询失败");
            return map;
        }catch (Exception e){
            log.error("问答查询失败:{}",e);
        }
        return map;
    }




    //判断siteId是否为空
    public Map<String,Object> getSiteId(Integer siteId){
        Map<String,Object> map = new HashedMap();
        if (StringUtil.isEmpty(siteId)){
            map.put("code",500);
            map.put("msg","siteId接收失败");
            return map;
        }
        return null;
    }

    //字符串去空格
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}

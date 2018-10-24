package com.jk51.modules.pandian.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.pandian.error.InventoryConfirmInfo2JaonException;
import com.jk51.modules.pandian.param.InventoryConfirmInfo;
import com.jk51.modules.pandian.param.InventoryConfirmParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.jk51.modules.pandian.util.Constant.ONCE;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-30
 * 修改记录:
 */
@Component
public class InventoryRedisManager {

    private Logger logger = LoggerFactory.getLogger(InventoryRedisManager.class);

    @Autowired
    private StringRedisTemplate template;


    /**
     *确认confirmInfo是否有保存记录
     **/
    public boolean notConfirmInfo(InventoryConfirmParam param) {

        String key = getConfirmInfoRedisKey(param);

        if(StringUtil.isEmpty(template.opsForValue().get(key))){

            saveConfirmInfo(key);
            return true;
        }

        deleteConfirmInfo(key);

        return false;
    }

    /**
     * 保存盘点确认信息
     *
     * */
    /*public void saveConfirmInfo(InventoryConfirmParam param) {

        String key = getConfirmInfoRedisKey(param);

        template.opsForValue().set(key,ONCE);
    }*/

    private void saveConfirmInfo(String key){
        template.opsForValue().set(key,ONCE);
    }

    /**
     * 删除盘点信息
     * */
    public void deleteConfirmInfo(InventoryConfirmParam param){

        String key = getConfirmInfoRedisKey(param);
        template.delete(key);
    }

    private void deleteConfirmInfo(String key){
        template.delete(key);
    }




    private String getConfirmInfoRedisKey(InventoryConfirmParam param){


        InventoryConfirmInfo info = new InventoryConfirmInfo();
        info.setPandian_num(param.getPandian_num());
        info.setGoods_code(param.getGoods_code());
        info.setStoreAdminId(param.getStoreAdminId());

        String str = "";

        try {
            str = JacksonUtils.obj2json(info);
        } catch (Exception e) {
            logger.error("对象转换Jason异常, info:{},保错信息：{}",info,e.getMessage());

            throw new InventoryConfirmInfo2JaonException("InventoryConfirmInfo 对象转换Jason异常");
        }

        return str ;
    }
}

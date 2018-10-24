package com.jk51.modules.im.expires;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/28
 * 修改记录:
 */
public interface ExpireRedisKeyManager {

    boolean exits(String key);

    void addKey(String key,Map<String,String> value);

    void delete(String str);

    String getValue(String key,String hashKey);
}

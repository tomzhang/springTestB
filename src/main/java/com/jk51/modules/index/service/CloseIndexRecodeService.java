package com.jk51.modules.index.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.CloseIndexRecode;
import com.jk51.modules.index.mapper.CloseIndexRecodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-31
 * 修改记录:
 */
@Service
public class CloseIndexRecodeService {

    @Autowired
    private CloseIndexRecodeMapper closeIndexRecodeMapper;

    @Cacheable(value="closeIndexRecode",keyGenerator = "defaultKeyGenerator")
    public List<CloseIndexRecode> findCloseIndexRecodeBySenderAndSiteId(String sender, int site_id, Date now, Date before) {

       return closeIndexRecodeMapper.findCloseIndexRecodeBySenderAndSiteId(sender,site_id,now,before);
    }
}

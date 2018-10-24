package com.jk51.modules.goods.service;

import com.jk51.model.goods.YbCategory;
import com.jk51.modules.goods.mapper.YbCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-04-04
 * 修改记录:
 */
@Service
public class YbCategoryService {

    @Autowired
    private YbCategoryMapper ybCategoryMapper;


    @CacheEvict(value="ybCategory",allEntries = true)
    public int addGoodsClassify(YbCategory category) {

        return ybCategoryMapper.insert(category);
    }

    public int updateByprimaryKey(YbCategory category) {
        return ybCategoryMapper.updateByprimaryKey(category);
    }


    public String selectCateName(Integer cateId) {
        return ybCategoryMapper.selectCateNameById(cateId);
    }
}

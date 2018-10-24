package com.jk51.modules.erpprice.domain;

import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.erpprice.BGoodsErpExample;
import com.jk51.modules.erpprice.mapper.BGoodsErpMapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * 门店ERP价格
 */
public class LatelyStoreSearchStrategy implements SearchStrategy {
    private final BGoodsErpExample erpExample = new BGoodsErpExample();

    public LatelyStoreSearchStrategy(Integer siteId, Integer erpStoreId, List<Integer> goodsIds) {
        BGoodsErpExample.Criteria criteria = erpExample.createCriteria()
            .andSiteIdEqualTo(siteId)
            .andStoreIdEqualTo(erpStoreId);

        if (CollectionUtils.isNotEmpty(goodsIds)) {
            criteria.andGoodsIdIn(goodsIds).andStatusEqualTo((byte) 10);
        }
    }

    @Override
    public List<BGoodsErp> search(BGoodsErpMapper mapper) {
        return mapper.selectByExample(erpExample);
    }

    @Override
    public Map<Integer, BGoodsErp> convertMap(List<BGoodsErp> erpGoods) {
        return erpGoods.stream().collect(HashMap::new, (m, e) -> m.put(e.getGoodsId(), e), (m1, m2) -> m1.putAll(m2));
    }
}

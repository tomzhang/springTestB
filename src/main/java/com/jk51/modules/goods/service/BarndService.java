package com.jk51.modules.goods.service;

import com.jk51.model.Barnd;
import com.jk51.modules.goods.mapper.BarndMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BarndService {
    @Autowired
    BarndMapper barndMapper;

    public Barnd findById(int barndId, int siteId) {
        return barndMapper.findById(barndId, siteId);
    }
}

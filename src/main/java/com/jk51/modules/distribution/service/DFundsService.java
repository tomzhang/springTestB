package com.jk51.modules.distribution.service;

import com.jk51.model.distribute.DFunds;
import com.jk51.modules.distribution.mapper.DFundsMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2017/5/22.
 */
public class DFundsService {
	@Autowired
	DFundsMapper fundsMapper;
	
	public int insert(DFunds funds){
		return fundsMapper.insert(funds);
	}
}

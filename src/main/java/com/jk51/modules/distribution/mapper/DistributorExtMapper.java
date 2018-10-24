package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.DistributorExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/27.
 */
@Mapper
public interface DistributorExtMapper {

    int insertSelective(Map<String,Object> param);
    int updateByDisSelective(Map<String,Object> param);

    DistributorExt selectByDid(Integer did);

    DistributorExt selectByCode(@Param("code") String code);

    List<String> selectCode(@Param("siteId") String siteId);
    
    int updateAccountChange(@Param("distributorId") int distributorId, @Param("money") int money);
    
}

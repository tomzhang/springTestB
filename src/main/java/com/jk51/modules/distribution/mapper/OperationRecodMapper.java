package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.OperationRecond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */
@Mapper
public interface OperationRecodMapper {

    public int insert(OperationRecond record);

    public List<OperationRecond> getDistributorChangeRecord(@Param("d_id")Integer d_id,@Param("siteId")Integer siteId);
}

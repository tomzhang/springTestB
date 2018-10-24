package com.jk51.modules.clerkvisit.mapper;

import com.jk51.model.clerkvisit.BVisitDeploy;
import com.jk51.model.clerkvisit.BVisitDeployExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BVisitDeployMapper {
    long countByExample(BVisitDeployExample example);

    int deleteByExample(BVisitDeployExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BVisitDeploy record);

    int insertSelective(BVisitDeploy record);

    List<BVisitDeploy> selectByExample(BVisitDeployExample example);

    BVisitDeploy selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BVisitDeploy record, @Param("example") BVisitDeployExample example);

    int updateByExample(@Param("record") BVisitDeploy record, @Param("example") BVisitDeployExample example);

    int updateByPrimaryKeySelective(BVisitDeploy record);

    int updateByPrimaryKey(BVisitDeploy record);
}
package com.jk51.modules.task.mapper;

import com.jk51.model.task.BTaskBlob;
import com.jk51.model.task.BTaskBlobExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BTaskBlobMapper {
    long countByExample(BTaskBlobExample example);

    int deleteByExample(BTaskBlobExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BTaskBlob record);

    int insertSelective(BTaskBlob record);

    List<BTaskBlob> selectByExampleWithBLOBs(BTaskBlobExample example);

    List<BTaskBlob> selectByExample(BTaskBlobExample example);

    BTaskBlob selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BTaskBlob record, @Param("example") BTaskBlobExample example);

    int updateByExampleWithBLOBs(@Param("record") BTaskBlob record, @Param("example") BTaskBlobExample example);

    int updateByExample(@Param("record") BTaskBlob record, @Param("example") BTaskBlobExample example);

    int updateByPrimaryKeySelective(BTaskBlob record);

    int updateByPrimaryKeyWithBLOBs(BTaskBlob record);

    int updateByPrimaryKey(BTaskBlob record);

    BTaskBlob selectByTaskId(@Param("taskId") Integer taskId);
}
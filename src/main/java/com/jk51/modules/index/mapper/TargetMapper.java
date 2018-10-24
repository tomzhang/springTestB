package com.jk51.modules.index.mapper;

import com.jk51.model.Target;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by admin on 2017/2/13.
 */
@Mapper
public interface TargetMapper {

    /**
     * 根据主键指标ID，获取当前指标
     * @param targetId
     * @return
     */
    public Target getTargetById(String targetId);

    /**
     * 根据商家ID和一层权重ID，获取当前一层权重下指标列表
     * @param firstWeigthId
     * @param owner
     * @return
     */
    public List<Target> getTargetByOwnerAndFirstWeigthId(@Param("firstWeigthId") String firstWeigthId, @Param("owner") String owner);


    public List<Target> getTargetBySiteIdAndTargetName(@Param("site_id")String site_id,@Param("targetName")String targetName);

    public List<Target> getTargetBySiteId(int site_id);

    public int updateTarget(Target target);

    public int insertTarget(Target target);

    List<Target> getAllTarget();
}

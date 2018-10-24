package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.Recruit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by admin on 2017/2/9.
 */
@Mapper
public interface RecruitMapper {

    /**
     * 根据招募表主键ID查询
     * @param id
     * @return
     */
    public Recruit getRecruitById(String id);

    /**
     * 根据商家ID查询对应招募信息
     * @param owner
     * @return
     */
    public List<Recruit> getRecruitListByOwner(String owner);

    public int insertRecruit(Recruit recruit);

    public int updateRecruit(Recruit recruit);
}

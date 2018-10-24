package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.Distributor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/8.
 */

@Mapper
public interface DistributorMapper {

    /**
     *根據owner查詢所有的分銷商信息
     * 如果存在user_name/apply_type/level/status/createTime時使用條件查詢
     *@param params
     * */
    public List<Distributor> getList(Map<String, Object> params);
    
    Map<String, Object> getDistributorByID(@Param("id") int id, @Param("siteId") int siteId);
    Map<String, Object> getDistributorBySiteId(@Param("siteId") int siteId);

    int setDistributorStore(@Param("siteId") int siteId, @Param("isOpen") int isOpen);
    
    int createDistributorStore(@Param("siteId") int siteId, @Param("isOpen") int isOpen);
    
    Map<String, Object> getDistributorInfoByID(@Param("id") int id, @Param("siteId") int siteId);
    
    int updateDistributor(@Param("id") int id,@Param("siteId") int siteId, @Param("status") int status, @Param("note") String note);

    int getDistributorTotalByOwner(@Param("owner") String owner, @Param("status")Integer status);

    com.jk51.model.distribute.Distributor selectByUid(@Param("memberId") Integer memberId ,@Param("siteId") Integer siteId);

    Distributor selectDistributorInfo(@Param("id") int id, @Param("siteId") int siteId);

    Map<String,Object> selectDistributorById(@Param("siteId") Integer siteId,@Param("distributorId") Object distributorId);

    Integer selectDistributorIdByUsername(@Param("siteId")Integer siteId , @Param("distributorName")String distributorName);

    Map<String,Object> getDistributor(Map<String,Object> param);
    
    Map<String,String> getDistributorInfoByMobile(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    int createDistributor(Map<String,Object> param);

    List<Integer> selectCommissionLevelDistributorIdList(@Param("distributorIdList") List<Integer> distributorIdList);

    List<Map<String,Object>> selectBindUsers();
    List<Map<String,Object>> getMyTeam(Map<String, Object> params);
    List<Integer> getDistributorId(Map<String, Object> params);

    Integer updateDistributorLevel(@Param("level") int level, @Param("id") Integer id,@Param("siteId") int siteId);
    Integer updateDistributorRoot(@Param("root") int root, @Param("id") Integer id,@Param("siteId") int siteId);

    Map<String,Object> selectDistributorByUid(@Param("uid") String uid);

    Map<String,Object> findDistributorByUidandStatus(@Param("uid") String uid,@Param("siteId") int siteId);
}

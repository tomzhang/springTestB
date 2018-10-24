package com.jk51.modules.clerkvisit.mapper;

import com.jk51.model.clerkvisit.BVisitDesc;
import com.jk51.model.clerkvisit.BVisitDescWithDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BVisitDescMapper {


    int updateByPrimaryKey(BVisitDesc record);

    List<BVisitDescWithDetail> queryVisitDetailList(Map<String,Object> param);

    List<Map<String,Object>> queryVisitDetailReport(Map<String,Object> param);

    Map<String,Object> checkVisitResult(Map<String, Object> parameterMap);

    int  updateSmsStatusByVisitId(@Param("siteId")String siteId,@Param("visitId") String visitId, @Param("storeAdminId")String storeAdminId);

    void updatePageStatus(@Param("siteId")int siteId,@Param("visitId")int visitId);

    Map<String,Object> queryVisitCount(Map<String,Object> param);
}

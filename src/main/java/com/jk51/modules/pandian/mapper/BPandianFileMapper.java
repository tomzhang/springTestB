package com.jk51.modules.pandian.mapper;

import com.jk51.model.BPandianFile;
import org.apache.ibatis.annotations.Param;

public interface BPandianFileMapper {
    int insertSelective(BPandianFile record);
    BPandianFile selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(BPandianFile record);
    int updateStatusById(@Param("fileId") Integer fileId, @Param("status") Integer status, @Param("ext") String ext, @Param("parseTime") String parseTime);
    String getFileUrlByPDNum(@Param("siteId") Integer siteId, @Param("pdNum") String pdNum);
}

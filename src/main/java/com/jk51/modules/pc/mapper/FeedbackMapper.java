package com.jk51.modules.pc.mapper;

import com.jk51.model.pc.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
@Mapper
public interface FeedbackMapper {
    Integer reply(@Param("reply") String reply,@Param("siteId") Integer siteId,@Param("id") Integer id);

    List<Feedback> getLst(@Param("siteId") Integer siteId,@Param("buyerId") Integer buyerId);

    Integer add(Feedback feedback);

    List<Feedback> getLstByUserId(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId);
}

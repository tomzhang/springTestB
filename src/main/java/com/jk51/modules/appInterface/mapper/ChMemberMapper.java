package com.jk51.modules.appInterface.mapper;

import com.jk51.model.ChMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-07
 * 修改记录:
 */
@Mapper
public interface ChMemberMapper {

    ChMember selectByPrimaryKey(String memberId);

    List<ChMember> selectByOpenId(String openId);

    Map<String,Object> getMemberInfo(@Param("ybmemberId")String ybmemberId,@Param("siteId") int siteId);
}

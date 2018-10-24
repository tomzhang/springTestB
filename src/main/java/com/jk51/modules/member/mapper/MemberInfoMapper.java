package com.jk51.modules.member.mapper;

import com.jk51.model.order.BMemberInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/7-03-07
 * 修改记录 :
 */
public interface MemberInfoMapper {

    int insertByShare(BMemberInfo bMemberInfo);

    void updateVipMemberInfo(@Param(value = "vipMemberAddSelectParams") BMemberInfo bMemberInfo);

    List<Map<String, Object>> getOpenIdByPhone(@Param("siteId") Integer siteId, @Param("mobile") List<String> phone);

    Map<String, Object> getMemberInfoByMobile(@Param("siteId") Integer siteId, @Param("mobile") String phone);
}

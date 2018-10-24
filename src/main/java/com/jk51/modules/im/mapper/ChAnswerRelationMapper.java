package com.jk51.modules.im.mapper;

import com.jk51.model.ChAnswerRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@Mapper
public interface ChAnswerRelationMapper {

    List<String> findUserOpenId(String sender);

    int updateRemark(@Param("sender") String sender, @Param("receiver") String receiver, @Param("remark") String remark);

    List<String> getRemark(@Param("sender") String sender, @Param("receiver") String receiver);

    ChAnswerRelation findByUserOpenId(String openId);

    ChAnswerRelation findByUserOpenIdAndharmacistUserid(@Param("sender") String sender, @Param("receiver") String receiver);

    int update(ChAnswerRelation chAnswerRelation);

    int insert(ChAnswerRelation cr);

    int delete(@Param("sender")String sender,@Param("imServiceId")Integer imServiceId);

    String getReceiverBySender(@Param("rlToken") String user_openid);


    Integer updateBySenderAndRecervice(@Param("sender") String sender, @Param("receiver")String recervice);

    Integer findImServiceId(@Param("sender") String sender, @Param("receiver")String recervice);

    int deleteBySender(String sender);
}

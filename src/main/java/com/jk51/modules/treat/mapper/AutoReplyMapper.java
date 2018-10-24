package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.AutoReply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: Administrator
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Mapper
public interface AutoReplyMapper {
    void addReply(AutoReply autoReply);

    void updateReply(int siteId, int replyType);

    void updateKeywordsReply(AutoReply autoReply);

    AutoReply getReplyBySiteId(int siteId, int replyType);

    List<AutoReply> getKeywordReplyBySiteId(int siteId, int replyType, String keyword);
}

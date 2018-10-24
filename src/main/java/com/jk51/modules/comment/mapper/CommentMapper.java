package com.jk51.modules.comment.mapper;

import com.jk51.model.Comments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/24.
 */
@Mapper
public interface CommentMapper {

    List<Comments> findCommentsList(Map<String, Object> paramsMap);

    void updateState(Comments comments);

    List<Map<String, Object>> findItemComments(@Param("siteId") String siteId, @Param("goodsId") String goodsId);

    List<Comments> findOrderComments(String tradesId);

    void addServiceComment(Map<String, Object> paramterMap);

    int addTradeComment(List<Comments> commentss);

    int updateComments(Map param);
}

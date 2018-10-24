package com.jk51.typeHandler;

import com.alibaba.fastjson.JSONObject;
import com.jk51.modules.task.domain.RewardRule;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(RewardRule.class)
public class RewardRuleTypeHandler extends BaseTypeHandler<RewardRule> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RewardRule parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONObject.toJSONString(parameter));
    }

    @Override
    public RewardRule getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);

        return JSONObject.parseObject(string, RewardRule.class);
    }

    @Override
    public RewardRule getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String string = rs.getString(columnIndex);

        return JSONObject.parseObject(string, RewardRule.class);
    }

    @Override
    public RewardRule getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String string = cs.getString(columnIndex);

        return JSONObject.parseObject(string, RewardRule.class);
    }
}

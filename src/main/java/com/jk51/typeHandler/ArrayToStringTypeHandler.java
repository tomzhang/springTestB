package com.jk51.typeHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jk51.commons.string.StringUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

/**
 * 用逗号分隔的字符串与T数组的相互转换
 * 未经过测试
 */
@MappedJdbcTypes({VARCHAR})
@MappedTypes({String[].class, int[].class, Integer[].class})
public class ArrayToStringTypeHandler<T> extends BaseTypeHandler<T[]> {
    private static final CharSequence DELIMITER = ",";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T[] parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.isNull(parameter)) {
            ps.setString(i, "");
        } else {
            StringJoiner sj = new StringJoiner(DELIMITER);
            for (T value : parameter)
                sj.add(value.toString());

            ps.setString(i, sj.toString());
        }
    }

    @Override
    public T[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);

        return readValue(value);
    }

    @Override
    public T[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);

        return readValue(value);
    }

    @Override
    public T[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);

        return readValue(value);
    }

    private T[] readValue(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }

        try {
            List<T> resultList = new LinkedList<>();
            String[] split = value.split(",");

            for (String s : split) {
                T temp = objectMapper.readValue(s, new TypeReference<T>() {});

                resultList.add(temp);
            }

            return (T[])resultList.toArray();
        } catch (IOException e) {
            throw new RuntimeException(value + "无法转换成目标类型数组" + getRawType());
        }
    }
}

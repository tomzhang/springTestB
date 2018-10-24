package com.jk51.typeHandler;

import com.jk51.model.ValuedEnum;
import com.jk51.model.task.BTaskConsumeLog;
import com.jk51.model.task.HandleStatus;
import com.jk51.model.task.MsgType;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.ibatis.type.JdbcType.INTEGER;
import static org.apache.ibatis.type.JdbcType.SMALLINT;
import static org.apache.ibatis.type.JdbcType.TINYINT;

/**
 * 不支持通过公共接口等获取TypeHandle
 * @param <E>
 * @see org.apache.ibatis.builder.SqlSourceBuilder
 * @see ParameterMapping.Builder#build()
 */
@MappedJdbcTypes(value = {INTEGER, TINYINT, SMALLINT})
@MappedTypes({MsgType.class, HandleStatus.class})
public class ValuedEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private Class<E> type;
    private Map<Integer, E> map = new HashMap<>();
    public ValuedEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        E[] enums = type.getEnumConstants();
        if (enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
        for (E e : enums) {
            ValuedEnum valuedEnum = (ValuedEnum) e;
            map.put(valuedEnum.getValue(), e);
        }
    }
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ValuedEnum valuedEnum = (ValuedEnum) parameter;
        ps.setInt(i, valuedEnum.getValue());
    }
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int i = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return getValuedEnum(i);
        }
    }
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int i = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return getValuedEnum(i);
        }
    }
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int i = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return getValuedEnum(i);
        }
    }
    private E getValuedEnum(int value) {
        try {
            return map.get(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                "Cannot convert " + value + " to " + type.getSimpleName() + " by value.", ex);
        }
    }
}



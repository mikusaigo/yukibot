package com.yuki.yukibot.util.typehandler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class LongListTypeHandler extends BaseTypeHandler<List<Long>> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<Long> longs, JdbcType jdbcType) throws SQLException {
        List<String> strings = convertLongListToStrList(longs);
        preparedStatement.setString(i, CharSequenceUtil.join(StrPool.COMMA, strings));
    }

    @Override
    public List<Long> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String string = resultSet.getString(s);
        List<String> split = CharSequenceUtil.split(string, StrPool.COMMA);
        return convertStrListToLongList(split);
    }

    @Override
    public List<Long> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String string = resultSet.getString(i);
        List<String> split = CharSequenceUtil.split(string, StrPool.COMMA);
        return convertStrListToLongList(split);
    }

    @Override
    public List<Long> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String string = callableStatement.getString(i);
        List<String> split = CharSequenceUtil.split(string, StrPool.COMMA);
        return convertStrListToLongList(split);
    }

    public static List<Long> convertStrListToLongList(List<String> targerList){
        List<Long> result = new ArrayList<>();
        for (String s : targerList) {
            long l = Long.parseLong(s);
            result.add(l);
        }
        return result;
    }
    public static List<String> convertLongListToStrList(List<Long> targerList){
        List<String> result = new ArrayList<>();
        for (Long l : targerList) {
            String s = String.valueOf(l);
            result.add(s);
        }
        return result;
    }
}

package com.jk51.erpDataConfig.dekang;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:德康大药房获取元数据
 * 作者: dumingliang
 * 创建日期: 2017-07-24
 * 修改记录:
 */
@Configuration
public class DataSoureConfig_DeKang {

    private volatile static JdbcTemplate jdbcTemplate;

    public static JdbcTemplate getDekangJdbcTemplate() {

        if (jdbcTemplate == null) {
            synchronized (DataSoureConfig_DeKang.class) {
                if (jdbcTemplate == null) {
                    jdbcTemplate = getJdbcTemplate();
                }
            }
        }

        return jdbcTemplate;
    }


    private static JdbcTemplate getJdbcTemplate() {
        DruidDataSource druidDataSource = new DruidDataSource();
        List<Filter> filteList = new ArrayList();
        filteList.add(getWallFilter_DeKang());
        //注册dataSource
        druidDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        druidDataSource.setUrl("jdbc:sqlserver://36.7.71.141:6566;DatabaseName=hydee_dkdyf;");
        druidDataSource.setUsername("888");
        druidDataSource.setPassword("51jk");
        druidDataSource.setInitialSize(Integer.parseInt("1"));
        druidDataSource.setMinIdle(Integer.parseInt("5"));
        druidDataSource.setMaxActive(Integer.parseInt("10"));
        druidDataSource.setMaxWait(Integer.parseInt("60000"));
        druidDataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong("60000"));
        druidDataSource.setMinEvictableIdleTimeMillis(Long.parseLong("300000"));
        druidDataSource.setValidationQuery("SELECT 'x'");
        druidDataSource.setTestWhileIdle(Boolean.parseBoolean("true"));
        druidDataSource.setTestOnBorrow(Boolean.parseBoolean("false"));
        druidDataSource.setTestOnReturn(Boolean.parseBoolean("false"));
        druidDataSource.setPoolPreparedStatements(Boolean.parseBoolean("true"));
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt("20"));
        //druidDataSource.setFilters(resolver.getProperty("filters"));
        //druidDataSource.setProxyFilters(filteList);
        return new JdbcTemplate(druidDataSource);
    }

    @Bean
    private static WallFilter getWallFilter_DeKang() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(getWallConfig_DeKang());
        return wallFilter;
    }

    @Bean
    private static WallConfig getWallConfig_DeKang() {
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        // 允许有注释 flyway建表语句中有
        wallConfig.setCommentAllow(true);
        wallConfig.setMultiStatementAllow(true);
        return wallConfig;
    }
}

package com.jk51.erpDataConfig;

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
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:100253安吉中联对接
 * 作者: dumingliang
 * 创建日期: 2018-04-24
 * 修改记录:
 */
@Configuration
public class DataSourceConfig_RuiSen {

    private volatile static JdbcTemplate jdbcTemplate;

    public static JdbcTemplate getRuiSenJDBCTemplate() {

        if (jdbcTemplate == null) {
            synchronized (DataSourceConfig_RuiSen.class) {
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
        filteList.add(getWallFilter_RuiSen());
        //注册dataSource
        druidDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        druidDataSource.setUrl("jdbc:sqlserver://183.222.58.218:5100;DatabaseName=wx51sc;");
        druidDataSource.setUsername("rs51jk");
        druidDataSource.setPassword("rs51jk");
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
        return new JdbcTemplate(druidDataSource);
    }

    @Bean
    private static WallFilter getWallFilter_RuiSen() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(getWallConfig_RuiSen());
        return wallFilter;
    }

    @Bean
    private static WallConfig getWallConfig_RuiSen() {
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        // 允许有注释 flyway建表语句中有
        wallConfig.setCommentAllow(true);
        wallConfig.setMultiStatementAllow(true);
        return wallConfig;
    }
}

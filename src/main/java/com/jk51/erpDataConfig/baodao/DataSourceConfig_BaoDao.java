package com.jk51.erpDataConfig.baodao;

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
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-12-06
 * 修改记录:
 */
@Configuration
public class DataSourceConfig_BaoDao {
    private volatile static JdbcTemplate jdbcTemplate;

    public static JdbcTemplate getBaoDaoJDBCTemplate() {

        if (jdbcTemplate == null) {
            synchronized (DataSourceConfig_BaoDao.class) {
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
        filteList.add(getWallFilter_baodao());
        druidDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");//注册dataSource
        druidDataSource.setUrl("jdbc:oracle:thin:@//zb.bddyf.icevpn.org:30689/BDDB");
        druidDataSource.setUsername("wxhyxx");
        druidDataSource.setPassword("hc1234");
        druidDataSource.setInitialSize(Integer.parseInt("1"));
        druidDataSource.setMinIdle(Integer.parseInt("5"));
        druidDataSource.setMaxActive(Integer.parseInt("10"));
        druidDataSource.setMaxWait(Integer.parseInt("60000"));
        druidDataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong("60000"));
        druidDataSource.setMinEvictableIdleTimeMillis(Long.parseLong("300000"));
        druidDataSource.setValidationQuery("select 1 from dual");
        druidDataSource.setTestWhileIdle(Boolean.parseBoolean("true"));
        druidDataSource.setTestOnBorrow(Boolean.parseBoolean("false"));
        druidDataSource.setTestOnReturn(Boolean.parseBoolean("false"));
        druidDataSource.setPoolPreparedStatements(Boolean.parseBoolean("true"));
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt("20"));
        return new JdbcTemplate(druidDataSource);
    }

    @Bean
    private static WallFilter getWallFilter_baodao() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(getWallConfig_baodao());
        return wallFilter;
    }

    @Bean
    private static WallConfig getWallConfig_baodao() {
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        // 允许有注释 flyway建表语句中有
        wallConfig.setCommentAllow(true);
        wallConfig.setMultiStatementAllow(true);
        return wallConfig;
    }
}

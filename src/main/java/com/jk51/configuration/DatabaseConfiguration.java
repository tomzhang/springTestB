package com.jk51.configuration;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.jk51.interceptor.DBOperationInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2017/1/18.
 */
@Configuration
@EnableTransactionManagement
@MapperScan(value = "com.jk51.modules.**.mapper")
public class DatabaseConfiguration implements EnvironmentAware {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Resource
    private Environment env;

    //创建拦截器对象
    private DBOperationInterceptor db = new DBOperationInterceptor();

    private RelaxedPropertyResolver resolver;

    @Autowired
//    private DBOperationInterceptor databaseConfiguration;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        this.resolver = new RelaxedPropertyResolver(environment,"spring.datasource.");
    }

    //注册dataSource
    @Bean(initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource() throws SQLException {
        if (StringUtils.isEmpty(resolver.getProperty("url"))) {
            logger.error("Your database connection pool configuration is incorrect!"
                    + " Please check your Spring profile, current profiles are:"
                    + Arrays.toString(env.getActiveProfiles()));
            throw new ApplicationContextException(
                    "Database connection pool is not configured correctly");
        }

        List<Filter> filteList = new ArrayList();
        filteList.add(getWallFilter());

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(resolver.getProperty("driver-class-name"));
        druidDataSource.setUrl(resolver.getProperty("url"));
        druidDataSource.setUsername(resolver.getProperty("username"));
        druidDataSource.setPassword(resolver.getProperty("password"));
        druidDataSource.setInitialSize(Integer.parseInt(resolver.getProperty("initialSize")));
        druidDataSource.setMinIdle(Integer.parseInt(resolver.getProperty("minIdle")));
        druidDataSource.setMaxActive(Integer.parseInt(resolver.getProperty("maxActive")));
        druidDataSource.setMaxWait(Integer.parseInt(resolver.getProperty("maxWait")));
        druidDataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(resolver.getProperty("timeBetweenEvictionRunsMillis")));
        druidDataSource.setMinEvictableIdleTimeMillis(Long.parseLong(resolver.getProperty("minEvictableIdleTimeMillis")));
        druidDataSource.setValidationQuery(resolver.getProperty("validationQuery"));
        druidDataSource.setTestWhileIdle(Boolean.parseBoolean(resolver.getProperty("testWhileIdle")));
        druidDataSource.setTestOnBorrow(Boolean.parseBoolean(resolver.getProperty("testOnBorrow")));
        druidDataSource.setTestOnReturn(Boolean.parseBoolean(resolver.getProperty("testOnReturn")));
        druidDataSource.setPoolPreparedStatements(Boolean.parseBoolean(resolver.getProperty("poolPreparedStatements")));
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(resolver.getProperty("maxPoolPreparedStatementPerConnectionSize")));
//        druidDataSource.setFilters(resolver.getProperty("filters"));
        druidDataSource.setProxyFilters(filteList);

        return druidDataSource;
    }


    @Bean public PlatformTransactionManager transactionManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public WallFilter getWallFilter(){
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(getWallConfig());
        return wallFilter;
    }

    @Bean
    public WallConfig getWallConfig(){
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        // 允许有注释 flyway建表语句中有
        wallConfig.setCommentAllow(true);
        wallConfig.setMultiStatementAllow(true);
        return wallConfig;
    }

    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(dataSource());//指定数据源(这个必须有，否则报错)
        //下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        fb.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));//指定基包
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapperLocations")));//指定xml文件位置
        fb.setTypeHandlersPackage("com.jk51.typeHandler");
        fb.setConfigLocation(new ClassPathResource("mybatis.xml"));
        //向sqlSessionFactory中添加拦截器
        // 先注释 日志太多
//        fb.setPlugins(new Interceptor[]{db});
        return fb.getObject();
    }

}

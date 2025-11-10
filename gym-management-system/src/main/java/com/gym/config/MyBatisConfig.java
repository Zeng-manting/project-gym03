package com.gym.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * MyBatis 配置类
 * 启用 Mapper 扫描，配置 SqlSessionFactory
 */
@Configuration
@MapperScan("com.gym.mapper") // 扫描 Mapper 接口所在的包
public class MyBatisConfig {

    @Autowired
    private DataSource dataSource; // 自动注入从 application.properties 配置的数据源

    /**
     * 配置 SqlSessionFactory
     * 数据源从 application.properties 读取
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        
        // 设置数据源
        factoryBean.setDataSource(dataSource);
        
        // 设置类型别名包
        factoryBean.setTypeAliasesPackage("com.gym.entity");
        
        // 配置 Mapper XML 文件路径
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        
        // 配置 MyBatis 全局设置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // 开启驼峰命名转换（数据库下划线命名 -> Java 驼峰命名）
        configuration.setMapUnderscoreToCamelCase(true);
        // 设置日志实现
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        
        factoryBean.setConfiguration(configuration);
        
        return factoryBean.getObject();
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
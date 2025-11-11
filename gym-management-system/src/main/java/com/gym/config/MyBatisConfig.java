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
 */
@Configuration
@MapperScan("com.gym.mapper") // ✅ 必须加上这一行，让 Spring 扫描 Mapper 接口
public class MyBatisConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * 配置 SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        // 设置数据源
        factoryBean.setDataSource(dataSource);

        // 设置类型别名包（可选）
        factoryBean.setTypeAliasesPackage("com.gym.entity");

        // ❌ 删除或注释掉下面这行（因为你不用 XML）
        // ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));

        // 配置 MyBatis 全局设置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true); // 开启驼峰命名转换
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class); // 输出 SQL 日志

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
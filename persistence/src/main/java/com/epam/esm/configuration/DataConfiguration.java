package com.epam.esm.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("com.epam.esm")
@EnableTransactionManagement
public class DataConfiguration {
    private final static String PACKAGE_TO_SCAN = "com.epam.esm.entity";
    private final static String HIBERNATE_DIALECT ="hibernate.dialect";
    private final static String HIBERNATE_FORMAT_SQL ="hibernate.format_sql";
    private final static String HIBERNATE_SHOW_SQL ="hibernate.show_sql";
    private final static String HIBERNATE_HBM2DDL_AUTO ="hibernate.hbm2ddl.auto";
    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private String maxPoolSize;
    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;
    @Value("${spring.jpa.properties.hibernate.format_sql}")
    private String format_sql;
    @Value("${spring.jpa.show-sql}")
    private String showSql;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    public DataSource dataSource() {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setDriverClassName(driverName);
        jdbcConfig.setJdbcUrl(jdbcUrl);
        jdbcConfig.setUsername(username);
        jdbcConfig.setPassword(password);
        jdbcConfig.setMaximumPoolSize(Integer.parseInt(maxPoolSize));
        return new HikariDataSource(jdbcConfig);
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(PACKAGE_TO_SCAN);
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProp());
        return em;
    }

    private Properties hibernateProp() {
        Properties hibernateProp = new Properties();
        hibernateProp.put(HIBERNATE_DIALECT, hibernateDialect);
        hibernateProp.put(HIBERNATE_FORMAT_SQL, format_sql);
        hibernateProp.put(HIBERNATE_SHOW_SQL, showSql);
        hibernateProp.put(HIBERNATE_HBM2DDL_AUTO, ddlAuto);
        return hibernateProp;
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}

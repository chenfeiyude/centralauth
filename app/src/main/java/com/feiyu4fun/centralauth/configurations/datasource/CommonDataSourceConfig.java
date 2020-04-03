package com.feiyu4fun.centralauth.configurations.datasource;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public abstract class CommonDataSourceConfig {
	protected static Log logger = LogFactory.getLog(CommonDataSourceConfig.class.getName());
	
    protected Environment env;
	private String dataSourceName;
	
    public CommonDataSourceConfig(Environment env, String dataSourceName) {
		this.env = env;
		this.dataSourceName = dataSourceName;
	}
	
	public abstract DataSourceProperties dataSourceProperties();
	
	/**
	 * This data source is using c3p0 pool
	 * @return
	 */
	public ComboPooledDataSource poolDataSource() {
    	// a named datasource is best practice for later jmx monitoring
        ComboPooledDataSource dataSource = new ComboPooledDataSource(dataSourceName);

        try {
            dataSource.setDriverClass(dataSourceProperties().getDriverClassName());
        } catch (PropertyVetoException pve){
            logger.error("Cannot load datasource driver (" + dataSourceProperties().getDriverClassName() +") : " + pve.getMessage());
            return null;
        }
        
        dataSource.setJdbcUrl(dataSourceProperties().getUrl());
        dataSource.setUser(dataSourceProperties().getUsername());
        dataSource.setPassword(dataSourceProperties().getPassword());
           
        dataSource.setAcquireIncrement(Integer.parseInt(env.getProperty("hibernate.c3p0.acquire_increment")));
        dataSource.setIdleConnectionTestPeriod(Integer.parseInt(env.getProperty("hibernate.c3p0.idle_test_period")));
        dataSource.setMinPoolSize(Integer.parseInt(env.getProperty("hibernate.c3p0.min_size")));
        dataSource.setMaxPoolSize(Integer.parseInt(env.getProperty("hibernate.c3p0.max_size")));
        dataSource.setMaxStatements(Integer.parseInt(env.getProperty("hibernate.c3p0.max_statements")));
        dataSource.setMaxIdleTime(Integer.parseInt(env.getProperty("hibernate.c3p0.timeout")));
       

        return dataSource;
    }
	
	/**
	 * Simple datasource without any extra pool
	 * @return
	 */
    public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(dataSourceProperties().getUrl(), 
																		 dataSourceProperties().getUsername(), 
																		 dataSourceProperties().getPassword());
        dataSource.setDriverClassName(dataSourceProperties().getDriverClassName());
        return dataSource;
    }
    
	protected Properties getJpaProperties() {
		Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        jpaProperties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
        jpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        jpaProperties.put("hibernate.physical_naming_strategy", env.getProperty("spring.jpa.hibernate.naming.physical-strategy"));// fix the _ for dto fields when using jpa
        jpaProperties.put("hibernate.implicit_naming_strategy", env.getProperty("spring.jpa.hibernate.naming.implicit-strategy"));// fix the _ for dto fields when using jpa
        jpaProperties.put("hibernate.current_session_context_class", env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
        jpaProperties.put("hibernate.jdbc.time_zone", env.getProperty("spring.jpa.properties.hibernate.jdbc.time_zone"));
        
        return jpaProperties;
	}
}

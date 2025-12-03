package com.featherworld.project.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:/config.properties")
public class DBConfig {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		// config.properties에 작성한 spring.datasource.hikari로 시작하는 설정 모두 적용
		return new HikariConfig();
	}
	
	@Bean
	public DataSource dataSource(HikariConfig config) {
		// Bean으로 등록한 HikariConfig 객체를 받아, DataSource 객체 생성
		DataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}
	
	@Bean
	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception {
		
		// Connection Pool + mybatis-config.xml 이용 SqlSessionFactory를 만드는 객체
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		
		// mapper.xml 파일의 경로 지정
		sessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mappers/**.xml"));
		
		// com.featherworld.project 하위 모든 클래스 -> 클래스명을 별칭으로 지정
		sessionFactoryBean.setTypeAliasesPackage("com.featherworld.project");
		
		// mybatis 설정 적용 (Java null -> DB NULL & Java 필드명 -> DB 컬럼명)
		sessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
		
		return sessionFactoryBean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sessionTemplate(SqlSessionFactory sessionFactory) {
		// SqlSession을 대체하는 객체
		return new SqlSessionTemplate(sessionFactory);
	}
	
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}

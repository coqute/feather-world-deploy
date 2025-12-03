package com.featherworld.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
@PropertySource("classpath:/config.properties")
public class FileConfig implements WebMvcConfigurer{
	
	// 파일 업로드 임계값
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold;	// 52428800
	
	// 요청당 파일 최대 크기
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize; // 52428800
	
	// 개별 파일당 최대 크기
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize; // 10485760
	
	// 입계값 초과 시 파일의 임시 저장경로
	@Value("${spring.servlet.multipart.location}")
	private String location; // C:/uploadFiles/temp/
	
	// ------------------------------------------------------------
	
	// 프로필 이미지 관련 경로 (오른쪾 탭에있는 profile 버튼용 이미지)
	@Value("${my.profile.resource-handler}")
	private String profileResourceHandler;
	
	@Value("${my.profile.resource-location}")
	private String profileResourceLocation;
	
	// ------------------------------------------------------------
	
	// 게시판 이미지 관련 경로
	@Value("${my.board.resource-handler}")
	private String boardResourceHandler;
	
	@Value("${my.board.resource-location}")
	private String boardResourceLocation;
	
	
	//-------------------------------------------------------------
	
	// left 프로필 이미지 관련 경로
	
	@Value("${my.left-profile.resource-handler}")
	private String leftProfileResourceHandler;
	
	@Value("${my.left-profile.resource-location}")
	private String leftProfileResourceLocation;

	//---------------------------------------
	//leftProfile 쓸때는 my.left-profile.resource 이런식으로만들어서추가
	
	
	// 요청 주소에 따라
	// 서버 컴퓨터의 어떤 경로에 접근할지 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry
		.addResourceHandler(profileResourceHandler) // /profile/**
		.addResourceLocations(profileResourceLocation); // file:///C:/uploadFiles/profile/
		
		registry
		.addResourceHandler(boardResourceHandler) // /images/board/**
		.addResourceLocations(boardResourceLocation); // file:///C:/uploadFiles/semi/
		
		registry
		.addResourceHandler(leftProfileResourceHandler)    //왼쪽 프로필 이미지관련
		.addResourceLocations(leftProfileResourceLocation); // 왼쪽 프로필 이미지 관련
	}
	
	// MultipartResolver 설정
	
	@Bean
	public MultipartConfigElement configElement() {
		// MultipartResolver :
		// 파일 업로드를 처리하는 데 사용되는 MultipartConfigElement를 구성하고 반환
		// 파일 업로드를 위한 구성 옵션을 설정하는 데 사용되는 객체
		// 업로드 파일의 최대 크기, 메모리에서의 임시 저장경로 등 설정 가능
		
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		// 파일 업로드 임계값
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));
		
		// HTTP 요청당 파일 최대 크기
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
		
		// 개별 파일당 최대 크기
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
		
		// 임계값 초과 시 파일의 임시 저장 경로
		factory.setLocation(location);
		
		return factory.createMultipartConfig();
		
	}
	// MultipartResolver 객체를 생성하여 Bean으로 등록
	// -> Bean으로 등록하면서 위에서 만든 MultipartConfigElement 자동으로 이용함
	@Bean
	public MultipartResolver multipartResolver() {
		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
		
		return multipartResolver;
	}

}

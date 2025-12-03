	package com.featherworld.project.common.config;
	
	import com.featherworld.project.common.interceptor.BoardTypeInterceptor;
	import com.featherworld.project.common.interceptor.MemberInterceptor;
	import com.featherworld.project.common.interceptor.ProfileInterceptor;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
	import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
	
	
	/**
	 * 어떤 요청을 가로챌지 결정하는 설정 클래스
	 * @author Jiho
	 */
	@Configuration
	public class InterceptorConfig implements WebMvcConfigurer {
	    
	    @Bean
	    public ProfileInterceptor profileInterceptor() {
			return new ProfileInterceptor();
		}

		@Bean
		public BoardTypeInterceptor boardTypeInterceptor() {
			return new BoardTypeInterceptor();
		}
	    
	    @Bean
	    public MemberInterceptor memberInterceptor() {
	        return new MemberInterceptor();
	    }
	    
	    @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        
	       registry.addInterceptor(memberInterceptor())
	            .addPathPatterns("/**")
	            .excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico",
	                                "/member/**", "/email/**","/error");

		   registry.addInterceptor(boardTypeInterceptor())
			   .addPathPatterns("/**/board/**")
			   .excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico",
					   				"/member/**", "/email/**","/error");
	        
	        // 2. ProfileInterceptor 등록 - minihome 포함하여 모든 {memberNo} 패턴에 적용
	        registry.addInterceptor(profileInterceptor())
	            .addPathPatterns("/**") // 모든 패턴 매칭
	            .excludePathPatterns(
	            		"/css/**",
	            		"/js/**",
	            		"/images/**",
	            		"/static/**",
	            		"/favicon.ico",
	            		"/api/**",
	            		"/error",
            		   "/member/**",
	            		"/email/**",
	            		"/login",
	            		"/register",
	            		"/**/images/**",  
	            		"/**/*.jpg",       
	            		"/**/*.png",      
	          		    "/**/*.gif",      
	            		"/**/*.css",      
	            		"/**/*.js" ,
	            		"/**/ilchon-comment",
	            		"/"// ← 추가/	            		            
            		);
	        
	           
	        WebMvcConfigurer.super.addInterceptors(registry);
	    }
	}
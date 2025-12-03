package com.featherworld.project.common.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:/config.properties") // Google SMTP에 사용할 username, password(앱 비밀번호) 
                                              
// ************각자 config에 email 값 이랑 password 입력해주기***********************
public class EmailConfig {
	// 이메일 인증 관련  config
	
	@Value("${spring.mail.username}")
	private String userName;
	
	@Value("${spring.mail.password}")
	private String password;
	
	@Bean
	public JavaMailSender javaMailSender() {
	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		Properties prop = new Properties();
		prop.setProperty("mail.transport.protocol", "smtp"); // 전송 프로토콜을 설정. 여기서는 SMTP를 사용
		prop.setProperty("mail.smtp.auth", "true"); // SMTP 서버 인증을 사용할지 여부를 설정함.
															// true로 설정되어 있으므로 인증이 사용됨
															// SMTP 서버를 사용하여 이메일을 보내려면 보안 상의 이유로 인증이 필요.
															// (사용자이름(이메일)과 비밀번호(앱비밀번호) 확인)
		prop.setProperty("mail.smtp.starttls.enable", "true"); // STARTTLS를 사용하여 안전한 연결을 활성화할지 여부를 설정
		prop.setProperty("mail.debug", "true"); 		// 디버그 모드를 설정
		prop.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com"); // 신뢰할 수 있는 SMTP 서버 호스트를 지정
		prop.setProperty("mail.smtp.ssl.protocols", "TLSv1.2"); // SSL 프로토콜을 설정. 여기서는 TLSv1.2를 사용
				
				
		mailSender.setUsername(userName); // 이메일 계정의 사용자
		mailSender.setPassword(password); // 이메일 계정의 비밀번호
		mailSender.setHost("smtp.gmail.com"); // SMTP 서버 호스트를 설정.  Gmail의 SMTP 서버인 "smtp.gmail.com"을 사용
		mailSender.setPort(587); 			// SMTP 서버의 포트 587로 설정
		mailSender.setDefaultEncoding("UTF-8"); // 기본 인코딩을 설정
		mailSender.setJavaMailProperties(prop); // JavaMail의 속성을 설정(앞서 정의해둔 prop 있는 설정들을 여기에 추가)
				
		
		return mailSender;
	}
}

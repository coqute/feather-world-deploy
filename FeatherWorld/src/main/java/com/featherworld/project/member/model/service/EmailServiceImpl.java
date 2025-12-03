package com.featherworld.project.member.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.featherworld.project.member.model.mapper.EmailMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service

public class EmailServiceImpl implements EmailService {

	@Autowired
	private EmailMapper mapper;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	

	
	/** 인증키 발급 메서드
	 * @return
	 * @author 영민
	 */
	private String createAuthkey() {
		
		return UUID.randomUUID().toString().substring(0,6);
	}
	
	/** 인증키와 이메일을 DB에 저장메서드
	 * @param map
	 * @return
	 * @author 영민
	 * 
	 */
	private boolean saveAuthKey(Map<String, String> map) {
		
		//1. 기존 이메일에서 인증키 받은게 있다면 수정시킴
		int result = mapper.updateAuthKey(map);
		
		if(result == 0) {
			
			result = mapper.insertAuthKey(map);
		}
		
		return result > 0; // 성공여부
	}
	
	/**이메일과 인증번호가 같은지 조회하는 메서드
	 * @param map
	 * @return
	 * @author  영민
	 */
	public int checkAuthKey(Map<String, String> map) {
		return mapper.checkAuthKey(map);
	}
	
	
	/** HTML 템플릿에 데이터를 바인딩하여 최종 HTML 생성 메서드
	 * @param authKey
	 * @param name
	 * @author 영민
	 * @return
	 */
	private String loadHtml(String authKey, String name) {
		
		//타임리프 제공하는 HTML 템플릿에 데이터를 전달 클래스
		Context context = new Context();
		context.setVariable("authKey", authKey);
		
		return templateEngine.process("email/"+name, context);
		// templates/email/signup.html
	}
	
	/** 이메일 전송 메서드
	 *@author 영민
	 */
	@Override
	public String sendEmail(String name, String email) {
		
		String authKey = createAuthkey();//인증키 발급
		
		Map<String, String> map = new HashMap<>();
		
		map.put("authKey", authKey);
		map.put("email", email);
		// map 에다가 인증번호랑 이메일 넣어줌
		
		if(!saveAuthKey(map)) {
			return null;
		}
		
		// ---------------DB 성공 이후 ---------------------------
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true , "UTF-8");
			
			// - mimeMessage : MimeMessage 객체로 , 이메일 메시지의 내용을 담고 있음
			// 헬퍼를 이용해서 간단하게 해줌 true는 파일첨부 할건지말건지
			
			helper.setTo(email);
			helper.setSubject("[FeatherWorld] 인증번호");
			helper.setText(loadHtml(authKey,name), true);
			
			helper.addInline("logo", new ClassPathResource("static/images/logo.png"));
			
			mailSender.send(mimeMessage);
			
			return authKey;
			
		}catch(MessagingException e){
			e.printStackTrace();
			return null;
			
		}
		
	}


	
	
}

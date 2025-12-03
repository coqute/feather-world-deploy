package com.featherworld.project.member.model.service;

import java.util.Map;

public interface EmailService {

	/** 이메일 보내는 메서드
	 * @param string
	 * @param email 받는 사람 이메일
	 * @return authkey (인증번호)
	 * @author 영민
	 */
	String sendEmail(String string, String email);

	/** 인증번호와 이메일이 맞는지 확인하는 메서드
	 * @param map 
	 * @return
	 */
	int checkAuthKey(Map<String, String> map);

}

package com.featherworld.project.profile.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.featherworld.project.profile.model.dto.Profile;

public interface ProfileService {
    
	/** 회원
	 * @param memberNo
	 * @return
	 */
	Profile selectProfile(int memberNo);
   
	/** 사진 업로드
	 * @param loginMemberNo
	 * @param uploadFile
	 * @param bio
	 * @return
	 * @throws Exception
	 */
	int saveOrUpdateProfile(int loginMemberNo, MultipartFile uploadFile, String bio) throws Exception;
	
	/** 회원 탈퇴
	 * @param memberPw
	 * @param memberNo
	 * @return
	 */
	int secession(String memberPw, int memberNo);

	/** 카카오 회원 탈퇴
	 * @param memberNo
	 * @return
	 */
	int secessionKakaoMember(int memberNo);
}

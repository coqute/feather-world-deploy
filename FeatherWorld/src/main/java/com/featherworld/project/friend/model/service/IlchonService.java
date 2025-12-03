package com.featherworld.project.friend.model.service;

import java.util.Map;


import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.member.model.dto.Member;

public interface IlchonService {

	/**loginMember의 일촌 리스트 불러오기
	 * @param loginMemberNo
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectIlchonMemberList(int loginMemberNo, int cp);
	/**loginMember의 지금까지 들어온 일촌신청 리스트 불러오기
	 * @param loginMemberNo
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectIncomingIlchonMemberList(int loginMemberNo, int cp);
	Map<String, Object> selectSendedIlchonMemberList(int loginMemberNo, int cp);
	/*Ilchon updateIlchon();*/
	
	/** 일촌명 자동 업데이트(요청 session에 따라 TO_NICKNAME, FROM_NICKNAME 자동 구별)
	 * @param loginMemberNo
	 * @param memberNo
	 * @param nickname
	 * @return
	 */
	int updateIlchonNickname(int loginMemberNo, int memberNo,String nickname);
	/**  updateIlchonNickname + IS_ILCHON을 'N'->'Y'로 바꾸기
	 * @param loginMemberNo
	 * @param memberNo
	 * @param nickname
	 * @return
	 */
	int updateIlchonRow(int loginMemberNo, int memberNo, String nickname);
	public Ilchon selectOne(int loginMemberNo, int memberNo);

	int insertNewIlchon(int loginMemberNo, int memberNo, String nickname);
	int deleteIlchon(int loginMemberNo, int memberNo);
	
	public int isIncomingIlchonExists(int loginMemberNo,int targetMemberNo);
	
	
}

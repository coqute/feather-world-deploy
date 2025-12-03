package com.featherworld.project.member.model.service;

import java.util.List;
import java.util.Map;

import com.featherworld.project.member.model.dto.Member;
import org.springframework.http.ResponseEntity;

/** 멤버service 인터페이스
 * @author 영민
 */
public interface MemberService {

	/** 회원 여부 확인
	 * @author Jiho
	 * @param memberNo 현재 조회 중인 회원 번호
	 */
	int checkMember(int memberNo);

	/** 탈퇴한 회원 조회
	 * @author Jiho
	 * @return 탈퇴된 회원 번호 리스트
	 */
	List<Member> deletedMembers();

	/** 탈퇴한 회원 한 명 삭제
	 * @author Jiho
	 * @param memberNo 탈퇴 회원 번호
	 */
	int deleteMember(int memberNo);
	
	/** 회원가입 메서드
	 * @author 영민
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int signUp(Member inputMember, String[] memberAddress);

	/** 첫회원가입시 boardType을 하나 만들어주는 메서드
	 * @author 영민
	 * @param memberNo
	 * @return
	 */
	int setDefaultBoardType(int memberNo);

	/** 로그인 서비스
	 * @param inputMember
	 * @return
	 */
	Member login(Member inputMember);

	/** 이메일 중복 확인 서비스
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	
	/** 가입된 회원의 이메일 찾기
	 * @param inputMember
	 * @return
	 */
	Member findId(Member inputMember);

	/** 가입된 회원의 비밀번호 변경
	 * @param map
	 * @return
	 * @author 영민
	 */
	int resetPassword(Map<String, String> map);

	/** 메인홈의 회원들을 검색하는 기능
	 * @param memberName
	 * @return
	 * @author 영민
	 */
	List<Member> searchMember(String memberName);

	/** 회원가입중 전화번호 입력값이 중복인지 확인
	 * @param memberTel
	 * @return
	 * @author 영민
	 */
	int checkTel(String memberTel);



	/** 카카오 로그인중 우리회원이라면 그토큰값을 넣어줌
	 * @param memberEmail
	 * @param kakaoToken
	 * @return
	 * @author 영민
	 */
	int kakaoMemberUpdate(String memberEmail, String kakaoToken);

	/** 기존회원의 이메일을 통해 정보들을 가져옴
	 * @param memberEmail
	 * @return
	 */
	Member checkmemberEmail(String memberEmail);

	

	/** 카카오 토큰으로 새로운 회원 가입
	 * @param insertMember
	 * @return
	 * @author 영민
	 */
	int insertMember(Member insertMember);

	/** 회원정보수정에서 비밀번호 확인
	 * @param memberEmail
	 * @param password
	 * @return
	 */
	boolean validatePassword(String memberEmail, String password);

	/** 회원정보 수정페이지에서 닉네임 , 전화번호 , 비밀번호 수정 
	 * @param inputMember
	 * @return
	 */
	int updateMember(Member inputMember);

	/** 메인페이지의 투데이 베스트를 조회할 서비스
	 * @return
	 * @author 영민
	 */
	List<Member> getTodayBestMembers();
	
	/** DB에 있는 이미지 이름 조회
	 * @author Jiho
	 * @return 
	 */
	List<String> selectDbImageList();

	Member checkmemberEmailIncludingDeleted(String memberEmail);
	
}

package com.featherworld.project.member.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.featherworld.project.member.model.dto.Member;

/** 멤버 mapper 인터페이스
 * @author 영민
 * 
 */
@Mapper
public interface MemberMapper {

	int checkMember(int memberNo);

	List<Member> deletedMembers();

	int deleteMember(int memberNo);
	
	/** 회원가입 메서드
	 * @author 영민
	 * @param inputMember
	 * @return
	 */
	int signUp(Member inputMember);

	/** 회원가입을 하고나서 boardtype을 바로 추가해주는 메서드
	 * @param memberNo
	 * @author 영민
	 * @return
	 */
	int setDefaultBoardType(int memberNo);

	/** 로그인을 할때 이메일이 있는지 조회하는 메서드
	 * @param memberEmail
	 * @return
	 * @author 영민
	 */
	Member login(String memberEmail);

	/** 회원가입도중 이메일 중복확인
	 * @param memberEmail
	 * @return
	 * @author 영민
	 */
	int checkEmail(String memberEmail);

	/** 가입된 회원의 이메일 찾기
	 * @param inputMember
	 * @return
	 * @author 영민
	 */
	Member findId(Member inputMember);

	/** 가입된 회원의 비밀번호 재설정
	 * @param inputMember
	 * @return
	 */
	int resetPassword(Member inputMember);

	/** 메인에서 회원들을 검색
	 * @param memberName
	 * @return
	 * @author 영민
	 */
	List<Member> searchMember(String memberName);

	/** 전화번호 중복확인
	 * @param memberTel
	 * @return
	 * @author 영민
	 */
	int checkTel(String memberTel);

	/** 카카오토큰의 null 인지 아닌지 확인하는메서드
	 * @param memberEmail
	 * @return
	 * @author 영민
	 */
	Member findKakaoMember(String memberEmail);

	/** 기존회원 이 카카오토큰이 null 이면 변경해줌
	 * @param map
	 * @return
	 * @author 영민
	 */
	int updateKakaToken(Map<String, String> map);

	/**
	 * @param memberEmail
	 * @return
	 */
	Member checkmemberEmail(String memberEmail);

	/** 카카오 토큰으로 회원가입
	 * @param insertMember
	 * @return
	 */
	int insertMember(Member insertMember);

	/** 닉네임 , 전화번호 , 비밀번호 수정
	 * @param inputMember
	 * @return
	 */
	int updateMember(Member inputMember);

	/** 메인페이지 투데이 베스트 조회
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

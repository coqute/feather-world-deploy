package com.featherworld.project.guestBook.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.featherworld.project.guestBook.model.dto.GuestBook;
import com.featherworld.project.member.model.dto.Member;

public interface GuestBookService {
	
	/**
	 * 방명록 목록 조회 (페이징 포함)
	 * @param memberNo 홈피 주인 번호
	 * @param cp 현재 페이지
	 * @return 방명록 목록과 페이징 정보
	 */
	Map<String, Object> selectGuestBookList(int ownerNo, int cp, Integer loginMamberNo);
	
	/**
	 * 방명록 작성
	 * @param guestBook 방명록 정보
	 * @return 작성 결과 (성공 시 1, 실패 시 0)
	 * @throws Exception
	 */
	int guestBookInsert(GuestBook guestBook);
	
	/**
	 * 방명록 수정
	 * @param guestBook 수정할 방명록 정보
	 * @return 수정 결과 (성공 시 1, 실패 시 0)
	 * @throws Exception
	 */
	int guestBookUpdate(GuestBook guestBook);
	
	/**
	 * 방명록 삭제
	 * @param guestBookNo 삭제할 방명록 번호
	 * @return 삭제 결과 (성공 시 1, 실패 시 0)
	 */
	int guestBookDelete(int guestBookNo);

	
	/**
	 * 방명록 목록 조회 (로그인 회원 정보를 기반으로)
	 *
	 * @param memberNo     방명록 주인 회원 번호
	 * @param cp           현재 페이지 번호
	 * @param loginMember  로그인한 회원 객체 (null일 수 있음)
	 * @return             방명록 목록 데이터(Map 형태)
	 */
	Map<String, Object> selectGuestBookList(int memberNo, int cp, Member loginMember);

}

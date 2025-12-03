package com.featherworld.project.guestBook.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.featherworld.project.guestBook.model.dto.GuestBook;

@Mapper
public interface GuestBookMapper {
	
	/**
	 * 방명록 목록 조회 (작성자 정보 포함, 페이징)
	 * @param memberNo 홈피 주인 번호
	 * @param rowBounds 페이징 정보
	 * @return 방명록 목록
	 */
	List<GuestBook> selectGuestBookList(Map<String, Object> param);
	
	/**
	 * 방명록 총 개수 조회
	 * @param memberNo 홈피 주인 번호
	 * @return 방명록 총 개수
	 */
	int getGuestBookCount(Map<String, Object> param);
	
	/**
	 * 방명록 작성
	 * @param guestBook 방명록 정보
	 * @return 작성된 행 수
	 */
	int guestBookInsert(GuestBook guestBook);
	
	/**
	 * 방명록 삭제
	 * @param guestBookNo 방명록 번호
	 * @return 삭제된 행 수
	 */
	int guestBookDelete(@Param("guestBookNo") int guestBookNo);
	
	/**
	 * 방명록 수정
	 * @param guestBook 수정할 방명록 정보
	 * @return 수정된 행 수
	 */
	int guestBookUpdate(GuestBook guestBook);

	
	/** 방명록 1개 조회
	 * @param guestBookNo
	 * @return
	 */
	GuestBook selectOne(Integer guestBookNo);

}

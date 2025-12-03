package com.featherworld.project.guestBook.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.featherworld.project.common.dto.Pagination;
import com.featherworld.project.guestBook.model.dto.GuestBook;
import com.featherworld.project.guestBook.model.mapper.GuestBookMapper;
import com.featherworld.project.member.model.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor=Exception.class)
@Slf4j
public class GuestBookServiceImpl implements GuestBookService{

	@Autowired
	private GuestBookMapper mapper;
	
	@Override
	public Map<String, Object> selectGuestBookList(int ownerNo, int cp, Integer loginMemberNo) {
		
		Map<String, Object> param = new HashMap<>();
		param.put("ownerNo", ownerNo);
		param.put("loginMemberNo", loginMemberNo); // 로그인 사용자 번호
		
		// 1. (필터링 된) 방명록 총 개수 조회
		int listCount = mapper.getGuestBookCount(param);
		
		// 2. 방명록이 없으면 빈 데이터 반환
		Map<String, Object> result = new HashMap<>();
		if (listCount == 0) {
			result.put("guestBookList", List.of());
			result.put("pagination", null);
			return result;
		}
		
		// 3. 페이징 객체 생성 (한 페이지에 3개씩, 페이지 버튼 5개씩)
		Pagination pagination = new Pagination(cp, listCount, 3, 5);
		
		// 4. RowBounds로 페이징 처리
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		
		param.put("offset", offset);
		param.put("limit", limit);
//		RowBounds rowBounds = new RowBounds(offset, limit);
		
		// 5. 방명록 목록 조회
		List<GuestBook> guestBookList = mapper.selectGuestBookList(param);
		
		// 6. 결과 담기
		result.put("guestBookList", guestBookList);
		result.put("pagination", pagination);
		
		return result;
	}

	@Override
	public int guestBookInsert(GuestBook guestBook) {
		return mapper.guestBookInsert(guestBook);
	}

	@Override
	public int guestBookUpdate(GuestBook guestBook) {
		return mapper.guestBookUpdate(guestBook);
	}

	@Override
	public int guestBookDelete(int guestBookNo) {
		return mapper.guestBookDelete(guestBookNo);
	}

	
	/**
	 * 방명록 목록 조회 (로그인 회원 정보를 기반으로)
	 *
	 * @param memberNo     방명록 주인 회원 번호
	 * @param cp           현재 페이지 번호
	 * @param loginMember  로그인한 회원 객체 (null일 수 있음)
	 * @return             방명록 목록 데이터(Map 형태)
	 */
	@Override
	public Map<String, Object> selectGuestBookList(int memberNo, int cp, Member loginMember) {
		// 로그인한 사용자의 회원 번호를 추출 (로그인하지 않은 경우 null)
	    Integer loginMemberNo = (loginMember != null) ? loginMember.getMemberNo() : null;
	    
	    
	    return selectGuestBookList(memberNo, cp, loginMemberNo);
	}

}

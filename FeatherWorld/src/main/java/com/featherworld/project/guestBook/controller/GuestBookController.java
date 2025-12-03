package com.featherworld.project.guestBook.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.featherworld.project.common.dto.Pagination;
import com.featherworld.project.guestBook.model.dto.GuestBook;
import com.featherworld.project.guestBook.service.GuestBookService;
import com.featherworld.project.member.model.dto.Member;

@Controller
@RequestMapping
public class GuestBookController {

	@Autowired
	private GuestBookService service;

	/**
	 * 방명록 페이지 이동 (초기 로딩)
	 * @author 윤진
	 * @param memberNo 홈피 주인 번호
	 * @param loginMember 로그인한 회원 정보
	 * @param cp 현재 페이지
	 * @param model 모델
	 * @return 방명록 페이지
	 */
	@GetMapping("{memberNo:[0-9]+}/guestbook")
	public String guestBookPage(@PathVariable("memberNo") int memberNo,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			Model model) {
		
		// 방명록 목록과 페이징 정보 조회
		Map<String, Object> result = service.selectGuestBookList(memberNo, cp, loginMember);
		
		model.addAttribute("guestBookList", result.get("guestBookList"));
		model.addAttribute("pagination", result.get("pagination"));
		model.addAttribute("loginMember", loginMember);
		model.addAttribute("ownerNo", memberNo);

		return "guestBook/guestBook";
	}


	
	/**
	 * 방명록 조회 (비동기)
	 * @param memberNo
	 * @param cp
	 * @param loginMember
	 * @return
	 */
	@GetMapping("{memberNo:[0-9]+}/guestbook/list")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getGuestBookList(
			@PathVariable("memberNo") int memberNo,
			@RequestParam(value = "cp", defaultValue = "1") int cp,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {

		try {
			Integer loginMemberNo = (loginMember != null) ? loginMember.getMemberNo() : null;
			Map<String, Object> result = service.selectGuestBookList(memberNo, cp, loginMemberNo);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			Map<String, Object> errorMap = new HashMap<>();
			errorMap.put("success", false);
			errorMap.put("message", "방명록 목록 조회 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
		}
	}

	/**
	 * 방명록 작성 (비동기)
	 * @author 
	 * @param loginMember 로그인한 회원
	 * @param guestBook 방명록 정보
	 * @param memberNo 홈피 주인 번호
	 * @return 작성 결과
	 */
	@PostMapping("{memberNo:[0-9]+}/guestbook")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> insertGuestBook(
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@RequestBody GuestBook guestBook,
			@PathVariable("memberNo") int memberNo) {

		Map<String, Object> result = new HashMap<>();

		try {
			// 로그인 체크
			if (loginMember == null) {
				result.put("success", false);
				result.put("message", "로그인이 필요합니다.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
			}

			// 작성자/홈피 주인 정보 설정
			guestBook.setVisitorNo(loginMember.getMemberNo());
			guestBook.setOwnerNo(memberNo);

			// 방명록 작성
			int insertResult = service.guestBookInsert(guestBook);

			if (insertResult > 0) {
				result.put("success", true);
				result.put("message", "방명록이 작성되었습니다.");
			} else {
				result.put("success", false);
				result.put("message", "방명록 작성에 실패했습니다.");
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "방명록 작성 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	/**
	 * 방명록 수정 (비동기)
	 * @author 윤진
	 * @param loginMember 로그인한 회원
	 * @param guestBook 수정할 방명록 정보
	 * @param memberNo 홈피 주인 번호
	 * @return 수정 결과
	 */
	@PutMapping("{memberNo:[0-9]+}/guestbook")
	@ResponseBody
	public int updateGuestBook(
	    @SessionAttribute(value = "loginMember", required = false) Member loginMember,
	    @RequestBody GuestBook guestBook,
	    @PathVariable("memberNo") int memberNo) {

	    // 로그인 안 했을 경우 0 반환
	    if (loginMember == null) return 0;

	    // 작성자 번호 설정 (검증용)
	    guestBook.setVisitorNo(loginMember.getMemberNo());

	    // 서비스로 업데이트 요청
	    return service.guestBookUpdate(guestBook); // 성공 시 1, 실패 시 0
	}


	/**
	 * 방명록 삭제 (비동기)
	 * @param loginMember 로그인한 회원
	 * @param memberNo 홈피 주인 번호
	 * @param requestBody 삭제할 방명록 번호
	 * @return 삭제 결과
	 */
	@DeleteMapping("{memberNo:[0-9]+}/guestbook")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteGuestBook(
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			@PathVariable("memberNo") int memberNo,
			@RequestBody Map<String, Integer> requestBody) {

		Map<String, Object> result = new HashMap<>();

		try {
			// 로그인 체크
			if (loginMember == null) {
				result.put("success", false);
				result.put("message", "로그인이 필요합니다.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
			}

			int guestBookNo = requestBody.get("guestBookNo");

			// 삭제 실행
			int deleteResult = service.guestBookDelete(guestBookNo);

			if (deleteResult > 0) {
				result.put("success", true);
				result.put("message", "방명록이 삭제되었습니다.");
			} else {
				result.put("success", false);
				result.put("message", "방명록 삭제에 실패했습니다.");
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "방명록 삭제 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

}

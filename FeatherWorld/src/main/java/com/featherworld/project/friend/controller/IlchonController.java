package com.featherworld.project.friend.controller;



import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.featherworld.project.board.controller.BoardController;
import com.featherworld.project.common.dto.Pagination;
import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.friend.model.service.IlchonService;
import com.featherworld.project.member.model.dto.Member;

import jakarta.servlet.http.HttpServletRequest;

@Controller
//@RestController // @Controller + @ResponseBody
public class IlchonController {

    private final BoardController boardController;
	
	@Autowired
	private IlchonService service;


    IlchonController(BoardController boardController) {
        this.boardController = boardController;
    }


	@GetMapping("{memberNo:[0-9]+}/friendList")
	
	public String select(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable("memberNo") int memberNo
			,@RequestParam(value = "cp", required = false, defaultValue = "1") int cp
			,Model model,   HttpServletRequest request){


		//Session에서 loginMember의 MEMBER_NO를 불러오기.
		
		int loginMemberNo=0;
		// loginMember의 MEMBER_NO를 불러오기
		if(loginMember != null) {loginMemberNo = loginMember.getMemberNo(); }

		
		Map<String, Object> map = service.selectIlchonMemberList(memberNo, cp);
		//map에서 ilchons 따로 변수로 뺴낼것
		
		//friendList page에 전달한 현재 홈피 주인의 member DTO
		int ilchonStatus = service.isIncomingIlchonExists(loginMemberNo,memberNo);/****************************250526 확인못한 일촌신청 있는지 확인하는 매커니즘 추가***************************/
		System.out.println("ilchons: " +  map.get("ilchons"));
	
		model.addAttribute("ilchons", map.get("ilchons"));
		model.addAttribute("memberNo", memberNo);

	    model.addAttribute("pagination", map.get("pagination"));
	    model.addAttribute("ilchonStatus", ilchonStatus);
	    System.out.println("ilchonStatus : " + ilchonStatus);
	    
	    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
	        return "friendList/friendListPart";
	    }
		return "friendList/friendListCopy";
		
	}
	
	@GetMapping("{memberNo:[0-9]+}/friendList/incoming")
	
	public String selectIncoming(@SessionAttribute(name = "loginMember", required = true) Member loginMember,
			@PathVariable("memberNo") int memberNo
			,@RequestParam(value = "cp", required = false, defaultValue = "1") int cp /*내게 들어온 일촌신청 list요소의 pagination cp항목*/
			,@RequestParam(value = "cpFrom", required = false, defaultValue = "1") int cpFrom /*내게 들어온 일촌신청 list요소의 pagination cp항목*/
			,Model model, HttpServletRequest request){

		//Session에서 loginMember의 MEMBER_NO를 불러오기.
		
		int loginMemberNo=0;
		// loginMember의 MEMBER_NO를 불러오기
		if(loginMember != null) {loginMemberNo = loginMember.getMemberNo(); }

		
		Map<String, Object> map = service.selectIncomingIlchonMemberList(loginMemberNo, cp);
		//map에서 ilchons 따로 변수로 뺴낼것
		Map<String, Object> mapFrom = service.selectSendedIlchonMemberList(loginMemberNo, cpFrom); // 내가 보낸 일촌신청 목록 조회 서비스
		
		//friendList page에 전달한 현재 홈피 주인의 member DTO

		model.addAttribute("ilchons", map.get("ilchons"));
		model.addAttribute("ilchonsIncomingCount", map.size()); // 일촌신청(incoming) count 개수
		model.addAttribute("memberNo", memberNo);
		/***내가 보낸 일촌신청 리스트 model에 추가하는 코드  추가 250527***/
		model.addAttribute("ilchonsFrom", mapFrom.get("ilchons"));
		model.addAttribute("ilchonsFromIncomingCount", map.size()); // 내가보낸일촌신청(incoming) count 개수
	    model.addAttribute("pagination", map.get("pagination"));
	    model.addAttribute("paginationFrom", mapFrom.get("pagination"));
	    
	    Pagination pagination = (Pagination) map.get("pagination");
	    Pagination paginationFrom = (Pagination) mapFrom.get("pagination");
	    System.out.println("currPagination:" + pagination.getCurrentPage());
	    System.out.println("currPaginationFrom:" + paginationFrom.getCurrentPage());
	    
	    
		
	    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
	        return "friendList/incomingfriendListPart";
	    }
	    return "friendList/incomingFriendListCopy";
		
	}
	
	@PostMapping("update/nickname")
	@ResponseBody
	public Map<String, Object> updateIlchonNickname(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@RequestBody Map<String, String> payload /*클라이언트쪽에서 건너오는 요청*/
			, Model model) {
		String memberNoStr = 	payload.get("memberNo");

		int memberNo =  Integer.parseInt(memberNoStr); // 1. loginMember의 일촌의 memberId 값

		int loginMemberNo=0;
		if(loginMember != null) {loginMemberNo = loginMember.getMemberNo(); }// 2. loginMember본인의 memberId 값

		String nickname = payload.get("nickname");
		System.out.println("loginMember: "+loginMemberNo + ",memberNo: "+ memberNo +",nickname:"+ nickname);
		int result = service.updateIlchonNickname(loginMemberNo,memberNo,nickname);

		if(result == 2) { // 수정 성공시
			Ilchon ilchon = service.selectOne(loginMemberNo, memberNo);

		    //model.addAttribute("pagination", map.get("pagination"));
			Map<String, Object> response = new HashMap<>();
			response.put("Ilchon", ilchon);
			response.put("status", 2);//TO_NICKNAME update success
			return response;

		}
		else if(result == 1) { // 수정 성공시
			Ilchon ilchon = service.selectOne(loginMemberNo, memberNo);
			Map<String, Object> response = new HashMap<>();
			response.put("Ilchon", ilchon);
			response.put("status", 1);//FROM_NICKNAME update success
		    //model.addAttribute("pagination", map.get("pagination"));
			return response;

		}else {//수정 실패시
			Map<String, Object> response = new HashMap<>();

			response.put("status", 0);//NICKNAME update failure
			return response;

		}//홍길동, 김철수

	}

	@PostMapping("/update/accept")
	@ResponseBody
	public Map<String, Object> updateIsIlchon(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@RequestBody Map<String, String> payload /*클라이언트쪽에서 건너오는 요청*/
			, Model model) {
		
		String memberNoStr = payload.get("memberNo");

		int memberNo =  Integer.parseInt(memberNoStr); // 1. loginMember의 일촌의 memberId 값

		int loginMemberNo=0;
		if(loginMember != null) {loginMemberNo = loginMember.getMemberNo(); }// 2. loginMember본인의 memberId 값

		String nickname = payload.get("nickname");
		System.out.println("loginMember: "+loginMemberNo + ",memberNo: "+ memberNo +",nickname:"+ nickname);
		/*int result = service.updateIlchonNickname(loginMemberNo,memberNo,nickname);*/
		int result = service.updateIlchonRow(loginMemberNo,memberNo,nickname);
		if(result > 0) {// 수정 성공시 is_ilchon = 'Y'로 업데이트
			
			
		} 
		if(result == 2) { // 수정 성공시
			Ilchon ilchon = service.selectOne(loginMemberNo, memberNo);

		    //model.addAttribute("pagination", map.get("pagination"));
			Map<String, Object> response = new HashMap<>();
			response.put("Ilchon", ilchon);
			response.put("status", 2);//TO_NICKNAME update success
			return response;

		}
		else if(result == 1) { // 수정 성공시
			Ilchon ilchon = service.selectOne(loginMemberNo, memberNo);
			Map<String, Object> response = new HashMap<>();
			response.put("Ilchon", ilchon);
			response.put("status", 1);//FROM_NICKNAME update success
		    //model.addAttribute("pagination", map.get("pagination"));
			return response;

		}else {//수정 실패시
			Map<String, Object> response = new HashMap<>();

			response.put("status", 0);//NICKNAME update failure
			return response;

		}//홍길동, 김철수

	}

	@GetMapping("{memberNo:[0-9]+}/newFriend/input")
	public String inputNewIlchonReq(@PathVariable("memberNo") int memberNo /*클라이언트쪽에서 건너오는 요청*/,
			Model model) {
		model.addAttribute("memberNo", memberNo);
		return "friendList/sendFriendReq";
	}

	//@PostMapping("insert/newFriend")
	//@ResponseBody // 비동기? 동기?
	@PostMapping("insert/newFriend")
	public String insertNewIlchon(@SessionAttribute(name = "loginMember", required = false) Member loginMember,

			/*클라이언트쪽에서 건너오는 요청*//*@RequestBody Map<String, String> payload*/ 
			@RequestParam("memberNo") int memberNo, @RequestParam("nickname") String nickname, Model model) {
		
		
		/*String memberNoStr = 	payload.get("memberNo");
		
		int memberNo =  Integer.parseInt(memberNoStr); */

		int loginMemberNo = loginMember.getMemberNo();
		int result = service.insertNewIlchon(loginMemberNo,memberNo, nickname);
		if(result == 1) {
			return "redirect:/" + memberNo + "/friendList";  //pagination 보존 안함
		}else if(result == 0)  {
			return "redirect:/" + memberNo + "/friendList"; //pagination 보존 안함

		}else if(result == -1) {
			return "redirect:/" + memberNo + "/friendList";  //pagination 보존 안함
		}
		return "redirect:/" + memberNo + "/friendList"; //pagination 보존 안함

	}
	
	@PostMapping("delete")/*작성중*/
	@ResponseBody
	public Map<String, Object> deleteIlchon(@SessionAttribute(name = "loginMember", required = true) Member loginMember,
			@RequestBody Map<String, String> payload /*클라이언트쪽에서 건너오는 요청*/
			, Model model) {
		String memberNoStr = 	payload.get("memberNo");

		int memberNo =  Integer.parseInt(memberNoStr); // 1. loginMember의 일촌의 memberId 값

		int loginMemberNo=0;
		if(loginMember != null) {loginMemberNo = loginMember.getMemberNo(); }// 2. loginMember본인의 memberId 값

		String nickname = payload.get("nickname");
		System.out.println("/delete info: loginMember: "+loginMemberNo + ",memberNo: "+ memberNo +",nickname:"+ nickname);
		int result = service.deleteIlchon(loginMemberNo,memberNo);

		if(result == 1) { // 수정 성공시
			Ilchon ilchon = service.selectOne(loginMemberNo, memberNo);

		    //model.addAttribute("pagination", map.get("pagination"));
			Map<String, Object> response = new HashMap<>();
			response.put("deleted", ilchon);
			response.put("status", 1);//TO_NICKNAME update success
			return response;

		}
		else if(result == 0) { // 수정 실패시
			Ilchon ilchon = service.selectOne(loginMemberNo, memberNo);
			Map<String, Object> response = new HashMap<>();
			response.put("Ilchon", ilchon);
			response.put("status", 0);//FROM_NICKNAME update success
		    //model.addAttribute("pagination", map.get("pagination"));
			return response;

		}else {//수정 실패시
			Map<String, Object> response = new HashMap<>();

			response.put("status", -1);//NICKNAME update failure
			return response;

		}//홍길동, 김철수

	}
}


package com.featherworld.project.member.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.featherworld.project.member.model.dto.Member;

import com.featherworld.project.member.model.service.MemberService;
import com.featherworld.project.miniHome.model.service.MiniHomeService;

import jakarta.servlet.http.HttpSession;



/** member 컨트롤러 클래스
 * @author 영민
 */
@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("member")
public class MemberController {

	@Autowired
	private MemberService service;
	
	/** 회원가입 하는 페이지로 이동하는 메서드(get)
	 * @author 영민
	 */
	@GetMapping("signup")
	public String signUp() {
		return "member/signUp";
	}
	
	/** 회원가입 메서드 (post)
	 * @author 영민
	 * @param inputMember
	 * @param memberAddress
	 * @param ra
	 */
	@PostMapping("signup")
	public String signUp(Member inputMember, @RequestParam("memberAddress") String[] memberAddress, RedirectAttributes ra) {
		
		int result = service.signUp(inputMember,memberAddress);
		
		String path = null; //경로
		String message = null; // ra 메시지
		
		if(result > 0) {
			
			message = inputMember.getMemberName()+ "님 회원가입완료";
			path = "/";
	
		}else {
		 
			message = "회원가입실패..";
			path = "signup";
		}
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	/** 로그인 하는 메서드
	 * @author 영민
	 */
	// 아이디 저장은 아직 안함
	@PostMapping("login")
	public String login(Member inputMember,RedirectAttributes ra, Model model) {
		
		Member loginMember = service.login(inputMember);
		
		if(loginMember == null) {
			// 로그인실패
			ra.addFlashAttribute("message","아이디또는 비밀번호가 맞지않습니다,");
			
		}else {
			//로그인성공
			model.addAttribute("loginMember",loginMember);
			
			// --- saveId 할꺼면 여기서부터 진행..
		}
	
		return "redirect:/";
	}
	
		/** 이메일 중복하는 메서드(비동기..)
		 * @param memberEmail
		 * @author 영민
		 */
		@GetMapping("checkEmail")
		@ResponseBody
		public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}
	/** 회원가입중 중복 전화번호 있는지
	 * @param memberTel
	 * @return
	 * @author 영민
	 */
	@GetMapping("checkTel")
	@ResponseBody
	public int checkTel(@RequestParam("memberTel") String memberTel) {
		return service.checkTel(memberTel);
	}
	
	/** 로그아웃
	 * @param status
	 * @author 영민
	 */
	@PostMapping("logout")
	public String logout(HttpSession session , SessionStatus status) {
		session.invalidate();
		 status.setComplete();
		
		// 페이지 리다이렉트
		return "redirect:/";
	}
	/** 아이디 찾기 사이트로 포워드
	 * @author 영민
	 */
	@GetMapping("findId")
	public String findId() {
		return "member/findId";
	}
	
	/** 비밀번호 찾기 로 이동
	 * @return
	 */
	@GetMapping("findPw")
	public String finePw() {
		return "member/findPw";
	}
	
	
	/** 아이디 찾는 서비스
	 * @param ra
	 * @author 영민
	 * @return memberEmail
	 */
	@PostMapping("findId")
	@ResponseBody
	public String findId(Member inputMember,
						 RedirectAttributes ra) {
		
		Member inputMemberEmail = service.findId(inputMember);
		
		if(inputMemberEmail == null) {
		
			return "";
		}
		
		String getEmail = inputMemberEmail.getMemberEmail();
	
		return getEmail;
	}
	
	/**비밀번호 재설정
	 * @param map
	 * @return
	 * @author 영민
	 */
	@PostMapping("resetPassword")
	@ResponseBody
	public int resetPassword(@RequestBody Map<String, String> map) {
		
		int result = service.resetPassword(map);
		
		if(result >0) {
			
			return result;
		}else {
			
			return 0;
		}
		
	}
	
	/** 메인 페이지에서 회원검색
	 * @param memberName
	 * @return
	 * @author 영민
	 */
	@GetMapping("search")
	@ResponseBody
	public List<Member> searchMembers(@RequestParam("memberName") String memberName) {
	 
		return service.searchMember(memberName);
	}
	
	/** 카카오 APi를 이용한 로그인 처리
	 * @param kakaodate
	 * @param session
	 * @return
	 */
   	@ResponseBody
    @PostMapping("kakaoLogin")
   	public Map<String, Object> kakaoLogin(@RequestBody Map<String, String> kakaodata, HttpSession session) {

		Map<String, Object> response = new HashMap<>();
	
		try {
			String memberEmail = kakaodata.get("memberEmail");
			String kakaoToken = kakaodata.get("kakaoToken");
			String memberName = kakaodata.get("memberName");

			// 이메일로 기존 활성 회원 조회
			Member member = service.checkmemberEmail(memberEmail);

			if (member != null) {
				// 기존 활성 회원이 있는 경우

				// 카카오 토큰 업데이트
				 int result = service.kakaoMemberUpdate(memberEmail, kakaoToken);

				 	if (result > 0) {
                         // 업데이트 성공 - 최신 정보로 회원 다시 조회
				 			member = service.checkmemberEmail(memberEmail);

				 			// 세션에 로그인 정보 저장
				 			session.setAttribute("loginMember", member);

				 			response.put("success", true);
							response.put("message", "로그인 성공");
							response.put("isNewMember", false);
					} else {
						// 업데이트 실패
						response.put("success", false);
						response.put("message", "카카오 토큰 업데이트 실패");
					}
			} else {
				// 활성 회원이 없는 경우 - 탈퇴한 회원인지 확인
				
				// 🚨 추가: 탈퇴한 회원 포함해서 조회
				Member deletedMember = service.checkmemberEmailIncludingDeleted(memberEmail);
							
				if (deletedMember != null) {
				// 탈퇴한 회원이 존재하는 경우 - 재가입 차단
				response.put("success", false);
				response.put("message", "이미 사용된 이메일입니다. 해당 이메일로는 재가입이 불가능합니다.");

				} else {
					// 완전히 새로운 회원인 경우 - 신규 가입 허용

					Member newMember = new Member();
					newMember.setMemberEmail(memberEmail);
					newMember.setMemberName(memberName);
					newMember.setKakaoAccessToken(kakaoToken);

					int insertResult = service.insertMember(newMember);

					if (insertResult > 0) {
						// 신규 회원 등록 성공
						// 등록 후 최신 정보로 회원 조회
						Member insertedMember = service.checkmemberEmail(memberEmail);

							//	세션에 로그인 정보 저장
						   session.setAttribute("loginMember", insertedMember);

						   response.put("success", true);
						   response.put("message", "회원가입 및 로그인 성공");
						   response.put("isNewMember", true);
					} else {
						
						// 신규 회원 등록 실패
						response.put("success", false);
						response.put("message", "회원가입 실패");
					}
				}
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
		}

		return response;
	}

}
	


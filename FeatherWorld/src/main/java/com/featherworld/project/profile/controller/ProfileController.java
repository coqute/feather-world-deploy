package com.featherworld.project.profile.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.profile.model.dto.Profile;
import com.featherworld.project.profile.model.service.ProfileService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({ "loginMember" })
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	// 프로필 보기
	@GetMapping("{memberNo:[0-9]+}/profile")
	public String Profile(@PathVariable("memberNo") int memberNo, Model model) {
		Profile profile = profileService.selectProfile(memberNo);
		log.debug("profile {}", profile);
		model.addAttribute("profile", profile);
		model.addAttribute("memberNo", memberNo);
		return "profile/profile";
	}

	@GetMapping("{memberNo:[0-9]+}/editprofile")
	public String editProfilePage(@PathVariable("memberNo") int memberNo, Model model) {
		Profile profile = profileService.selectProfile(memberNo);
		model.addAttribute("profile", profile);
		model.addAttribute("memberNo", memberNo);
		return "profile/editprofile";
	}

	@GetMapping("{memberNo:[0-9]+}/profileupdate")
	public String showProfileUpdateForm(@PathVariable("memberNo") int memberNo, Model model,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes ra) {
		if (loginMember == null || loginMember.getMemberNo() != memberNo) {
			ra.addFlashAttribute("message", "접근 할 수 없는 없는 경로입니다!(다른사람 프로필 업데이트 불가)");
			return "redirect:/";
		}
			
		Profile profile = profileService.selectProfile(memberNo);
		model.addAttribute("profile", profile);
		return "profile/profileupdate";
	}

	@PostMapping("{memberNo:[0-9]+}/profileupdate")
	public String updateProfile(@PathVariable("memberNo") int memberNo,
			@SessionAttribute("loginMember") Member loginMember, @RequestParam("uploadFile") MultipartFile uploadFile,
			@RequestParam("bio") String bio, RedirectAttributes ra) throws Exception {

		if (loginMember.getMemberNo() == memberNo) {
			int result = profileService.saveOrUpdateProfile(loginMember.getMemberNo(), uploadFile, bio);

			String message = null;
			if (result > 0)
				message = "변경 성공!";
			else
				message = "변경 실패";

			ra.addFlashAttribute("message", message);
			return "redirect:/" + memberNo + "/profile";

		} else {
			ra.addFlashAttribute("message", "접근 할 수 없는 없는 경로입니다!(다른사람 프로필 업데이트 불가)");
			return "redirect:/";
		}
	}

	/** 
	 * 회원 탈퇴 페이지 (GET)
	 */
	@GetMapping("{memberNo}/profiledelete")
	public String profileDelete(@PathVariable("memberNo") int memberNo, HttpSession session, 
			RedirectAttributes ra, Model model) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		if (loginMember == null || loginMember.getMemberNo() != memberNo) {
			ra.addFlashAttribute("message", "잘못된 접근입니다.");
			return "redirect:/";
		}

		model.addAttribute("memberNo", memberNo);
		return "profile/profiledelete";
	}

	/** 
	 * 회원 탈퇴 처리 (POST)
	 * - memberPw가 null이면 카카오 회원, 있으면 일반 회원으로 구분
	 */
	@PostMapping("{memberNo}/profiledelete")
	public String secession(@PathVariable("memberNo") int memberNo,
	                        @RequestParam(value = "memberPw", required = false) String memberPw,
	                        HttpSession session,
	                        RedirectAttributes ra,
	                        SessionStatus status) {

		Member loginMember = (Member) session.getAttribute("loginMember");
		
		
		log.info("=== 탈퇴 처리 시작 ===");
	    log.info("memberNo: {}", memberNo);
	    log.info("loginMember: {}", loginMember);
	    log.info("loginMember.getMemberPw(): {}", loginMember != null ? loginMember.getMemberPw() : "null");
	    log.info("입력된 memberPw: {}", memberPw);

		
		// 1. 접근 권한 확인
		if (loginMember == null || loginMember.getMemberNo() != memberNo) {
			ra.addFlashAttribute("message", "잘못된 접근입니다.");
			return "redirect:/" + memberNo + "/profiledelete";
		}

		int result = 0;
		String message;
		String path;

		try {
			// 2. 🚨 memberPw로 카카오/일반 회원 구분
			if (loginMember.getMemberPw() == null) {
				// 카카오 회원 탈퇴 처리
				 log.info("카카오 회원으로 판단");
				log.info("카카오 회원 탈퇴 처리 - memberNo: {}", memberNo);
				result = profileService.secessionKakaoMember(memberNo);
				log.info("카카오 탈퇴 결과: {}", result);
				
			} else {
				// 일반 회원 탈퇴 처리
				log.info("일반 회원 탈퇴 처리 - memberNo: {}", memberNo);
				
				if (memberPw == null || memberPw.trim().isEmpty()) {
					ra.addFlashAttribute("message", "비밀번호를 입력해주세요.");
					return "redirect:/" + memberNo + "/profiledelete";
				}
				
				result = profileService.secession(memberPw, memberNo);
			}

			// 3. 결과 처리
			if (result > 0) {
				// 탈퇴 성공
				if (loginMember.getMemberPw() == null) {
					message = "카카오 계정 탈퇴가 완료되었습니다.\n" +
					         "카카오 연결 해제는 카카오톡 > 더보기 > 설정 > 카카오계정 > 연결된 서비스 관리에서 수동으로 진행해주세요.";
				} else {
					message = "회원탈퇴가 완료되었습니다.";
				}
				
				path = "/";
				
				// 세션 완전 무효화
				status.setComplete();
				session.invalidate();
				
				log.info("회원 탈퇴 완료 - memberNo: {}, type: {}", 
						memberNo, (loginMember.getMemberPw() == null ? "KAKAO" : "NORMAL"));
				
			} else {
				// 탈퇴 실패
				if (loginMember.getMemberPw() == null) {
					message = "카카오 계정 탈퇴 처리 중 오류가 발생했습니다.";
				} else {
					message = "비밀번호가 일치하지 않습니다.";
				}
				path = "/" + memberNo + "/profiledelete";
			}

		} catch (Exception e) {
			log.error("예외 발생!!!", e);
			log.error("회원 탈퇴 처리 중 예외 발생 - memberNo: {}", memberNo, e);
			message = "탈퇴 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
			path = "/" + memberNo + "/profiledelete";
		}

		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}
}
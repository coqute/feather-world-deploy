package com.featherworld.project.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.member.model.service.MemberService;

/** 메인 페이지 컨트롤러
 * @author Jiho
 */
@Controller
public class MainController {
	
	@Autowired
	private MemberService memberService;
	
	
	/** 메인 페이지 forward 메서드
	 * @author Jiho , 영민 (여기다가 today's best를 구현하려고)
	 * @return src/main/resources/templates/common/main.html
	 */
	@RequestMapping("/")
	public String mainPage(Model model) {
		
		// 오늘 의 투데이 베스트 담을 6... 명 ? 조회
		List<Member> todayBestMembers = memberService.getTodayBestMembers();
		model.addAttribute("todayBestMembers", todayBestMembers);
		
		return "common/main";
	}

	@RequestMapping("{memberNo:[0-9]+}")
	public String memberPage(@PathVariable("memberNo") int memberNo) {
		return "redirect:/"+ memberNo +"/minihome";

	}
	
}

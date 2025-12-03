package com.featherworld.project.playlist;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.playlist.model.service.PlaylistService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class PlaylistController {

	@Autowired
	private PlaylistService service;

	/**
	 * 플레이리스트 조회
	 * 
	 * @param memberNo
	 * @return
	 */
	@GetMapping("{memberNo:[0-9]+}/playlist")
	public String forwardPlaylist(@PathVariable("memberNo") int memberNo, Model model) {

		// 이미 저장된 플리가 있는지 체크
		String url = service.selectPlaylist(memberNo);
		model.addAttribute("youtubeUrl", url);
		// youtubeUrl = https://www.youtube.com/embed/videoseries?list=PLRgBLD35YamvDsbX-tlCEXPlubd7nMUoJ

		return "playList/playList";
	}

	/**
	 * 플레이리스트 저장 or 수정
	 * 
	 * @param memberNo
	 * @param youtubeUrl
	 * @return insert 결과행의 갯수
	 */
	@ResponseBody
	@PostMapping("{memberNo:[0-9]+}/playlist/insert")
	public int insertPlaylist(@PathVariable("memberNo") int memberNo, 
							@RequestBody String youtubeUrl) {

		// 이미 저장된 플리가 있는지 체크
		String url = service.selectPlaylist(memberNo);

		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", memberNo);
		map.put("youtubeUrl", youtubeUrl);
		
		// 이미 저장된 플리가 있다면 수정으로
		if (url != null) {
			return service.updatePlaylist(map);
			
		} else {
			// 이미 저장된 플리가 없다면 삽입으로
			return service.insertPlaylist(map);
		}

	}
	
	/** 플레이리스트 삭제
	 * @param memberNo
	 * @return
	 */
	@ResponseBody
	@DeleteMapping("{memberNo:[0-9]+}/playlist/delete")
	public int deletePlaylist(@PathVariable("memberNo") int memberNo) {
		return service.deletePlaylist(memberNo);
	}

}

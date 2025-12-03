package com.featherworld.project.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.miniHome.model.service.MiniHomeService;

/**
 * 전역에서 사용되는 공통 API들
 * 모든 페이지에서 접근 가능한 API 엔드포인트 제공
 */
@RestController
public class GlobalApiController {

    @Autowired
    private MiniHomeService miniHomeService;

    /**
     * 일촌신청 API (전역에서 사용)
     * 어떤 페이지에서든 {memberNo}/follow로 요청 가능
     * 
     * 사용 예:
     * - /123/guestbook 페이지에서 → POST /123/follow 
     * - /456/playlist 페이지에서 → POST /456/follow
     * - /789/diary 페이지에서 → POST /789/follow
     */
    @PostMapping("{memberNo:[0-9]+}/follow")
    @ResponseBody
    public Map<String, Object> sendFollowRequest(@PathVariable("memberNo") int memberNo,
                                               @RequestParam("toMemberNo") int toMemberNo,
                                               @SessionAttribute("loginMember") Member loginMember) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // URL 일관성 검증
            if (memberNo != toMemberNo) {
                response.put("success", false);
                response.put("message", "잘못된 요청입니다.");
                return response;
            }
            
            // 로그인 체크
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }
            
            // 본인에게 일촌 신청하는 경우 방지
            if (loginMember.getMemberNo() == toMemberNo) {
                response.put("success", false);
                response.put("message", "본인에게는 일촌 신청을 할 수 없습니다.");
                return response;
            }
            
            // 내가 이미 신청했는지 확인
            Ilchon myRequest = new Ilchon();
            myRequest.setFromMemberNo(loginMember.getMemberNo());
            myRequest.setToMemberNo(toMemberNo);
            
            int myRequestCount = miniHomeService.findIlchon(myRequest);
            if (myRequestCount > 0) {
                response.put("success", false);
                response.put("message", "이미 일촌 신청을 보냈거나 일촌 관계입니다.");
                return response;
            }
            
            // 상대방이 나에게 신청했는지 확인
            Ilchon theirRequest = new Ilchon();
            theirRequest.setFromMemberNo(toMemberNo);
            theirRequest.setToMemberNo(loginMember.getMemberNo());
            
            int theirRequestCount = miniHomeService.findPendingIlchon(theirRequest);
            if (theirRequestCount > 0) {
                response.put("success", false);
                response.put("message", "상대방이 이미 일촌 신청을 보냈습니다. 일촌 신청 목록을 확인해주세요.");
                return response;
            }
            
            // 일촌 신청 진행         
            Ilchon followRequest = new Ilchon();
            followRequest.setFromMemberNo(loginMember.getMemberNo());
            followRequest.setToMemberNo(toMemberNo);
            followRequest.setFromNickname(loginMember.getMemberName());
            
            Member toMember = miniHomeService.findmember(toMemberNo);
            if (toMember == null) {
                response.put("success", false);
                response.put("message", "존재하지 않는 사용자입니다.");
                return response;
            }
            
            followRequest.setToNickname(toMember.getMemberName());
            
            // 일촌 신청 저장
            int result = miniHomeService.sendFollowRequest(followRequest);
            
            if (result > 0) {
                response.put("success", true);
                response.put("message", toMember.getMemberName() + "님에게 일촌 신청을 보냈습니다!");
                response.put("targetMember", toMember.getMemberName());
            } else {
                response.put("success", false);
                response.put("message", "일촌 신청에 실패했습니다. 다시 시도해주세요.");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "일촌 신청 처리 중 오류가 발생했습니다.");
           
        }
        
        return response;
    }
    
    
   
}
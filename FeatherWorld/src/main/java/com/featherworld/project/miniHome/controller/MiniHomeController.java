package com.featherworld.project.miniHome.controller;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.featherworld.project.board.model.dto.Board;
import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.miniHome.model.service.MiniHomeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MiniHomeController {

    @Autowired
    private MiniHomeService miniHomeService;
    
    /**
     * 미니홈 메인 페이지
     */
    @GetMapping("{memberNo:[0-9]+}/minihome")
    public String miniHomePage(@PathVariable("memberNo") int memberNo,
                              @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                              Model model) {

        Map<String, Integer> map = new HashMap<>();
        map.put("memberNo", memberNo);
        
        int loginMemberNo = (loginMember != null) ? loginMember.getMemberNo() : 0;
        map.put("loginMemberNo", loginMemberNo);
        
        List<Board> recentBoardList = miniHomeService.getRecentBoards(map);
        
        // 일촌평 조회
        List<Ilchon> ilchonComments = miniHomeService.getIlchonComments(memberNo);
      
        
        int totalBoardCount = miniHomeService.getTotalBoardCount(map);
        int totalGuestBookCount = miniHomeService.getTotalGuestBookCount(map);
        
        
        model.addAttribute("totalBoardCount", totalBoardCount);
        model.addAttribute("totalGuestBookCount", totalGuestBookCount);
        model.addAttribute("recentBoards", recentBoardList);
        model.addAttribute("ilchonComments", ilchonComments); 
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("memberNo", memberNo);
        
        
        
        return "miniHome/miniHome";
    }

    /**
     * 일촌평 작성/수정
     */
    @PostMapping("{memberNo:[0-9]+}/ilchoncomment")
    @ResponseBody
    public Map<String, Object> createOrUpdateIlchonComment(
            @PathVariable("memberNo") int pageOwnerNo,
            @RequestBody Map<String, Object> requestData,
            @SessionAttribute("loginMember") Member loginMember) {

        Map<String, Object> response = new HashMap<>();

        try {
          
          
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }

            String commentContent = (String) requestData.get("ilchonCommentContent");
            
            // 내용 검증
            if (commentContent == null || commentContent.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "일촌평 내용을 입력해주세요.");
                return response;
            }

            if (commentContent.length() > 30) {
                response.put("success", false);
                response.put("message", "일촌평은 30자 이내로 작성해주세요.");
                return response;
            }

            // 권한 확인
            if (!checkWritePermission(loginMember, pageOwnerNo)) {
                response.put("success", false);
                response.put("message", "일촌평 작성 권한이 없습니다.");
                return response;
            }

            // 일촌평 저장 (양방향 시도)
            int result = saveIlchonComment(loginMember.getMemberNo(), pageOwnerNo, commentContent.trim());

            if (result > 0) {
              
                response.put("success", true);
                response.put("message", "일촌평이 작성되었습니다.");
            } else {
               
                response.put("success", false);
                response.put("message", "일촌평 작성에 실패했습니다.");
            }

        } catch (Exception e) {
           
            response.put("success", false);
            response.put("message", "일촌평 처리 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return response;
    }
    /**
     * 일촌평 삭제
     */
    @DeleteMapping("{memberNo:[0-9]+}/ilchoncomment")
    @ResponseBody
    public Map<String, Object> deleteIlchonComment(
            @PathVariable("memberNo") int pageOwnerNo,
            @RequestBody Map<String, Object> requestData,
            @SessionAttribute("loginMember") Member loginMember) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
           
            
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }
            
            int authorNo = (Integer)requestData.get("authorNo");  // 실제 작성자 번호
            
           
            
            // 삭제 권한 확인 (작성자이거나 페이지 주인)
            boolean canDelete = (loginMember.getMemberNo() == authorNo || 
                               loginMember.getMemberNo() == pageOwnerNo);
            
            if (!canDelete) {
                response.put("success", false);
                response.put("message", "일촌평 삭제 권한이 없습니다.");
                return response;
            }
            
            // 일촌평 삭제 (양방향 시도)
            int result = deleteIlchonCommentInternal(authorNo, pageOwnerNo);
            
            if (result > 0) {
                response.put("success", true);
                response.put("message", "일촌평이 삭제되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "일촌평 삭제에 실패했습니다.");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "일촌평 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        
        return response;
    }
    /**
     * 일촌평 저장 (양방향 시도)
     */
    private int saveIlchonComment(int authorNo, int pageOwnerNo, String content) {
        
        
        // 1차: 작성자→페이지주인 방향으로 FROM_COMMENT에 저장
       
        Ilchon relation1 = new Ilchon();
        relation1.setFromMemberNo(authorNo);
        relation1.setToMemberNo(pageOwnerNo);
        relation1.setFromComment(content);
        
        int result = miniHomeService.updateIlchonFromComment(relation1);
       
        
        if (result == 0) {
            // 2차: 페이지주인→작성자 방향으로 TO_COMMENT에 저장
           
            Ilchon relation2 = new Ilchon();
            relation2.setFromMemberNo(pageOwnerNo);
            relation2.setToMemberNo(authorNo);
            relation2.setToComment(content);
            
            result = miniHomeService.updateIlchonToComment(relation2);
           
        }
        
        return result;
    }

    /**
     * 일촌평 삭제 (양방향 시도) - 내부 메소드
     */
    private int deleteIlchonCommentInternal(int authorNo, int pageOwnerNo) {
      
        
        // 1차: 작성자→페이지주인 방향의 FROM_COMMENT 삭제
       
        Ilchon relation1 = new Ilchon();
        relation1.setFromMemberNo(authorNo);
        relation1.setToMemberNo(pageOwnerNo);
        
        int result = miniHomeService.deleteIlchonFromComment(relation1);
      
        
        if (result == 0) {
            // 2차: 페이지주인→작성자 방향의 TO_COMMENT 삭제
           
            Ilchon relation2 = new Ilchon();
            relation2.setFromMemberNo(pageOwnerNo);
            relation2.setToMemberNo(authorNo);
            
            result = miniHomeService.deleteIlchonToComment(relation2);
           
        }
        
        return result;
    }
   
    /**
     * 일촌평 작성 권한 확인
     */
    private boolean checkWritePermission(Member loginMember, int pageOwnerNo) {
        // 본인 페이지는 항상 가능
        if (loginMember.getMemberNo() == pageOwnerNo) {
            return true;
        }
        
        // 일촌 관계 확인
        Ilchon relationCheck = new Ilchon();
        relationCheck.setFromMemberNo(loginMember.getMemberNo());
        relationCheck.setToMemberNo(pageOwnerNo);
        
        int acceptedCount = miniHomeService.findIlchon(relationCheck);
        return acceptedCount > 0;
    }
    
    /** 왼쪽 프로필업데이트 하는 서비스
     * @param memberNo
     * @param memberIntro
     * @param memberImg
     * @param loginMember
     * @return
     * @throws Exception 
     */
    @PostMapping("{memberNo:[0-9]+}/leftProfileUpdate")
    @ResponseBody
    public Map<String,Object> updateLeftProfile(@PathVariable("memberNo") int memberNo,
                              @RequestParam(value = "memberIntro", required = false) String memberIntro,
                              @RequestParam(value = "memberImg", required = false) MultipartFile memberImg,
                              @RequestParam(value = "deleteImage", required = false) String deleteImage,
                              @SessionAttribute("loginMember") Member loginMember,
                              HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (memberNo != loginMember.getMemberNo()) {
                response.put("success", false);
                response.put("message", "현재 주인이 아닙니다.");
                return response;
            }
            
            boolean imageUpdated = false;
            boolean introUpdated = false;
            
            // 이미지 삭제 처리
            if ("true".equals(deleteImage)) {
                int deleteResult = miniHomeService.deleteMemberImage(loginMember);
                if (deleteResult > 0) {
                    loginMember.setMemberImg(null); // 세션 객체도 업데이트
                    imageUpdated = true;
                }
            }
            // 이미지 업로드 처리
            else if(memberImg != null && !memberImg.isEmpty()) {
                int imageResult = miniHomeService.leftprofileUpdate(loginMember, memberImg);
                if(imageResult > 0) {
                    imageUpdated = true;
                    // 새로운 이미지 경로를 세션에 반영 (서비스에서 설정된 값)
                }
            }
            
            // 자기소개 업데이트 처리
            if(memberIntro != null) {
                int introResult = miniHomeService.leftprofileintroUpdate(loginMember, memberIntro);
                
                if (introResult > 0) {
                    loginMember.setMemberIntro(memberIntro); // 세션 객체 업데이트
                    introUpdated = true;
                }
            }
            
            if (imageUpdated || introUpdated) {
                session.setAttribute("loginMember", loginMember); // 업데이트된 객체를 세션에 저장
                response.put("success", true);
                
                if (imageUpdated && introUpdated) {
                    response.put("message", "프로필 이미지와 자기소개가 업데이트되었습니다.");
                } else if (imageUpdated) {
                    response.put("message", "프로필 이미지가 업데이트되었습니다.");
                } else if (introUpdated) {
                    response.put("message", "자기소개가 업데이트되었습니다.");
                }
            } else {
                response.put("success", false);
                response.put("message", "업데이트할 내용이 없거나 업데이트에 실패했습니다.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return response;
    }
    	
    
    
    /** 임의의 회원의 미니홈으로 랜덤해서 방문하기
     * @return
     */
    @GetMapping("{memberNo:[0-9]+}/surfing")
    @ResponseBody
    public String getRandomMember(@SessionAttribute(value = "loginMember", required = false) Member loginMember,
                                @PathVariable("memberNo") int memberNo) {

        Integer loginNo = 0;  // 기본값 설정

        if (loginMember != null) {
            loginNo = loginMember.getMemberNo();  // 변수명 통일: loginNo (대문자 O가 아닌 소문자 o)
        }

        Integer randomMemberNo;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            randomMemberNo = miniHomeService.getRandomActiveMember();
            attempts++;
        } while ((randomMemberNo != null && 
                  (randomMemberNo.equals(loginNo) || randomMemberNo.equals(memberNo))) 
                 && attempts < maxAttempts);

        return randomMemberNo != null ? randomMemberNo.toString() : "0";
    }
    
}
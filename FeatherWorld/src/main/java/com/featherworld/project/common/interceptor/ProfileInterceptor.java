package com.featherworld.project.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.member.model.dto.Today;
import com.featherworld.project.miniHome.model.service.MiniHomeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfileInterceptor implements HandlerInterceptor {

    @Autowired
    private MiniHomeService miniHomeService;

    /**
     * leftfragment를 사용하지 않는 페이지들 확인
     */
    private boolean shouldExcludeFromProfile(String requestURI) {
        if (requestURI.matches(".*/(\\d+)/board.*")) {
            return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, 
                          HttpServletResponse response, 
                          Object handler, 
                          ModelAndView modelAndView) throws Exception {
        
    	// JSON 응답 (AJAX 요청) 또는 modelAndView 없는 경우 바로 리턴
        if (modelAndView == null || modelAndView.getViewName() == null) {
            return;
        }
        // 비동기 처리 (POST/PUT/DELETE 등)은 인터셉터 로직 제외
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return;
        }
        
        String requestURI = request.getRequestURI();
      
        
        if (!requestURI.matches(".*/(\\d+)/.*")) {
            
            return;
        }
        
        if (shouldExcludeFromProfile(requestURI)) {
            
            return;
        }
        
        try {
            int memberNo = extractMemberNoFromURI(requestURI);
            if (memberNo <= 0) {
               
                return;
            }
            
            HttpSession session = request.getSession();
            Member loginMember = (Member) session.getAttribute("loginMember");
            
      
   
            setCommonProfileData(memberNo, loginMember, modelAndView);
            
            if (loginMember != null) {
                processVisitor(memberNo, loginMember);
            }
            
            
            
        } catch (Exception e) {
           
            e.printStackTrace();
        }
    }	

    private void setCommonProfileData(int memberNo, Member loginMember, ModelAndView modelAndView) {
        
        Member member = miniHomeService.findmember(memberNo);
       
        
        Today todayQuery = new Today();
        todayQuery.setHomeNo(memberNo);
        int todayCount = miniHomeService.todayCount(todayQuery);
        int totalCount = miniHomeService.totalCount(memberNo);
        int followerCount = miniHomeService.getFollowerCount(memberNo);
        int followingCount = miniHomeService.getFollowingCount(memberNo);
        
        boolean hasPendingFollowers = false;
        if (loginMember != null && loginMember.getMemberNo() == memberNo) {
            int pendingFollowerCount = miniHomeService.getPendingFollowerCount(memberNo);
            hasPendingFollowers = (pendingFollowerCount > 0);
        }
        
        boolean isIlchon = false;
        boolean isPendingRequest = false;
        
       
        if (loginMember != null && loginMember.getMemberNo() != memberNo) {
            
           
            
            Ilchon myRequest = new Ilchon();
            myRequest.setFromMemberNo(loginMember.getMemberNo());
            myRequest.setToMemberNo(memberNo);
            
            int myAcceptedCount = miniHomeService.findIlchon(myRequest);
            int myPendingCount = miniHomeService.findPendingIlchon(myRequest);
            
           
            
            Ilchon theirRequest = new Ilchon();
            theirRequest.setFromMemberNo(memberNo);
            theirRequest.setToMemberNo(loginMember.getMemberNo());
            
            int theirAcceptedCount = miniHomeService.findIlchon(theirRequest);
          
            
            isIlchon = (myAcceptedCount > 0 || theirAcceptedCount > 0);
            isPendingRequest = (myPendingCount > 0);
            
            
        } else if (loginMember != null && loginMember.getMemberNo() == memberNo) {
           
        }
        
        modelAndView.addObject("totalCount", totalCount);
        modelAndView.addObject("todayCount", todayCount);
        modelAndView.addObject("member", member);
        modelAndView.addObject("followerCount", followerCount);
        modelAndView.addObject("followingCount", followingCount);
        modelAndView.addObject("hasPendingFollowers", hasPendingFollowers);
        modelAndView.addObject("memberNo", memberNo);
        modelAndView.addObject("isIlchon", isIlchon);
        modelAndView.addObject("isPendingRequest", isPendingRequest);
       
       
        if (loginMember != null) {
          
        }
        
      
    }

    private void processVisitor(int memberNo, Member loginMember) {
        if (loginMember != null && loginMember.getMemberNo() != memberNo) {
            Today today = new Today(); 
            today.setHomeNo(memberNo);
            today.setVisitNo(loginMember.getMemberNo());
            
            int todayConfirm = miniHomeService.todayConfirm(today);
            
            if (todayConfirm == 0) {
                miniHomeService.todayAdd(today);
               
            }
        }
    }

    private int extractMemberNoFromURI(String requestURI) {
        String[] pathSegments = requestURI.split("/");
        for (String segment : pathSegments) {
            if (segment.matches("\\d+")) {
              
                return Integer.parseInt(segment);
            }
        }
        return -1;
    }
}
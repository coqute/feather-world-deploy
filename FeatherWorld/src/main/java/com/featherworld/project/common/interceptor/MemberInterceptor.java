package com.featherworld.project.common.interceptor;

import com.featherworld.project.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 회원인지 여부를 확인하여 접근 제한
 * @author Jiho 
 */
@Slf4j
public class MemberInterceptor implements HandlerInterceptor {

    @Autowired
    private MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        log.debug("uri = {}", uri);

        // 정규식 활용 -> memberNo 가져오기
        Pattern pattern = Pattern.compile("^/(\\d+)(/.*)?$");
        Matcher matcher = pattern.matcher(uri);
      
        // memberNo를 가져오면
        if(matcher.matches()) {
            String memberNo = matcher.group(1);
            log.debug("memberNo: {}", memberNo);

            // 회원 번호 존재 유무 검사
            int result = memberService.checkMember(Integer.parseInt(memberNo));

            if(result == 0) { // DB에 존재하지 않는 회원 번호인 경우 main page로 redirect

				// Controller 단의 ra와 동일한 역할
				FlashMap flashMap = new FlashMap();
				flashMap.put("message", "탈퇴했거나, 유효하지 않은 회원입니다.");

				// FlashMapManager를 통해 FlashMap 저장
				FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
				if (flashMapManager != null) {
					flashMapManager.saveOutputFlashMap(flashMap, request, response);
				}

                response.sendRedirect("/");
                return false;
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

package com.featherworld.project.common.interceptor;

import com.featherworld.project.board.model.dto.BoardType;
import com.featherworld.project.board.model.service.BoardTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class BoardTypeInterceptor implements HandlerInterceptor {

    @Autowired
    private BoardTypeService boardTypeService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        log.debug("uri = {}", uri);

        // 정규식 활용 -> memberNo 가져오기
        Pattern pattern = Pattern.compile("^/(\\d+)(/.*)?$");
        Matcher matcher = pattern.matcher(uri);

        // 회원 번호 유효성은 이미 다른 MemberInterceptor 에서 처리해줌.
        // 요청 주소에 회원 번호가 들어오면 Session에 해당 회원 번호에 대한 게시판 목록을 저장
        HttpSession session = request.getSession();
        if(matcher.matches()) {

            String memberNo = matcher.group(1);
            log.debug("memberNo: {}", memberNo);

            // 현재 회원의 게시판 목록을 조회해서 가져옴
            List<BoardType> boardTypeList = boardTypeService.selectBoardType(Integer.parseInt(memberNo));

            // session scope에 boardTypeList 갱신
            request.getSession().setAttribute("boardTypeList", boardTypeList);
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

package com.featherworld.project.board.controller;

import com.featherworld.project.board.model.dto.BoardType;
import com.featherworld.project.board.model.service.BoardService;
import com.featherworld.project.board.model.service.BoardTypeService;
import com.featherworld.project.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@Slf4j
public class BoardTypeController {

    @Autowired
    private BoardTypeService boardTypeService;

    /** 현재 회원의 게시판 목록 조회
     * @author Jiho
     * @param memberNo 현재 조회 중인 회원 번호
     * @return 게시판 목록
     */
    @GetMapping("{memberNo:[0-9]+}/board/select")
    public List<BoardType> selectBoardType(@PathVariable("memberNo") int memberNo, HttpSession session) {

        // 현재 회원의 게시판 목록을 조회해서 가져옴
        // 이미 Session에 게시판 목록이 존재함!
        // 하지만 혹시나 현재 회원의 게시판 목록과 일치하지 않을 경우가 있을 것 같아서
        // 한 번 더 DB에 다녀오는 것으로 남겨둠.
        List<BoardType> boardTypeList = boardTypeService.selectBoardType(memberNo);

        // session scope에 boardTypeList 갱신
        // -> 기존에 갱신했으나, BoardTypeInterceptor 에서 대신 처리!

        return boardTypeList;
    }

    /** 현재 회원의 새로운 게시판 생성
     * @param memberNo 현재 회원번호
     * @param boardType 전달받은 게시판 제목, 권한(0, 1)
     * @param loginMember 로그인한 회원
     * @param ra message 전달
     * @return result 새로운 게시판 생성 성공 1, 실패 0
     */
    @PostMapping("{memberNo:[0-9]+}/board/insert")
    public int createBoardType(@PathVariable("memberNo") int memberNo,
                               @RequestBody BoardType boardType,
                               @SessionAttribute(value="loginMember", required = false) Member loginMember,
                               RedirectAttributes ra) {

        if(loginMember == null) {
            ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
            return 0;

        } else if(loginMember.getMemberNo() != memberNo) {
            ra.addFlashAttribute("message", "본인의 게시판만 수정할 수 있습니다!");
            return 0;
        }

        // 현재 회원번호 정보를 세팅
        boardType.setMemberNo(memberNo);

        return boardTypeService.insertBoardType(boardType);
    }

    /** 현재 회원의 기존 게시판 수정(이름, 권한)
     * @param memberNo 현재 회원번호
     * @param boardType 전달받은 게시판 제목, 권한(0, 1)
     * @param loginMember 로그인한 회원
     * @param ra message 전달
     * @return 기존 게시판 수정 성공 1, 실패 0
     */
    @PutMapping("{memberNo:[0-9]+}/board/update")
    public int updateBoardType(@PathVariable("memberNo") int memberNo,
                               @RequestBody BoardType boardType,
                               @SessionAttribute(value="loginMember", required = false) Member loginMember,
                               RedirectAttributes ra) {

        if(loginMember == null) {
            ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
            return 0;

        } else if(loginMember.getMemberNo() != memberNo) {
            ra.addFlashAttribute("message", "본인의 게시판만 수정할 수 있습니다!");
            return 0;
        }

        // 현재 회원번호 정보를 세팅
        boardType.setMemberNo(memberNo);

        return boardTypeService.updateBoardType(boardType);
    }

    /** 현재 회원의 기존 게시판 삭제
     * @param memberNo 현재 회원번호
     * @param boardType 전달받은 게시판 종류 번호 포함
     * @param loginMember 로그인한 회원
     * @param ra message 전달
     * @return 기존 게시판 삭제 성공 1, 실패 0
     */
    @DeleteMapping("{memberNo:[0-9]+}/board/delete")
    public int deleteBoardType(@PathVariable("memberNo") int memberNo,
                               @RequestBody BoardType boardType,
                               @SessionAttribute(value="loginMember", required = false) Member loginMember,
                               RedirectAttributes ra) {

        if(loginMember == null) {
            ra.addFlashAttribute("message", "로그인 후 이용해주세요.");
            return 0;

        } else if(loginMember.getMemberNo() != memberNo) {
            ra.addFlashAttribute("message", "본인의 게시판만 수정할 수 있습니다!");
            return 0;
        }

        // 현재 회원번호 정보를 세팅
        boardType.setMemberNo(memberNo);

        return boardTypeService.deleteBoardType(boardType);
    }
}

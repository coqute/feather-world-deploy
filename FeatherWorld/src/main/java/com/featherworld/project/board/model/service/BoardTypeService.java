package com.featherworld.project.board.model.service;

import com.featherworld.project.board.model.dto.BoardType;

import java.util.List;

public interface BoardTypeService {

    /** 현재 회원의 게시판 목록 조회
     * @author Jiho
     * @param memberNo 현재 조회 중인 회원 번호
     */
    List<BoardType> selectBoardType(int memberNo);

    /** 현재 회원의 새로운 게시판 생성
     * @author Jiho
     * @param boardType 전달받은 현재 회원 번호, 게시판 제목, 권한(0, 1)
     * @return result : 1(성공), 0(실패)
     */
    int insertBoardType(BoardType boardType);

    /** 현재 회원의 기존 게시판 수정
     * @param boardType 전달받은 현재 회원 번호, 수정된 게시판 이름 & 권한(0, 1)
     * @return result : 1(성공), 0(실패)
     */
    int updateBoardType(BoardType boardType);

    /** 현재 회원의 기존 게시판 삭제
     * @param boardType 전달받은 현재 회원 번호, 게시판 종류 번호
     * @return result: 1(성공), 0(실패)
     */
    int deleteBoardType(BoardType boardType);
}

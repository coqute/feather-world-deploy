package com.featherworld.project.board.model.service;

import com.featherworld.project.board.model.dto.BoardType;
import com.featherworld.project.board.model.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BoardTypeServiceImpl implements BoardTypeService {

    @Autowired
    BoardMapper mapper;

    // 현재 회원의 게시판 종류 번호(boardCode) 조회
    @Override
    public List<BoardType> selectBoardType(int memberNo) {
        return mapper.selectBoardType(memberNo);
    }

    @Override
    public int insertBoardType(BoardType boardType) {
        return mapper.insertBoardType(boardType);
    }

    @Override
    public int updateBoardType(BoardType boardType) {

        // 유효하지 않은 게시판(회원이 게시판을 가지고 있지 않은 경우)
        if(!isBoardValid(boardType)) return 0;

        return mapper.updateBoardType(boardType);
    }

    @Override
    public int deleteBoardType(BoardType boardType) {

        // 유효하지 않은 게시판(회원이 게시판을 가지고 있지 않은 경우)
        if(!isBoardValid(boardType)) return 0;

        return mapper.deleteBoardType(boardType);
    }

    private boolean isBoardValid(BoardType boardType) {

        // 현재 회원의 게시판 목록을 가져옴
        List<BoardType> boardTypes = mapper.selectBoardType(boardType.getMemberNo());

        // 현재 회원이 해당 게시판을 가지고 있는지 확인
        for(BoardType memberBoard : boardTypes) {

            // 가지고 있다면 true
            if(memberBoard.getBoardCode() == boardType.getBoardCode()) {
                return true;
            }
        }
        // 가지고 있지 않다면 false 반환
        log.debug("회원 {}의 존재하지 않는 게시판 {}", boardType.getMemberNo(), boardType.getBoardCode());
        return false;
    }
}

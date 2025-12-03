package com.featherworld.project.board.model.service;

import java.util.List;
import java.util.Map;

import com.featherworld.project.board.model.dto.Board;
import com.featherworld.project.board.model.dto.BoardType;
import org.springframework.web.multipart.MultipartFile;

public interface BoardService {

	/** 현재 선택된 게시판의 삭제되지 않은 게시글 목록 조회 / pagination 객체 반환
	 * @author Jiho
	 * @param currentBoardCode 현재 게시판 종류 번호
	 * @param cp 현재 페이지 번호
	 * @return {"boardList" : List<Board>, "pagination" : Pagination}
	 */
	Map<String, Object> selectBoardList(int currentBoardCode, int cp);

	/** 좋아요 체크 여부 서비스
	 * @author 허배령
	 * @param map
	 * @return
	 */
	int boardLike(Map<String, Integer> map);

	/** 게시글 상세 조회
	 * @author 허배령
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);

	/** 조회수 1 증가 서비스
	 * @author 허배령
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);


	/** 게시글 삽입
	 * @author Jiho
	 * @param board 게시글 제목/내용, 회원 번호, 게시판 종류 번호
	 * @param imageList 이미지 값
	 * @return boardNo 삽입된 게시글 번호
	 */
	int boardInsert(Board board, List<MultipartFile> imageList) throws Exception;

	/** 게시글 삭제
	 * @param board 회원 번호, 게시판 종류 번호, 게시글 번호
	 * @return result 1(성공), 0(실패)
	 */
	int boardDelete(Board board);

	/** 게시글 수정
	 * @param board 게시글 제목/내용, 회원 번호, 게시판 종류 번호, 게시글 번호
	 * @param deletedImageList 기존에 있던 BOARD_IMG 내의 삭제될 imgNo들
	 * @param imageList 새롭게 추가될 BoardImg 관련 내용들
	 * @return result 1(성공) 0(실패)
	 */
	int boardUpdate(Board board, String deletedImageList, List<MultipartFile> imageList) throws Exception;
}

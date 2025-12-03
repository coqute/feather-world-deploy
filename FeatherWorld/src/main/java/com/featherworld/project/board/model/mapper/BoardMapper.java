package com.featherworld.project.board.model.mapper;

import java.util.List;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.featherworld.project.board.model.dto.Board;
import com.featherworld.project.board.model.dto.BoardImg;
import com.featherworld.project.board.model.dto.BoardType;

import java.util.Map;


import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardMapper {

	// BoardService
	List<Board> selectBoardList(int currentBoardCode, RowBounds rowBounds);

	int getListCount(int currentBoardCode);

	int boardInsert(Board board);

	int insertUploadList(List<BoardImg> uploadList);

	int boardDelete(Board board);

	int boardUpdate(Board board);

	int deleteImage(Map<String, Object> map);

	int updateImage(BoardImg img);

	int insertImage(BoardImg img);

	// BoardTypeService
	List<BoardType> selectBoardType(int memberNo);

	int insertBoardType(BoardType boardType);

	int deleteBoardType(BoardType boardType);

	int updateBoardType(BoardType boardType);
	
	/** 게시글 좋아요 해제
	 * @author 허배령
	 * @param map
	 * @return
	 */
	int deleteBoardLike(Map<String, Integer> map);
	
	/** 게시글 좋아요 체크
	 * @author 허배령
	 * @param map
	 * @return
	 */
	int insertBoardLike(Map<String, Integer> map);

	/** 게시글 좋아요 개수 조회
	 * @author 허배령
	 * @param integer
	 * @return
	 */
	int selectLikeCount(Integer integer);

	/** 게시글 상세 조회
	 * @author 허배령
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);

	/** 조회수 1 증가
	 * @author 허배령
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);

	/** 조회수 조회
	 * @author 허배령
	 * @param boardNo
	 * @return
	 */
	int selectReadCount(int boardNo);

	List<BoardImg> selectImageList(Integer integer);



}

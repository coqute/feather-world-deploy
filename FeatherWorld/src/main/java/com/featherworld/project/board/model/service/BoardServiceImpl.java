package com.featherworld.project.board.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.featherworld.project.common.utill.Utility;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.featherworld.project.board.model.dto.Board;
import com.featherworld.project.board.model.dto.BoardImg;
import com.featherworld.project.board.model.dto.BoardType;
import com.featherworld.project.board.model.dto.Comment;
import com.featherworld.project.board.model.mapper.BoardMapper;
import com.featherworld.project.common.dto.Pagination;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
@Slf4j
public class BoardServiceImpl implements BoardService {

	@Autowired
	BoardMapper mapper;

	@Value("${my.board.web-path}")
	private String webPath;

	@Value("${my.board.folder-path}")
	private String folderPath;

	// 현재 선택된 게시판의 삭제되지 않은 게시글 목록 조회/해당 pagination 객체 반환
	@Override
	public Map<String, Object> selectBoardList(int currentBoardCode, int cp) {
		
		// 0. 반환할 Map 인스턴스(객체) 생성
		Map<String, Object> map = new HashMap<>();
		
		// 1. 게시판 종류 번호로 해당 게시판의 게시글 목록 가져오기
		
		// 1-1. 해당 게시판의 삭제되지 않은 총 게시글 개수(listCount) 조회
		int listCount = mapper.getListCount(currentBoardCode);
		
		// 게시글이 존재하지 않는다면, 빈 map return
		if(listCount == 0) return map;
		
		// 1-2. 현재 페이지(cp), 총 게시글 개수(listCount)를 기준으로
		// 		pagination 객체 생성
		Pagination pagination = new Pagination(cp, listCount);
		
		// 1-3. 생성된 pagination 객체의 필드값(limit)을 기준으로
		// 		해당 페이지에 포함되는 게시글 목록만 가져옴
		//		RowBounds 객체(MyBatis 제공) 활용
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		// RowBounds와 현재 게시판 종류 번호(currentBoardCode)를 매개변수로 지정
		// rowBounds 순서는 반드시 두 번째!
		List<Board> boardList = mapper.selectBoardList(currentBoardCode, rowBounds);
		
		// 생성한 pagination, boardList 넣어주기
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		// pagination, boardList 들어있는 map 반환
		return map;
	}

	// 게시글 삽입 - 삽입된 게시글 번호 반환 메서드
	@Override
	public int boardInsert(Board board, List<MultipartFile> imageList) throws Exception {

		// 게시글 insert
		// 삽입된 게시글 번호 반환
		int result = mapper.boardInsert(board);

		// 게시글 삽입 실패
		if(result == 0) return 0;

		// board-mapper.xml - <selectKey>로 만들어진 boardNo
		int boardNo = board.getBoardNo();

		// 업로드 이미지 정보 List
		List<BoardImg> uploadList = new ArrayList<>();

		for(int i = 0; i < imageList.size(); i++) {
			// 이미지가 입력되어 있는 경우만
			if(!imageList.get(i).isEmpty()) {

				// 원본명/변경명
				String originalName = imageList.get(i).getOriginalFilename();
				String rename = Utility.fileRename(originalName);
				
				BoardImg img = BoardImg.builder()
						.imgOriginalName(originalName)
						.imgRename(rename)
						.imgPath(webPath)
						.boardNo(boardNo)
						.imgOrder(i)
						.uploadFile(imageList.get(i))
						.build();

				// 실제 업로드할 리스트에 저장
				uploadList.add(img);
			}
		}
		
		// 업로드 할 이미지가 하나도 없는 경우
		if(uploadList.isEmpty()) return boardNo;

		result = mapper.insertUploadList(uploadList);

		// 다중 INSERT 성공 확인
		if(result == uploadList.size()) {

			// 서버에 파일 저장
			for(BoardImg img : uploadList) {
				img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
			}

		} else {
			throw new RuntimeException();
		}

		return boardNo;
	}

	// 게시글 삭제 - 삭제 결과 반환
	@Override
	public int boardDelete(Board board) {
		return mapper.boardDelete(board);
	}

	// 게시글 수정 - 수정 결과 반환
	@Override
	public int boardUpdate(Board board, String deletedImageList, List<MultipartFile> imageList) throws Exception {

		// 게시글 제목/내용 수정 결과 반환
		int result = mapper.boardUpdate(board);

		if(result == 0) return 0;

		// 삭제된 이미지에 대한 처리
		// 해당 게시글(boardNo)에 이미지(imgNo) 포함되어 있음
		// 그럼 삭제되는 배열이랑 비교해서 업로드할 애만 업로드하고 나머지는 수정/삭제 하면 됨.
		if(deletedImageList != null && !deletedImageList.equals("")) {

			Map<String, Object> map = new HashMap<>();
			map.put("deletedImageList", deletedImageList);
			map.put("boardNo", board.getBoardNo());

			result = mapper.deleteImage(map);

			// 삭제 실패한 경우 -> 롤백
			if(result == 0) {
				throw new RuntimeException();
			}
		}

		List<BoardImg> uploadList = new ArrayList<>();

		// images List에서 하나씩 꺼내어 파일이 있는지 검사
		for(int i = 0; i < imageList.size(); i++) {

			// 실제 선택된 파일이 존재하는 경우
			if(!imageList.get(i).isEmpty()) {

				// 원본명
				String originalName = imageList.get(i).getOriginalFilename();

				// 변경명
				String rename = Utility.fileRename(originalName);

				// 모든 값을 저장할 DTO 생성 (BoardImg)
				BoardImg img = BoardImg.builder()
						.imgOriginalName(originalName)
						.imgRename(rename)
						.imgPath(webPath)
						.boardNo(board.getBoardNo())
						.imgOrder(i)
						.uploadFile(imageList.get(i))
						.build();

				// 해당 BoardImg를 uploadList 추가
				uploadList.add(img);

				// 업로드 하려는 이미지 정보를 이용해서
				// 수정 or 삽입 수행

				// 1) 기존 O -> 새 이미지로 변경 -> 수정
				result = mapper.updateImage(img);

				if(result == 0) {
					// 수정 실패 == 기존 해당 순서(IMG_ORDER)에 이미지가 없었다!
					// -> 삽입 수행

					// 2) 기존 X -> 새 이미지 추가
					result = mapper.insertImage(img);
				}
			}

			// 수정 또는 삽입이 실패한 경우
			if(result == 0) {
				throw new RuntimeException(); // 예외 발생 -> 롤백
			}
		}

		// 선택한 파일이 하나도 없을 경우
		if(uploadList.isEmpty()) return result;

		// 수정, 새로 삽입한 이미지 파일을 서버에 실제로 저장!
		for(BoardImg img : uploadList) {
			img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
		}

		return result;
	}

	/** 게시글 좋아요 체크/해제
	 * @author 허배령
	 */
	@Override
	public int boardLike(Map<String, Integer> map) {
		
		int result = 0;
		
		// 1. 좋아요가 체크된 상태인 경우 (likeCheck == 1)
		// -> BOARD_LIKE 테이블에 DELETE 수행
		if(map.get("likeCheck") == 1) {
			
			result = mapper.deleteBoardLike(map);
			
			
		} else {
		// 2. 좋아요가 해제된 해제 경우 (likeCheck == 0)
		// -> BOARD_LIKE 테이블에 INSERT 수행
			result = mapper.insertBoardLike(map);
	    }
		
		// 3. 다시 해당 게시글의 좋아요 개수를 조회해서 반환
		if(result > 0) {
			return mapper.selectLikeCount(map.get("boardNo"));
		}
		
		return -1; // 좋아요 처리 실패
  }

	/** 게시글 상세 조회 서비스
	 * @author 허배령
	 */
	@Override
	public Board selectOne(Map<String, Integer> map) {
		
		Board board = mapper.selectOne(map);
		
		if(board != null) {
	        
	        // 2) 해당 게시글의 이미지 목록 조회
	        List<BoardImg> imageList = mapper.selectImageList(map.get("boardNo"));
	        board.setImageList(imageList);
	        
	    }
	    
	    return board;
	}
	

	/** 조회수 1 증가 서비스
	 * @author 허배령
	 */
	@Override
	public int updateReadCount(int boardNo) {
		
		// 1. 조회 수 1 증가 (UPDATE)
		int result = mapper.updateReadCount(boardNo);
		
		// 2. 현재 조회 수 조회
		if(result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		
		// 실패한 경우 -1 반환
		return -1;
		
		
	}
}

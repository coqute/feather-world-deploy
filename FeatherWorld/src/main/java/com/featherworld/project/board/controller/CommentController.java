package com.featherworld.project.board.controller;

import com.featherworld.project.board.model.dto.Comment;
import com.featherworld.project.board.model.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comments")
public class CommentController {
	
	@Autowired
	private CommentService service;
	/** 댓글 목록 조회
	 * @author 허배령
	 * @param boardNo
	 */
	@GetMapping("")
	public List<Comment> select(@RequestParam("boardNo") int boardNo) {
		
		return service.select(boardNo);
	}
	
	/** 댓글/답글 등록
	 * @author 허배령
	 */
	@PostMapping("")
	public int insert(@RequestBody Comment comment) {
		return service.insert(comment);
		
	}
	
	/** 댓글 삭제
	 * @author 허배령
	 * @param commentNo
	 */
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		return service.delete(commentNo);
	}
	
	/** 댓글 수정
	 * @author 허배령
	 */
	@PutMapping("")
	public int update(@RequestBody Comment comment) {
		return service.update(comment);
	}
	
	
	

}

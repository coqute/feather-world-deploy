package com.featherworld.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
	
	private int boardCommentNo;
	private String boardCommentContent;
	private String boardCommentWriteDate;
	private String boardCommentDelFl;
	private int boardNo;
	private int memberNo;
	private int parentCommentNo;
	
	// 댓글 조회 시 회원 프로필, 이름
	private String memberName;
	private String memberImg;
	

}

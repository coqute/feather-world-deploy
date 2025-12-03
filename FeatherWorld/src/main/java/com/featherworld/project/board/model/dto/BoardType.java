package com.featherworld.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardType {
	
	private int boardCode; // 게시판 종류 번호
	private String boardName; // 게시판 종류 이름
	private int authority; // 접근 권한(0: 전체, 1: 일촌)
	private int memberNo; // 회원 번호
}

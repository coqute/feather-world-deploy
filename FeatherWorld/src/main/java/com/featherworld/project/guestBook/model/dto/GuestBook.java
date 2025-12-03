package com.featherworld.project.guestBook.model.dto;

import com.featherworld.project.member.model.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestBook {

	//GuestBook 테이블 컬럼
	private int guestBookNo;
	private String guestBookContent;
	private String guestBookWriteDate;
	private int ownerNo;
	private int visitorNo;
	private Member visitor;
	private int secret;// 0 = 공개글, 1 = 비밀글
	
	private String memberName;
	private String memberImg;
	
}

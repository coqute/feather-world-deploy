package com.featherworld.project.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Today{
	
	// 투데이 DTO
	
	private int homeNo;
	private int visitNo;
	private String visitDate;
}

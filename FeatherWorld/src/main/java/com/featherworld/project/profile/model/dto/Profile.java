package com.featherworld.project.profile.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data // Getter+Setter +ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
	private int memberNo;
	private String imgPath;
	private String imgOriginalName;
	private String imgRename;
	private String profileContent;

}

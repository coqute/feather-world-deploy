package com.featherworld.project.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
  
   private int memberNo;
   private String memberEmail;
   private String memberPw;
   private String memberName;
   private String memberTel;
   private String memberAddress;
   private String memberImg;
   private String memberIntro;
   private String homeTitle;
   private String enrollDate;
   private String memberDelFl;
   private String kakaoAccessToken;

}
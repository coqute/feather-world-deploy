package com.featherworld.project.miniHome.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniHomeRecentBoard {
    private int boardNo;
    private String boardTitle;      // title → boardTitle로 변경
    private String boardWriteDate; // 날짜 필드 추가
    private String thumbnailImg;    // 기존 필드 유지
}

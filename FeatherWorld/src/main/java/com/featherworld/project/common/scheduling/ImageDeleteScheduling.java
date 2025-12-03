package com.featherworld.project.common.scheduling;

import com.featherworld.project.member.model.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@PropertySource("classpath:/config.properties")
public class ImageDeleteScheduling {

    @Autowired
    private MemberService memberService;
    
    // 좌측 프로필 이미지 파일 저장 경로
    @Value("${my.left-profile.folder-path}")
    private String leftProfileFolderPath;
    
    // 프로필 탭 이미지 파일 저장 경로
    @Value("${my.profile.folder-path}")
    private String profileFolderPath;

    // 게시판 이미지 파일 저장 경로
    @Value("${my.board.folder-path}")
    private String boardFolderPath;

    // 매일 오전 3시에 삭제 진행
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteImage() {
        log.info("이미지 삭제 시작");

        // 서버 파일 목록 조회
        File leftProfileFolder = new File(leftProfileFolderPath);
        File profileFolder = new File(profileFolderPath);
        File boardFolder = new File(boardFolderPath);
        
        // 파일 목록 불러오기
        File[] leftProfileArray = leftProfileFolder.listFiles();
        File[] profileArray = profileFolder.listFiles();
        File[] boardArray = boardFolder.listFiles();

        if (leftProfileArray == null) leftProfileArray = new File[0];
        if (profileArray == null) profileArray = new File[0];
        if (boardArray == null) boardArray = new File[0];
        
        // 하나로 합칠 새로운 File 배열 선언
        File[] imageArray = new File[leftProfileArray.length + profileArray.length + boardArray.length];
        
        // 배열 내용 복사
        System.arraycopy(leftProfileArray, 0, imageArray, 0, leftProfileArray.length);
        System.arraycopy(profileArray, 0, imageArray, leftProfileArray.length, profileArray.length);
        System.arraycopy(boardArray, 0, imageArray, leftProfileArray.length + profileArray.length, boardArray.length);

        // 배열 -> List
        List<File> serverImageList = Arrays.asList(imageArray);

        // DB 이미지 파일 이름 조회
        List<String> dbImageList = memberService.selectDbImageList();
        
        // DB에 없고, Server에만 있는 파일 삭제
        if(!serverImageList.isEmpty()) {
            
            StringBuilder sb = new StringBuilder();
            
            for(File serverImage : serverImageList) {

                if(!dbImageList.contains(serverImage.getName()) && serverImage.exists()) {

                    sb.append(serverImage.getName()).append(" ");
                    serverImage.delete(); // 파일 삭제
                }
            }
            
            log.info("{} 파일 삭제", sb.toString());
        }
    }
}

package com.featherworld.project.common.scheduling;

import com.featherworld.project.member.model.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.featherworld.project.member.model.dto.Member;

import java.util.List;

@Slf4j
@Component
public class MemberDeleteScheduling {

    @Autowired
    private MemberService memberService;

    // 15일마다 오전 3시에 삭제 진행
    @Scheduled(cron = "0 0 3 */15 * *")
    public void deleteMember() {
        log.info("탈퇴 회원 삭제 시작");

        List<Member> memberList = memberService.deletedMembers();

        for(Member member : memberList) {
            log.info("탈퇴 회원 정보: " +
                    "회원 번호: {}, " +
                    "이름: {}, " +
                    "이메일: {}, " +
                    "전화번호: {}, " +
                    "회원가입일: {}",
                    member.getMemberNo(), member.getMemberName(),
                    member.getMemberEmail(), member.getMemberTel(),
                    member.getEnrollDate());

            memberService.deleteMember(member.getMemberNo());
        }
    }
}

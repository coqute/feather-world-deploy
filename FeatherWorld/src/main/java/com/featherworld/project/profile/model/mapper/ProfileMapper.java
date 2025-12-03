package com.featherworld.project.profile.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.featherworld.project.profile.model.dto.Profile;

@Mapper
public interface ProfileMapper {
    Profile selectProfile(int memberNo);
    int insertProfile(Profile profile);
    int updateProfile(Profile profile);
    int profileExists(int memberNo); // 사용하지 않으면 삭제해도 무방

    // 🔐 비밀번호 조회
    String selectEncodedPw(int memberNo);

    // ❌ 회원 탈퇴 처리 (soft delete 또는 hard delete 선택)
    int deleteMember(int memberNo);
	String selectPw(int memberNo);
	int secession(int memberNo);
	int secessionKakaoMember(int memberNo);
}

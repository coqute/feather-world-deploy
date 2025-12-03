package com.featherworld.project.miniHome.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.featherworld.project.board.model.dto.Board;
import com.featherworld.project.friend.model.dto.Ilchon;

import com.featherworld.project.member.model.dto.Member;

import com.featherworld.project.member.model.dto.Today;

@Mapper
public interface MiniHomeMapper {
    
    Member findmember(int memberNo);
    int findIlchon(Ilchon friend);  
    List<Ilchon> getIlchonComments(int memberNo);

    // 방문자 관련
    int todayAdd(Today today);
    int todayCount(Today today);
    int todayConfirm(Today today);  
    int totalCount(int memberNo);
    
    // 팔로우 관련
    int getFollowerCount(int memberNo);
    int getFollowingCount(int memberNo);
    int getPendingFollowerCount(int memberNo);
    int sendFollowRequest(Ilchon followRequest);
    int findPendingIlchon(Ilchon theirRequest);
    
    
    // 게시글 관련
    List<Board> getRecentBoards(Map<String, Integer> map);
    int getTotalBoardCount(Map<String, Integer> map);
    int getTotalGuestBookCount(Map<String, Integer> map);
    
    // 일촌평 관련
    int deleteIlchonFromComment(Ilchon ilchonRelation);
    int deleteIlchonToComment(Ilchon ilchonRelation);
    int updateIlchonFromComment(Ilchon ilchonRelation);
    int updateIlchonToComment(Ilchon reverseRelation);
    
    // 프로필 관련
    int leftprofileUpdate(Member member);
    int leftprofileintroUpdate(Member loginMember);
    int deleteMemberImage(Member loginMember);
    
    // 기타
    Integer getRandomActiveMember();
    
    // -------------------정리 .. -------------------- 이러면 깔끔 ?
}

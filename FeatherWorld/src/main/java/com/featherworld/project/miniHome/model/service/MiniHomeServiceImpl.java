package com.featherworld.project.miniHome.model.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.featherworld.project.board.model.dto.Board;
import com.featherworld.project.common.utill.Utility;
import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.member.model.dto.Today;
import com.featherworld.project.miniHome.model.mapper.MiniHomeMapper;
	
@Service
@Transactional(rollbackFor = Exception.class)
public class MiniHomeServiceImpl implements MiniHomeService {
	
	@Autowired
	private MiniHomeMapper mapper;
	    
	@Value("${my.left-profile.web-path}")
	private String leftProfileWebPath;
	
	@Value("${my.left-profile.folder-path}")
	private String leftProfileFolderPath;
	
	
	
	// ================ 일촌평 관련 (ILCHON 테이블 통합) ================
	
	
	@Override
	public List<Ilchon> getIlchonComments(int memberNo) {
	    return mapper.getIlchonComments(memberNo);
	}
	
	@Override
	public int updateIlchonFromComment(Ilchon ilchonRelation) {
	    return mapper.updateIlchonFromComment(ilchonRelation);
	}
	
	@Override
	public int updateIlchonToComment(Ilchon reverseRelation) {
	    return mapper.updateIlchonToComment(reverseRelation);
	}
	
	@Override
	public int deleteIlchonFromComment(Ilchon ilchonRelation) {
	    return mapper.deleteIlchonFromComment(ilchonRelation);
	}
	
	@Override
	public int deleteIlchonToComment(Ilchon ilchonRelation) {
	    return mapper.deleteIlchonToComment(ilchonRelation);
	}
	
	// ================ 회원 및 일촌 관계 ================
	
	@Override
	public Member findmember(int memberNo) {
	    return mapper.findmember(memberNo);
	}
	
	@Override
	public int findIlchon(Ilchon friend) {
	    return mapper.findIlchon(friend);
	}
	
	@Override
	public int findPendingIlchon(Ilchon theirRequest) {
	    return mapper.findPendingIlchon(theirRequest);
	}
	
	@Override
	public int sendFollowRequest(Ilchon followRequest) {
	    return mapper.sendFollowRequest(followRequest);
	}
	
	@Override
	public int getFollowerCount(int memberNo) {
	    return mapper.getFollowerCount(memberNo);
	}
	
	@Override
	public int getFollowingCount(int memberNo) {
	    return mapper.getFollowingCount(memberNo);
	}
	
	@Override
	public int getPendingFollowerCount(int memberNo) {
	    return mapper.getPendingFollowerCount(memberNo);
	}
	
	// ================ 방문자 관련 ================
	
	@Override
	public int todayAdd(Today today) {
	    return mapper.todayAdd(today);
	}
	
	@Override
	public int todayCount(Today today) {
	    return mapper.todayCount(today);
	}
	
	@Override
	public int todayConfirm(Today today) {
	    return mapper.todayConfirm(today);
	}
	
	@Override
	public int totalCount(int memberNo) {
	    return mapper.totalCount(memberNo);
	}
	
	// ================ 게시글 및 방명록 ================
	
	@Override
	public List<Board> getRecentBoards(Map<String, Integer> map) {
	    return mapper.getRecentBoards(map);
	}
	
	@Override
	public int getTotalBoardCount(Map<String, Integer> map) {
	    return mapper.getTotalBoardCount(map);
	}
	
	@Override
	public int getTotalGuestBookCount(Map<String, Integer> map) {
	    return mapper.getTotalGuestBookCount(map);
	}
	
	// 왼쪽프로필 수정하는 서비스
	@Override
	public int leftprofileUpdate(Member loginMember, MultipartFile memberImg)throws Exception {
	
		//프로필 이미지 경로
		String updatePath = null;
		
		//변경명 저장
		String rename = null;
		
		rename = Utility.fileRename(memberImg.getOriginalFilename());
		
		updatePath = leftProfileWebPath + rename;
		
		//수정된 프로필 이미지 경로 + 회원번호를 저장할 DTO 객체
		Member member = Member.builder()
						.memberNo(loginMember.getMemberNo())
						.memberImg(updatePath)
						.build();
		
		int result = mapper.leftprofileUpdate(member);
		
		if (result > 0) {
			// 프로필 이미지를 없애는 update를 한경우는 제외
			// -> 업로드한 이미지가 있을경우
			if(!memberImg.isEmpty()) {
				
				memberImg.transferTo(new File(leftProfileFolderPath+rename));
			}
				
			    loginMember.setMemberImg(updatePath);
			}
		
			return result;
		}
	
	
	@Override
	public int leftprofileintroUpdate(Member loginMember, String memberIntro) {
		
		Member member = Member.builder()
	            .memberNo(loginMember.getMemberNo())
	            .memberIntro(memberIntro)
	            .build();
		
		int result = mapper.leftprofileintroUpdate(member);
		
		if(result >0) {
			loginMember.setMemberIntro(memberIntro);
		}
		
		return result;
	}
	
	
	// 랜덤 으로 미니홈의 감~
	@Override
	public Integer getRandomActiveMember() {
		
	    	return mapper.getRandomActiveMember();
	    }
    @Override
	public int deleteMemberImage(Member loginMember) {
			
			return mapper.deleteMemberImage(loginMember);
		}
	}
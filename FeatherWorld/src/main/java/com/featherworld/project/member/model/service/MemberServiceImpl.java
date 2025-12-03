package com.featherworld.project.member.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.featherworld.project.member.model.dto.Member;
import com.featherworld.project.member.model.mapper.MemberMapper;

import lombok.extern.slf4j.Slf4j;

/** 서비스 인터페이스를 상속받는 서비스클래스
 * @author 영민
 */
@Transactional(rollbackFor = Exception.class) 
@Service 
@Slf4j 
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	private MemberMapper mapper; 
	
	@Autowired
	private BCryptPasswordEncoder bcrypt; 
	//암호화 객체 의존성 주입

	// --------------------------------------------

	// 회원 여부 확인
	@Override
	public int checkMember(int memberNo) {
		return mapper.checkMember(memberNo);
	}

	// 탈퇴한 회원 조회
	@Override
	public List<Member> deletedMembers() {
		return mapper.deletedMembers();
	}

	// 탈퇴 회원 한 명 삭제
	@Override
	public int deleteMember(int memberNo) {
		return mapper.deleteMember(memberNo);
	}
	
	/** 회원가입 메서드
	 *	@author 영민
	 *
	 */
	@Override
	public int signUp(Member inputMember, String[] memberAddress) {
	    
	    // 주소 처리 로직 
	    if(!inputMember.getMemberAddress().equals(",,")) { 
	        String address = String.join("^^^", memberAddress);
	        inputMember.setMemberAddress(address);
	    } else {
	        inputMember.setMemberAddress(null);
	    }
	    
	    // 비밀번호 암호화
	    String encPw = bcrypt.encode(inputMember.getMemberPw());
	    inputMember.setMemberPw(encPw);
	    
	    // 회원 가입 처리
	    int result = mapper.signUp(inputMember);
	    
	    // 확인용 ..
	    log.info("생성된 회원 번호: {}", inputMember.getMemberNo());
	    
	    // 회원 가입이 성공한 경우에만 기본 게시판 타입 생성
	    if(result > 0) {
	     
	    	int memberNo	= inputMember.getMemberNo();
	    	
	      result = mapper.setDefaultBoardType(memberNo);
	        
	      log.info("기본 게시판 타입 설정 결과: {}", result);
	    }
	    
	    return result;
	}
	
	/** 회원가입이 되었을때 default로 boardType을 하나 생성해주는 메서드
	 *  (회원가입이 성공하면 memberNo가 생겨서 그memberNo로 만들어줌)
	 *	@author 영민
	 */
	@Override
	
	public int setDefaultBoardType(int memberNo) {
		
		return mapper.setDefaultBoardType(memberNo);
	}

	/** 로그인 메서드
	 * @author 영민
	 */
	@Override
	public Member login(Member inputMember) {
		
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		if (loginMember == null) return null; // 조회해도 맞는 이메일이없을때
		
		
		if(!bcrypt.matches(inputMember.getMemberPw(),loginMember.getMemberPw()))return null;
		// 로그인회원의 비밀번호와 입력받은 비밀번호가 같지않다면
		
		return loginMember;
	}

	/** 회원입도중 이메일 중복확인
	 *@author 영민
	 */
	@Override
	public int checkEmail(String memberEmail) {
		
		return mapper.checkEmail(memberEmail);
	}
	
	/** 가입된 회원의 이메일 찾기
	 * @author 영민
	 *
	 */
	@Override
		public Member findId(Member inputMember) {
			
			return mapper.findId(inputMember);
		}
	
	/** 가입된 회원의 비밀번호 변경
	 *@author 영민
	 */
	@Override
	public int resetPassword(Map<String, String> map) {
	    try {
	        Member inputMember = new Member();
	        
	        String memberEmail = map.get("memberEmail");
	        String memberPw = map.get("memberPw");
	        
	        if (memberEmail == null || memberPw == null) {
	            log.error("이메일 또는 비밀번호가 null입니다.");
	            return 0;
	        }
	        
	        String encPw = bcrypt.encode(memberPw);
	        
	        inputMember.setMemberPw(encPw);
	        inputMember.setMemberEmail(memberEmail);
	        
	        int result = mapper.resetPassword(inputMember);
	        log.info("비밀번호 재설정 결과: {}", result);
	        
	        return result;
	    } catch (Exception e) {
	        log.error("비밀번호 재설정 중 오류 발생: {}", e.getMessage(), e);
	        return 0;
	    }
	}
	@Override
	public List<Member> searchMember(String memberName) {
	
		return mapper.searchMember(memberName);
	}
	@Override
	public int checkTel(String memberTel) {
		
		return mapper.checkTel(memberTel);
	}
	
	@Override
	public Member checkmemberEmail(String memberEmail) {
		
		return mapper.checkmemberEmail(memberEmail);
	}
	
	
	/** 카카오회원의 토큰을 업데이트해주는 구문
	 * 만약 기존회원 최초로그인시 null일텐데 이게 null이면 token으로 바꿔줘야함
	 *
	 *@author 영민
	 */
	@Override
	public int kakaoMemberUpdate(String memberEmail, String kakaoToken) {
        try {
            // 회원 정보 조회
            Member member = mapper.findKakaoMember(memberEmail);
            
            if (member == null) {
                log.error("카카오 토큰 업데이트 실패: 회원 정보가 없음 (이메일: {})", memberEmail);
                return 0;
            }
            
            // 토큰 정보를 Map에 설정
            Map<String, String> map = new HashMap<>();
            map.put("kakaoAccessToken", kakaoToken);
            map.put("memberEmail", memberEmail); 
            
            // 기존 토큰과 관계없이 항상 최신 토큰으로 업데이트
            int result = mapper.updateKakaToken(map);
            
            if (result > 0) {
                log.info("카카오 토큰 업데이트 성공: 이메일={}", memberEmail);
            } else {
                log.error("카카오 토큰 업데이트 실패: 이메일={}", memberEmail);
            }
            
            return result;
        } catch (Exception e) {
            log.error("카카오 토큰 업데이트 중 오류 발생: {}", e.getMessage(), e);
            return 0;
        }
    }
	


	public int insertMember(Member insertMember) {
	
		try {
            int result = mapper.insertMember(insertMember);
            
            if (result > 0) {
                log.info("카카오 회원 등록 성공: 이메일={}, 회원번호={}", 
                        insertMember.getMemberEmail(), insertMember.getMemberNo());
                
                // 회원가입 후 기본 게시판 타입 생성
                int boardTypeResult = mapper.setDefaultBoardType(insertMember.getMemberNo());
                log.info("기본 게시판 타입 설정 결과: {}", boardTypeResult);
            } else {
                log.error("카카오 회원 등록 실패: 이메일={}", insertMember.getMemberEmail());
            }
            
            return result;
        } catch (Exception e) {
            log.error("카카오 회원 등록 중 오류 발생: {}", e.getMessage(), e);
            return 0;
        }
    }
	

	@Override
	public boolean validatePassword(String memberEmail, String password) {
	    System.out.println("=== Service validatePassword ===");
	    System.out.println("이메일: " + memberEmail);
	    System.out.println("입력 비밀번호: " + password);
	    
	    Member member = mapper.checkmemberEmail(memberEmail);
	    
	    if(member == null) {
	        System.out.println("멤버를 찾을 수 없음");
	        return false;
	    }
	    
	    String memberPw = member.getMemberPw();
	    System.out.println("DB 암호화된 비밀번호: " + memberPw);
	    
	    boolean result = bcrypt.matches(password, memberPw);
	    // 순서: (평문 비밀번호, 암호화된 비밀번호)
	    System.out.println("BCrypt 매칭 결과: " + result);
	    
	    return result;
	}

	@Override
	public int updateMember(Member inputMember) {
	    try {
	        String inputPw = inputMember.getMemberPw();
	        
	        // 비밀번호가 입력되었을 때만 암호화
	        if(inputPw != null && !inputPw.trim().isEmpty()) {
	            String encPw = bcrypt.encode(inputPw);
	            inputMember.setMemberPw(encPw);
	        } else {
	            // 비밀번호를 변경하지 않는 경우 null로 설정
	            inputMember.setMemberPw(null);
	        }
	        
	        return mapper.updateMember(inputMember);
	        
	    } catch (Exception e) {
	        log.error("회원정보 수정 중 오류 발생: {}", e.getMessage(), e);
	        return 0;
	    }
	}
	
	// 메인페이지에서 투데이베스트 회원 6명 구하기
	@Override
	public List<Member> getTodayBestMembers() {
		
		return mapper.getTodayBestMembers();
	}

	@Override
	public Member checkmemberEmailIncludingDeleted(String memberEmail) {
		return mapper.checkmemberEmailIncludingDeleted(memberEmail);
	}
	
	// DB에 있는 이미지 이름 조회
	@Override
	public List<String> selectDbImageList() {
		return mapper.selectDbImageList();
	}
}

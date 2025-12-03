package com.featherworld.project.friend.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.featherworld.project.board.controller.BoardController;

import com.featherworld.project.board.controller.CommentController;
import com.featherworld.project.board.model.service.BoardServiceImpl;
import com.featherworld.project.board.model.service.CommentServiceImpl;
import com.featherworld.project.common.dto.Pagination;
import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.friend.model.mapper.IlchonMapper;
import com.featherworld.project.member.model.dto.Member;

@Transactional(rollbackFor=Exception.class)
@Service
public class IlchonServiceImpl implements IlchonService {

	@Autowired
	private IlchonMapper mapper;

    public Ilchon selectOne(int loginMemberNo, int memberNo) {
    	
    	return mapper.selectOne(loginMemberNo, memberNo);
    }
	@Override
	public Map<String, Object> selectIlchonMemberList(int loginMemberNo, int cp) {
		
		int ilchonsCount =  mapper.countIlchons(loginMemberNo);
		
		// pagination 객체 생성 

	    Pagination pagination = new Pagination(cp, ilchonsCount, 6, 8);

		
		// 지정된 inchon들의 목록 조회
		/*
		 * ROWBOUNDS 객체 (MyBatis 제공 객체)
		 * : 지정된 크기만큼 건너 뛰고(offset)
		 * 제한된 크기만큼(limit)의 행을 조회하는 객체
		 * 
		 * --> 페이징 처리가 굉장히 간단해짐
		 * 
		 * */
		
		int limit = pagination.getLimit(); // 한 페이지당 default(=10개)
		
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		
		// Mapper 메서드 호출 시 원래 전달할 수 있는 매개변수 1개
		// -> 2개를 전달할 수 있는 경우가 있음
		// rowBounds를 이용할때!
		// -> 첫번째 매개변수 -> SQL 에 전달할 파라미터
		// -> 두번째 매개변수 -> RowBounds 객체 전달
		List<Ilchon> ilchons = mapper.selectPagination(loginMemberNo, rowBounds);
	
		// 4. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();
				
		map.put("pagination", pagination);
		map.put("ilchons", ilchons);
		
				
		return map;
	
	}
	@Override
	public Map<String, Object> selectIncomingIlchonMemberList(int loginMemberNo, int cp) {
		// TODO Auto-generated method stub
		
		
				int ilchonsCount =  mapper.countIncomingIlchons(loginMemberNo);
				
				// pagination 객체 생성 

			    Pagination pagination = new Pagination(cp, ilchonsCount);

				
				// 지정된 inchon들의 목록 조회
				/*
				 * ROWBOUNDS 객체 (MyBatis 제공 객체)
				 * : 지정된 크기만큼 건너 뛰고(offset)
				 * 제한된 크기만큼(limit)의 행을 조회하는 객체
				 * 
				 * --> 페이징 처리가 굉장히 간단해짐
				 * 
				 * */
				
				int limit = pagination.getLimit(); // 한 페이지당 default(=10개)
				
				int offset = (cp - 1) * limit;
				
				RowBounds rowBounds = new RowBounds(offset, limit);
				

				// Mapper 메서드 호출 시 원래 전달할 수 있는 매개변수 1개
				// -> 2개를 전달할 수 있는 경우가 있음
				// rowBounds를 이용할때!
				// -> 첫번째 매개변수 -> SQL 에 전달할 파라미터
				// -> 두번째 매개변수 -> RowBounds 객체 전달
				List<Ilchon> ilchons = mapper.selectIncomingPagination(loginMemberNo, rowBounds);
			
				// 4. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
				Map<String, Object> map = new HashMap<>();
						
				map.put("pagination", pagination);
				map.put("ilchons", ilchons);
				
						
				return map;
	}

	@Override
	public Map<String, Object> selectSendedIlchonMemberList(int loginMemberNo, int cp) {
		// TODO Auto-generated method stub
		
		
		int ilchonsCount =  mapper.countSendedIlchons(loginMemberNo);
		
		// pagination 객체 생성 

	    Pagination pagination = new Pagination(cp, ilchonsCount);

		
		// 지정된 inchon들의 목록 조회
		/*
		 * ROWBOUNDS 객체 (MyBatis 제공 객체)
		 * : 지정된 크기만큼 건너 뛰고(offset)
		 * 제한된 크기만큼(limit)의 행을 조회하는 객체
		 * 
		 * --> 페이징 처리가 굉장히 간단해짐
		 * 
		 * */
		
		int limit = pagination.getLimit(); // 한 페이지당 default(=10개)
		
		int offset = (cp - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		

		// Mapper 메서드 호출 시 원래 전달할 수 있는 매개변수 1개
		// -> 2개를 전달할 수 있는 경우가 있음
		// rowBounds를 이용할때!
		// -> 첫번째 매개변수 -> SQL 에 전달할 파라미터
		// -> 두번째 매개변수 -> RowBounds 객체 전달
		List<Ilchon> ilchonsFrom = mapper.selectSendedPagination(loginMemberNo, rowBounds);
	
		// 4. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();
				
		map.put("pagination", pagination);
		map.put("ilchons", ilchonsFrom);
		
				
		return map;
	}
	
	@Override
	public int updateIlchonNickname(int loginMemberNo,int memberNo,String nickname) {
		// TODO Auto-generated method stub
		/*
		Map<String, Object> mapSelect = new HashMap<String, Object>();
		
		mapSelect.put("loginMemberNo", loginMemberNo);
		mapSelect.put("memberNo", memberNo);
		
		
		Map<String, Object> mapUpdate = new HashMap<String, Object>();
		mapUpdate.put("loginMemberNo", loginMemberNo);
		mapUpdate.put("memberNo", memberNo);
		mapUpdate.put("nickname", nickname);
		
		int resultTo = 0;
		Ilchon friend = mapper.selectOne(mapSelect);
		System.out.println("selectOne : " + friend.getFromMemberNo());
		System.out.println("loginMemberNo : " + loginMemberNo);
		
		if(loginMemberNo == friend.getFromMemberNo()) { //!= 시 query 지연되는듯?
			resultTo = mapper.updateFromIlchonNickName( mapUpdate );
			return resultTo;
			
		}
		resultTo = mapper.updateToIlchonNickName( mapUpdate );*/

		int resultTo= mapper.updateToIlchonNickName( loginMemberNo, memberNo, nickname); 
		
		
		int resultFrom = mapper.updateFromIlchonNickName( loginMemberNo, memberNo, nickname);
		if(resultTo == 1 && resultFrom == 0) {
			return 2 ; 
		}else if(resultTo == 0 && resultFrom == 1){
			return 1;
			
		}else {return 0;}
		/*System.out.println("Mapper 호출 결과 resultFrom: " + resultFrom);*/
		
		
	}
	
	
	
	@Override
	public int updateIlchonRow(int loginMemberNo, int targetMemberNo, String nickname) {
		// TODO Auto-generated method stub
		int resultTo= mapper.updateIlchonRow_FromNickname( loginMemberNo, targetMemberNo, nickname); 
		
		
		int resultFrom = mapper.updateIlchonRow_ToNickname( loginMemberNo, targetMemberNo, nickname);
		if(resultTo == 1 && resultFrom == 0) {
			return 2 ; 
		}else if(resultTo == 0 && resultFrom == 1){
			return 1;
			
		}else {return 0;}
		/*System.out.println("Mapper 호출 결과 resultFrom: " + resultFrom);*/
		
	}

	@Override
	public int insertNewIlchon(int loginMemberNo, int targetMemberNo, String nickname) {
		
		Ilchon il = selectOne(loginMemberNo, targetMemberNo);
		
		
		if (il == null){ // 일촌이 존재하지 않는다면 
			int result = mapper.insertIlchon(loginMemberNo, targetMemberNo, nickname);
			if(result == 1) {// 삽입 성공시
				return 1;
				
			}else { // 삽입 실패시(아무것도 삽입되지 않음)
				return 0;
			}
			
		} else {// 이미 일촌이 존재한다면
			return -1; 
		}
		
		
	}

	@Override
	public int deleteIlchon(int loginMemberNo, int targetMemberNo) {
		Ilchon il = mapper.selectOneYN(loginMemberNo, targetMemberNo);
		
		
		if (il != null){ // 삭제대상 일촌정보가 존재한다면  
			int result = mapper.deleteIlchon(loginMemberNo, targetMemberNo);
			if(result == 1) {// 삭제 성공시
				return 1;
				
			}else { // 삭제 실패시(아무것도 삭제되지 않음)
				return 0;
			}
			
		} else {// 일촌정보를 찾을수 없다면
			return -1; 
		}
		
	}

	
	public int isIncomingIlchonExists(int loginMemberNo,int targetMemberNo) {
		
		Ilchon ilchon = mapper.selectOne(loginMemberNo, targetMemberNo);
		Ilchon ilchonAppend = mapper.selectOneYN(loginMemberNo, targetMemberNo);
		if (ilchon != null && ilchonAppend != null) return 2; //이미 둘이 일촌관계일경우
		else if(ilchonAppend != null) return 1; // 이미 일촌관계 신청을 보냈지만 아직 다른쪽에서 수락하지 않은 경우
		else return 0; // 일촌관계도, 아직 일촌신청도 보내지 않은경우
		
	}
	

	
}

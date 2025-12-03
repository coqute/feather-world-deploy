package com.featherworld.project.friend.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.featherworld.project.friend.model.dto.Ilchon;
import com.featherworld.project.member.model.dto.Member;
@Mapper
public interface IlchonMapper {
	
	/*FROM_MEMBER_NO - TO_MEMBER_NO 또는 TO-MEMBER_NO - FROM_MEMBER_NO 쌍을 체크해 
	 * 순서 상관없이 동일한 No쌍(=number쌍)이 존재하면 그 Ilchon DTO를 리턴*/
	Ilchon selectOne(@Param("memberNo1")int memberNo1, @Param("memberNo2")int memberNo2);
	/**순서 상관없이 동일한 No쌍(=number쌍)이 존재하면 그 Ilchon DTO를 리턴
	 * (*****IS_ILCHON='Y' 조건문 삭제한 SQL구문*****)
	 * @param memberNo1
	 * @param memberNo2
	 * @return
	 */
	Ilchon selectOneYN(@Param("memberNo1")int memberNo1, @Param("memberNo2")int memberNo2);
	//Ilchon selectOne(Map<String, Object> map);
	/**일촌 리스트를 select하는 함수(IS_ILCHON = 'Y' 한정)
	 * @param loginMemberNo
	 * @param rowBounds
	 * @return
	 */
	List<Ilchon> selectPagination(int loginMemberNo, RowBounds rowBounds);
	/**일촌 일촌신청 *(incoming) 리스트를 select하는 함수(IS_ILCHON = 'N' 한정)
	 * @param loginMemberNo
	 * @param rowBounds
	 * @return
	 */
	List<Ilchon> selectIncomingPagination(int loginMemberNo, RowBounds rowBounds);
	/**일촌 내가보낸 일촌신청 *(incoming 내부 sended) 리스트를 select하는 함수(IS_ILCHON = 'N' 한정)
	 * @param loginMemberNo
	 * @param rowBounds
	 * @return
	 */
	List<Ilchon> selectSendedPagination(int loginMemberNo, RowBounds rowBounds);
	/**일촌 리스트 명수를 count하는 함수(IS_ILCHON = 'Y' 한정)
	 * @param loginMemberNo
	 * @return
	 */
	int countIlchons(int loginMemberNo);
	/**받은 일촌신청 *(incoming) 리스트 명수를 count하는 함수(IS_ILCHON = 'N' 한정)
	 * @param loginMemberNo
	 * @return
	 */
	int countIncomingIlchons(int loginMemberNo);
	/**보낸 일촌신청 *(incoming) 리스트 명수를 count하는 함수(IS_ILCHON = 'N' 한정)
	 * @param loginMemberNo
	 * @return
	 */
	int countSendedIlchons(int loginMemberNo);
	//int updateToIlchonNickName(Map<String, Object> paramMap);
	//int updateFromIlchonNickName(Map<String, Object> paramMap);
	int updateToIlchonNickName(@Param("loginMemberNo") int loginMemberNo/*session*/,@Param("memberNo") int targetMemberNo,@Param("nickname") String nickname);
	int updateFromIlchonNickName(@Param("loginMemberNo")int loginMemberNo/*session*/,@Param("memberNo") int targetMemberNo,@Param("nickname") String nickname);
	int insertIlchon(@Param("loginMemberNo")int loginMemberNo,@Param("targetMemberNo") int targetMemberNo,@Param("nickname") String nickname);
	/** FROM_NICKNAME, IS_ILCHON = 'Y'로 업데이트
	 * @param loginMemberNo
	 * @param targetMemberNo
	 * @param nickname
	 * @return
	 */
	int updateIlchonRow_FromNickname(@Param("loginMemberNo")int loginMemberNo,@Param("targetMemberNo") int targetMemberNo,@Param("nickname") String nickname);
	/** TO_NICKNAME, IS_ILCHON = 'Y'로 업데이트
	 * @param loginMemberNo
	 * @param targetMemberNo
	 * @param nickname
	 * @return
	 */
	int updateIlchonRow_ToNickname(@Param("loginMemberNo")int loginMemberNo,@Param("targetMemberNo") int targetMemberNo,@Param("nickname") String nickname);
	/**일촌 정보 삭제
	 * @param loginMemberNo
	 * @param targetMemberNo
	 * @return
	 */
	int deleteIlchon(@Param("loginMemberNo")int loginMemberNo,@Param("targetMemberNo")  int targetMemberNo);
	
	
}

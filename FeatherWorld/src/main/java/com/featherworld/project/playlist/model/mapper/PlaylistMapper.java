package com.featherworld.project.playlist.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlaylistMapper {


	/** 플레이리스트 조회
	 * @param memberNo
	 * @return
	 */
	String selectPlaylist(int memberNo);
	

	/** 플레이리스트 저장
	 * @param memberNo
	 * @param youtubeUrl 
	 * @return
	 */
	int insertPlaylist(Map<String, Object> map);
	
	/** 플레이리스트 수정
	 * @param map
	 * @return
	 */
	int updatePlaylist(Map<String, Object> map);


	/** 플레이리스트 삭제
	 * @param map
	 * @return
	 */
	int deletePlaylist(int memberNo);

}

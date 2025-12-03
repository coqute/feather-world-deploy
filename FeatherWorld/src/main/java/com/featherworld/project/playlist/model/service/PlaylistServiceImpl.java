package com.featherworld.project.playlist.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.featherworld.project.playlist.model.mapper.PlaylistMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlaylistServiceImpl implements PlaylistService{
	
	@Autowired
	private PlaylistMapper mapper;
	
	@Override
	public String selectPlaylist(int memberNo) {
		return mapper.selectPlaylist(memberNo);
	}

	@Override
	public int insertPlaylist(Map<String, Object> map) {
		return mapper.insertPlaylist(map);
	}
	
	@Override
	public int updatePlaylist(Map<String, Object> map) {
		return mapper.updatePlaylist(map);
	}
	
	@Override
	public int deletePlaylist(int memberNo) {
		return mapper.deletePlaylist(memberNo);
	}
}

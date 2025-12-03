package com.featherworld.project.profile.model.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.featherworld.project.common.utill.Utility;
import com.featherworld.project.profile.model.dto.Profile;
import com.featherworld.project.profile.model.mapper.ProfileMapper;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileMapper mapper;

	@Value("${my.profile.web-path}")
	private String profileWebPath; /// myPage/profile/

	@Value("${my.profile.folder-path}")
	private String profileFolderPath; // C:/uploadFiles/profile/

	@Override
	public Profile selectProfile(int memberNo) {
		return mapper.selectProfile(memberNo);
	}

	@Override
	public int saveOrUpdateProfile(int loginMemberNo, MultipartFile uploadFile, String bio) throws Exception {
	    int result = 0;
	    String rename = null;

	    // 기존 프로필 불러오기
	    Profile findProfileData = selectProfile(loginMemberNo);

	    // 업로드한 이미지가 있을 경우 이름 변경
	    if (uploadFile != null && !uploadFile.isEmpty()) {
	        rename = Utility.fileRename(uploadFile.getOriginalFilename());
	    }

	    // bio가 null이거나 공백이면 기존 bio 유지
	    if ((bio == null || bio.trim().isEmpty()) && findProfileData != null) {
	        bio = findProfileData.getProfileContent();
	    }

	    // 원래 이름 및 리네임 파일 이름 설정
	    String imgOriginalName = null;
	    String imgRename = null;

	    if (!uploadFile.isEmpty()) {
	        imgOriginalName = uploadFile.getOriginalFilename();
	        imgRename = rename;
	    } else if (findProfileData != null) {
	        imgOriginalName = findProfileData.getImgOriginalName();
	        imgRename = findProfileData.getImgRename();
	    }

	    // 프로필 객체 구성
	    Profile newProfile = Profile.builder()
	        .memberNo(loginMemberNo)
	        .imgPath(profileWebPath)
	        .imgOriginalName(imgOriginalName)
	        .imgRename(imgRename)
	        .profileContent(bio)
	        .build();

	    // DB에 저장 (기존 데이터가 있으면 update, 없으면 insert)
	    if (findProfileData != null) {
	        result = mapper.updateProfile(newProfile);
	    } else {
	        result = mapper.insertProfile(newProfile);
	    }

	    // 실제 파일 저장은 DB 반영 후 수행
	    if (result > 0 && !uploadFile.isEmpty()) {
	        File directory = new File(profileFolderPath);
	        if (!directory.exists()) {
	            directory.mkdirs();  // 폴더가 없으면 생성
	        }

	        File dest = new File(directory, rename);
	        uploadFile.transferTo(dest);
	    }

	    return result;
	

	}

	/**
	 *  회원 탈퇴
	 */
	@Autowired
	private PasswordEncoder bcrypt;

	@Override
	public int secession(String memberPw, int memberNo) {
	    String originPw = mapper.selectEncodedPw(memberNo); // XML id와 동일한 메서드명 사용

	    if (!bcrypt.matches(memberPw, originPw)) {
	        return 0;
	    }

	    return mapper.deleteMember(memberNo); // XML id와 동일한 메서드명 사용
	}


@Override
public int secessionKakaoMember(int memberNo) {
	
	return mapper.secessionKakaoMember(memberNo);
}

}

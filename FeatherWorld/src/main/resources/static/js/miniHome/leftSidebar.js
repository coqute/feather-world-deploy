// 프로필 수정 모드 상태 변수
let isEditMode = false;
let selectedImageFile = null;
let originalBioText = "";
let originalImageSrc = "";

// 일촌 신청 함수
function follow(toMemberNo) {
  if (!confirm("이 분에게 일촌 신청을 보내시겠습니까?")) {
    return;
  }

  const followBtn = document.getElementById("profileFollowBtn");
  if (followBtn) {
    followBtn.disabled = true;
    followBtn.textContent = "신청 중...";
  }

  fetch(`/${toMemberNo}/follow`, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: "toMemberNo=" + toMemberNo,
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("네트워크 오류: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      if (data.success) {
        alert("✅" + data.message);
        if (followBtn) {
          followBtn.textContent = "신청됨";
          followBtn.style.backgroundColor = "#6c757d";
          followBtn.disabled = true;
        }
      } else {
        alert("❌ " + data.message);
        if (followBtn) {
          followBtn.disabled = false;
          followBtn.textContent = "Follow";
        }
      }
    });
}

// 미수락 팔로워 신청 보기
function showPendingFollowers() {
  alert(
    "새로운 일촌 신청이 있습니다!\n일촌 신청 목록 페이지로 이동하시겠습니까?"
  );
  window.location.href = `/${memberNo}/friendList/incoming`;
}

// 프로필 수정 모드 토글
function editProfile() {
  if (!isEditMode) {
    // 수정 모드 시작
    enterEditMode();
  } else {
    // 수정 모드에서 Save 버튼을 눌렀을 때
    if (confirm("프로필 수정을 저장하시겠습니까?")) {
      saveProfileChanges();
    }
  }
}

// 수정 모드 진입
function enterEditMode() {
  isEditMode = true;

  // 원본 데이터 백업
  const bioDisplay = document.getElementById("profileBioDisplay");
  const profileImg = document.querySelector("#profileMainImage");

  if (bioDisplay) {
    originalBioText = bioDisplay.textContent || bioDisplay.innerText || "";
  }
  if (profileImg) {
    originalImageSrc = profileImg.src;
  }

  // 버튼 영역 전환
  const ownerButtons = document.getElementById("profileOwnerButtons");
  const editModeButtons = document.getElementById("profileEditModeButtons");

  if (ownerButtons) ownerButtons.style.display = "none";
  if (editModeButtons) editModeButtons.style.display = "block";

  // 프로필 이미지 edit, delete 아이콘 표시
  const editIcon = document.getElementById("profileImageEditIcon");
  if (editIcon) {
    editIcon.style.display = "block";
  }
  const deleteIcon = document.getElementById("profileImageDeleteIcon");
  if (deleteIcon) {
    deleteIcon.style.display = "block";
  }

  // 다른 수정 가능한 요소들 활성화
  enableEditableFields();

  console.log("프로필 수정 모드 활성화");
  console.log("원본 Bio 텍스트 백업:", originalBioText);
  console.log("원본 이미지 소스 백업:", originalImageSrc);
}

// 수정 모드 종료
function exitEditMode() {
  isEditMode = false;
  selectedImageFile = null;

  // 버튼 영역 원복
  const ownerButtons = document.getElementById("profileOwnerButtons");
  const editModeButtons = document.getElementById("profileEditModeButtons");

  if (ownerButtons) ownerButtons.style.display = "block";
  if (editModeButtons) editModeButtons.style.display = "none";

  // 프로필 이미지 edit, delete 아이콘 숨기기
  const editIcon = document.getElementById("profileImageEditIcon");
  if (editIcon) {
    editIcon.style.display = "none";
  }
  const deleteIcon = document.getElementById("profileImageDeleteIcon");
  if (deleteIcon) {
    deleteIcon.style.display = "none";
  }

  // 수정 가능한 요소들 비활성화
  disableEditableFields();

  console.log("프로필 수정 모드 비활성화");
}

// 수정 가능한 필드들 활성화
function enableEditableFields() {
  const bioDisplay = document.getElementById("profileBioDisplay");
  if (bioDisplay) {
    bioDisplay.contentEditable = true;
    bioDisplay.style.border = "1px dashed #9f2120";
    bioDisplay.style.padding = "8px";
    bioDisplay.style.borderRadius = "4px";
    bioDisplay.style.backgroundColor = "#fff9f9";
    bioDisplay.title = "클릭해서 수정하세요";
  }
}

// 수정 가능한 필드들 비활성화
function disableEditableFields() {
  const bioDisplay = document.getElementById("profileBioDisplay");
  if (bioDisplay) {
    bioDisplay.contentEditable = false;
    bioDisplay.style.border = "none";
    bioDisplay.style.padding = "12px";
    bioDisplay.style.backgroundColor = "transparent";
    bioDisplay.title = "";
  }
}

// 프로필 변경사항 저장
function saveProfileChanges() {
  const formData = new FormData();
  const defaultImageUrl = `${location.origin}/images/default/user.png`;
  const userProfileImage = document.getElementById("profileMainImage");

  // bio 내용 가져오기
  const bioDisplay = document.getElementById("profileBioDisplay");
  if (bioDisplay) {
    const bioText = bioDisplay.textContent || bioDisplay.innerText || "";
    formData.append("memberIntro", bioText.trim());
  }

  // 이미지 파일이 선택되었으면 추가
  if (selectedImageFile) {
    formData.append("memberImg", selectedImageFile);
    console.log("이미지 파일 추가:", selectedImageFile.name);
  }

  // 이미지가 기본 이미지로 변경된 경우 (삭제된 경우)
  let isImageDeleted = false;
  if (
    userProfileImage.src === defaultImageUrl &&
    originalImageSrc !== defaultImageUrl
  ) {
    isImageDeleted = true;
    formData.append("deleteImage", "true"); // 이미지 삭제 플래그 추가
  }

  // 서버로 전송
  fetch(`/${memberNo}/leftProfileUpdate`, {
    method: "POST",
    body: formData,
    credentials: "same-origin",
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("네트워크 오류: " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      console.log("서버 응답:", data);

      if (data.success) {
        alert("✅ " + data.message);
        exitEditMode();
        location.reload();
      } else {
        alert("❌ " + data.message);
      }
    })
    .catch((error) => {
      console.error("프로필 업데이트 오류:", error);
      alert("❌ 프로필 업데이트 중 오류가 발생했습니다.");
    });
}

// 프로필 삭제
const deleteProfileImage = () => {
  // 기본 이미지 주소, 현재 회원 프로필 이미지
  const defaultImageUrl = `${location.origin}/images/default/user.png`;
  const userProfileImage = document.getElementById("profileMainImage");

  // 기본 이미지가 아닌 경우만 삭제
  if (defaultImageUrl !== userProfileImage.src) {
    // 제출되는 이미지 초기화
    selectedImageFile = null;
    userProfileImage.src = defaultImageUrl; // 미리보기 기본 이미지로 변경
  }
};

// 프로필 수정 취소
function cancelEdit() {
  if (confirm("수정사항을 취소하시겠습니까? 변경된 내용이 모두 사라집니다.")) {
    // 원래 데이터로 복원
    restoreOriginalData();
    exitEditMode();
  }
}

// 원래 데이터 복원
function restoreOriginalData() {
  // Bio 내용 복원
  const bioDisplay = document.getElementById("profileBioDisplay");
  if (bioDisplay && originalBioText !== "") {
    bioDisplay.textContent = originalBioText;
  }

  // 이미지 복원
  const profileImg = document.querySelector("#profileMainImage");
  if (profileImg && originalImageSrc !== "") {
    profileImg.src = originalImageSrc;
  }

  // 선택된 파일 초기화
  selectedImageFile = null;

  console.log("원본 데이터로 복원 완료");
}

// 프로필 이미지 수정
function editProfileImage() {
  if (!isEditMode) {
    alert("프로필 수정 모드에서만 이미지를 변경할 수 있습니다.");
    return;
  }

  const fileInput = document.createElement("input");
  fileInput.type = "file";
  fileInput.accept = "image/*";
  fileInput.onchange = function (event) {
    const file = event.target.files[0];
    if (file) {
      // 파일 크기 체크 (5MB 제한)
      if (file.size > 5 * 1024 * 1024) {
        alert("이미지 파일 크기는 5MB 이하여야 합니다.");
        return;
      }

      selectedImageFile = file;
      const reader = new FileReader();
      reader.onload = function (e) {
        const profileImg = document.querySelector("#profileMainImage");
        if (profileImg) {
          profileImg.src = e.target.result;
        }
      };
      reader.readAsDataURL(file);

      console.log("이미지 변경 예정:", file.name);
    }
  };
  fileInput.click();
}

// 로그아웃
function logout() {
  if (confirm("로그아웃 하시겠습니까?")) {
    fetch("/member/logout", {
      method: "POST",
      credentials: "same-origin",
    })
      .then(() => {
        window.location.href = "/";
      })
      .catch((error) => {
        console.error("로그아웃 오류:", error);
        window.location.href = "/";
      });
  }
}

// 서핑 (랜덤 미니홈 방문)
function goSurfing() {
  if (confirm("랜덤으로 다른 사람의 미니홈을 방문하시겠습니까?")) {
    fetch(`/${memberNo}/surfing`)
      .then((response) => response.text())
      .then((data) => {
        if (data !== "0") {
          window.location.href = "/" + data + "/minihome";
        } else {
          alert("현재 방문할 수 있는 미니홈이 없습니다.");
        }
      })
      .catch((error) => {
        console.error("서핑 오류:", error);
        alert("서핑 중 오류가 발생했습니다.");
      });
  }
}

// DOM 로드 완료 시 초기화
document.addEventListener("DOMContentLoaded", function () {
  // 프로필 이미지 edit 아이콘 초기에 숨기기
  const editIcon = document.getElementById("profileImageEditIcon");
  if (editIcon) {
    editIcon.style.display = "none";
  }

  // 편집 모드 버튼들 초기에 숨기기
  const editModeButtons = document.getElementById("profileEditModeButtons");
  if (editModeButtons) {
    editModeButtons.style.display = "none";
  }
});

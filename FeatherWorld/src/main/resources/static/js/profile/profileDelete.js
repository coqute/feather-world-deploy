function validateForm() {
  // HTML data 속성에서 카카오 회원 여부 확인
  const isKakaoMember = document.body.getAttribute("data-is-kakao") === "true";

  if (isKakaoMember) {
    // 카카오 회원 처리
    const kakaoAgree = document.getElementById("kakaoAgree").checked;
    if (!kakaoAgree) {
      alert("카카오 계정 탈퇴 안내사항에 동의하셔야 탈퇴가 가능합니다.");
      return false;
    }

    // 카카오 회원 최종 확인
    const confirmKakaoDelete = confirm(
      "정말로 카카오 계정을 탈퇴하시겠습니까?\n\n" +
        "⚠️ 주의사항:\n" +
        "• 같은 카카오 계정으로 15일간 재가입 불가\n" +
        "• 모든 데이터가 삭제됩니다\n" +
        "• 이 작업은 되돌릴 수 없습니다"
    );

    if (!confirmKakaoDelete) {
      return false;
    }

    // 한 번 더 확인
    const finalConfirm = confirm(
      "정말로 탈퇴하시겠습니까?\n마지막 확인입니다."
    );
    return finalConfirm;
  } else {
    // 일반 회원 처리
    const pw = document.getElementById("memberPw").value.trim();
    const pwCheck = document.getElementById("memberPwCheck").value.trim();

    if (pw.length === 0) {
      alert("비밀번호를 입력해주세요.");
      return false;
    }

    if (pw !== pwCheck) {
      alert("비밀번호가 일치하지 않습니다.");
      return false;
    }

    const agree = document.getElementById("agree").checked;
    if (!agree) {
      alert("이용약관에 동의하셔야 탈퇴가 가능합니다.");
      return false;
    }

    // 일반 회원 최종 확인
    const confirmDelete = confirm(
      "정말로 계정을 탈퇴하시겠습니까?\n\n" +
        "⚠️ 주의사항:\n" +
        "• 모든 개인 데이터가 15일 이후 영구 삭제됩니다\n" +
        "• 작성한 게시글, 댓글 등이 모두 15일 이후 영구 삭제됩니다\n" +
        "• 이 작업은 되돌릴 수 없습니다"
    );

    if (!confirmDelete) {
      return false;
    }

    // 한 번 더 확인
    const finalConfirm = confirm(
      "정말로 탈퇴하시겠습니까?\n마지막 확인입니다."
    );
    return finalConfirm;
  }
}

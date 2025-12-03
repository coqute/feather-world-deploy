// 입력값 검증 및 오류 메시지 표시를 위한 스크립트
document.addEventListener("DOMContentLoaded", function () {
  const nameInput = document.getElementById("name");
  const phoneInput = document.getElementById("phone");
  const nameError = document.getElementById("nameError");
  const phoneError = document.getElementById("phoneError");
  const confirmBtn = document.getElementById("confirmBtn");

  // 이름 정규식: 2~20자 한글, 영문
  const nameRegex = /^[가-힣a-zA-Z0-9]{2,10}$/;

  // 전화번호 정규식: 10~11자리 숫자 (하이픈 없이)
  const phoneRegex = /^[0-9]{8,11}$/;

  // 이름 입력 검증
  nameInput.addEventListener("input", function () {
    const name = nameInput.value;
    if (name.trim() === "") {
      nameError.textContent = "이름을 입력해주세요.";
    } else if (!nameRegex.test(name)) {
      nameError.textContent = "이름은 2~20자 한글 또는 영문만 가능합니다.";
    } else {
      nameError.textContent = "";
    }
    validateForm();
  });

  // 전화번호 입력 검증
  phoneInput.addEventListener("input", function () {
    const phone = phoneInput.value;
    if (phone.trim() === "") {
      phoneError.textContent = "전화번호를 입력해주세요.";
    } else if (!phoneRegex.test(phone)) {
      phoneError.textContent = "전화번호는 10~11자리 숫자만 입력 가능합니다.";
    } else {
      phoneError.textContent = "";
    }
    validateForm();
  });

  // 폼 전체 검증 및 버튼 활성화
  function validateForm() {
    const isNameValid =
      nameInput.value.trim() !== "" && nameRegex.test(nameInput.value);
    const isPhoneValid =
      phoneInput.value.trim() !== "" && phoneRegex.test(phoneInput.value);

    // 모든 필드가 유효하면 버튼 활성화
    confirmBtn.disabled = !(isNameValid && isPhoneValid);
    return isNameValid && isPhoneValid; // 반환값 추가
  }

  // 폼 제출
  confirmBtn.addEventListener("click", function (e) {
    e.preventDefault(); // 폼 제출 방지

    if (!validateForm()) {
      alert("입력값을 확인해주세요.");
      return; // 유효성 검사 실패 시 함수 종료
    }

    // 버튼 상태 저장 및 변경
    const originalBtnText = confirmBtn.textContent;
    confirmBtn.textContent = "확인 중...";
    confirmBtn.disabled = true;

    // 결과 영역 초기화
    document.getElementById("emailResult").style.display = "none";

    // FormData 객체 생성 및 데이터 추가
    const formData = new FormData();
    formData.append("memberName", nameInput.value); // HTML의 name 속성과 일치시킴
    formData.append("memberTel", phoneInput.value); // HTML의 name 속성과 일치시킴

    fetch("/member/findId", {
      method: "POST",
      body: formData,
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result == "") {
          alert("등록된 이메일이 없습니다.");
        } else {
          document.getElementById("foundEmail").textContent = result;
          document.getElementById("emailResult").style.display = "block";
          confirmBtn.style.display = "none";
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("요청 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
      })
      .finally(() => {
        // 이메일을 찾지 못한 경우에만 버튼 상태 복구
        if (document.getElementById("emailResult").style.display === "none") {
          confirmBtn.textContent = originalBtnText;
          confirmBtn.disabled = false;
        }
      });
  });

  // 초기 폼 검증
  validateForm();
});

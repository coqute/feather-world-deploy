// 전역 변수 및 정규식 정의
const phoneRegex = /^01[0|1|6|7|8|9][0-9]{7,8}$/; // 하이픈 없는 전화번호 형식
const passwordRegex =
  /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%])[A-Za-z\d!@#$%]{8,12}$/; // 비밀번호 형식

// 유효성 검사 함수들
function validatePhone() {
  const phoneInput = document.getElementById("memberTel");

  // 요소가 없으면 true 반환 (수정 폼이 아직 표시되지 않은 경우)
  if (!phoneInput) return true;

  const phoneValue = phoneInput.value.trim();

  // 입력 필드의 CSS 클래스 초기화
  phoneInput.classList.remove("valid", "invalid");

  // 빈 값이면 통과 (선택사항이므로)
  if (phoneValue === "") {
    removeValidationMessage(phoneInput);
    return true;
  }

  if (!phoneRegex.test(phoneValue)) {
    phoneInput.classList.add("invalid");
    showInvalidMessage(
      phoneInput,
      "올바른 전화번호 형식이 아닙니다. (예: 01012345678)"
    );
    return false;
  } else {
    phoneInput.classList.add("valid");
    // 전화번호 중복 확인 (비동기)
    fetch("/member/checkTel?memberTel=" + phoneValue)
      .then((resp) => resp.text())
      .then((result) => {
        if (result >= 1) {
          phoneInput.classList.remove("valid");
          phoneInput.classList.add("invalid");
          showInvalidMessage(phoneInput, "이미 사용중인 전화번호입니다.");
        } else {
          phoneInput.classList.remove("invalid");
          phoneInput.classList.add("valid");
          showValidMessage(phoneInput, "사용 가능한 전화번호입니다.");
        }
      })
      .catch((error) => {
        console.error("전화번호 중복 확인 오류:", error);
        phoneInput.classList.remove("valid");
        phoneInput.classList.add("invalid");
        showInvalidMessage(phoneInput, "전화번호 확인 중 오류가 발생했습니다.");
      });

    // 일단 형식이 맞으면 true 반환 (중복 확인은 비동기로 처리)
    showValidMessage(phoneInput, "전화번호 형식이 올바릅니다.");
    return true;
  }
}

function validatePassword() {
  const passwordInput = document.getElementById("memberPw");

  // 요소가 없으면 true 반환 (수정 폼이 아직 표시되지 않은 경우)
  if (!passwordInput) return true;

  const passwordValue = passwordInput.value.trim();

  // 입력 필드의 CSS 클래스 초기화
  passwordInput.classList.remove("valid", "invalid");

  // 빈 값이면 기존 비밀번호 유지 (업데이트 안함)
  if (passwordValue === "") {
    removeValidationMessage(passwordInput);
    showInfoMessage(passwordInput, "빈 값이면 기존 비밀번호를 유지합니다.");
    return true;
  }

  if (!passwordRegex.test(passwordValue)) {
    passwordInput.classList.add("invalid");
    showInvalidMessage(
      passwordInput,
      "영문, 숫자, 특수문자(!@#$%)를 포함한 8~12자"
    );
    return false;
  } else {
    passwordInput.classList.add("valid");
    showValidMessage(passwordInput, "새 비밀번호로 변경됩니다.");
    return true;
  }
}

// 메시지 표시 함수들
function showValidMessage(input, message) {
  const existingMsg = input.parentNode.querySelector(".validation-message");
  if (existingMsg) {
    existingMsg.remove();
  }

  if (message) {
    const msgElement = document.createElement("div");
    msgElement.className = "validation-message valid";
    msgElement.textContent = message;
    input.parentNode.appendChild(msgElement);
  }
}

function showInvalidMessage(input, message) {
  const existingMsg = input.parentNode.querySelector(".validation-message");
  if (existingMsg) {
    existingMsg.remove();
  }

  const msgElement = document.createElement("div");
  msgElement.className = "validation-message invalid";
  msgElement.textContent = message;
  input.parentNode.appendChild(msgElement);
}

function showInfoMessage(input, message) {
  const existingMsg = input.parentNode.querySelector(".validation-message");
  if (existingMsg) {
    existingMsg.remove();
  }

  const msgElement = document.createElement("div");
  msgElement.className = "validation-message info";
  msgElement.textContent = message;
  input.parentNode.appendChild(msgElement);
}

function removeValidationMessage(input) {
  const existingMsg = input.parentNode.querySelector(".validation-message");
  if (existingMsg) {
    existingMsg.remove();
  }
}

// 화면 전환 함수들
function showAuthSection() {
  document.getElementById("authSection").style.display = "block";
  document.getElementById("editSection").style.display = "none";
}

function showEditSection() {
  document.getElementById("authSection").style.display = "none";
  document.getElementById("editSection").style.display = "block";

  // 수정 폼이 표시된 후 유효성 검사 리스너 추가
  setTimeout(addValidationListeners, 100);
}

// 유효성 검사 이벤트 리스너 추가
function addValidationListeners() {
  const phoneInput = document.getElementById("memberTel");
  const passwordInput = document.getElementById("memberPw");

  if (phoneInput) {
    phoneInput.addEventListener("input", validatePhone);
    phoneInput.addEventListener("blur", validatePhone);
  }

  if (passwordInput) {
    passwordInput.addEventListener("input", validatePassword);
    passwordInput.addEventListener("blur", validatePassword);
  }
}

// 비밀번호 인증 버튼 클릭 이벤트
document.getElementById("verifyBtn").addEventListener("click", function () {
  const password = document.getElementById("password").value;

  if (!password.trim()) {
    alert("비밀번호를 입력해주세요.");
    return;
  }

  // 비동기 비밀번호 확인
  fetch(`/${memberNo}/validatePassword`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ password: password }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showEditSection();
      } else {
        alert(data.message);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("인증 중 오류가 발생했습니다.");
    });
});

// Enter 키 처리
document.getElementById("password").addEventListener("keypress", function (e) {
  if (e.key === "Enter") {
    document.getElementById("verifyBtn").click();
  }
});

// 폼 제출 시 유효성 검사
document.addEventListener("DOMContentLoaded", function () {
  document.addEventListener("submit", function (e) {
    if (e.target.id === "updateForm") {
      const isPhoneValid = validatePhone();
      const isPasswordValid = validatePassword();

      if (!isPhoneValid || !isPasswordValid) {
        e.preventDefault();
        alert("입력 정보를 확인해주세요.");
        return false;
      }
    }
  });
});

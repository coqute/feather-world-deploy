document.addEventListener("DOMContentLoaded", function () {
  // DOM 요소
  const emailInput = document.getElementById("email");
  const authKeyInput = document.getElementById("authKey");
  const sendAuthKeyBtn = document.getElementById("sendAuthKeyBtn");
  const verifyAuthKeyBtn = document.getElementById("verifyAuthKeyBtn");
  const confirmBtn = document.getElementById("confirmBtn");
  const emailError = document.getElementById("emailError");
  const authKeyError = document.getElementById("authKeyError");

  const emailAuthForm = document.getElementById("emailAuthForm");
  const passwordResetForm = document.getElementById("passwordResetForm");

  const passwordInput = document.getElementById("password");
  const passwordCheckInput = document.getElementById("passwordCheck");
  const resetPasswordBtn = document.getElementById("resetPasswordBtn");
  const passwordError = document.getElementById("passwordError");
  const passwordCheckError = document.getElementById("passwordCheckError");

  // 타이머 관련 변수
  let authTimer;
  const initMin = 5;
  const initSec = 0;
  let min = initMin;
  let sec = initSec;

  // 인증된 회원 정보 저장 변수
  let verifiedEmail = null;

  // 이메일 정규식
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

  // 비밀번호 정규식 (영문, 숫자, 특수문자(!@#$%) 포함 8~12자)
  const passwordRegex =
    /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%])[A-Za-z\d!@#$%]{8,12}$/;

  // 이메일 유효성 검사
  emailInput.addEventListener("input", function () {
    const email = emailInput.value.trim();

    if (email === "") {
      emailError.textContent = "이메일을 입력해주세요.";
      sendAuthKeyBtn.disabled = true;
    } else if (!emailRegex.test(email)) {
      emailError.textContent = "유효한 이메일 형식이 아닙니다.";
      sendAuthKeyBtn.disabled = true;
    } else {
      emailError.textContent = "";
      sendAuthKeyBtn.disabled = false;
    }
  });

  // 타이머 시작 함수
  function startAuthTimer() {
    clearInterval(authTimer); // 기존 타이머 정리

    // 시간 초기화
    min = initMin;
    sec = initSec;

    // 초기값 표시
    authKeyError.innerText = `${addZero(min)}:${addZero(sec)}`;

    authTimer = setInterval(() => {
      // 0 분 0 초인 경우 ("00:00 출력 후")
      if (min == 0 && sec == 0) {
        clearInterval(authTimer); // interval 멈춤
        authKeyError.textContent = "인증시간이 초과되었습니다.";
        authKeyError.className = "error-message";
        return;
      }

      // 0 초인 경우
      if (sec == 0) {
        sec = 59;
        min--;
      } else {
        sec--; // 1초 감소
      }

      authKeyError.innerText = `${addZero(min)}:${addZero(sec)}`;
    }, 1000); // 1초 지연시간
  }

  // 숫자 앞에 0 붙이는 함수
  function addZero(number) {
    return number < 10 ? "0" + number : number;
  }

  // 인증키 입력 활성화 여부 설정
  function setAuthKeyInputEnabled(enabled) {
    authKeyInput.disabled = !enabled;
    verifyAuthKeyBtn.disabled = !enabled;

    if (enabled) {
      authKeyInput.focus();
    }
  }

  // 인증키 발송 버튼 클릭 이벤트
  sendAuthKeyBtn.addEventListener("click", function () {
    const email = emailInput.value.trim();

    if (!emailRegex.test(email)) {
      alert("유효한 이메일을 입력해주세요.");
      return;
    }

    // 버튼 상태 변경
    sendAuthKeyBtn.disabled = true;
    sendAuthKeyBtn.textContent = "전송 중...";

    // 서버로 인증키 발송 요청
    fetch("/email/findPw", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: email }),
    })
      .then((response) => response.text())
      .then((result) => {
        if (result == 1) {
          alert("인증번호가 발송되었습니다. 이메일을 확인해주세요.");
          setAuthKeyInputEnabled(true);
          startAuthTimer();
        } else {
          alert("등록되지 않은 이메일입니다.");
          emailError.textContent = "등록되지 않은 이메일입니다.";
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("인증번호 발송 중 오류가 발생했습니다.");
      })
      .finally(() => {
        sendAuthKeyBtn.textContent = "Send Authkey";
        sendAuthKeyBtn.disabled = false;
      });
  });

  // 인증키 확인 버튼 클릭 이벤트
  verifyAuthKeyBtn.addEventListener("click", function () {
    const email = emailInput.value.trim();
    const authKey = authKeyInput.value.trim();

    if (authKey === "") {
      authKeyError.textContent = "인증번호를 입력해주세요.";
      return;
    }

    // 버튼 상태 변경
    verifyAuthKeyBtn.disabled = true;
    verifyAuthKeyBtn.textContent = "확인 중...";

    // 서버로 인증키 확인 요청 (JSON 형식으로 통일)
    fetch("/email/checkAuthKey", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: email,
        authKey: authKey,
      }),
    })
      .then((response) => response.text())
      .then((result) => {
        if (result == 1) {
          // 결과값이 1이면 성공
          clearInterval(authTimer); // 타이머 중지
          alert("인증이 완료되었습니다.");
          authKeyError.textContent = "";
          confirmBtn.disabled = false;

          // 인증된 이메일 저장
          verifiedEmail = email;

          // 인증키 입력 필드와 버튼 비활성화
          authKeyInput.disabled = true;
          verifyAuthKeyBtn.disabled = true;
          sendAuthKeyBtn.disabled = true;
          emailInput.disabled = true;
        } else {
          alert("인증번호가 일치하지 않습니다.");
          authKeyError.textContent = "인증번호가 일치하지 않습니다.";
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("인증 확인 중 오류가 발생했습니다.");
      })
      .finally(() => {
        verifyAuthKeyBtn.textContent = "Authkey Confirm";
        verifyAuthKeyBtn.disabled = false;
      });
  });

  // Confirm 버튼 클릭 이벤트 (비밀번호 재설정 폼으로 전환)
  confirmBtn.addEventListener("click", function () {
    if (!verifiedEmail) {
      alert("이메일 인증을 먼저 완료해주세요.");
      return;
    }

    // 이메일 인증 폼 숨기기
    emailAuthForm.style.display = "none";

    // 비밀번호 재설정 폼 표시
    passwordResetForm.style.display = "block";
  });

  // 비밀번호 유효성 검사
  passwordInput.addEventListener("input", function () {
    const password = passwordInput.value;

    if (password === "") {
      passwordError.textContent = "비밀번호를 입력해주세요.";
    } else if (!passwordRegex.test(password)) {
      passwordError.textContent =
        "비밀번호는 영문, 숫자, 특수문자(!@#$%)를 포함한 8~12자로 입력해주세요.";
    } else {
      passwordError.textContent = "";
    }

    validatePasswordMatch();
  });

  // 비밀번호 확인 유효성 검사
  passwordCheckInput.addEventListener("input", function () {
    validatePasswordMatch();
  });

  // 비밀번호 일치 여부 확인
  function validatePasswordMatch() {
    const password = passwordInput.value;
    const passwordCheck = passwordCheckInput.value;

    if (passwordCheck === "") {
      passwordCheckError.textContent = "비밀번호 확인을 입력해주세요.";
      resetPasswordBtn.disabled = true;
    } else if (password !== passwordCheck) {
      passwordCheckError.textContent = "비밀번호가 일치하지 않습니다.";
      resetPasswordBtn.disabled = true;
    } else {
      passwordCheckError.textContent = "";
      resetPasswordBtn.disabled = !passwordRegex.test(password);
    }
  }

  // 비밀번호 재설정 버튼 클릭 이벤트
  resetPasswordBtn.addEventListener("click", function () {
    const password = passwordInput.value;

    if (!verifiedEmail) {
      alert("인증 세션이 만료되었습니다. 다시 인증해주세요.");
      // 이메일 인증 폼으로 돌아가기
      passwordResetForm.style.display = "none";
      emailAuthForm.style.display = "block";
      return;
    }

    if (!passwordRegex.test(password)) {
      alert("유효한 비밀번호를 입력해주세요.");
      return;
    }

    if (password !== passwordCheckInput.value) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    // 버튼 상태 변경
    resetPasswordBtn.disabled = true;
    resetPasswordBtn.textContent = "처리 중...";

    // 서버로 비밀번호 변경 요청 (JSON 형식으로 통일)
    fetch("/member/resetPassword", {
      // 적절한 비밀번호 변경 엔드포인트로 변경
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        memberEmail: verifiedEmail,
        memberPw: password,
      }),
    })
      .then((response) => response.text())
      .then((result) => {
        if (result == 1) {
          // 성공 응답
          alert(
            "비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다."
          );
          window.location.href = "/";
        } else {
          alert("비밀번호 변경 중 오류가 발생했습니다.");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("비밀번호 변경 중 오류가 발생했습니다.");
      })
      .finally(() => {
        resetPasswordBtn.textContent = "Confirm";
        resetPasswordBtn.disabled = false;
      });
  });
});

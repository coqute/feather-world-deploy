document.addEventListener("DOMContentLoaded", function () {
  // DOM 요소 선택
  const searchInput = document.querySelector(".member-search");
  const searchResults = document.getElementById("searchResults");
  const closeBtn = document.getElementById("closeBtn");
  const kakaoLoginBtn = document.querySelector(".kakao-btn");
  const logoutBtn = document.querySelector(".logout-btn");
  const profileImg = document.querySelector(".profile-avatar img");

  // 키보드 네비게이션 변수
  let currentSelectedIndex = -1;
  let isKeyboardNavigation = false;

  // Kakao SDK 초기화 확인 및 재시도
  if (profileImg != null) {
    profileImg.addEventListener("click", () => {
      if (memberNo) {
        window.location.href = `/${memberNo}/updateMember`;
      }
    });
  }

  function ensureKakaoInit() {
    if (typeof Kakao !== "undefined") {
      if (!Kakao.isInitialized()) {
        try {
          Kakao.init("e03376ec020087e66ba936c86bceebe2");
          console.log("Kakao SDK 초기화 상태:", Kakao.isInitialized());
        } catch (e) {
          console.error("Kakao SDK 초기화 오류:", e);
          return false;
        }
      }
      return Kakao.isInitialized();
    } else {
      console.warn("Kakao SDK가 로드되지 않았습니다.");
      return false;
    }
  }

  // 로그아웃 버튼 이벤트
  if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
      console.log("로그아웃 버튼 클릭됨");
      fetch("/member/logout", {
        method: "POST",
        credentials: "same-origin",
      }).then(() => {
        window.location.href = "/";
      });
    });
  }

  // 카카오 로그인 함수
  function kakaoLogin() {
    try {
      if (!ensureKakaoInit()) {
        alert("카카오 SDK를 불러오지 못했습니다. 페이지를 새로고침해 주세요.");
        return;
      }

      Kakao.Auth.login({
        throughTalk: false,
        scope: "profile_nickname account_email",
        success: function (authObj) {
          console.log("카카오 인증 성공:", authObj);

          Kakao.API.request({
            url: "/v2/user/me",
            success: function (res) {
              const kakao_account = res.kakao_account || {};
              console.log("카카오 사용자 정보:", kakao_account);

              const memberEmail = kakao_account.email || "";
              const memberName =
                kakao_account.profile?.nickname || "카카오 사용자";

              fetch("/member/kakaoLogin", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                  memberEmail: memberEmail,
                  memberName: memberName,
                  kakaoToken: authObj.access_token,
                }),
              })
                .then((response) => {
                  if (!response.ok) {
                    throw new Error("서버 응답 오류: " + response.status);
                  }
                  return response.json();
                })
                .then((data) => {
                  if (data.success) {
                    console.log("카카오 로그인 성공:", data.message);
                    if (data.isNewMember) {
                      alert("환영합니다! 회원가입이 완료되었습니다.");
                    }
                    window.location.href = "/";
                  } else {
                    console.error("로그인 실패:", data.message);
                    alert("로그인에 실패했습니다: " + data.message);
                  }
                });
            },
            fail: function (error) {
              console.error("카카오 사용자 정보 요청 실패:", error);
              alert("카카오 계정 정보를 가져오는데 실패했습니다.");
            },
          });
        },
        fail: function (error) {
          console.error("카카오 로그인 실패:", error);
          alert(
            "카카오 로그인에 실패했습니다. 오류 코드: " +
              (error.error_code || "알 수 없음")
          );
        },
      });
    } catch (error) {
      console.error("카카오 로그인 실행 중 오류:", error);
      alert(
        "카카오 로그인 중 오류가 발생했습니다: " +
          (error.message || "알 수 없는 오류")
      );
    }
  }

  // 카카오 로그인 버튼 이벤트
  if (kakaoLoginBtn) {
    kakaoLoginBtn.addEventListener("click", function () {
      kakaoLogin();
    });
  }

  // 키보드 네비게이션 관련 함수들
  function resetSelection() {
    currentSelectedIndex = -1;
    const resultItems = document.querySelectorAll("#searchResults li");
    resultItems.forEach((item) => item.classList.remove("selected"));
  }

  function selectItem(index) {
    const resultItems = document.querySelectorAll("#searchResults li");
    if (resultItems.length === 0) return;

    resultItems.forEach((item) => item.classList.remove("selected"));

    if (index >= 0 && index < resultItems.length) {
      currentSelectedIndex = index;
      resultItems[index].classList.add("selected");
      resultItems[index].scrollIntoView({
        block: "nearest",
        behavior: "smooth",
      });
    }
  }

  // 검색 결과 표시 함수
  function displayResults(members) {
    if (!searchResults) return;

    searchResults.innerHTML = "";
    resetSelection();

    if (!members || members.length === 0) {
      const li = document.createElement("li");
      li.textContent = "검색 결과가 없습니다.";
      li.className = "no-results";
      searchResults.appendChild(li);
    } else {
      members.forEach((member, index) => {
        const li = document.createElement("li");
        li.className = "member-result-item";
        li.dataset.memberNo = member.memberNo;

        li.innerHTML = `
          <div class="member-result-wrapper">
            <div class="member-result-avatar">
              <img src="${
                member.memberImg || "/images/default/user.png"
              }" alt="${member.memberName || "사용자"}">
            </div>
            <div class="member-result-info">
              <div class="member-result-name">${
                member.memberName || "사용자"
              }</div>
              <div class="member-result-email">${member.memberEmail || ""}</div>
            </div>
          </div>
        `;

        li.addEventListener("click", () => {
          if (member.memberNo) {
            window.location.href = `/${member.memberNo}/minihome`;
          }
        });

        searchResults.appendChild(li);
      });

      if (members.length > 0) {
        selectItem(0);
      }
    }

    searchResults.style.display = "block";
  }

  // 검색 입력 및 키보드 네비게이션
  if (searchInput) {
    let debounceTimer = null;
    let isNavigating = false;
    let lastSearchTerm = ""; // 마지막으로 검색한 내용 추적

    searchInput.addEventListener("input", function () {
      const searchTerm = this.value.trim();

      // 검색어가 이전과 같으면 검색하지 않음 (키보드 네비게이션 중이라도)
      if (searchTerm === lastSearchTerm) {
        return;
      }

      // 검색어가 달라졌으므로 네비게이션 모드 해제 및 새로운 검색 시작
      if (isNavigating && searchTerm !== lastSearchTerm) {
        isNavigating = false;
        isKeyboardNavigation = false;
      }

      clearTimeout(debounceTimer);

      if (searchTerm === "") {
        if (searchResults) searchResults.style.display = "none";
        if (closeBtn) closeBtn.style.display = "none";
        resetSelection();
        isNavigating = false;
        isKeyboardNavigation = false;
        lastSearchTerm = ""; // 마지막 검색어 초기화
        return;
      }

      if (closeBtn) closeBtn.style.display = "inline";

      if (searchResults) {
        searchResults.innerHTML = "<li class='loading'>검색 중...</li>";
        searchResults.style.display = "block";
        resetSelection();
      }

      debounceTimer = setTimeout(() => {
        // 검색 시작할 때 현재 검색어를 마지막 검색어로 저장
        lastSearchTerm = searchTerm;

        fetch(`/member/search?memberName=${encodeURIComponent(searchTerm)}`)
          .then((response) => {
            if (!response.ok) {
              throw new Error(
                `HTTP ${response.status}: ${response.statusText}`
              );
            }
            return response.json();
          })
          .then((data) => {
            displayResults(data);
            // ✅ 검색 결과가 나타나면 바로 키보드 네비게이션 활성화
            if (data && data.length > 0) {
              isNavigating = true;
              isKeyboardNavigation = true;
            } else {
              isNavigating = false;
              isKeyboardNavigation = false;
            }
          })
          .catch((error) => {
            if (searchResults) {
              resetSelection();
              searchResults.innerHTML =
                "<li class='error'>검색 중 오류가 발생했습니다.</li>";
              searchResults.style.display = "block";
            }
            isNavigating = false;
            isKeyboardNavigation = false;
          });
      }, 500);
    });

    // 포커스 이벤트
    searchInput.addEventListener("focus", function () {
      // 포커스만으로는 네비게이션 모드를 해제하지 않음
      // 실제 입력이 있을 때만 해제되도록 변경
    });

    // 키보드 네비게이션
    searchInput.addEventListener("keydown", function (e) {
      const resultItems = document.querySelectorAll(
        "#searchResults li:not(.no-results):not(.loading):not(.error)"
      );

      switch (e.key) {
        case "ArrowDown":
        case "ArrowUp":
          if (resultItems.length === 0) return;

          e.preventDefault();
          isNavigating = true;
          isKeyboardNavigation = true;

          if (e.key === "ArrowDown") {
            const nextIndex =
              currentSelectedIndex < resultItems.length - 1
                ? currentSelectedIndex + 1
                : 0;
            selectItem(nextIndex);
          } else {
            const prevIndex =
              currentSelectedIndex > 0
                ? currentSelectedIndex - 1
                : resultItems.length - 1;
            selectItem(prevIndex);
          }
          break;

        case "Enter":
          e.preventDefault();
          if (currentSelectedIndex >= 0 && resultItems.length > 0) {
            const selectedItem = resultItems[currentSelectedIndex];
            const memberNo = selectedItem.dataset.memberNo;
            if (memberNo) {
              window.location.href = `/${memberNo}/minihome`;
            }
          }
          break;

        case "Escape":
          e.preventDefault();
          if (searchResults) searchResults.style.display = "none";
          if (closeBtn) closeBtn.style.display = "none";
          isNavigating = false;
          isKeyboardNavigation = false;
          resetSelection();

          break;

        case "Tab":
          isNavigating = false;
          isKeyboardNavigation = false;

          break;

        case "Backspace":
        case "Delete":
          // 백스페이스나 Delete 키는 입력 변경이므로 네비게이션 모드 해제 준비

          // input 이벤트에서 실제 변경 여부를 확인할 예정
          break;
      }
    });

    // 텍스트 입력 시 - keypress는 유지하되 로직 단순화
    searchInput.addEventListener("keypress", function (e) {
      if (e.key.length === 1) {
        // input 이벤트에서 실제 검색어 변경 여부를 확인할 예정
      }
    });
  }

  // 닫기 버튼 클릭 이벤트
  if (closeBtn) {
    closeBtn.addEventListener("click", function () {
      if (searchInput) searchInput.value = "";
      if (searchResults) searchResults.style.display = "none";
      closeBtn.style.display = "none";
      resetSelection();
      isNavigating = false; // ✅ 추가됨
      isKeyboardNavigation = false;
    });
  }

  // 검색창 외부 클릭 시 결과 숨기기
  document.addEventListener("click", function (event) {
    if (
      searchInput &&
      searchResults &&
      closeBtn &&
      !searchInput.contains(event.target) &&
      !searchResults.contains(event.target) &&
      event.target !== closeBtn
    ) {
      searchResults.style.display = "none";
      resetSelection();
      isNavigating = false; // ✅ 추가됨
      isKeyboardNavigation = false;
    }
  });
});

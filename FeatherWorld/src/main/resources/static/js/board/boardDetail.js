// 1. #boardLike가 클릭 되었을 때
document.querySelector("#boardLike").addEventListener("mouseup", (e) => {
  // 2. 로그인 상태가 아닌 경우 동작 X
  if (loginMemberNo == null) {
    alert("로그인 후 이용해주세요");
    return;
  }

  const obj = {
    memberNo: loginMemberNo,
    boardNo: boardNo,
    likeCheck: likeCheck,
  };

  fetch("/board/like", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(obj),
  })
    .then((resp) => resp.text())
    .then((count) => {
      if (count == -1) {
        console.log("좋아요 처리 실패");
        return;
      }

      likeCheck = likeCheck == 0 ? 1 : 0;
      e.target.classList.toggle("fa-regular");
      e.target.classList.toggle("fa-solid");
      e.target.nextElementSibling.innerText = count;
    });
});

// 개선된 뒤로가기 버튼 처리 - 히스토리 체크 방식
document.addEventListener("DOMContentLoaded", () => {
  const boardDetailBackBtn = document.querySelector(".back-button");

  if (boardDetailBackBtn) {
    boardDetailBackBtn.addEventListener("click", () => {
      const referrer = document.referrer;
      const queryString = location.search;

      console.log("이전 페이지:", referrer);
      console.log("히스토리 길이:", history.length);

      // 1. 수정 페이지에서 온 경우 - 바로 목록이나 미니홈으로 이동
      if (referrer && referrer.includes("/update")) {
        console.log("수정 페이지에서 돌아옴");

        // 세션 스토리지에서 수정 전 출발지 확인
        const beforeUpdatePage = sessionStorage.getItem("beforeUpdatePage");

        if (beforeUpdatePage) {
          console.log("수정 전 출발지로 이동:", beforeUpdatePage);
          sessionStorage.removeItem("beforeUpdatePage"); // 사용 후 제거
          location.href = beforeUpdatePage;
          return;
        }

        // 세션 스토리지에 정보가 없으면 히스토리로 판단
        // 히스토리가 2보다 크면 (현재페이지 + 수정페이지 + 원래페이지) 2단계 뒤로
        if (history.length > 2) {
          console.log("히스토리 2단계 뒤로 이동");
          history.go(-2); // 2단계 뒤로 가기
          return;
        }
      }

      // 2. 미니홈에서 온 경우
      if (referrer && referrer.includes("/minihome")) {
        console.log("미니홈에서 돌아옴");
        const minihomeMatch = referrer.match(/\/(\d+)\/minihome/);
        if (minihomeMatch) {
          const targetMemberNo = minihomeMatch[1];
          location.href = `/${targetMemberNo}/minihome`;
          return;
        }
      }

      // 3. 게시판 목록에서 온 경우
      if (
        referrer &&
        referrer.includes(`/${memberNo}/board/${boardCode}`) &&
        !referrer.includes("/minihome")
      ) {
        console.log("게시판 목록에서 돌아옴");
        location.href = `/${memberNo}/board/${boardCode}${queryString}`;
        return;
      }

      // 4. 기본 동작
      console.log("기본 동작 - 게시판 목록으로 이동");
      location.href = `/${memberNo}/board/${boardCode}${queryString}`;
    });
  }
});

// 수정 버튼 처리 - 출발지 정보 저장
document.addEventListener("DOMContentLoaded", () => {
  const boardDetailEditBtn = document.querySelector("#updateBtn");

  if (boardDetailEditBtn) {
    boardDetailEditBtn.addEventListener("click", () => {
      // 현재 페이지의 출발지 정보를 세션 스토리지에 저장
      const referrer = document.referrer;

      console.log("수정 버튼 클릭 - 출발지 저장:", referrer);

      if (referrer && referrer.includes("/minihome")) {
        // 미니홈에서 온 경우
        const minihomeMatch = referrer.match(/\/(\d+)\/minihome/);
        if (minihomeMatch) {
          const targetMemberNo = minihomeMatch[1];
          const minihomePage = `/${targetMemberNo}/minihome`;
          sessionStorage.setItem("beforeUpdatePage", minihomePage);
          console.log("미니홈 출발지 저장:", minihomePage);
        }
      } else {
        // 게시판 목록에서 온 경우
        const queryString = location.search;
        const boardListPage = `/${memberNo}/board/${boardCode}${queryString}`;
        sessionStorage.setItem("beforeUpdatePage", boardListPage);
        console.log("게시판 목록 출발지 저장:", boardListPage);
      }

      const queryString = location.search;
      location.href = `/${memberNo}/board/${boardCode}/${boardNo}/update${queryString}`;
    });
  }
});

// 삭제 버튼 처리
document.addEventListener("DOMContentLoaded", () => {
  const boardDeleteBtn = document.querySelector("#deleteBtn");

  if (boardDeleteBtn) {
    boardDeleteBtn.addEventListener("click", async () => {
      if (!confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
        return;
      }

      try {
        const resp = await fetch(
          `/${memberNo}/board/${boardCode}/${boardNo}/delete`,
          {
            method: "DELETE",
          }
        );
        const result = await resp.text();

        if (result == 0) {
          alert("게시글 삭제 실패");
          return;
        }

        alert("게시글을 성공적으로 삭제했습니다!");

        // 삭제 후 출발지 확인해서 이동
        const beforeUpdatePage = sessionStorage.getItem("beforeUpdatePage");
        if (beforeUpdatePage) {
          console.log("삭제 후 출발지로 이동:", beforeUpdatePage);
          sessionStorage.removeItem("beforeUpdatePage");
          location.href = beforeUpdatePage;
        } else {
          // 기본적으로 게시판 목록으로 이동
          const queryString = location.search;
          location.href = `/${memberNo}/board/${boardCode}${queryString}`;
        }
      } catch (error) {
        console.error("삭제 중 오류 발생:", error);
        alert("게시글 삭제 중 오류가 발생했습니다.");
      }
    });
  }
});

// 게시글 작성자의 프로필, 이름 누르면 해당 멤버 홈피 이동
document.addEventListener("DOMContentLoaded", () => {
  const writerNo = memberNo;
  const memberImg = document.querySelectorAll(".board-writer img");
  const writerName = document.querySelector(".board-writer span");

  const goToMiniHome = () => {
    if (writerNo) {
      location.href = `/${writerNo}/minihome`;
    }
  };

  memberImg.forEach((img) => {
    img.style.cursor = "pointer";
    img.addEventListener("click", goToMiniHome);
  });

  if (writerName) {
    writerName.style.cursor = "pointer";
    writerName.addEventListener("click", goToMiniHome);
  }
});

// 페이지 로드 시 디버깅 정보 출력
document.addEventListener("DOMContentLoaded", () => {
  console.log("=== 페이지 로드 정보 ===");
  console.log("현재 URL:", location.href);
  console.log("이전 페이지:", document.referrer);
  console.log(
    "세션 스토리지 - beforeUpdatePage:",
    sessionStorage.getItem("beforeUpdatePage")
  );
  console.log("히스토리 길이:", history.length);
  console.log("=====================");
});

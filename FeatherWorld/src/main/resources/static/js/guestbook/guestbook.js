// 현재 페이지(cp)값을 기록함
const recodeCp = (page) => {
  const url = new URL(location);
  url.searchParams.set("cp", String(page));
  history.pushState({}, "", url);
};

// 현재 url의 cp값을 검색
const searchCp = () => {
  const urlParams = new URLSearchParams(location.search);
  return parseInt(urlParams.get("cp")) || 1;
};

let currentPage = searchCp();

// 방명록 목록을 서버에서 조회해서 화면에 렌더링하는 함수
const selectGuestBookList = (cp = 1) => {
  currentPage = cp; // 현재 페이지(cp). 현재는 고정값 1. (나중에 페이징 처리용으로 수정 가능)

  // 로그인한 사용자 정보 가져오기 (최상단으로 이동)
  const loginMemberNo = document.querySelector("#loginMemberNo")?.value || null;

  // 방명록 주인 번호(ownerNo) 가져옴. 없으면 기본값 1
  const ownerNo = document.querySelector("#ownerNo")?.value || 1;

  // const cp = 1; // 현재 페이지(cp). 현재는 고정값 1. (나중에 페이징 처리용으로 수정 가능)

  // 서버에 방명록 리스트 요청 (비동기 fetch)
  fetch(`/${ownerNo}/guestbook/list?cp=${cp}`) // (05.23 배령 수정)
    .then((resp) => resp.json()) // 응답을 JSON으로 변환
    .then((response) => {
      const guestBookList = response.guestBookList; // 방명록 목록 배열
      const pagination = response.pagination;

      const container = document.querySelector("#guestbook-list"); // 방명록이 들어갈 컨테이너 (05.23 배령 수정)
      container.innerHTML = ""; // 기존 목록 초기화 (덮어쓰기)

      // 방명록이 없으면 안내 문구 출력 후 종료
      if (!Array.isArray(guestBookList) || guestBookList.length === 0) {
        container.innerHTML = "<p>등록된 방명록이 없습니다.</p>";
        return;
      }

      // 05.26 배령 수정
      // 방명록이 있을 경우, 각각의 방명록 데이터를 순회하며 DOM 요소 생성
      guestBookList.forEach((item) => {
        // 🔒 비밀글인데, 홈피 주인도 아니고 작성자도 아니면 랜더링 x
        const isSecret = item.secret == 1;
        const isWriter = String(loginMemberNo) === String(item.visitorNo);
        const isOwner = String(loginMemberNo) === String(ownerNo);
        const canView = isWriter || isOwner;

        // ✅ 비밀글이고 열람 권한이 없으면 return (렌더링 아예 안 함)
        if (isSecret && !canView) {
          return;
        }

        const itemDiv = document.createElement("div");
        itemDiv.className = "guestbook-item"; // 전체 방명록 한 개의 최상위 div

        const wrapDiv = document.createElement("div");
        wrapDiv.className = "guestbook-wrap"; // 내부 컨텐츠를 감싸는 wrapper

        const mainDiv = document.createElement("div");
        mainDiv.className = "guestbook-main"; // 실제 내용 부분을 감싸는 div

        // 프로필 이미지
        const profileImg = document.createElement("img");
        profileImg.className = "guestbook-writer-img";

        if (
          item.visitor?.memberImg == null ||
          item.visitor.memberImg.trim() === ""
        ) {
          profileImg.src = userDefaultImage;
        } else {
          profileImg.src = item.visitor.memberImg;
        }

        // 프로필 이미지 누르면 해당 회원 홈피로!
        profileImg.style.cursor = "pointer";

        profileImg.addEventListener("click", () => {
          window.location.href = `/${item.visitorNo}/minihome`;
        });

        // mainDiv.prepend(profileImg); // 0529 작성자를 프로필사진 옆으로 이동시키려 주석!

        // // 아이콘 생성 (비밀글일 경우)
        // if (item.secret == 1) {
        //   const lockIcon = document.createElement("i");
        //   lockIcon.className = "fa-solid fa-lock"; // FontAwesome 자물쇠 아이콘
        //   lockIcon.style.marginRight = "8px"; // 오른쪽 간격 조절
        //   mainDiv.appendChild(lockIcon);
        // }

        // 방명록 모달창 관련!!
        // 🔽 본문 출력용 wrapper (2줄 제한 + ...더보기)
        const contentWrapper = document.createElement("div");
        contentWrapper.className = "guestbook-content-wrapper";

        const contentDiv = document.createElement("div");
        contentDiv.className = "guestbook-content";
        contentDiv.dataset.full = item.guestBookContent; // 전체 텍스트 저장
        contentDiv.textContent = item.guestBookContent; // 첫 2줄까지만 보여질 것임

        contentWrapper.appendChild(contentDiv);
        mainDiv.appendChild(contentWrapper); // ✅ mainDiv에 contentWrapper 삽입
        wrapDiv.appendChild(mainDiv); // wrap 안에 main 삽입
        itemDiv.appendChild(wrapDiv); // item 안에 wrap 삽입

        // 방명록 모달창 관련!
        // 💡 2줄 초과인 경우만 '...더보기'를 적용할 클래스 추가
        requestAnimationFrame(() => {
          const lineHeight = parseFloat(
            getComputedStyle(contentDiv).lineHeight
          );
          const lines = Math.round(contentDiv.scrollHeight / lineHeight);

          if (lines > 2) {
            // ✅ 더보기 버튼 생성
            const moreBtn = document.createElement("span");
            moreBtn.className = "more-button";
            moreBtn.textContent = "...더보기";
            moreBtn.style.color = "#9f2120";
            moreBtn.style.cursor = "pointer";
            moreBtn.style.marginLeft = "6px";
            moreBtn.style.fontWeight = "bold";
            moreBtn.style.fontSize = "0.7em";

            // ✅ 버튼 클릭 시 모달 오픈
            moreBtn.addEventListener("click", (e) => {
              e.stopPropagation(); // 혹시나 버블링 방지
              const fullText = contentDiv.dataset.full;
              document.querySelector("#modalContentText").textContent =
                fullText;
              document.querySelector("#guestbookModal").style.display = "flex";
            });

            contentWrapper.appendChild(moreBtn);
          }
        });

        const infoDiv = document.createElement("div");
        infoDiv.className = "guestbook-info"; // 작성자 및 날짜 정보 영역

        const writerSpan = document.createElement("a");
        writerSpan.textContent = item.visitor?.memberName || "익명"; // 작성자 이름 (없으면 '익명')

        writerSpan.style.cursor = "pointer";
        writerSpan.style.fontWeight = "bold";
        writerSpan.style.textDecoration = "none";
        writerSpan.style.color = "#333";
        writerSpan.style.fontSize = "14px";

        // 작성자 누르면 홈페이지 이동
        writerSpan.addEventListener("click", () => {
          window.location.href = `/${item.visitorNo}/minihome`;
        });

        // 🔽 여기에 아래 코드 추가 🔽 // 0529 작성자를 프로필사진 옆으로!
        const writerBox = document.createElement("div");
        writerBox.className = "guestbook-writer-box";
        writerBox.style.display = "flex";
        writerBox.style.alignItems = "center";
        writerBox.style.gap = "13px";
        writerBox.style.marginBottom = "6px";
        writerBox.style.position = "relative";

        // [💡 이미지 wrapper]
        const imgWrapper = document.createElement("div");
        imgWrapper.style.position = "relative";
        imgWrapper.style.width = "20px";
        imgWrapper.style.height = "20px";

        // [💡 자물쇠 아이콘 위치]
        if (item.secret == 1) {
          const lockIcon = document.createElement("i");
          lockIcon.className = "fa-solid fa-lock";
          lockIcon.style.position = "absolute";
          lockIcon.style.top = "-10px";
          lockIcon.style.left = "-10px";
          lockIcon.style.fontSize = "14px";
          lockIcon.style.color = "#9f2120";
          imgWrapper.appendChild(lockIcon);
        }

        imgWrapper.appendChild(profileImg);

        writerBox.appendChild(imgWrapper);
        writerBox.appendChild(writerSpan);

        mainDiv.prepend(writerBox);

        const dateDiv = document.createElement("div");
        dateDiv.className = "guestbook-date"; // 작성일 표시 영역
        dateDiv.textContent = item.guestBookWriteDate; // 작성일 삽입

        // 작성자, 작성일을 info 영역에 추가
        // infoDiv.appendChild(writerSpan); // 0529 작성자를 프로필사진 옆으로 이동시키려 주석!
        infoDiv.appendChild(dateDiv);

        // 전체 item div에 info 추가
        itemDiv.appendChild(infoDiv);

        // 최종적으로 guestbook-list 영역에 추가
        container.appendChild(itemDiv);

        // 작성자 번호와 로그인한 사용자가 같을 경우에만 버튼 표시
        // 이거 근데 기존 edit, delete 버튼이랑 다르게 나오네;;;
        if (isWriter || isOwner) {
          const actionDiv = document.createElement("div");
          actionDiv.className = "guestbook-actions";

          if (isWriter) {
            const editBtn = document.createElement("button");
            editBtn.textContent = "Edit";
            editBtn.addEventListener("click", () =>
              showUpdateGuestBook(item.guestBookNo, editBtn)
            );
            actionDiv.appendChild(editBtn);
          }
          const deleteBtn = document.createElement("button");
          deleteBtn.textContent = "Delete";
          deleteBtn.addEventListener("click", () =>
            deleteGuestBook(item.guestBookNo)
          );

          actionDiv.appendChild(deleteBtn);
          infoDiv.appendChild(actionDiv);
        }

        // 전체 item div에 info 추가
        itemDiv.appendChild(infoDiv);

        // 최종적으로 guestbook-list 영역에 추가 (중복 제거)
        container.appendChild(itemDiv);
      });

      // 페이지네이션 렌더링
      if (pagination) renderPagination(pagination);

      applyModalViewer();
    })
    .catch((err) => {
      console.error("방명록 목록 조회 실패:", err); // 요청 실패 시 콘솔 출력
    });
};

// 방명록 등록 (ajax)
document.addEventListener("DOMContentLoaded", () => {
  selectGuestBookList();
  const guestBookContent = document.querySelector("#guestBookContent");
  const addGuestBook = document.querySelector("#addGuestBook");

  // 최대 150자 입력 제한
  guestBookContent.addEventListener("input", (e) => {
    const len = e.target.value.length;

    if (e.target.value.length > 150) {
      alert("방명록 내용은 최대 150자까지 작성할 수 있습니다.");
      e.target.value = e.target.value.slice(0, 150);
    }
  });

  // [비밀글 토글 관련 요소] DOMContentLoaded 안에서 가져오기
  const lockIcon = document.querySelector("#lockIcon");
  const toggleBtn = document.querySelector("#toggleSecret");
  const secretCheck = document.querySelector("#secretCheck");

  toggleBtn.addEventListener("click", () => {
    secretCheck.checked = !secretCheck.checked;

    // 🔒 좌물쇠 아이콘 전환
    lockIcon.classList.remove("fa-lock", "fa-lock-open");
    lockIcon.classList.add(secretCheck.checked ? "fa-lock" : "fa-lock-open");

    // 🔄 토글 아이콘 방향 전환 (ON = 오른쪽 = 비밀글 O)
    toggleBtn.classList.remove("fa-toggle-on", "fa-toggle-off");
    toggleBtn.classList.add(
      secretCheck.checked ? "fa-toggle-on" : "fa-toggle-off"
    );
  });

  addGuestBook.addEventListener("click", () => {
    const loginMemberNo =
      document.querySelector("#loginMemberNo")?.value || null;
    const ownerNo = document.querySelector("#ownerNo")?.value || 1;
    // 방명록 등록 버튼 클릭 시
    if (loginMemberNo === null) {
      alert("로그인 후 작성 가능합니다.");
      return;
    }
    // 댓글 내용이 작성되지 않은 경우(textarea 비우고 눌렀을 때)
    if (guestBookContent.value.trim().length === 0) {
      alert("내용 작성 후 등록 버튼 클릭해주세요");
      guestBookContent.focus();
      return;
    }

    //ajax를 이용해 방명록 등록 요청
    const data = {
      guestBookContent: guestBookContent.value,
      visitorNo: loginMemberNo,
      secret: document.querySelector("#secretCheck").checked ? 1 : 0,
    };
    fetch(`/${ownerNo}/guestbook`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    })
      .then((resp) => resp.json())
      .then((result) => {
        if (result < 0) {
          alert("방명록 등록 실패");
        } else {
          alert("방명록이 등록되었습니다.");
          selectGuestBookList(); // 방명록 목록을 다시 조회해서 화면에 출력
          guestBookContent.value = ""; // textarea에 작성한 방명록 내용 지우기
        }
      })
      .catch((err) => console.log("에러 발생:", err));
  });
});

//방명록 삭제 ( ajax)
const deleteGuestBook = (guestBookNo) => {
  const ownerNo = document.querySelector("#ownerNo")?.value || 1;
  //취소 선택 시
  if (!confirm("삭제 하시겠습니까?")) return;

  fetch(`/${ownerNo}/guestbook`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ guestBookNo }),
  })
    .then((resp) => resp.json()) // json()으로 받아 result.success를 명확히 확인해야 함
    .then((result) => {
      console.log("삭제 응답:", result); // 응답 형식 확인
      // 05.23 수정 배령
      if (result.success === true || result.success === "true") {
        alert("삭제 되었습니다");
        selectGuestBookList();
      } else {
        alert("삭제 실패");
      }
    })
    .catch((err) => {
      console.error("삭제 오류: ", err);
    });
};

//방명록 수정

// 수정 취소 시 원래 방명록 형태로 돌아가기 위한 백업 변수
let beforeGuestBookRow;

const showUpdateGuestBook = (guestBookNo, btn) => {
  const ownerNo = document.querySelector("#ownerNo")?.value || 1;
  const temp = document.querySelector(".update-textarea");

  if (temp != null) {
    if (
      confirm("수정 중인 방명록이 있습니다. 현재 방명록을 수정 하시겠습니까?")
    ) {
      const guestBookRow = temp.parentElement;
      guestBookRow.after(beforeGuestBookRow);
      guestBookRow.remove();
    } else {
      return;
    }
  }

  // div 구조에 맞게 수정 (li -> div)
  const guestBookRow = btn.closest(".guestbook-item");
  beforeGuestBookRow = guestBookRow.cloneNode(true);
  const beforeContent =
    guestBookRow.querySelector(".guestbook-content").innerText;

  guestBookRow.innerHTML = "";

  const textarea = document.createElement("textarea");
  textarea.classList.add("update-textarea");
  textarea.value = beforeContent;
  guestBookRow.append(textarea);

  const btnArea = document.createElement("div");
  btnArea.classList.add("guestbook-btn-area");

  const updateBtn = document.createElement("button");
  updateBtn.innerText = "Edit";
  updateBtn.addEventListener("click", () =>
    updateGuestBook(guestBookNo, updateBtn)
  );

  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "Cancel";
  cancelBtn.addEventListener("click", () => cancelGuestBookUpdate(cancelBtn));

  btnArea.append(updateBtn, cancelBtn);
  guestBookRow.append(btnArea);
};

const cancelGuestBookUpdate = (btn) => {
  if (confirm("수정을 취소하시겠습니까?")) {
    const guestBookRow = btn.closest(".guestbook-item");
    guestBookRow.after(beforeGuestBookRow);
    guestBookRow.remove();
    selectGuestBookList(searchCp());
  }
};

const updateGuestBook = (guestBookNo, btn) => {
  const textarea = btn.parentElement.previousElementSibling;

  if (textarea.value.trim().length === 0) {
    alert("내용 작성 후 수정 버튼을 클릭해주세요.");
    textarea.focus();
    return;
  }

  const ownerNo = document.querySelector("#ownerNo")?.value || 1;

  const data = {
    guestBookNo: guestBookNo,
    guestBookContent: textarea.value,
  };

  fetch(`/${ownerNo}/guestbook`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (parseInt(result) > 0) {
        alert("수정되었습니다.");
      } else {
        alert("수정 실패");
      }
      selectGuestBookList(searchCp()); // 현재 페이지 다시 조회
    });
};

// 페이지네이션 렌더링 함수
const renderPagination = (pagination) => {
  const paginationContainer = document.querySelector("#guestbook-pagination");
  if (!paginationContainer) return;

  paginationContainer.innerHTML = "";

  /** 각각의 페이징 목록을 생성하고, 페이지 변경 click 이벤트 부여
   * @param {number} page 페이지 번호(cp)
   * @param {String} text innerText 내용
   * @param {string} className 클래스명
   * @returns {HTMLSpanElement} span 태그
   */
  const createPageSpan = (page, text, className = "") => {
    const span = document.createElement("span");
    span.innerText = text;
    span.dataset.page = String(page);
    if (className) span.classList.add(className);

    // 페이지 클릭 이벤트 추가 (올바른 페이지 번호 사용)
    span.addEventListener("click", () => {
      recodeCp(page);
      selectGuestBookList(page); // 클릭한 페이지로 이동
    });

    return span;
  };

  const paginationDiv = document.createElement("div");
  paginationDiv.classList.add("pagination");

  // << 첫 페이지
  paginationDiv.append(createPageSpan(1, "<<"));

  // < 이전 페이지
  paginationDiv.append(createPageSpan(pagination.prevPage, "<", "page-nav"));

  // 페이지 번호 목록
  for (let i = pagination.startPage; i <= pagination.endPage; i++) {
    const span = createPageSpan(i, i);
    if (i === pagination.currentPage) {
      span.classList.add("current");
    }
    paginationDiv.append(span);
  }

  // > 다음 페이지
  paginationDiv.append(createPageSpan(pagination.nextPage, ">", "page-nav"));

  // >> 마지막 페이지
  paginationDiv.append(createPageSpan(pagination.maxPage, ">>"));

  paginationContainer.append(paginationDiv);
};

// 사용하지 않는 createBoardFooter 함수 제거

// 브라우저에서 뒤로가기 버튼을 눌렀을 경우(뒤로 가기)
window.addEventListener("popstate", () => {
  selectGuestBookList(searchCp());
});

selectGuestBookList(searchCp());

//-----------------------
// 방명록 모달창!!!

const applyModalViewer = () => {
  // 모달이 없으면 만들기
  if (!document.querySelector("#guestbookModal")) {
    const modal = document.createElement("div");
    modal.id = "guestbookModal";
    modal.style = `
      position: fixed;
      top: 0; left: 0;
      width: 100%; height: 100%;
      background-color: rgba(0,0,0,0.6);
      display: none;
      align-items: center;
      justify-content: center;
      z-index: 9999;
    `;

    const contentBox = document.createElement("div");
    contentBox.style = `
      background: #f9f9f9;
      padding: 20px;
      border-radius: 12px;
      max-width: 600px;
      width: 90%;
      max-height: 80vh;
      overflow-y: auto;
      white-space: pre-wrap;
    `;

    const contentText = document.createElement("div");
    contentText.id = "modalContentText";
    contentText.style.fontSize = "16px";
    contentText.style.wordBreak = "break-word";
    contentText.style.overflowWrap = "break-word";

    const closeWrapper = document.createElement("div");
    closeWrapper.style = `
  text-align: right;
  margin-top: 15px;
`;

    const closeBtn = document.createElement("button");
    closeBtn.textContent = "닫기";
    closeBtn.style = `
      margin-top: 15px;
      background-color: #9f2120;
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: 8px;
      cursor: pointer;
    `;

    closeBtn.addEventListener("click", () => {
      modal.style.display = "none";
    });

    contentBox.append(contentText, closeBtn);
    contentBox.append(closeWrapper);
    closeWrapper.appendChild(closeBtn);
    modal.appendChild(contentBox);
    document.body.appendChild(modal);
  }

  // 더보기 클릭 처리
  // document.querySelectorAll(".guestbook-content").forEach((contentDiv) => {
  //   contentDiv.addEventListener("click", () => {
  //     const fullText = contentDiv.dataset.full;
  //     document.querySelector("#modalContentText").textContent = fullText;
  //     document.querySelector("#guestbookModal").style.display = "flex";
  //   });
  // });
};

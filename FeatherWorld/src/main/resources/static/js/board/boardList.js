// 현재 게시판 종류 번호를 저장하는 boardCode 변수 선언
let boardCode = currentBoardCode;

// 좌측 사이드 바
const leftSidebar = document.querySelector(".left-sidebar");

// 수정 모드 flag
let editMode = false;

/** 현재 선택한 페이지(cp)를 url에 반영 (history에 저장)
 * @author Jiho
 * @param {number} page
 */
const recodeCp = (page) => {
  const url = new URL(location);
  url.searchParams.set("cp", String(page));
  history.pushState({}, "", url);
};

/** 현재 url에 cp가 존재한다면 cp, 없다면 null 반환
 * @author Jiho
 */
const searchCp = () => {
  // URL 에서 cp 파라미터 추출
  const urlParams = new URLSearchParams(location.search);
  return parseInt(urlParams.get("cp")) || null;
};

/** 특정 class를 가진 div 태그 생성
 * @author Jiho
 * @param {string} className 클래스명
 * @param {string} text innerText 내용
 * @returns {HTMLDivElement} div 태그
 */
const createDiv = (className = "", text = "") => {
  const div = document.createElement("div");
  if (className) div.classList.add(className);
  if (text) div.innerText = text;

  return div;
};

/** 페이징 목록/글쓰기 버튼 포함 div 생성 메서드
 * @author Jiho
 * @param pagination Pagination 객체
 * @return 페이징 목록/글쓰기 버튼 포함 div
 */
const createBoardFooter = (pagination) => {
  /** 각각의 페이징 목록을 생성하고, 페이지 변경 click 이벤트 부여
   * @author Jiho
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

    // 페이지 클릭 이벤트 추가
    span.addEventListener("click", async () => {
      // 변경된 cp값 적용/history 저장
      recodeCp(page);
      // 해당 페이지에 맞게 게시글/페이징 목록 갱신
      renderBoardList(boardCode, page).catch(console.error);
    });

    return span;
  };

  const containerDiv = document.createElement("div");

  if (pagination) {
    // 임시 페이징 목록 div
    const updatedPagination = document.createElement("div");
    updatedPagination.classList.add("pagination");

    // << 첫 페이지
    updatedPagination.append(createPageSpan(1, "<<"));

    // < 이전 페이지
    updatedPagination.append(
      createPageSpan(pagination.prevPage, "<", "page-nav")
    );

    // 페이지 번호 목록
    for (let i = pagination.startPage; i <= pagination.endPage; i++) {
      const span = createPageSpan(i, i);
      if (i === pagination.currentPage) {
        span.classList.add("current");
      }
      updatedPagination.append(span);
    }

    // > 다음 페이지
    updatedPagination.append(
      createPageSpan(pagination.nextPage, ">", "page-nav")
    );

    // >> 마지막 페이지
    updatedPagination.append(createPageSpan(pagination.maxPage, ">>"));

    containerDiv.append(updatedPagination);
  }

  // 본인 게시판일 경우만
  if(loginMemberNo === memberNo) {
    // 글쓰기 버튼 추가
    const writeDiv = createDiv("write-button");
    const writeSpan = document.createElement("span");
    writeSpan.innerText = "Write";
    writeDiv.append(writeSpan);

    // 쓰기 버튼 클릭시 동기식 페이지 전환
    writeSpan.addEventListener("click", () => {
      if (boardCode === 0) {
        alert("존재하지 않는 게시판입니다.");
        return;
      }

      location.href = `/${memberNo}/board/${boardCode}/write`;
    });

    containerDiv.append(writeDiv);
  }

  return containerDiv;
};

/** 게시판 갱신시 갱신된 게시글/페이징 목록 렌더링
 * @author Jiho
 * @param {number} boardType 게시판 종류 번호
 * @param page 페이지 번호(cp)
 */
const renderBoardList = async (boardType, page) => {
  // 중앙 게시글 목록 div 생성
  const mainContent = createDiv("main-content");
  const updatedBoardContainer = createDiv("board-list");

  // page 값에 따라 요청 변경
  let queryString;
  if (page == null) queryString = "";
  else queryString = `?cp=${page}`;

  // ajax를 통해 비동기로 회원별 게시판 목록 각 요소 클릭시 boardList & pagination 구해옴
  const resp = await fetch(`/board/${boardType}${queryString}`);
  const map = await resp.json();

  // map 내부에 있는 pagination & boardList 선언
  const pagination = map.pagination;
  const boardList = map.boardList;

  // 게시글이 없는 경우
  if (boardList == null) {
    const span = document.createElement("span");
    span.innerText = "게시글이 존재하지 않습니다.";

    updatedBoardContainer.append(span);
    mainContent.append(updatedBoardContainer);

    mainContent.append(createBoardFooter(pagination));

    document.querySelector(".main-content").replaceWith(mainContent);

    return;
  }

  // 비동기로 가져온 게시글 내용을 꺼내서 html 요소로 대입
  for (const board of boardList) {
    // 게시글 하나를 담는 div 생성
    const boardItem = createDiv("board-item");

    // 게시글 썸네일과 게시글 제목/내용을 담는 묶음 div 생성
    const boardWrap = createDiv("board-wrap");

    // 게시글 썸네일이 있을 경우에만 썸네일 이미지가 들어간 div 넣어줌
    if (board.thumbnail) {
      const boardThumbnail = createDiv("board-thumbnail");

      const thumbnailImg = document.createElement("img");
      thumbnailImg.src = board.thumbnail;
      thumbnailImg.alt = "thumbnail";

      boardThumbnail.append(thumbnailImg);
      boardWrap.append(boardThumbnail);
    }

    // 게시글 제목/내용을 담은 div 생성
    const boardMain = createDiv("board-main");

    boardMain.append(createDiv("board-title", board.boardTitle));
    boardMain.append(createDiv("board-content", board.boardContent));

    boardWrap.append(boardMain);

    // 게시글 작성일/조회수를 담는 묶음 div 생성
    const boardInfo = createDiv("board-info");

    // 게시글 작성일 담은 div 생성, 추가
    boardInfo.append(createDiv("board-date", board.boardWriteDate));

    // 게시글 조회수를 담은 div 생성
    const boardReads = createDiv("board-reads");

    const iconRead = document.createElement("span");
    iconRead.classList.add("fa-solid", "fa-book-open-reader");
    const readCount = document.createElement("span");
    readCount.innerText = board.readCount;

    boardReads.append(iconRead, readCount);
    boardInfo.append(boardReads);

    // 게시글 하나에 모든 내용 넣기
    boardItem.append(boardWrap, boardInfo);

    boardItem.addEventListener("click", () => {
      const cp = searchCp();

      // cp 값에 따라 요청 변경
      const queryString = cp ? `?cp=${cp}` : "";

      location.href = `/${memberNo}/board/${boardCode}/${board.boardNo}${queryString}`;
    });

    // 게시글 목록 div에 게시글 하나 넣기
    updatedBoardContainer.append(boardItem);
  }

  // 게시글 목록 추가
  mainContent.append(updatedBoardContainer);

  mainContent.append(createBoardFooter(pagination));

  // 기존 중앙 컨텐츠 영역 교체
  document.querySelector(".main-content").replaceWith(mainContent);
};

/** 현재 url을 통해 현재 선택된 게시판 & 페이지로 게시글 목록 불러오기
 * @author Jiho
 * @param boardType 게시판 종류
 */
const loadBoardList = (boardType) => {
  // cp 값으로 렌더링 (새로고침 가능)
  const cp = searchCp();
  renderBoardList(boardType, cp).catch(console.error);
};

/** 게시판 목록 갱신시 갱신된 게시판 목록 렌더링
 * @author Jiho
 */
const renderBoardTypeList = async () => {
  const resp = await fetch(`/${memberNo}/board/select`);
  const boardTypeList = await resp.json();

  const boardTypeSidebar = createDiv("board-type-sidebar");

  const div = document.createElement("div");
  const titleSpan = document.createElement("span");
  titleSpan.innerText = "Board Type";
  titleSpan.classList.add("board-type-title");

  div.append(titleSpan);

  if (loginMemberNo === memberNo) {
    const editBtn = document.createElement("span");
    editBtn.classList.add("edit-button");
    editBtn.innerText = "edit";
    const editIcon = document.createElement("i");
    editIcon.classList.add("fa-solid", "fa-pen-to-square");

    editBtn.append(editIcon);
    div.append(editBtn);
  }

  boardTypeSidebar.append(div);

  boardTypeList.forEach(boardType => {
    if (boardType.authority === 0 || (boardType.authority === 1 && loginMemberNo === memberNo)) {
      const boardTypeItem = document.createElement("div");
      boardTypeItem.classList.add("board-type-item");
      boardTypeItem.dataset.boardCode = boardType.boardCode;
      // 현재 게시판인 경우, selected 클래스 추가
      if(boardType.boardCode === currentBoardCode) boardTypeItem.classList.add("selected");

      const boardTypeTitle = document.createElement("span");
      boardTypeTitle.classList.add("board-type-title");
      boardTypeTitle.innerText = boardType.boardName;

      // 기본 게시판에는 수정 허용 안됨!
      if(boardType.boardCode !== defaultBoardCode) {
        const iconSpan = document.createElement("span");
        iconSpan.classList.add("icon-wrap");

        const editIcon = document.createElement("span");
        editIcon.classList.add("edit-icon");
        const pencilIcon = document.createElement("span");
        editIcon.classList.add("fa-solid", "fa-pencil");

        editIcon.append(pencilIcon);

        const deleteIcon = document.createElement("span");
        deleteIcon.classList.add("delete-icon");
        const trashIcon = document.createElement("span");
        trashIcon.classList.add("fa-solid", "fa-trash");

        deleteIcon.append(trashIcon);

        iconSpan.append(editIcon, deleteIcon);
        
        if(boardType.authority === 1) {
          const lock = document.createElement("span");
          lock.className = "fa-solid fa-lock";
          
          boardTypeItem.append(lock, boardTypeTitle, iconSpan);
          
        } else {
          boardTypeItem.append(boardTypeTitle, iconSpan);
        }

      } else {
        boardTypeItem.append(boardTypeTitle);
      }

      boardTypeSidebar.append(boardTypeItem);
    }
  });

  document.querySelector(".board-type-sidebar").replaceWith(boardTypeSidebar);

  updateEditIcons();
};

const loadBoardTypeList = () => {
  renderBoardTypeList().catch(console.error);

  if(loginMemberNo === memberNo) {
    leftSidebar.append(createAddFolder());
  }
}

/** 폴더 추가 버튼 생성
 * @author Jiho
 * @returns {HTMLDivElement}
 */
const createAddFolder = () => {
  const div = createDiv("add-folder", "Add Folder");
  const span = document.createElement("span");
  span.classList.add("fa-solid", "fa-folder-plus");

  div.append(span);

  return div;
};

/** 게시판 추가 시 나타나는 폼 요소 생성
 * @author Jiho
 * @returns {HTMLDivElement}
 */
const createFolderForm = () => {
  const folderForm = createDiv("add-folder-form");

  const folderTitleInput = document.createElement("input");
  folderTitleInput.classList.add("add-folder-input");
  folderTitleInput.name = "boardName";
  folderTitleInput.placeholder = "Input Folder Title";

  const folderFormFooter = createDiv("folder-form-footer");

  // 푸터 좌측 권한 설정 부분
  const leftTempDiv = document.createElement("div");

  const lockSpan = document.createElement("span");
  lockSpan.classList.add("fa-solid", "fa-lock-open");

  const toggleAuthority = createDiv("toggle-authority");
  const toggleDiv = document.createElement("div");
  toggleAuthority.append(toggleDiv);

  leftTempDiv.append(lockSpan, toggleAuthority);

  // 푸터 우측 취소, 확정 부분
  const rightTempDiv = document.createElement("div");

  const cancel = document.createElement("span");
  cancel.classList.add("cancel-add-folder");
  cancel.innerText = "Cancel";

  const confirm = document.createElement("span");
  confirm.classList.add("confirm-add-folder");
  confirm.innerText = "Confirm";

  rightTempDiv.append(cancel, confirm);

  const authority = document.createElement("input");
  authority.type = "hidden";
  authority.name = "authority";
  authority.value = "0";
  authority.required = true;

  toggleAuthority.addEventListener("click", () => {
    toggleDiv.style.left = toggleDiv.style.left === "" ? "15px" : "";

    if (lockSpan.classList.contains("fa-lock-open")) {
      lockSpan.classList.replace("fa-lock-open", "fa-lock");
    } else {
      lockSpan.classList.replace("fa-lock", "fa-lock-open");
    }

    authority.value = authority.value === "0" ? "1" : "0";
  });

  folderFormFooter.append(leftTempDiv, rightTempDiv);

  folderForm.append(folderTitleInput, folderFormFooter, authority);

  return folderForm;
}

// 게시판 수정, 삭제 아이콘 가시성 업데이트
const updateEditIcons = () => {
  const iconSpan = document.querySelectorAll(".board-type-item > .icon-wrap");
  iconSpan.forEach(icon => {
    icon.style.display = editMode ? "inline-block" : "none";
  });
}

if (leftSidebar) {

  leftSidebar.addEventListener("click", async (e) => {

    // edit 버튼
    const editBtn = e.target.closest(".edit-button");
    if(editBtn) {
      // 수정모드 전환
      editMode = !editMode;

      const iconSpan = document.querySelectorAll(
          ".board-type-item > .icon-wrap"
      );

      updateEditIcons();
      return;
    }

    // 연필 아이콘 클릭시
    const editIcon = e.target.closest(".edit-icon");
    if(editMode && editIcon) {
      
      // 선택한 게시판
      const parentBoardItem = editIcon.closest(".board-type-item");
      // 선택한 게시판 종류 번호
      const editBoardCode = parentBoardItem.dataset.boardCode;
      // 게시판 제목
      const boardTitle = parentBoardItem.querySelector(".board-type-title");
      
      // 게시판 이름 수정 & 게시판 목록 갱신
      const editFn = async (e, input) => {
        
        // 클릭, 엔터 입력시에만 수정 진행
        if (e.type === "click" || (e.type === "keydown" && e.key === "Enter")) {
          
          if (input.value.trim() === "") {
            alert("게시판 이름을 입력하세요.");
            return;
          }
          
          if (input.value.length > 10) {
            alert("게시판 이름은 10자 이내로 작성해주세요.");
            input.focus();
            return;
          }
          
          const folderObj = {
            boardCode: editBoardCode,
            boardName: input.value
          };
          
          const resp = await fetch(`/${memberNo}/board/update`, {
            method: "put",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(folderObj)
          });
          const result = await resp.text();
          
          if (result == 0) alert("게시판 수정 실패");
          
          renderBoardTypeList().catch(console.error);
        }
      }
      
      // 게시글 제목 span이 존재하는 경우
      if(boardTitle) {
        const input = document.createElement("input");
        input.name = "boardName";
        input.size = 10;
        input.value = boardTitle.innerText;
        
        editIcon.addEventListener("click", e => editFn(e, input));
        input.addEventListener("keydown", e => editFn(e, input));
        
        boardTitle.replaceWith(input);
        input.focus();
        
        return;
      }

      return;
    }

    // 쓰레기통 아이콘 클릭시
    const deleteIcon = e.target.closest(".delete-icon");
    if(editMode && deleteIcon) {

      const deleteBoardCode = deleteIcon.parentElement.parentElement.dataset.boardCode;

      // 삭제되는 게시판이 기본 게시판인 경우
      if(deleteBoardCode == defaultBoardCode) {
        alert("기본 게시판은 삭제할 수 없습니다.");
        return;
      }

      const resp = await fetch(`/${memberNo}/board/delete`, {
        method: "delete",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ boardCode: deleteBoardCode })
      });
      const result = await resp.text();

      if(result == 0) {
        alert("게시판 삭제 실패");
        return;
      }

      alert("게시판을 삭제했습니다.");

      renderBoardTypeList().catch(console.error);

      updateEditIcons();
      return;
    }

    // 좌측 게시판 목록 선택
    const boardTypeItem = e.target.closest(".board-type-item");
    if (boardTypeItem) {
      // 기존 선택된 항목에서 selected 클래스 제거
      document.querySelector(".board-type-item.selected")?.classList.remove("selected");
      
      // 현재 클릭한 항목에 selected 클래스 추가
      boardTypeItem.classList.add("selected");
      
      // 현재 게시판 종류 번호 갱신
      boardCode = boardTypeItem.dataset.boardCode;

      // boardCode 반영해 url 갱신
      history.pushState({}, '', boardCode);

      // 게시글 목록 갱신
      renderBoardList(boardCode, null).catch(console.error);

      return;
    }

    // 폴더 추가 버튼 선택
    const addFolder = e.target.closest(".add-folder");
    if(addFolder) {
      const addFolderForm = createFolderForm();

      leftSidebar.append(addFolderForm);

      addFolderForm.querySelector(".add-folder-input").focus();
      addFolder.remove();

      return;
    }

    // 폴더 추가 취소 버튼
    const cancelAddFolder = e.target.closest(".cancel-add-folder");
    if(cancelAddFolder) {
      document.querySelector(".add-folder-form")?.remove();
      leftSidebar.append(createAddFolder());

      return;
    }
    
    // 폴더 추가 확정 버튼
    const confirmAddFolder = e.target.closest(".confirm-add-folder");
    if(confirmAddFolder) {
      const boardName = document.querySelector("input[name='boardName']").value;
      if (boardName.trim().length === 0) {
        alert("게시판 이름을 작성해주세요.");
        document.querySelector(".add-folder-input").focus();
        return;
      }
      
      if (boardName.length > 10) {
        alert("게시판 이름은 10자 이내로 작성해주세요.");
        document.querySelector(".add-folder-input").focus();
        return;
      }

      const authority = document.querySelector("input[name='authority']").value;

      const folderObj = {
        boardName: boardName,
        authority: authority
      };

      // 입력한 값들을 모두 ajax로 비동기 요청
      const resp = await fetch(`/${memberNo}/board/insert`, {
        method: "post",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(folderObj),
      });
      const result = await resp.text();

      if(result == 0) {
        alert("게시판 추가 실패");
        return;
      }

      alert("새로운 게시판이 생성되었습니다.");

      // 현재 세션 갱신 후 화면에 게시판 목록 렌더링
      renderBoardTypeList().catch(console.error);
      editMode = false;

      document.querySelector(".add-folder-form")?.remove();
      leftSidebar.append(createAddFolder());
    }
  });
}

// 뒤로가기 실행 시
window.addEventListener("popstate", () => {
  // 현재 게시판 종류 번호를 이전 게시판 종류 번호로 바꿈
  // 페이지에 맞게 게시글 목록을 다시 불러옴
  boardCode = location.pathname.split("/")[3];
  loadBoardList(boardCode);
});

document.addEventListener("DOMContentLoaded", () => {
  loadBoardTypeList();
  loadBoardList(currentBoardCode);
});
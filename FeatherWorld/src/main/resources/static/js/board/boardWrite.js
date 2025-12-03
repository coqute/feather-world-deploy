// 이미지 업로드 관련 변수
let imageList = []; // 업로드된 이미지들을 저장할 배열

// 이미지 업로드 리스트 최대 갯수
const MAX_LENGTH = 5;

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', () => {
  initializeImageUpload();
  initializeBackButton();
});

// 이미지 업로드 초기화
const initializeImageUpload = () => {
  const imageSlots = document.querySelectorAll('.image-slot');
  const hiddenFileInput = document.getElementById('imageUpload');

  imageSlots.forEach((slot, index) => {
    // 각 이미지 슬롯에 클릭 이벤트 추가
    slot.addEventListener('click', () => {
      if (!slot.classList.contains('has-image')) {
        // 빈 슬롯 클릭 시 파일 선택 창 열기
        hiddenFileInput.dataset.slotIndex = index;
        hiddenFileInput.click();
      }
    });
    // 각 이미지 슬롯에 드래그 앤 드롭 이벤트 추가
    slot.addEventListener('dragover', (e) => {
      e.preventDefault();
      slot.classList.add('drag-over');
    });

    slot.addEventListener('dragleave', () => {
      slot.classList.remove('drag-over');
    });

    slot.addEventListener('drop', (e) => {
      e.preventDefault();
      slot.classList.remove('drag-over');

      const file = e.dataTransfer.files[0];

      if (file && file.type.startsWith('image/')) {
        addImageToSlot(file, index);
      } else {
        alert("이미지 파일만 업로드할 수 있습니다.");
      }
    });

    // 각 슬롯에 삭제 버튼 생성
    const removeBtn = document.createElement('button');
    removeBtn.className = 'remove-btn';
    removeBtn.innerHTML = '×';
    removeBtn.type = 'button';
    removeBtn.addEventListener('click', e => {
      // 상위 요소에 이벤트 전달 막음
      e.stopPropagation();
      removeImage(index);
    });
    slot.appendChild(removeBtn);
  });

  // 파일 선택 시 이벤트 처리
  hiddenFileInput.addEventListener('change', e => {
    const file = e.target.files[0];
    const slotIndex = parseInt(e.target.dataset.slotIndex);

    if (file && file.type.startsWith('image/')) {
      addImageToSlot(file, slotIndex);
    }

    // input 초기화
    e.target.value = '';
    delete e.target.dataset.slotIndex;
  });
}

// 이미지를 슬롯에 추가
const addImageToSlot = (file, slotIndex) => {
  const slot = document.querySelectorAll('.image-slot')[slotIndex];
  const reader = new FileReader();

  reader.onload = e => {
    // 기존 placeholder 제거
    const placeholder = slot.querySelector('.placeholder');
    if (placeholder) {
      placeholder.remove();
    }

    // 이미지 엘리먼트 생성
    const img = document.createElement('img');
    img.src = e.target.result;
    img.alt = 'Uploaded image';

    // 슬롯에 이미지 추가
    slot.appendChild(img);
    slot.classList.add('has-image');

    // 업로드된 이미지 배열에 저장
    imageList[slotIndex] = {
      file: file,
      dataUrl: e.target.result
    };

    console.log(`이미지가 슬롯 ${slotIndex + 1}에 추가되었습니다:`, file.name);
  };

  reader.readAsDataURL(file);
}

// 이미지 제거
const removeImage = slotIndex => {
  const slot = document.querySelectorAll('.image-slot')[slotIndex];

  // 이미지 엘리먼트 제거
  const img = slot.querySelector('img');
  if (img) {
    img.remove();
  }

  // has-image 클래스 제거
  slot.classList.remove('has-image');

  // placeholder 다시 추가
  if (!slot.querySelector('.placeholder')) {
    const placeholder = document.createElement('span');
    placeholder.className = 'placeholder';
    placeholder.innerHTML = '✕';
    slot.appendChild(placeholder);
  }

  // 업로드된 이미지 배열에서 제거
  delete imageList[slotIndex];

  console.log(`슬롯 ${slotIndex + 1}의 이미지가 제거되었습니다.`);
}

// 뒤로가기 버튼 초기화
const initializeBackButton = () => {
  const backButton = document.querySelector('.back-button span');
  if (backButton) {
    backButton.addEventListener('click', () => {
      if (confirm('작성 중인 내용이 사라집니다. 정말 뒤로 가시겠습니까?')) {
        window.history.back();
      }
    });
  }
}

// 폼 제출 시 이미지 데이터 처리
const handleFormSubmit = () => {
  const form = document.querySelector('.board-form');

  if (form) {
    // 폼 데이터 수집
    const formData = new FormData();
    const title = document.querySelector('input[name="boardTitle"]').value;
    const content = document.querySelector('textarea[name="boardContent"]').value;

    if(title.trim().length === 0 || content.trim().length === 0) {
      alert("제목과 내용을 모두 작성해주세요.");
      return;
    }
    
    // 기본 데이터 추가
    formData.append('boardTitle', title);
    formData.append('boardContent', content);
    formData.append('boardCode', currentBoardCode);

    // 이미지 파일들 추가 (비어있는 경우는 빈 File로 - index를 활용하기 위해)
    for (let i = 0; i < MAX_LENGTH; i++) {
      const imageData = imageList[i];
      if (imageData && imageData.file) {
        formData.append('images', imageData.file);
      } else {
        formData.append('images', new File([""], "empty.jpg", { type: "image/jpg" }));
      }
    }
    
    // 서버로 전송될 formData 내부 요소들
    console.log('폼 제출 데이터:', {
      title: title,
      content: content,
      boardCode: currentBoardCode,
      imageList: imageList
    });

    fetch(`/${memberNo}/board/${currentBoardCode}/insert`, {
      method: 'POST',
      body: formData
    })
    .then(resp => resp.text())
    .then(boardNo => {
      // fetch 요청으로 받게 될 게시글 작성 성공 여부
      // 성공시(DB에 삽입된 boardNo - 게시글 번호 반환)
      // 실패시(0 반환)

      // 게시글 작성 실패시 다시 게시글 작성으로 이동
      if(boardNo == 0) {
        alert("게시글 작성 실패");
        return;
      }
      // 게시글 작성 성공시 게시글 상세 조회로 이동
      alert('게시글이 성공적으로 작성되었습니다!');
      // 상대 경로로 작성
      // == memberNo/board/boardCode/insert -> memberNo/board/boardCode/boardNo
      location.replace(boardNo);
    });

  }
}

// 취소 버튼 이벤트
document.addEventListener('DOMContentLoaded', () => {
  const cancelBtn = document.querySelector('.btn-cancel');
  if (cancelBtn) {
    cancelBtn.addEventListener('click', function() {
      if (confirm('작성 중인 내용이 모두 삭제됩니다. 정말 취소하시겠습니까?')) {
        // 폼 초기화
        document.querySelector('.board-form').reset();

        // 이미지 슬롯 초기화
        document.querySelectorAll('.image-slot').forEach((slot, index) => {
          if (slot.classList.contains('has-image')) {
            removeImage(index);
          }
        });

        // 업로드된 이미지 배열 초기화
        imageList = [];

      }
    });
  }
});

// 확인 버튼 이벤트
document.addEventListener('DOMContentLoaded', () => {
  const confirmBtn = document.querySelector('.btn-confirm');
  if (confirmBtn) {
    confirmBtn.addEventListener('click', () => {

      if(loginMemberNo === 0) {
        alert("로그인 후 이용해주세요.");
        return;

      } else if(loginMemberNo !== memberNo) {
        alert("본인 게시판만 작성 가능합니다.");
        return;
      }

      handleFormSubmit()
    });
  }
});
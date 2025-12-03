// miniHome.js - 미니홈 관련 JavaScript 함수들

// 일촌평 작성 모달 열기
function openCommentModal() {
  console.log("모달 열기 클릭됨");
  document.getElementById("commentModal").style.display = "flex";
  document.getElementById("commentTextarea").focus();
}

// 일촌평 작성 모달 닫기
function closeCommentModal() {
  document.getElementById("commentModal").style.display = "none";
  document.getElementById("commentTextarea").value = "";
  document.getElementById("charCount").textContent = "0";
}

// 글자 수 카운트
document.addEventListener("DOMContentLoaded", function () {
  const textarea = document.getElementById("commentTextarea");
  const charCount = document.getElementById("charCount");

  if (textarea && charCount) {
    textarea.addEventListener("input", function (e) {
      charCount.textContent = e.target.value.length;
    });
  }
});

// 일촌평 작성 제출
function submitComment() {
  const comment = document.getElementById("commentTextarea").value.trim();

  if (!comment) {
    alert("일촌평을 입력해주세요.");
    return;
  }

  if (comment.length > 30) {
    alert("일촌평은 30자 이내로 작성해주세요.");
    return;
  }

  console.log("일촌평 작성 요청:", {
    memberNo: memberNo,
    comment: comment,
  });

  // AJAX 요청으로 일촌평 저장
  fetch(`/${memberNo}/ilchoncomment`, {
    method: "post",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      toMemberNo: memberNo,
      ilchonCommentContent: comment,
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        alert("일촌평이 작성되었습니다.");
        closeCommentModal();
        location.reload(); // 페이지 새로고침
      } else {
        alert(data.message || "일촌평 작성에 실패했습니다.");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("일촌평 작성 중 오류가 발생했습니다.");
    });
}

// 수정된 일촌평 삭제 함수
function deleteComment(actualAuthorNo) {
  if (!confirm("일촌평을 삭제하시겠습니까?")) {
    return;
  }

  console.log("삭제 요청:", {
    authorNo: actualAuthorNo,
    pageOwnerNo: memberNo,
  });

  fetch(`/${memberNo}/ilchoncomment`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      authorNo: actualAuthorNo, // 실제 작성자 번호만 전달
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        alert("일촌평이 삭제되었습니다.");
        location.reload();
      } else {
        alert(data.message || "일촌평 삭제에 실패했습니다.");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("일촌평 삭제 중 오류가 발생했습니다.");
    });
}

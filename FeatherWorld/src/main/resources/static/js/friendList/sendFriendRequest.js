// 좌측 게시판 목록 선택
const confirmBtn = document.querySelector(".btn-confirm");
const submitNickname = document.querySelector(".form-control");
/*
confirmBtn.addEventListener("click", () => {
  const memberNo = document.body.dataset.memberNo;
  fetch("/insert/newFriend", {
    method: "POST", // ← POST로 바꿔야 body 사용 가능
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      memberNo: parseInt(memberNo),
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      if (data.status == 1) {
        console.log("toNickname 수정 성공!");
      } else if (data.status == 0) {
        console.log("수정 실패!");
      } else {
        console.log("수정 실패!");
      }
    });
});*/

document.getElementById("back-btn").addEventListener("click", () => {
  history.back(); // 또는 history.go(-1);
});

// 미수락 팔로워 신청 보기
function showPendingFollowers() {
  window.location.href = `/${memberNo}/friendList/incoming`; // 일촌 신청 목록 페이지로 이동
}
const sendFriendRequestButton = document.getElementById(
  "send-friend-request-button"
);

if (sendFriendRequestButton) {
  sendFriendRequestButton.addEventListener("click", () => {
    window.location.href = `/${memberNo}/newFriend/input`;
  });
}
/**일촌명 수정 성공시 뜨는 초록색 V아이콘 */
const checkIcon = (friend) => {
  // 매개변수로 현재 일촌<span> 전달
  const icon = friend.querySelector(".check-icon");
  icon.classList.remove("hidden");

  setTimeout(() => {
    icon.classList.add("hidden");
  }, 1500);
};
/**일촌명 수정 실패시 뜨는 빨간색 V아이콘 */
const xIcon = (friend) => {
  // 매개변수로 현재 일촌<span> 전달
  const icon = friend.querySelector(".x-icon");
  icon.classList.remove("hidden");

  setTimeout(() => {
    icon.classList.add("hidden");
  }, 1500);
};
// DB 내부 일촌테이블 DELETE 전용 애니메이션 함수(fadeOut과는 반대방향으로 밀어버림) +  안에서 해당 요소를 DOM에서 제거.
const deleteFade = (friend) => {
  return new Promise((resolve) => {
    friend.classList.add("deleting");

    // transition 완료 후 remove()
    friend.addEventListener(
      "transitionend",
      () => {
        friend.remove();
        resolve();
      },
      { once: true }
    );
  });
};
const changeTitleForSec = (title, str) => {
  return new Promise((resolve) => {
    if (title) {
      const savedText = title.innerText;
      title.innerText = str; // string
      setTimeout(() => {
        title.innerText = savedText;
        resolve();
      }, 1000);
    } else {
      console.log("title이 null 입니다!");
      resolve();
    }
  });
};
//buttons
let editBtn = document.getElementById("edit-button"); // edit
let applyCancelBtnDiv = document.getElementById("apply-cancel-button-div"); //apply-cancel btn 을 담는 div
let cancelBtn = document.getElementById("cancel-button");
let sendFriendReqBtn = document.getElementById("send-friend-request-button");
let friendSpans = document.querySelectorAll(".friend-item");

let currPagination = parseInt(document.getElementById("curr-pagination").value);
function isItemExists(item) {
  // item : html 내부의 요소 List(querySelectorAll로 얻어온 요소 List)

  return item.length != 0 ? true : false;
}
function updateModel(cp) {
  // "{memberNo:[0-9]+}/friendList/incoming" 에서 model만 업데이트
  //for pagination
  console.log("cp: ", cp);
  return fetch(`/${memberNo}/friendList?cp=${cp}`, {
    headers: {
      "X-Requested-With": "XMLHttpRequest",
    },
  })
    .then((res) => {
      if (!res.ok) {
        throw new Error("요청 실패: " + res.status);
      }
      return res.text();
    })
    .then((html) => {
      //console.log("html response:", html);
      // 응답받은 HTML을 파싱해서 DOM으로 변환
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, "text/html");

      //console.log("doc: ", doc.body.innerText);
      return doc;
    });
}
function resetEditCancelBtn() {
  // 비동기로 update한 friend-item 과 Edit, Cancel, 일촌신청 버튼 전체 다시 연동.

  editBtn = document.getElementById("edit-button"); // edit
  applyCancelBtnDiv = document.getElementById("apply-cancel-button-div"); //apply-cancel btn 을 담는 div
  /*const applyBtn = document.getElementById("apply-button");*/
  cancelBtn = document.getElementById("cancel-button");
  sendFriendReqBtn = document.getElementById("send-friend-request-button");
  friendSpans = document.querySelectorAll(".friend-item");

  currPagination = parseInt(document.getElementById("curr-pagination").value);
  //1
  if (editBtn) {
    editBtn.addEventListener("click", (e) => {
      console.log("editBtn clicked!");
      applyCancelBtnDiv.classList.remove("hidden");
      /*applyBtn.classList.remove("hidden");*/
      cancelBtn.classList.remove("hidden");
      e.target.classList.add("hidden");

      friendSpans.forEach(function (friend) {
        friend.querySelector("[name=fromNickname]").classList.add("hidden");
        friend.querySelector("[name=ilchon-button]").classList.add("hidden");
        friend
          .querySelector("[name=fromNickname-input]")
          .classList.remove("hidden");
        friend.querySelector("[name=fromNickname-input]").value =
          friend.querySelector("[name=fromNickname]").innerText;
        friend
          .querySelector("[name=unfollow-button]")
          .classList.remove("hidden");
        /* 첫 요소밖에 뜨지않는 이슈로 잠시 주석처리해둠.
        //unfollow 요청 서버로 보내기
        friend.querySelector("[name=unfollow-button]").addEventListener(() => {
          // unfollow 비동기 요청
        });*/
      });
    });
  }

  //2
  if (cancelBtn) {
    cancelBtn.addEventListener("click", (e) => {
      editBtn.classList.remove("hidden");
      applyCancelBtnDiv.classList.add("hidden");
      /*applyBtn.classList.add("hidden");*/
      e.target.classList.add("hidden");

      friendSpans.forEach(function (friend) {
        friend.querySelector("[name=fromNickname]").classList.remove("hidden");
        friend.querySelector("[name=ilchon-button]").classList.remove("hidden");
        friend
          .querySelector("[name=fromNickname-input]")
          .classList.add("hidden");
        friend.querySelector("[name=unfollow-button]").classList.add("hidden");
      });
    });
  }

  //3
  friendSpans.forEach(function (friend) {
    const profileImg = friend.querySelector(".friend-profile");
    const profileName = friend.querySelector(".friend-name");
    if (profileImg) {
      profileImg.style.cursor = "pointer";
      profileImg.addEventListener("click", () => {
        window.location.href = `/${friend.dataset.memberNo}/minihome`;
      });
    }
    if (profileName) {
      profileImg.style.cursor = "pointer";
      profileName.addEventListener("click", () => {
        window.location.href = `/${friend.dataset.memberNo}/minihome`;
      });
    }
  });
  friendSpans.forEach(function (friend) {
    friend
      .querySelector("[name=fromNickname-input]")
      .addEventListener("change", (e) => {
        // 1. fetch
        const newNickName = e.target.value;
        console.log(e.target.value);
        fetch("/update/nickname", {
          method: "POST", // ← POST로 바꿔야 body 사용 가능
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            memberNo: parseInt(friend.dataset.memberNo),
            nickname: newNickName,
          }), // TO_NICKNAME/FROM_NICKNAME 판별 여부는 서버측에서 판단
        })
          .then((response) => response.json())
          .then((data) => {
            console.log(data);
            if (data.status == 2) {
              console.log("toNickname 수정 성공!");
              e.target.value = data.Ilchon.toNickname;
              if (friend) {
                friend.querySelector("[name=fromNickname]").textContent =
                  data.Ilchon.toNickname;
              } //refresh
              checkIcon(friend);
            } else if (data.status == 1) {
              console.log("fromNickname 수정 성공!");
              e.target.value = data.Ilchon.fromNickname;
              if (friend) {
                friend.querySelector("[name=fromNickname]").textContent =
                  data.Ilchon.fromNickname;
              } //refresh
              checkIcon(friend);
            } else {
              console.log("수정 실패!");
              xIcon(friend);
            }
          });
      });

    friend // unfollow 버튼 누른후 이벤트 핸들러 설정(accept 버튼 누른후 fetch문을 그대로 복사해서 필요한 부분만 바꾼 코드!!)
      .querySelector("[name=unfollow-button]")
      .addEventListener("click", (e) => {
        if (confirm("정말 unfollow하시겟습니까?")) {
          // 1. fetch
          const newNickName = e.target.value;
          console.log(e.target.value); // DEBUG용이므로 지우셔도 됩니다
          fetch("/delete", {
            method: "POST", // ← POST로 바꿔야 body 사용 가능

            headers: {
              "Content-Type": "application/json",
            },

            body: JSON.stringify({
              memberNo: parseInt(friend.dataset.memberNo),
            }),
          })
            .then((response) => response.json())
            .then(async (data) => {
              console.log(data); // DEBUG용이므로 지우셔도 됩니다
              if (data.status == 1) {
                console.log("삭제 성공!");
                /*e.target.value = data.Ilchon.toNickname;
              if (friend) {
                friend.querySelector("[name=fromNickname]").textContent =
                  data.Ilchon.toNickname;
              } //refresh*/

                await checkIcon(friend);
                await deleteFade(friend);
                let newItems = null;
                console.log(
                  isItemExists(document.querySelectorAll(".friend-item"))
                );
                if (
                  // 현재 페이지네이션에 유저가 존재하거나, 현재 page가 1일떄.
                  isItemExists(document.querySelectorAll(".friend-item")) ||
                  currPagination === 1
                ) {
                  console.log(
                    ".friend-item:",
                    isItemExists(document.querySelectorAll(".friend-item"))
                  );
                  console.log(currPagination);
                  console.log("if 분기 작동");
                  newItems = await updateModel(currPagination);
                } else {
                  // 현재 페이지네이션에 더이상 아무런 유저가 존재하지 않을때(현재 page가 1일떄는 제외).
                  // -> 대신 이전 Pagination의 정보를 가져옴
                  console.log("else 분기 작동");
                  newItems = await updateModel(currPagination - 1);
                }
                const currentBlock = document.querySelector(".main-content");
                //console.log(newItems);
                // 현재 내용을 실시간 업데이트된 model안의 정보로 페이지에 show.
                if (currentBlock) {
                  currentBlock.innerHTML =
                    newItems.body.firstElementChild.outerHTML;
                  resetEditCancelBtn();
                }
              } else if (data.status == 0) {
                console.log("삭제 실패! 0");
                /* e.target.value = data.Ilchon.fromNickname;
              if (friend) {
                friend.querySelector("[name=fromNickname]").textContent =
                  data.Ilchon.fromNickname;
              } //refresh*/
                checkIcon(friend);
              } else {
                console.log("삭제 실패! -1");
                xIcon(friend);
              }
            })
            .catch((err) => {
              console.error(err);
            });
        }
      });
  });
}
resetEditCancelBtn();

async function updateFriendList(cp) {
  //for pagination
  try {
    const doc = await updateModel(cp);
    const currentBlock = document.querySelector(".main-content");

    if (currentBlock) {
      currentBlock.innerHTML = doc.body.firstElementChild.outerHTML;
      resetEditCancelBtn();
    }
  } catch (err) {
    console.error("fragment 갱신 실패:", err);
  }
}
/*
if (applyBtn) {
  applyBtn.addEventListener("click", () => {
    // 1. fetch (or submit?)

    // 2. 각 요소들이 삽입되면 다시 th:block만 refresh 하거나 현재 페이지로 redirect(pagination 유지할것)
  });
}*/

//각 friendSpans의 <textarea>안 change이벤트 발생시 submit하는 이벤트 핸들러 지정

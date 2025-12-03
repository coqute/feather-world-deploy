/**************AUTHOR: 안준성
 * html 문서와 마찬가지로 friendListPart.js를 복붙한 코드라 변수명이 대개 일치합니다..**************** */

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
/*friend를 왼쪽으로 밀어버리는 애니메이션 동작함수 + 해당 요소를 DOM에서 제거*/
const fadeOut = (friend) => {
  return new Promise((resolve) => {
    friend.classList.add("removing");

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
/* changeTitleForSec()
  title : 동작을 수행할 웹페이지상 text 요소
  str : 바꿀 내용
*/
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

/********************여기까지 주요 기능 이외 함수******************** */

//buttons
let editBtn = document.getElementById("edit-button"); // edit
let applyCancelBtnDiv = document.getElementById("apply-cancel-button-div"); //apply-cancel btn 을 담는 div
let applyBtn = document.getElementById("apply-button");
let cancelBtn = document.getElementById("cancel-button");
let sendFriendReqBtn = document.getElementById("send-friend-request-button");
let friendSpans = document.querySelectorAll(".friend-item");
let friendSendedSpans = document.querySelectorAll(".friend-item-sended");

let currPagination = parseInt(document.getElementById("curr-pagination").value);
let currPaginationFrom = parseInt(
  document.getElementById("curr-pagination-from").value
);

document.getElementById("to-friendList-btn").addEventListener("click", () => {
  window.location.href = `/${memberNo}/friendList`;
});

friendSpans.forEach(function (friend) {
  console.log(friend);
});

function isItemExists(item) {
  // item : html 내부의 요소 List(querySelectorAll로 얻어온 요소 List)

  return item.length != 0 ? true : false;
}
function updateModel(cp, cpFrom) {
  // "{memberNo:[0-9]+}/friendList/incoming" 에서 model만 업데이트
  //for pagination
  console.log("cp: ", cp, "cpFrom:", cpFrom);
  return fetch(`/${memberNo}/friendList/incoming?cp=${cp}&cpFrom=${cpFrom}`, {
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
  // 비동기로 update한 friend-item 과 Edit, Cancel, 일촌신청 버튼 다시 연동.

  editBtn = document.getElementById("edit-button"); // edit
  applyCancelBtnDiv = document.getElementById("apply-cancel-button-div"); //apply-cancel btn 을 담는 div
  /*const applyBtn = document.getElementById("apply-button");*/
  cancelBtn = document.getElementById("cancel-button");
  sendFriendReqBtn = document.getElementById("send-friend-request-button");
  friendSpans = document.querySelectorAll(".friend-item");
  friendSendedSpans = document.querySelectorAll(".friend-item-sended");

  currPagination = parseInt(document.getElementById("curr-pagination").value);
  currPaginationFrom = parseInt(
    document.getElementById("curr-pagination-from").value
  );
  //1. 현재 일촌 리스트로 돌아가기 버튼 click 이벤트 다시 추가

  document.getElementById("to-friendList-btn").addEventListener("click", () => {
    window.location.href = `/${memberNo}/friendList`;
  });
  //3 // (내가 일촌신청을 받은사람 한정)프로필img / 이름 클릭시 헤당 user의 minihome으로
  friendSpans.forEach(function (friend) {
    const profileImg = friend.querySelector(".friend-profile");
    const profileName = friend.querySelector(".friend-name");
    profileImg.style.cursor = "pointer";
    if (profileImg) {
      profileImg.addEventListener("click", () => {
        window.location.href = `/${friend.dataset.memberNo}/minihome`;
      });
    }
    profileName.style.cursor = "pointer";
    if (profileName) {
      profileName.addEventListener("click", () => {
        window.location.href = `/${friend.dataset.memberNo}/minihome`;
      });
    }
  });
  //3 // (내가 일촌신청을 보낸사람 한정)프로필img / 이름 클릭시 헤당 user의 minihome으로
  friendSendedSpans.forEach(function (friend) {
    const profileImg2 = friend.querySelector(".toFriendImg");
    const profileName2 = friend.querySelector("#toFriendName");
    profileImg2.style.cursor = "pointer";
    if (profileImg2) {
      profileImg2.addEventListener("click", () => {
        window.location.href = `/${friend.dataset.memberNo}/minihome`;
      });
    }
    profileName2.style.cursor = "pointer";
    if (profileName2) {
      profileName2.addEventListener("click", () => {
        window.location.href = `/${friend.dataset.memberNo}/minihome`;
      });
    }
  });

  friendSpans?.forEach(function (friend) {
    //accept 버튼을 누르면 일촌명 지정을 위해 textarea의 hidden속성 제거
    if (friend) {
      const acceptButton = friend.querySelector("[name=accept-button]");
      const fromNicknameInput = friend.querySelector(
        "[name=fromNickname-input]"
      );
      acceptButton?.addEventListener("click", (e) => {
        console.log("accept button clicked!");
        // 1. fetch
        const newNickName = friend.querySelector(
          "[name=fromNickname-input]"
        ).value; // input 내부 text를 newNickname 변수에 저장
        console.log(fromNicknameInput.value); // DEBUG용이므로 지우셔도 됩니다
        fetch("/update/accept", {
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

          .then(async (data) => {
            if (data.status == 2 || data.status == 1) {
              // 2 : toNickname  수정성공, 1 : fromNickname 수정성공
              console.log(
                data.status == 2
                  ? "toNickname 수정 성공!"
                  : "fromNickname 수정 성공!"
              );

              await fadeOut(friend);

              //model 업데이트 후 다시 items 갱신
              console.log(currPagination, "+", currPaginationFrom);
              // 현재 페이지네이션에 유저가 존재하거나, 현재 page가 1일떄.
              let newItems = null;
              if (
                isItemExists(document.querySelectorAll(".friend-item")) ||
                currPagination == 1
              ) {
                console.log("if 분기 작동");
                newItems = await updateModel(
                  currPagination,
                  currPaginationFrom
                );
              } else {
                // 현재 페이지네이션에 더이상 아무런 유저가 존재하지 않을때(현재 page가 1일떄는 제외).
                // -> 대신 이전 Pagination의 정보를 가져옴
                console.log("else 분기 작동");
                newItems = await updateModel(
                  currPagination - 1,
                  currPaginationFrom
                );
              }
              const currentBlock = document.querySelector(".main-content");
              console.log(newItems);
              if (currentBlock) {
                currentBlock.innerHTML =
                  newItems.body.firstElementChild.outerHTML;
                resetEditCancelBtn();
              }
              await changeTitleForSec(
                document.getElementById("incoming-title"),
                "새 일촌을 수락했습니다!! 잘부탁드려요!"
              );
            } else {
              console.log("수정 실패!");
              xIcon(friend);
              return Promise.reject("닉네임 수정 실패");
            }
          })
          .catch((err) => {
            console.error(err);
          });

        //
      });
      //accept 버튼 누른후 nickname 수정요청 + is_ilchon = 'Y'로 변경
      fromNicknameInput?.addEventListener("input", (e) => {
        if (e.target.value.trim() !== "") {
          friend
            .querySelector("[name=accept-button]")
            .classList.remove("disabled-button"); // accept 버튼 활성화
          friend
            .querySelector("[name=accept-button]")
            .classList.add("hover-green"); // accept 버튼 활성화
        } else {
          // input의 내용이 다시 비게 되었을때
          friend
            .querySelector("[name=accept-button]")
            .classList.add("disabled-button"); // accept 버튼 다시 비활성화
          friend
            .querySelector("[name=accept-button]")
            .classList.remove("hover-green"); // accept 버튼 다시 비활성화
        }
      });

      friend // deny 버튼 누른후 이벤트 핸들러 설정(accept 버튼 누른후 fetch문을 그대로 복사해서 필요한 부분만 바꾼 코드!!)
        .querySelector("[name=deny-button]")
        .addEventListener("click", (e) => {
          // 1. fetch
          const newNickName = e.target.value;
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
              if (data.status == 1) {
                console.log("삭제 성공!");

                await deleteFade(friend); // 일촌테이블 DELETE 전용 동작함수. 안에서 friend.remove()를 자체적으로 수행함.

                let newItems = null;
                if (
                  isItemExists(document.querySelectorAll(".friend-item")) ||
                  currPagination == 1
                ) {
                  console.log("if 분기 작동");
                  newItems = await updateModel(
                    currPagination,
                    currPaginationFrom
                  );
                } else {
                  // 현재 페이지네이션에 더이상 아무런 유저가 존재하지 않을때(현재 page가 1일떄는 제외).
                  // -> 대신 이전 Pagination의 정보를 가져옴
                  console.log("else 분기 작동");
                  newItems = await updateModel(
                    currPagination - 1,
                    currPaginationFrom
                  );
                }
                const currentBlock = document.querySelector(".main-content");
                console.log(newItems);
                // 현재 내용을 실시간 업데이트된 model안의 정보로 페이지에 show.
                if (currentBlock) {
                  currentBlock.innerHTML =
                    newItems.body.firstElementChild.outerHTML;
                  resetEditCancelBtn();
                }
                changeTitleForSec(
                  document.getElementById("incoming-title"),
                  "일촌신청을 거절했습니다."
                );
              } else if (data.status == 0) {
                console.log("삭제 실패! 0");

                checkIcon(friend);
              } else {
                console.log("삭제 실패! -1");
                xIcon(friend);
              }
            })
            .catch((err) => {
              console.error(err);
            });
        });
    }
  });

  friendSendedSpans?.forEach(function (friend) {
    if (friend) {
      friend // unfollow 버튼 누른후 이벤트 핸들러 설정(accept 버튼 누른후 fetch문을 그대로 복사해서 필요한 부분만 바꾼 코드!!)
        .querySelector("[name=unfollow-button]")
        .addEventListener("click", (e) => {
          // 1. fetch
          const newNickName = e.target.value;
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
              if (data.status == 1) {
                console.log("삭제 성공!");
                /*e.target.value = data.Ilchon.toNickname;
            if (friend) {
              friend.querySelector("[name=fromNickname]").textContent =
                data.Ilchon.toNickname;
            } //refresh*/

                await checkIcon(friend);
                await deleteFade(friend); // 일촌테이블 DELETE 전용 동작함수. 안에서 friend.remove()를 자체적으로 수행함.
                let newItems = null;
                console.log(
                  isItemExists(document.querySelectorAll(".friend-item-sended"))
                );
                if (
                  isItemExists(
                    document.querySelectorAll(".friend-item-sended")
                  ) ||
                  currPaginationFrom === 1
                ) {
                  console.log(
                    ".friend-item-sended:",
                    isItemExists(
                      document.querySelectorAll(".friend-item-sended")
                    )
                  );
                  console.log(currPaginationFrom);
                  console.log("if 분기 작동");
                  newItems = await updateModel(
                    currPagination,
                    currPaginationFrom
                  );
                } else {
                  // 현재 페이지네이션에 더이상 아무런 유저가 존재하지 않을때(현재 page가 1일떄는 제외).
                  // -> 대신 이전 Pagination의 정보를 가져옴
                  console.log("else 분기 작동");
                  newItems = await updateModel(
                    currPagination,
                    currPaginationFrom - 1
                  );
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
        });
    }
  });
}

resetEditCancelBtn();

function updateFriendListIncoming(cp, cpFrom) {
  //for pagination
  fetch(`/${memberNo}/friendList/incoming?cp=${cp}&cpFrom=${cpFrom}`, {
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
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, "text/html");
      //console.log("doc: ", doc.body.innerText);
      // 기존 DOM의 요소 교체
      const currentBlock = document.querySelector(".main-content");

      if (currentBlock) {
        currentBlock.innerHTML = doc.body.firstElementChild.outerHTML;
        resetEditCancelBtn();
      }
    })
    .catch((err) => console.error("fragment 갱신 실패:", err));
}

async function updateFriendListIncoming_refactored(cp, cpFrom) {
  //for pagination
  console.log("cp: ", cp, "cpFrom:", cpFrom);

  try {
    const doc = await updateModel(cp, cpFrom);

    //console.log("doc: ", doc.body.innerText);
    // 기존 DOM의 요소 교체
    const currentBlock = document.querySelector(".main-content");

    if (currentBlock) {
      currentBlock.innerHTML = doc.body.firstElementChild.outerHTML;
      resetEditCancelBtn();
    }
  } catch (err) {
    console.error("fragment 갱신 실패:", err);
  }
}

/************************************************250530 end of copy */
//각 friendSpans의 <textarea>안 change이벤트 발생시 submit하는 이벤트 핸들러 지정

// 미니홈피 이동 기능: 프로필 이미지 or 이름 클릭 시
/*
[...friendSpans, ...friendSendedSpans].forEach(function (friend) {
  const memberNo = friend.dataset.memberNo;

  const profileImg = friend.querySelector(".friend-profile");
  const memberName = friend.querySelector(".friend-name");

  profileImg.style.cursor = "pointer";
  if (profileImg) {
    profileImg.addEventListener("click", () => {
      location.href = `/${memberNo}/minihome`;
    });
  }

  memberName.style.cursor = "pointer";
  if (memberName) {
    memberName.addEventListener("click", () => {
      location.href = `/${memberNo}/minihome`;
    });
  }
}
);*/

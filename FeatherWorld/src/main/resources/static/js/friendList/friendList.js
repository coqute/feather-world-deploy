function updateFriendList(cp) {
  //for pagination

  fetch(`/${memberNo}/friendList?cp=${cp}`, {
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
      console.log("응답 HTML:", html);
      // 응답받은 HTML을 파싱해서 DOM으로 변환
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, "text/html");

      const newBlock = doc.querySelector('[name="friend-list"]');
      console.log("파싱된 요소:", doc.querySelector('[name="friend-list"]'));
      // 기존 DOM의 요소 교체
      const currentBlock = document.querySelector('[name="friend-list"]');
      console.log("파싱된 요소:", doc.body.firstElementChild);
      if (newBlock && currentBlock) {
        currentBlock.outerHTML = doc.body.firstElementChild.outerHTML;
      }
    })
    .catch((err) => console.error("fragment 갱신 실패:", err));
}

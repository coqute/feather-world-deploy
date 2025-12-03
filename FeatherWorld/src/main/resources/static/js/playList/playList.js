const youtubeUrl = document.getElementById("youtubeUrl");
const videoContainer = document.getElementById("videoContainer");
const confirmYouTubeBtn = document.getElementById("confirmYouTubeBtn");
const deleteYouTubeBtn = document.getElementById("deleteYouTubeBtn");

// url을 embed가능한 url로 변경하는 함수
// 구현이유 : 평범한 youtube url은 임베드 할 수 없는 형태의 url임.
// 미리보기나 DB등록에 임베드된 url이 필요해서 만들게됨
function generateEmbedUrl(url) {
  let embedUrl = ""; // 임베드가능 url 저장 변수

  // 1. playlist 링크인지 확인
  const playlistMatch = url.match(/[?&]list=([a-zA-Z0-9_-]+)/);
  // 2. 단일 영상인지 확인
  const videoMatch = url.match(
    /(?:youtube\.com\/.*[?&]v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/
  );

  if (playlistMatch && url.includes("playlist")) {
    const listId = playlistMatch[1];
    embedUrl = `https://www.youtube.com/embed/videoseries?list=${listId}`;
  } else if (videoMatch) {
    const videoId = videoMatch[1];
    embedUrl = `https://www.youtube.com/embed/${videoId}`;
  } else {
    alert("올바른 유튜브 영상 또는 재생목록 URL을 입력해주세요.");
    return;
  }

  return embedUrl;
}

// 입력창에 유튜브 링크 삽입 후 엔터 누를 시 이벤트
if (youtubeUrl) {
  youtubeUrl.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
      const url = youtubeUrl.value.trim(); // 보통 유튜브 url

      let embedUrl = generateEmbedUrl(url); // 임베드 url 로 변환

      console.log(embedUrl);

      // iframe 생성
      videoContainer.innerHTML = `
          <div class="embed-container">
            <iframe 
              src="${embedUrl}" 
              allowfullscreen 
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture">
            </iframe>
          </div>
        `;
    }
  });
}

if (confirmYouTubeBtn) {
  // 저장 버튼 클릭 이벤트
  confirmYouTubeBtn.addEventListener("click", async () => {
    // 미리보기와는 무관하게 input에 입력된 youtubeUrl로 저장함
    let embedUrl = generateEmbedUrl(youtubeUrl.value); // 임베드 url 로 변환

    if (embedUrl) {
      const resp = await fetch(`/${memberNo}/playlist/insert`, {
        method: "post",
        headers: { "Content-Type": "application/json" },
        body: embedUrl,
      });

      const result = await resp.text();

      if (result > 0) {
        alert("등록 성공");
        location.reload();
      } else {
        alert("등록 실패");
      }
    }
  });
}

if (deleteYouTubeBtn) {
  // 삭제 버튼 클릭 이벤트
  deleteYouTubeBtn.addEventListener("click", async () => {
    if (!confirm("삭제 하시겠습니까?")) return;
    const resp = await fetch(`/${memberNo}/playlist/delete`, {
      method: "delete",
    });

    const result = await resp.text();

    if (result > 0) {
      alert("삭제 성공");
      location.reload();
    } else {
      alert("삭제 실패");
    }
  });
}

const profileForm = document.getElementById("profileForm");

if (profileForm != null) {

  const imageInput = document.getElementById("uploadFile");
  const previewImg = document.getElementById("preview");
  const bioInput = document.getElementById("bio-input"); 
  const MAX_SIZE = 1024 * 1024 * 5; // 5MB

  let previousImage = previewImg.src;
  let previousFile = null;

  if (imageInput && previewImg) {
    imageInput.addEventListener("change", () => {
      const file = imageInput.files[0];
      if (file) {
        if (file.size <= MAX_SIZE) {
          // 정상 이미지 크기
          if (previousImage && previousImage.startsWith("blob:")) {
            URL.revokeObjectURL(previousImage); // 이전 Blob URL 해제
          }
          const newImageUrl = URL.createObjectURL(file);
          previewImg.src = newImageUrl;
          previousImage = newImageUrl;
          previousFile = file;
        } else {
          // 크기 초과
          alert("5MB 이하의 이미지를 선택해주세요!");
          imageInput.value = ""; // 선택 초기화
          previewImg.src = previousImage; // 이전 이미지로 복원

          if (previousFile) {
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(previousFile);
            imageInput.files = dataTransfer.files;
          }
        }
      } else {
        // 파일 선택 취소 시
        previewImg.src = previousImage;
        if (previousFile) {
          const dataTransfer = new DataTransfer();
          dataTransfer.items.add(previousFile);
          imageInput.files = dataTransfer.files;
        }
      }
    });
  }

  profileForm.addEventListener("submit", (e) => {
    if (!bioInput) {
      console.error("bio-input 요소를 찾을 수 없습니다.");
      return;
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  const updateBtn = document.querySelector(".Profile-update-button");
  const deleteBtn = document.querySelector(".Delete-account-button");

  if (updateBtn) {
    updateBtn.addEventListener("click", () => {
      window.location.href = "/profile-update"; // 실제 URL로 수정
    });
  }

  if (deleteBtn) {
    deleteBtn.addEventListener("click", () => {
      window.location.href = "profileDelete"; // 실제 URL로 수정
    });
  }
});

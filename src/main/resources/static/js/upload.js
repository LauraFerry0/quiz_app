(() => {

  const $ = (sel) => document.querySelector(sel);

  const tokenMeta = $('meta[name="_csrf"]');
  const headerMeta = $('meta[name="_csrf_header"]');

  const getCsrf = () => {
    if (!tokenMeta || !headerMeta) return null;
    return { header: headerMeta.content, token: tokenMeta.content };
  };


  document.querySelectorAll(".flip-card").forEach((card) => {
    const toggle = () => card.classList.toggle("is-flipped");

    card.addEventListener("click", (e) => {
      if (e.target.closest("input, a, button")) return;
      toggle();
    });

    card.addEventListener("keydown", (e) => {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        toggle();
      }
    });
  });


  let cropper = null;

  const modal = $("#cropperModal");
  const img = $("#imageToCrop");
  const status = $("#uploadStatus");
  const input = $("#profilePictureUpload");

  if (!modal || !img || !status || !input) return;

  const openModal = () => {
    document.body.classList.add("modal-open");
    modal.style.display = "flex";
    status.textContent = "";
  };

  const closeModal = () => {
    modal.style.display = "none";
    document.body.classList.remove("modal-open");
    status.textContent = "";

    if (cropper) {
      cropper.destroy();
      cropper = null;
    }
    input.value = "";
  };

  const initCropper = () => {
    if (cropper) cropper.destroy();

    cropper = new Cropper(img, {
      aspectRatio: 1,
      viewMode: 1,
      dragMode: "move",
      autoCropArea: 1,
      movable: true,
      zoomable: true,
      rotatable: false,
      scalable: false,
      cropBoxMovable: false,
      cropBoxResizable: false,
      guides: false,
      center: true,
      background: false,
      toggleDragModeOnDblclick: false,
    });
  };

  input.addEventListener("change", (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      img.src = reader.result;
      openModal();
      initCropper();
    };
    reader.readAsDataURL(file);
  });

  const cropAndUpload = () => {
    if (!cropper) return;

    status.textContent = "Uploading...";

    const size = 300;
    const square = cropper.getCroppedCanvas({ width: size, height: size });

    const out = document.createElement("canvas");
    out.width = size;
    out.height = size;

    const ctx = out.getContext("2d");
    ctx.beginPath();
    ctx.arc(size / 2, size / 2, size / 2, 0, Math.PI * 2);
    ctx.clip();
    ctx.drawImage(square, 0, 0);

    out.toBlob(async (blob) => {
      try {
        const csrf = getCsrf();
        if (!csrf) {
          status.textContent = "CSRF meta tags not found.";
          return;
        }

        const formData = new FormData();
        formData.append("file", blob, "avatar.png");

        const res = await fetch("/settings/profile-pictures/upload", {
          method: "POST",
          headers: { [csrf.header]: csrf.token },
          body: formData,
          credentials: "same-origin",
        });

        const text = await res.text();

        if (!res.ok) {
          status.textContent = `Upload failed (${res.status}): ${text || "No response"}`;
          return;
        }

        status.textContent = "Uploaded!";
        setTimeout(() => location.reload(), 500);
      } catch (err) {
        console.error(err);
        status.textContent = "Upload failed (network/JS error).";
      }
    }, "image/png");
  };


  window.cropAndUpload = cropAndUpload;
  window.closeCropper = closeModal;
})();

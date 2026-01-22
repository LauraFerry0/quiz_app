(() => {
  const $ = (sel) => document.querySelector(sel);

  // --- CSRF helpers (Spring-style meta tags) ---
  const tokenMeta = $('meta[name="_csrf"]');
  const headerMeta = $('meta[name="_csrf_header"]');
  const getCsrf = () =>
    tokenMeta && headerMeta ? { header: headerMeta.content, token: tokenMeta.content } : null;

  // --- DOM refs ---
  const modal = $("#cropperModal");
  const img = $("#imageToCrop");
  const status = $("#uploadStatus");
  const input = $("#profilePictureUpload");
  if (!modal || !img || !status || !input) return;

  let cropper = null;
  let clamping = false;

  // --- Modal open/close + Cropper lifecycle ---
  const openModal = () => {
    document.body.classList.add("modal-open");
    modal.style.display = "flex";
    status.textContent = "";
  };

  const destroyCropper = () => {
    if (!cropper) return;
    cropper.destroy();
    cropper = null;
  };

  const closeModal = () => {
    modal.style.display = "none";
    document.body.classList.remove("modal-open");
    status.textContent = "";
    destroyCropper();
    input.value = "";
  };

  // --- Movement constraint: allow dragging, but never show empty space above the crop circle ---
  const clampTopEdge = () => {
    if (!cropper || clamping) return;

    const canvas = cropper.getCanvasData();   // drawn image position/size
    const cropBox = cropper.getCropBoxData(); // crop circle/box position/size

    if (canvas.top > cropBox.top) {
      clamping = true;
      cropper.move(0, cropBox.top - canvas.top);
      clamping = false;
    }
  };

  // --- Create cropper on the current <img> ---
  const initCropper = () => {
    destroyCropper();

    cropper = new Cropper(img, {
      aspectRatio: 1,
      viewMode: 0, // free movement; we clamp only what we care about
      dragMode: "move",
      autoCropArea: 0.7,

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

    // Enforce clamp while the user drags
    img.addEventListener("cropmove", clampTopEdge);
  };

  // --- Load selected file into <img>, then open modal + init cropper --- //
  input.addEventListener("change", (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      img.onload = () => {
        openModal();
        initCropper();
      };
      img.src = reader.result;
    };
    reader.readAsDataURL(file);
  });

  // --- Export circular PNG and upload to server ---
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

        const res = await fetch("/settings/profile-picture", {
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

  // --- Expose button handlers to the page ---
  window.cropAndUpload = cropAndUpload;
  window.closeCropper = closeModal;
})();

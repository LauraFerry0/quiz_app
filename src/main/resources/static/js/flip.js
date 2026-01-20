 // FLIP CARDS
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
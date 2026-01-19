function startTimer(duration, display) {
    let timer = duration, seconds;
    const interval = setInterval(function () {
        seconds = parseInt(timer, 10);

        display.textContent = seconds;

        if (--timer < 0) {
            clearInterval(interval);  // Stop the timer when it reaches 0
            display.textContent = "Time's up!";

            // Disable the submit button
            const submitButton = document.querySelector("button[type='submit']");
            if (submitButton) {
                submitButton.disabled = true;
            }

            // Redirect to the leaderboard page with a message
            window.location.href = "/leaderboard?message=" + encodeURIComponent("Sorry, your time has run out.");
        }
    }, 1000);
}

// Start the countdown when the page loads
window.onload = function () {
    const timeLeft = 30;
    const display = document.querySelector('#time-left');
    startTimer(timeLeft, display);
};

if (typeof countdownSeconds !== "undefined" && countdownSeconds > 0) {
    const countdownEl = document.querySelector(".countdown-container p");
    const progressEl = document.getElementById("progress");
    let timeLeft = countdownSeconds;

    const interval = setInterval(() => {
        timeLeft--;
        countdownEl.textContent = `Retry in ${timeLeft} seconds`;
        if(progressEl) progressEl.style.width = `${(timeLeft / countdownSeconds) * 100}%`;

        if(timeLeft <= 0) {
            clearInterval(interval);
            const retryBtn = document.querySelector(".error-actions .error-btn.secondary");
            if(retryBtn) retryBtn.style.display = "inline-block";
        }
    }, 1000);
}

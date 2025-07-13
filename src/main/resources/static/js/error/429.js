  let timeLeft = 30;
const countdownEl = document.getElementById('countdown');
const progressEl = document.getElementById('progress');

const interval = setInterval(() => {
    timeLeft--;
    countdownEl.textContent = timeLeft;
    progressEl.style.width = `${(timeLeft/30)*100}%`;
    if(timeLeft <= 0) {
        clearInterval(interval);
        document.querySelector('.error-actions')
            .innerHTML += '<a href="javascript:location.reload()" class="error-btn secondary">Retry Now</a>';
    }
}, 1000);
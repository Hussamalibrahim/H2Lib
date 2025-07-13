const lockTime = new Date(/*[[${details.locked_until}]]*/).getTime();
const refreshTime = lockTime - Date.now() + 1000; // Add 1 second buffer

if (refreshTime > 0) {
    setTimeout(() => {
        window.location.reload();
    }, refreshTime);
}
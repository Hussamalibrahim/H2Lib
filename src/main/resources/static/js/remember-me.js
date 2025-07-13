document.addEventListener('DOMContentLoaded', function() {
    const rememberCheckbox = document.getElementById('rememberMe');
    const loginForm = document.getElementById('loginForm');

    if (rememberCheckbox && loginForm) {
        // Check for existing remember-me preference
        const remembered = getCookie('rememberMePref') === 'true';
        rememberCheckbox.checked = remembered;

        // Save preference when changed
        rememberCheckbox.addEventListener('change', function() {
            setCookie('rememberMePref', this.checked, 365);
        });

        // Clear on explicit logout
        if ( window.location .search.includes('logout')) {
            eraseCookie('rememberMePref');
        }
    }

    // Cookie helper functions
    function setCookie(name, value, days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        document.cookie = `${name}=${value};expires=${date.toUTCString()};path=/;Secure;SameSite=Strict`;
    }

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }

    function eraseCookie(name) {
        document.cookie = `${name}=; Max-Age=-99999999; path=/`;
    }
});
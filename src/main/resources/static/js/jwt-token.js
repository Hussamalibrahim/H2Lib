// Updated jwt-token.js
const originalFetch = window.fetch;
window.fetch = async function(url, options = {}) {
    if (!options.headers) {
        options.headers = {};
    }

    // Check both storage locations for token
    const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
    if (token && !options.headers['Authorization']) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    // Add CSRF token for non-GET requests
    if (options.method && options.method !== 'GET') {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        if (csrfToken && !options.headers['X-CSRF-TOKEN']) {
            options.headers['X-CSRF-TOKEN'] = csrfToken;
        }
    }

    return originalFetch(url, options);
};
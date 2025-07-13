
document.addEventListener('DOMContentLoaded', function() {
    // Initialize authentication state
    updateAuthState();

    // ==================== LOGIN FORM HANDLER ====================
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const submitBtn = loginForm.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Logging in...';

            try {
                const response = await authenticatedFetch('/login-back', {
                    method: 'POST',
                    body: JSON.stringify({
                        email: document.getElementById('loginEmail').value,
                        password: document.getElementById('loginPassword').value,
                        rememberMe: document.getElementById('rememberMe').checked
                    })
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Login failed');
                }

                const data = await response.json();
                const token = response.headers.get('Authorization')?.replace('Bearer ', '');

                if (token && data.status === 'success') {
                    handleSuccessfulLogin(token, data.redirectUrl);
                } else {
                    throw new Error(data.message || 'Authentication failed - no token received');
                }
            } catch (error) {
                console.error('Login error:', error);
                showToast(error.message || 'Login failed. Please try again.', 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        });
    }

    // ==================== REGISTRATION FORM HANDLER ====================
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const submitBtn = registerForm.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Registering...';

            try {
                // Client-side validation
                if (!validateRegistrationForm()) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = originalBtnText;
                    return;
                }

                const response = await authenticatedFetch('/register-back', {
                    method: 'POST',
                    body: JSON.stringify({
                        name: document.getElementById('registerName').value,
                        email: document.getElementById('registerEmail').value,
                        password: document.getElementById('registerPassword').value,
                        confirmPassword: document.getElementById('registerConfirmPassword').value
                    })
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Registration failed');
                }

                const data = await response.json();
                if (data.status === 'success') {
                    showToast('Registration successful! Redirecting to login...', 'success');
                    setTimeout(() => {
                        window.location.href = data.redirectUrl || '/login';
                    }, 1500);
                }
            } catch (error) {
                console.error('Registration error:', error);
                showToast(error.message || 'Registration failed. Please try again.', 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        });

        // Password strength indicator
        const passwordInput = document.getElementById('registerPassword');
        if (passwordInput) {
            passwordInput.addEventListener('input', updatePasswordStrength);
        }
    }

    // ==================== HELPER FUNCTIONS ====================

    function handleSuccessfulLogin(token, redirectUrl) {
        // Store token based on rememberMe choice
        const storage = document.getElementById('rememberMe').checked ?
        localStorage : sessionStorage;
        storage.setItem('jwtToken', token);

        // Update UI state
        updateAuthState();

        // Show success message and redirect
        showToast('Login successful! Redirecting...', 'success');
        setTimeout(() => {
            window.location.href = redirectUrl || '/';
        }, 1000);
    }

    function validateRegistrationForm() {
        const password = document.getElementById('registerPassword').value;
        const confirmPassword = document.getElementById('registerConfirmPassword').value;

        if (password !== confirmPassword) {
            showToast('Passwords do not match', 'error');
            return false;
        }

        if (password.length < 8) {
            showToast('Password must be at least 8 characters', 'error');
            return false;
        }

        return true;
    }

    function updateAuthState() {
        const token = getJwtToken();
        document.body.classList.toggle('authenticated', !!token);
        document.body.classList.toggle('unauthenticated', !token);
    }

    function getJwtToken() {
        return localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
    }

    function updatePasswordStrength() {
        const password = this.value;
        const strengthMeter = document.querySelector('.strength-meter');
        const strengthFeedback = document.querySelector('.strength-feedback');

        if (!strengthMeter || !strengthFeedback) return;

        const strength = calculatePasswordStrength(password);
        strengthMeter.style.width = `${strength.score * 25}%`;
        strengthMeter.style.backgroundColor = getStrengthColor(strength.score);
        strengthFeedback.textContent = strength.feedback;
        strengthFeedback.style.color = getStrengthColor(strength.score);
    }

    function calculatePasswordStrength(password) {
        let score = 0;
        let feedback = '';

        // Length checks
        if (password.length > 0) score++;
        if (password.length >= 8) score++;
        if (password.length >= 12) score++;

        // Complexity checks
        if (/[A-Z]/.test(password)) score++;
        if (/[0-9]/.test(password)) score++;
        if (/[^A-Za-z0-9]/.test(password)) score++;

        // Determine feedback
        if (score <= 2) feedback = 'Weak';
        else if (score <= 4) feedback = 'Moderate';
        else feedback = 'Strong';

        return { score: Math.min(score, 4), feedback };
    }

    function getStrengthColor(score) {
        const colors = ['#ff4d4d', '#ffa64d', '#99cc33', '#33cc33'];
        return colors[Math.min(score, colors.length - 1)];
    }

    function showToast(message, type) {
        // Remove existing toasts
        const existingToasts = document.querySelectorAll('.custom-toast');
        existingToasts.forEach(toast => toast.remove());

        // Create new toast
        const toast = document.createElement('div');
        toast.className = `custom-toast ${type}`;
        toast.innerHTML = `
            <div class="toast-content">
                <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                <span>${message}</span>
            </div>
        `;

        // Add to DOM
        document.body.appendChild(toast);

        // Auto-remove after delay
        setTimeout(() => {
            toast.classList.add('fade-out');
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    }
});

// Enhanced fetch wrapper with JWT and CSRF
async function authenticatedFetch(url, options = {}) {
    const config = {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        }
    };

    // Add CSRF token for non-GET requests
    if (config.method && config.method !== 'GET') {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content ||
        document.querySelector('input[name="_csrf"]')?.value;
        if (csrfToken) {
            config.headers['X-CSRF-TOKEN'] = csrfToken;
        }
    }

    // Add JWT token if available
    const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, config);

    // Handle 401 Unauthorized (token expired)
    if (response.status === 401) {
        localStorage.removeItem('jwtToken');
        sessionStorage.removeItem('jwtToken');
        document.body.classList.remove('authenticated');
        document.body.classList.add('unauthenticated');
        showToast('Session expired. Please login again.', 'error');
    }

    return response;
}

// Intercept all fetch requests to add auth headers
const originalFetch = window.fetch;
window.fetch = async function(url, options = {}) {
    return authenticatedFetch(url, options);
};
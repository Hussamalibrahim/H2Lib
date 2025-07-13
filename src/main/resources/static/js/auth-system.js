document.addEventListener('DOMContentLoaded', function() {
    // Initialize authentication state
    updateAuthState();

    // Login Form Handler
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await handleLoginForm(loginForm);
        });
    }

    // Registration Form Handler
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await handleRegistrationForm(registerForm);
        });

        // Password strength indicator
        const passwordInput = document.getElementById('registerPassword');
        if (passwordInput) {
            passwordInput.addEventListener('input', updatePasswordStrength);
        }
    }

    // Forgot Password Link
    const forgotPasswordLink = document.querySelector('.forgot-password');
    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener('click', function(e) {
            e.preventDefault();
            window.location.href = '/forgot-password';
        });
    }
});

// ==================== FORM HANDLERS ====================

async function handleLoginForm(form) {
    const submitBtn = form.querySelector('button[type="submit"]');
    const buttonText = submitBtn.querySelector('.button-text');
    const buttonLoader = submitBtn.querySelector('.button-loader');

    try {
        // Show loading state
        buttonText.textContent = 'Logging in...';
        buttonLoader.style.display = 'inline-block';
        submitBtn.disabled = true;

        // Clear previous errors
        clearFormErrors(form);

        // Validate form
        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;

        if (!email || !password) {
            throw new Error('Please fill in all fields');
        }

        // Submit form
        const response = await authenticatedFetch('/login-back', {
            method: 'POST',
            body: JSON.stringify({
                email: email,
                password: password,
                rememberMe: document.getElementById('rememberMe').checked
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            if (errorData.fieldErrors) {
                displayFieldErrors(form, errorData.fieldErrors);
                throw new Error('Please correct the highlighted fields');
            }
            throw new Error(errorData.message || 'Login failed');
        }

        // Handle successful login
        const data = await response.json();
        handleSuccessfulLogin(data.token, data.redirectUrl);

    } catch (error) {
        showToast(error.message, 'error');
        console.error('Login error:', error);
    } finally {
        // Reset button state
        buttonText.textContent = 'Login';
        buttonLoader.style.display = 'none';
        submitBtn.disabled = false;
    }
}

async function handleRegistrationForm(form) {
    const submitBtn = form.querySelector('button[type="submit"]');
    const buttonText = submitBtn.querySelector('.button-text');
    const buttonLoader = submitBtn.querySelector('.button-loader');

    try {
        // Show loading state
        buttonText.textContent = 'Creating account...';
        buttonLoader.style.display = 'inline-block';
        submitBtn.disabled = true;

        // Clear previous errors
        clearFormErrors(form);

        // Validate form
        if (!validateRegistrationForm()) {
            submitBtn.disabled = false;
            return;
        }

        // Submit form
        const response = await authenticatedFetch('/register-back', {
            method: 'POST',
            body: JSON.stringify({
                name: document.getElementById('registerName').value.trim(),
                email: document.getElementById('registerEmail').value.trim(),
                password: document.getElementById('registerPassword').value,
                confirmPassword: document.getElementById('registerConfirmPassword').value
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            if (errorData.fieldErrors) {
                displayFieldErrors(form, errorData.fieldErrors);
                throw new Error('Please correct the highlighted fields');
            }
            throw new Error(errorData.message || 'Registration failed');
        }

        // Handle successful registration
        const data = await response.json();
        showToast('Registration successful! Redirecting to login...', 'success');
        setTimeout(() => {
            window.location.href = data.redirectUrl || '/login';
        }, 1500);

    } catch (error) {
        showToast(error.message, 'error');
        console.error('Registration error:', error);
    } finally {
        // Reset button state
        buttonText.textContent = 'Create Account';
        buttonLoader.style.display = 'none';
        submitBtn.disabled = false;
    }
}

// ==================== VALIDATION FUNCTIONS ====================

function validateRegistrationForm() {
    let isValid = true;
    const name = document.getElementById('registerName').value.trim();
    const email = document.getElementById('registerEmail').value.trim();
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('registerConfirmPassword').value;

    // Name validation
    if (!name) {
        showFieldError('nameError', 'Please enter your full name');
        isValid = false;
    }

    // Email validation
    if (!email) {
        showFieldError('emailError', 'Please enter your email');
        isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        showFieldError('emailError', 'Please enter a valid email address');
        isValid = false;
    }

    // Password validation
    if (!password) {
        showFieldError('passwordError', 'Please enter a password');
        isValid = false;
    } else if (password.length < 8) {
        showFieldError('passwordError', 'Password must be at least 8 characters');
        isValid = false;
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])/.test(password)) {
        showFieldError('passwordError',
            'Password must contain uppercase, lowercase, number and special character');
        isValid = false;
    }

    // Confirm password validation
    if (password !== confirmPassword) {
        showFieldError('confirmError', 'Passwords do not match');
        isValid = false;
    }

    return isValid;
}

function updatePasswordStrength() {
    const password = document.getElementById('registerPassword').value;
    const strengthMeter = document.querySelector('.strength-meter');
    const feedback = document.getElementById('strengthFeedback');

    // Reset
    strengthMeter.style.width = '0%';
    strengthMeter.style.backgroundColor = 'transparent';
    feedback.textContent = '';

    if (!password) return;

    // Calculate strength
    let score = 0;
    let messages = [];

    // Length check
    if (password.length >= 8) score++;
    else messages.push("at least 8 characters");

    // Complexity checks
    if (/[A-Z]/.test(password)) score++;
    else messages.push("uppercase letter");

    if (/[a-z]/.test(password)) score++;
    else messages.push("lowercase letter");

    if (/[0-9]/.test(password)) score++;
    else messages.push("number");

    if (/[^A-Za-z0-9]/.test(password)) score++;
    else messages.push("special character");

    // Determine feedback
    const colors = ['#ff4d4d', '#ffa64d', '#ffcc00', '#99cc33', '#339900'];
    const levels = ['Very Weak', 'Weak', 'Moderate', 'Strong', 'Very Strong'];

    // Update UI
    strengthMeter.style.width = `${score * 25}%`;
    strengthMeter.style.backgroundColor = colors[score];
    feedback.textContent = messages.length ?
        `Needs ${messages.join(', ')}` :
        levels[score];
    feedback.style.color = colors[score];
}

// ==================== HELPER FUNCTIONS ====================

function clearFormErrors(form) {
    const errorMessages = form.querySelectorAll('.error-message');
    errorMessages.forEach(el => {
        el.textContent = '';
        el.previousElementSibling.classList.remove('has-error');
    });
}

function displayFieldErrors(form, fieldErrors) {
    for (const [field, error] of Object.entries(fieldErrors)) {
        const errorElement = document.getElementById(`${field}Error`);
        if (errorElement) {
            errorElement.textContent = error;
            errorElement.previousElementSibling.classList.add('has-error');
        }
    }
}

function showFieldError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = message;
        element.previousElementSibling.classList.add('has-error');
    }
}

function showToast(message, type) {
    // Implement toast notification system
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 5000);
}

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

function handleSuccessfulLogin(token, redirectUrl) {
    // Store token based on remember me
    const rememberMe = document.getElementById('rememberMe').checked;
    if (rememberMe) {
        localStorage.setItem('jwtToken', token);
    } else {
        sessionStorage.setItem('jwtToken', token);
    }

    // Update UI state
    document.body.classList.add('authenticated');
    document.body.classList.remove('unauthenticated');

    // Redirect
    window.location.href = redirectUrl || '/';
}

function updateAuthState() {
    const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
    if (token) {
        document.body.classList.add('authenticated');
        document.body.classList.remove('unauthenticated');
    } else {
        document.body.classList.remove('authenticated');
        document.body.classList.add('unauthenticated');
    }
}
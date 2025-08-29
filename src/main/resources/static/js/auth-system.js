document.addEventListener('DOMContentLoaded', function() {
    // Initialize authentication state
    updateAuthState();

    // Login Form Handler
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            clearFormErrors(loginForm);
            await handleLoginForm(loginForm);
        });
    }

    // Registration Form Handler
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            clearFormErrors(registerForm);
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
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    const rememberMe = document.getElementById('rememberMe').checked;
    const targetUrl = new URLSearchParams(window.location.search).get('targetUrl') || "";
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;

    try {
  const response = await fetch('/login-back', {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          'X-CSRF-TOKEN': csrfToken || '',
          'X-Requested-With': 'XMLHttpRequest'  // <-- important
      },
      body: JSON.stringify({ email, password, rememberMe, targetUrl })
  });

        const data = await response.json();
        if (data.success) {
            handleSuccessfulLogin(data.token, data.redirectUrl);
        } else if (data.fieldErrors) {
            displayFieldErrors(form, data.fieldErrors);
        } else {
            showError(data.message || "Login failed");
        }
    } catch (err) {
        console.error("Login error:", err);
        showError("Error: " + err.message);
    }
}


async function handleRegistrationForm(form) {
    const name = document.getElementById('registerName').value.trim();
    const email = document.getElementById('registerEmail').value.trim();
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('registerConfirmPassword').value;

    // Local validation
    if (password !== confirmPassword) {
        showFieldError('confirmError', 'Passwords do not match');
        return;
    }

    try {
        const response = await fetch('/register-back', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, confirmPassword })
        });

        const data = await response.json();
        if (data.success) {
            showSuccess("Account created successfully!");
            setTimeout(() => window.location.href = data.redirectUrl || '/login', 1000);
        } else if (data.fieldErrors) {
            displayFieldErrors(form, data.fieldErrors);
        } else {
            showError(data.message || "Registration failed");
        }
    } catch (err) {
        console.error("Registration error:", err);
        showError("Error: " + err.message);
    }
}

// ==================== VALIDATION FUNCTIONS ====================

function updatePasswordStrength() {
    const password = document.getElementById('registerPassword').value;
    const strengthMeter = document.querySelector('.strength-meter');
    const feedback = document.getElementById('strengthFeedback');

    // Reset
    strengthMeter.style.width = '0%';
    strengthMeter.style.backgroundColor = 'transparent';
    feedback.textContent = '';

    if (!password) return;

    let score = 0;
    let messages = [];

    if (password.length >= 8) score++; else messages.push("at least 8 characters");
    if (/[A-Z]/.test(password)) score++; else messages.push("uppercase letter");
    if (/[a-z]/.test(password)) score++; else messages.push("lowercase letter");
    if (/[0-9]/.test(password)) score++; else messages.push("number");
    if (/[^A-Za-z0-9]/.test(password)) score++; else messages.push("special character");

    const colors = ['#ff4d4d', '#ffa64d', '#ffcc00', '#99cc33', '#339900'];
    const levels = ['Very Weak', 'Weak', 'Moderate', 'Strong', 'Very Strong'];

    strengthMeter.style.width = `${score * 20}%`;
    strengthMeter.style.backgroundColor = colors[score];
    feedback.textContent = messages.length ? `Needs ${messages.join(', ')}` : levels[score];
    feedback.style.color = colors[score];
}

// ==================== HELPER FUNCTIONS ====================

function clearFormErrors(form) {
    const errorMessages = form.querySelectorAll('.error-message');
    errorMessages.forEach(el => {
        el.textContent = '';
        el.previousElementSibling?.classList.remove('has-error');
    });
}

function displayFieldErrors(form, fieldErrors) {
    for (const [field, error] of Object.entries(fieldErrors)) {
        const errorElement = document.getElementById(`${field}Error`);
        if (errorElement) {
            errorElement.textContent = error;
            errorElement.previousElementSibling?.classList.add('has-error');
        }
    }
}

function showFieldError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = message;
        element.previousElementSibling?.classList.add('has-error');
    }
}

function showError(message) {
    showMessage(message, 'danger');
}

function showSuccess(message) {
    showMessage(message, 'success');
}

function showMessage(message, type) {
    const flash = document.getElementById("flashMessage");
    if (flash) {
        flash.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
        setTimeout(() => flash.innerHTML = "", 5000);
    } else {
        alert(message);
    }
}
function handleSuccessfulLogin(token, redirectUrl) {
    const rememberMe = document.getElementById('rememberMe')?.checked;
    if (rememberMe) localStorage.setItem('jwtToken', token);
    else sessionStorage.setItem('jwtToken', token);

    console.log("rememberMe:", rememberMe);
    console.log("token:", token);
    console.log("localStorage:", localStorage.getItem('jwtToken'));
    console.log("sessionStorage:", sessionStorage.getItem('jwtToken'));

    document.body.classList.add('authenticated');
    document.body.classList.remove('unauthenticated');

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


console.log("rememberMe:", rememberMe);
console.log("token:", token);
console.log("localStorage:", localStorage.getItem('jwtToken'));
console.log("sessionStorage:", sessionStorage.getItem('jwtToken'));

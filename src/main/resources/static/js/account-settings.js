class AccountSettings {
    constructor() {
        this.endpoints = {
            updatePassword: '/api/update-password'
        };

        this.init();
    }

    init() {
        const passwordForm = document.getElementById('passwordForm');
        if (passwordForm) {
            passwordForm.addEventListener('submit', (e) => this.handlePasswordSubmit(e));
            this.setupPasswordValidation(passwordForm);
        }
    }

    setupPasswordValidation(form) {
        const currentPassword = form.querySelector('input[name="currentPassword"]');
        const newPassword = form.querySelector('input[name="newPassword"]');
        const confirmPassword = form.querySelector('input[name="confirmPassword"]');

        currentPassword?.addEventListener('blur', () => this.validateCurrentPassword(currentPassword));
        newPassword?.addEventListener('blur', () => this.validateNewPassword(newPassword));
        newPassword?.addEventListener('input', (e) => this.updatePasswordStrength(e.target.value));
        confirmPassword?.addEventListener('blur', () =>
        this.validateConfirmPassword(newPassword.value, confirmPassword)
        );
    }

    validateCurrentPassword(input) {
        const password = input.value;
        const formGroup = input.closest('.form-group');

        if (!password) {
            formGroup.classList.add('has-error');
            formGroup.querySelector('.error-message').textContent = 'Current password is required';
            formGroup.querySelector('.error-message').style.display = 'block';
            return false;
        }

        formGroup.classList.remove('has-error');
        formGroup.querySelector('.error-message').style.display = 'none';
        return true;
    }

    validateNewPassword(input) {
        const password = input.value;
        const formGroup = input.closest('.form-group');

        if (!password) {
            formGroup.classList.add('has-error');
            formGroup.querySelector('.error-message').textContent = 'New password is required';
            formGroup.querySelector('.error-message').style.display = 'block';
            return false;
        }

        if (password.length < 8) {
            formGroup.classList.add('has-error');
            formGroup.querySelector('.error-message').textContent = 'Password must be at least 8 characters';
            formGroup.querySelector('.error-message').style.display = 'block';
            return false;
        }

        formGroup.classList.remove('has-error');
        formGroup.querySelector('.error-message').style.display = 'none';
        return true;
    }

    validateConfirmPassword(password, confirmInput) {
        const confirmPassword = confirmInput.value;
        const formGroup = confirmInput.closest('.form-group');

        if (!confirmPassword) {
            formGroup.classList.add('has-error');
            formGroup.querySelector('.error-message').textContent = 'Please confirm your password';
            formGroup.querySelector('.error-message').style.display = 'block';
            return false;
        }

        if (password !== confirmPassword) {
            formGroup.classList.add('has-error');
            formGroup.querySelector('.error-message').textContent = 'Passwords do not match';
            formGroup.querySelector('.error-message').style.display = 'block';
            return false;
        }

        formGroup.classList.remove('has-error');
        formGroup.querySelector('.error-message').style.display = 'none';
        return true;
    }

    validatePasswordForm(form) {
        let isValid = true;

        const currentPassword = form.querySelector('input[name="currentPassword"]');
        const newPassword = form.querySelector('input[name="newPassword"]');
        const confirmPassword = form.querySelector('input[name="confirmPassword"]');

        if (!this.validateCurrentPassword(currentPassword)) isValid = false;
        if (!this.validateNewPassword(newPassword)) isValid = false;
        if (!this.validateConfirmPassword(newPassword.value, confirmPassword)) isValid = false;

        return isValid;
    }

    updatePasswordStrength(password) {
        const meter = document.querySelector('.strength-meter');
        const feedback = document.querySelector('.strength-feedback');

        if (!meter || !feedback) return;

        if (!password) {
            meter.style.width = '0%';
            feedback.textContent = '';
            return;
        }

        let strength = 0;
        strength += Math.min(password.length * 5, 40);
        if (/[A-Z]/.test(password)) strength += 15;
        if (/[0-9]/.test(password)) strength += 15;
        if (/[^A-Za-z0-9]/.test(password)) strength += 15;
        if (password.length >= 12) strength += 15;

        strength = Math.min(strength, 100);

        meter.style.width = `${strength}%`;
        meter.style.backgroundColor = this.getStrengthColor(strength);
        feedback.textContent = this.getStrengthText(strength);
        feedback.style.color = this.getStrengthColor(strength);
    }

    getStrengthColor(strength) {
        if (strength < 40) return 'var(--error-color)';
        if (strength < 70) return 'var(--warning-color)';
        return 'var(--success-color)';
    }

    getStrengthText(strength) {
        if (strength < 40) return 'Weak';
        if (strength < 70) return 'Moderate';
        return 'Strong';
    }

    async handlePasswordSubmit(e) {
        e.preventDefault();
        const form = e.target;

        if (!this.validatePasswordForm(form)) {
            return;
        }

        this.showLoading(true, form, 'Updating...');

        try {
            const formData = new FormData(form);
            const response = await fetch(this.endpoints.updatePassword, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': formData.get('_csrf')
                },
                body: JSON.stringify({
                    currentPassword: formData.get('currentPassword'),
                    newPassword: formData.get('newPassword'),
                    _csrf: formData.get('_csrf')
                })
            });

            const result = await response.json();

            if (response.ok) {
                this.showFormSuccess(form, 'Password updated successfully');
                form.reset();
            } else {
                this.showFormError(form, result.message || 'Password change failed');
            }
        } catch (error) {
            this.showFormError(form, 'Network error. Please try again.');
            console.error('Password change error:', error);
        } finally {
            this.showLoading(false, form, 'Update Password');
        }
    }

    showFormError(form, message) {
        let errorElement = form.querySelector('.alert-error');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'alert alert-error';
            form.prepend(errorElement);
        }
        errorElement.textContent = message;
    }

    showFormSuccess(form, message) {
        let successElement = form.querySelector('.alert-success');
        if (!successElement) {
            successElement = document.createElement('div');
            successElement.className = 'alert alert-success';
            form.prepend(successElement);
        }
        successElement.textContent = message;
        setTimeout(() => successElement.remove(), 5000);
    }

    showLoading(show, form, loadingText = null) {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = show;
            submitBtn.innerHTML = show
                ? `<i class="fas fa-spinner fa-spin"></i> ${loadingText || 'Processing...'}`
                : 'Update Password';
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new AccountSettings();
});
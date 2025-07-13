document.addEventListener('DOMContentLoaded', function() {
    const contactForm = document.querySelector('.contact-form form');

    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = {
                name: document.getElementById('name').value.trim(),
                email: document.getElementById('email').value.trim(),
                subject: document.getElementById('subject').value.trim(),
                message: document.getElementById('message').value.trim()
            };

            if (validateForm(formData)) {
                submitForm(formData);
            }
        });
    }

    function validateForm(formData) {
        // Clear previous errors
        clearErrors();

        let isValid = true;

        // Name validation
        if (!formData.name) {
            showError('name', 'Please enter your name');
            isValid = false;
        }

        // Email validation
        if (!formData.email) {
            showError('email', 'Please enter your email');
            isValid = false;
        } else if (!isValidEmail(formData.email)) {
            showError('email', 'Please enter a valid email');
            isValid = false;
        }

        if (!formData.subject) {
            showError('subject', 'Please enter a subject');
            isValid = false;
        }

        if (!formData.message) {
            showError('message', 'Please enter your message');
            isValid = false;
        } else if (formData.message.length < 10) {
            showError('message', 'Message should be at least 10 characters');
            isValid = false;
        }

        return isValid;
    }

    function showError(fieldId, message) {
        const field = document.getElementById(fieldId);
        const formGroup = field.closest('.form-group');

        // Create error element if it doesn't exist
        let errorElement = formGroup.querySelector('.error-message');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'error-message';
            formGroup.appendChild(errorElement);
        }

        // Add error class to form group
        formGroup.classList.add('has-error');

        // Set error message
        errorElement.textContent = message;
        errorElement.style.color = 'var(--error-color)';
    }

    function clearErrors() {
        // Remove all error messages
        document.querySelectorAll('.error-message').forEach(el => el.remove());

        // Remove error classes
        document.querySelectorAll('.form-group').forEach(el => {
            el.classList.remove('has-error');
        });
    }

    function isValidEmail(email) {
        // Simple email validation regex
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    function submitForm(formData) {
        const submitBtn = contactForm.querySelector('button[type="submit"]');
        const originalBtnText = submitBtn.innerHTML;

        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Sending...';

        setTimeout(() => {
            console.log('Form submitted:', formData);

            const successMessage = document.createElement('div');
            successMessage.className = 'alert success';
            successMessage.innerHTML = `
                <i class="fas fa-check-circle"></i>
                Thank you! Your message has been sent. We'll get back to you soon.
            `;
            contactForm.prepend(successMessage);

            // Reset form
            contactForm.reset();

            // Reset button
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalBtnText;

            // Remove success message after 5 seconds
            setTimeout(() => {
                successMessage.remove();
            }, 5000);

        }, 1500);
    }
});
function toggleSection(header) {
    const section = header.parentElement;
    const content = header.nextElementSibling;
    const chevron = header.querySelector('i');

    // Toggle active class
    section.classList.toggle('active');

    // If section is being opened
    if (section.classList.contains('active')) {
        content.style.display = 'block';
        content.style.maxHeight = content.scrollHeight + 'px';
    } else {
        content.style.maxHeight = '0';
        setTimeout(() => {
            content.style.display = 'none';
        }, 300);
    }
}

// Initialize - open password section by default
document.addEventListener('DOMContentLoaded', function() {
    // Remove error messages when user starts typing
    document.querySelectorAll('.has-error input').forEach(input => {
        input.addEventListener('input', function() {
            const formGroup = this.closest('.form-group');
            if (formGroup) {
                formGroup.classList.remove('has-error');
                const errorMsg = formGroup.querySelector('.error-message');
                if (errorMsg) errorMsg.style.display = 'none';
            }
        });
    });
});
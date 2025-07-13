class Main {
    constructor() {
        this.initTooltips();
        this.initModals();
    }

    initTooltips() {
        const tooltips = document.querySelectorAll('[data-tooltip]');

        tooltips.forEach(element => {
            element.addEventListener('mouseenter', this.showTooltip);
            element.addEventListener('mouseleave', this.hideTooltip);
        });
    }

    showTooltip(e) {
        const tooltipText = this.getAttribute('data-tooltip');
        const tooltip = document.createElement('div');
        tooltip.className = 'tooltip';
        tooltip.textContent = tooltipText;

        document.body.appendChild(tooltip);

        const rect = this.getBoundingClientRect();
        tooltip.style.top = `${rect.bottom + 5}px`;
        tooltip.style.left = `${rect.left + rect.width/2 - tooltip.offsetWidth/2}px`;
    }
    m
    hideTooltip() {
        const tooltip = document.querySelector('.tooltip');
        if (tooltip) {
            tooltip.remove();
        }
    }

    initModals() {
        const modalTriggers = document.querySelectorAll('[data-modal]');

        modalTriggers.forEach(trigger => {
            trigger.addEventListener('click', () => {
                const modalId = trigger.getAttribute('data-modal');
                this.showModal(modalId);
            });
        });

        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                this.hideModals();
            }
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.hideModals();
            }
        });
    }

    showModal(id) {
        const modal = document.getElementById(id);
        if (modal) {
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
        }
    }

    hideModals() {
        document.querySelectorAll('.modal').forEach(modal => {
            modal.style.display = 'none';
        });
        document.body.style.overflow = 'auto';
    }
}
// Add this to your main page's JS to handle login/register links
document.addEventListener('DOMContentLoaded', () => {
    const authSystem = new AuthSystem();

    // Handle login/register links
    document.querySelectorAll('[href="/login"], [href="/register"]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const type = e.target.getAttribute('href') === '/login' ? 'login' : 'register';
            authSystem.showAuth(type);
        });
    });
});

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    new Main();
});
class Toolbar {
    constructor() {
        this.toolbar = document.querySelector('.toolbar');
        this.lastScroll = 0;
        this.scrollThreshold = 50;
        this.scrollDelta = 5;

        if (this.toolbar) {
            this.init();
        }
    }

    init() {
        window.addEventListener('scroll', () => this.handleScroll());
        this.setupAvatarDropdown();

        document.addEventListener('touchstart', this.handleTouchStart.bind(this), {passive: true});
        document.addEventListener('touchmove', this.handleTouchMove.bind(this), {passive: false});
    }

    handleScroll() {
        const currentScroll = window.scrollY;
        const scrollDifference = Math.abs(currentScroll - this.lastScroll);

        // Always show toolbar at top of page
        if (currentScroll <= 0) {
            this.toolbar.classList.remove('hidden');
            return;
        }

        // Only trigger if scrolled enough to prevent jitter
        if (scrollDifference > this.scrollDelta) {
            if (currentScroll > this.lastScroll && currentScroll > this.scrollThreshold) {
                // Scrolling down past threshold - hide
                this.toolbar.classList.add('hidden');
            } else {
                // Scrolling up - show
                this.toolbar.classList.remove('hidden');
            }
        }

        this.lastScroll = currentScroll;
    }

    handleTouchStart(e) {
        this.touchStartY = e.touches[0].clientY;
    }

    handleTouchMove(e) {
        if (!this.touchStartY) return;

        const touchY = e.touches[0].clientY;
        const touchDifference = this.touchStartY - touchY;

        if (Math.abs(touchDifference) > 10) { // Threshold for touch scroll
            if (touchDifference > 0 && window.scrollY > this.scrollThreshold) {
                // Swiping down - show
                this.toolbar.classList.remove('hidden');
            } else if (touchDifference < 0) {
                // Swiping up - hide
                this.toolbar.classList.add('hidden');
            }
        }

        e.preventDefault();
    }

    setupAvatarDropdown() {
        const avatar = document.querySelector('.user-avatar');
        if (!avatar) return;

        avatar.addEventListener('click', (e) => this.toggleDropdown(e));
    }

    toggleDropdown(e) {
        e.stopPropagation();

        const existingDropdown = document.querySelector('.user-dropdown');
        if (existingDropdown) {
            existingDropdown.remove();
            return;
        }

        const dropdown = document.createElement('div');
        dropdown.className = 'user-dropdown';
        dropdown.innerHTML = `
            <a href="/profile" class="dropdown-item">
                <i class="fas fa-user"></i> Profile
            </a>
            <a href="/settings" class="dropdown-item">
                <i class="fas fa-cog"></i> Settings
            </a>
            <a href="/logout" class="dropdown-item">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        `;

        document.body.appendChild(dropdown);

        // Position dropdown below avatar
        const rect = e.target.getBoundingClientRect();
        dropdown.style.top = `${rect.bottom + window.scrollY}px`;
        dropdown.style.right = `${window.innerWidth - rect.right}px`;

        // Close when clicking outside
        const clickHandler = (event) => {
            if (!dropdown.contains(event.target)) {
                dropdown.remove();
                document.removeEventListener('click', clickHandler);
            }
        };

        document.addEventListener('click', clickHandler);
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {new Toolbar();});
class ProfileAvatar {
    constructor() {
        this.initAvatars();
    }

    initAvatars() {
        document.querySelectorAll('.user-avatar, .avatar-large').forEach(avatar => {
            const username = avatar.dataset.username || 'U';
            const imageUrl = avatar.dataset.imageUrl;

            if (imageUrl && imageUrl.trim() !== '') {
                // Use the provided image URL
                avatar.style.backgroundImage = `url('${imageUrl}')`;
                avatar.style.backgroundSize = 'cover';
                avatar.style.backgroundPosition = 'center';
                avatar.textContent = ''; // Remove initials if image exists
            } else {
                // Generate avatar with initials and color
                const initials = this.getInitials(username);
                avatar.textContent = initials;
                avatar.style.backgroundColor = this.getColorFromName(username);
                avatar.style.color = 'white';
                avatar.style.display = 'flex';
                avatar.style.alignItems = 'center';
                avatar.style.justifyContent = 'center';
                avatar.style.fontWeight = 'bold';
            }

            if (avatar.classList.contains('avatar-large')) {
                avatar.style.fontSize = '2rem';
            }
        });
    }

    getInitials(name) {
        return name.split(' ')
            .filter(part => part.length > 0)
            .slice(0, 2)
            .map(part => part[0].toUpperCase())
            .join('');
    }

    getColorFromName(name) {
        let hash = 0;
        for (let i = 0; i < name.length; i++) {
            hash = name.charCodeAt(i) + ((hash << 5) - hash);
        }
        return `hsl(${Math.abs(hash) % 360}, 70%, 50%)`;
    }
}

document.addEventListener('DOMContentLoaded', () => new ProfileAvatar());
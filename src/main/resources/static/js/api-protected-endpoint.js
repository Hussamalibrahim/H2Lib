async function fetchProtectedData() {
    try {
        const response = await fetch('/api/protected-endpoint');

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Request failed');
        }

        const data = await response.json();
        console.log('Protected data:', data);
        return data;

    } catch (error) {
        console.error('API Error:', error);

        // Handle specific error cases
        if (error.message.includes('expired')) {
            // Token expired - redirect to login
            window.location.href = '/login?error=session_expired';
        } else if (error.message.includes('invalid')) {
            // Invalid token - clear storage
            localStorage.removeItem('jwtToken');
        }

        // Re-throw for calling code to handle
        throw error;
    }
}

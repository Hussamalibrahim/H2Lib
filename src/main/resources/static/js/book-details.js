class BookDetails {
    constructor() {
        this.initDownloadButton();
        this.initReadButton();
        this.initRating();
    }

    initDownloadButton() {
        const downloadBtn = document.getElementById('downloadBtn');
        if (!downloadBtn) return;

        downloadBtn.addEventListener('click', async () => {
            const bookId = downloadBtn.dataset.bookId || '0';

            try {
                const response = await fetch(`/api/books/${bookId}/download`, {
                    method: 'POST'
                });

                if (response.ok) {
                    const link = document.createElement('a');
                    link.href = `/api/books/${bookId}/file`;
                    link.download = downloadBtn.dataset.bookName || 'book.pdf';
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                } else {
                    throw new Error('Failed to download');
                }
            } catch (error) {
                console.error('Download error:', error);
                alert('Error downloading book');
            }
        });
    }

    initReadButton() {
        const readBtn = document.getElementById('readBtn');
        const pdfModal = document.getElementById('book-pdf-modal');

        if (!readBtn || !pdfModal) return;

        readBtn.addEventListener('click', () => {
            const bookId = readBtn.dataset.bookId || '0';
            const pdfViewer = pdfModal.querySelector('.pdf-viewer');

            pdfModal.style.display = 'block';
            pdfViewer.src = `/api/books/${bookId}/view`;
        });

        pdfModal.querySelector('.close-btn').addEventListener('click', () => {
            pdfModal.style.display = 'none';
        });

        window.addEventListener('click', (e) => {
            if (e.target === pdfModal) {
                pdfModal.style.display = 'none';
            }
        });
    }

    initRating() {
        document.querySelectorAll('.stars i').forEach(star => {
            star.addEventListener('click', async () => {
                const rating = star.dataset.value;
                const bookId = star.closest('.rating-container').dataset.bookId || '0';

                try {
                    const response = await fetch(`/api/books/${bookId}/rate`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ rating })
                    });

                    if (response.ok) {
                        location.reload();
                    } else {
                        throw new Error('Rating failed');
                    }
                } catch (error) {
                    console.error('Rating error:', error);
                    alert('Error submitting rating');
                }
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', () => new BookDetails());
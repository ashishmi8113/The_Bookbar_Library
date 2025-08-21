// Optional: fetch cover images from OpenLibrary based on book title
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.card').forEach(async (card) => {
        const title = card.getAttribute('data-title');
        if (!title) return;
        try {
            const res = await fetch('https://openlibrary.org/search.json?title=' + encodeURIComponent(title));
            const data = await res.json();
            if (data.docs && data.docs.length > 0) {
                const d = data.docs[0];
                if (d.cover_i) {
                    const img = card.querySelector('.cover');
                    if (img) img.src = 'https://covers.openlibrary.org/b/id/' + d.cover_i + '-M.jpg';
                }
            }
        } catch (e) { /* ignore */ }
    });
});

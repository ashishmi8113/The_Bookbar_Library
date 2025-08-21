const searchBtn = document.getElementById("searchBtn");
const searchInput = document.getElementById("searchInput");
const resultsDiv = document.getElementById("results");

searchBtn.addEventListener("click", () => {
  const query = searchInput.value.trim();
  if (!query) return;

  axios.get(`https://openlibrary.org/search.json?title=${encodeURIComponent(query)}`)
    .then(res => {
      resultsDiv.innerHTML = '';
      const books = res.data.docs.slice(0, 20); // show max 20 books
      books.forEach(book => {
        const coverUrl = book.cover_i 
          ? `https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg` 
          : 'images/default-cover.png';

        const div = document.createElement("div");
        div.classList.add("book");
        div.innerHTML = `
          <img src="${coverUrl}" alt="cover">
          <div class="book-details">
            <div class="book-title">${book.title}</div>
            <div class="book-author">Author: ${book.author_name ? book.author_name.join(', ') : 'Unknown'}</div>
            <div>First Published: ${book.first_publish_year || 'N/A'}</div>
            <button class="borrowBtn" data-id="${book.key}">Borrow</button>
          </div>
        `;
        resultsDiv.appendChild(div);
      });

      // Borrow button click handler
      document.querySelectorAll(".borrowBtn").forEach(btn => {
        btn.addEventListener("click", () => {
          const bookId = btn.getAttribute("data-id");
          axios.post("/books/borrow", { bookId }) // Call borrow servlet
            .then(resp => {
              alert(resp.data.message);
              btn.disabled = true;
              btn.textContent = "Borrowed";
            })
            .catch(err => {
              console.error(err);
              alert("Failed to borrow book.");
            });
        });
      });

    })
    .catch(err => console.error(err));
});

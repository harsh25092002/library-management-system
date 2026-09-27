package com.library.app.service;

import com.library.app.dao.BookDao;
import com.library.app.exception.ResourceNotFoundException;
import com.library.app.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookDao bookDao;

    public BookService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    public Book getBookById(Long id) {
        return bookDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public Book addBook(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        return bookDao.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book updated) {
        Book existing = getBookById(id);
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setIsbn(updated.getIsbn());
        existing.setTotalCopies(updated.getTotalCopies());
        existing.setAvailableCopies(updated.getAvailableCopies());
        bookDao.update(existing);
        return existing;
    }

    @Transactional
    public void deleteBook(Long id) {
        getBookById(id);
        bookDao.deleteById(id);
    }
}

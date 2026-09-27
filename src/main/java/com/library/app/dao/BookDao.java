package com.library.app.dao;

import com.library.app.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Book records.
 * Uses Spring's JdbcTemplate (a thin wrapper around raw JDBC) to keep full
 * control over the SQL that is executed, matching the JDBC + SQL skill set.
 */
@Repository
public class BookDao {

    private final JdbcTemplate jdbcTemplate;

    public BookDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> findAll() {
        String sql = "SELECT id, title, author, isbn, total_copies, available_copies FROM books ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Optional<Book> findById(Long id) {
        String sql = "SELECT id, title, author, isbn, total_copies, available_copies FROM books WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id).stream().findFirst();
    }

    public Book save(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            return ps;
        }, keyHolder);
        book.setId(keyHolder.getKey().longValue());
        return book;
    }

    public int update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, total_copies = ?, available_copies = ? WHERE id = ?";
        return jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getTotalCopies(), book.getAvailableCopies(), book.getId());
    }

    public int decrementAvailableCopies(Long bookId) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ? AND available_copies > 0";
        return jdbcTemplate.update(sql, bookId);
    }

    public int incrementAvailableCopies(Long bookId) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, bookId);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private Book mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies")
        );
    }
}

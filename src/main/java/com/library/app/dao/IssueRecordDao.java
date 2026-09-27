package com.library.app.dao;

import com.library.app.model.IssueRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class IssueRecordDao {

    private final JdbcTemplate jdbcTemplate;

    public IssueRecordDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<IssueRecord> findAll() {
        String sql = "SELECT id, book_id, member_id, issue_date, due_date, return_date, status FROM issue_records ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<IssueRecord> findActiveByMember(Long memberId) {
        String sql = "SELECT id, book_id, member_id, issue_date, due_date, return_date, status " +
                "FROM issue_records WHERE member_id = ? AND status = 'ISSUED'";
        return jdbcTemplate.query(sql, this::mapRow, memberId);
    }

    public Optional<IssueRecord> findById(Long id) {
        String sql = "SELECT id, book_id, member_id, issue_date, due_date, return_date, status FROM issue_records WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id).stream().findFirst();
    }

    public IssueRecord save(IssueRecord record) {
        String sql = "INSERT INTO issue_records (book_id, member_id, issue_date, due_date, return_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, record.getBookId());
            ps.setLong(2, record.getMemberId());
            ps.setDate(3, Date.valueOf(record.getIssueDate()));
            ps.setDate(4, Date.valueOf(record.getDueDate()));
            ps.setDate(5, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            ps.setString(6, record.getStatus());
            return ps;
        }, keyHolder);
        record.setId(keyHolder.getKey().longValue());
        return record;
    }

    public int markReturned(Long id, java.time.LocalDate returnDate) {
        String sql = "UPDATE issue_records SET return_date = ?, status = 'RETURNED' WHERE id = ?";
        return jdbcTemplate.update(sql, Date.valueOf(returnDate), id);
    }

    private IssueRecord mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        java.sql.Date returnDate = rs.getDate("return_date");
        return new IssueRecord(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getLong("member_id"),
                rs.getDate("issue_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                returnDate != null ? returnDate.toLocalDate() : null,
                rs.getString("status")
        );
    }
}

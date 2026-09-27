package com.library.app.dao;

import com.library.app.model.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public MemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Member> findAll() {
        String sql = "SELECT id, name, email, phone FROM members ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Optional<Member> findById(Long id) {
        String sql = "SELECT id, name, email, phone FROM members WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id).stream().findFirst();
    }

    public Member save(Member member) {
        String sql = "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            return ps;
        }, keyHolder);
        member.setId(keyHolder.getKey().longValue());
        return member;
    }

    public int update(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, phone = ? WHERE id = ?";
        return jdbcTemplate.update(sql, member.getName(), member.getEmail(), member.getPhone(), member.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM members WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private Member mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Member(rs.getLong("id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"));
    }
}

package com.library.app.service;

import com.library.app.dao.MemberDao;
import com.library.app.exception.ResourceNotFoundException;
import com.library.app.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberDao memberDao;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public List<Member> getAllMembers() {
        return memberDao.findAll();
    }

    public Member getMemberById(Long id) {
        return memberDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    @Transactional
    public Member addMember(Member member) {
        return memberDao.save(member);
    }

    @Transactional
    public Member updateMember(Long id, Member updated) {
        Member existing = getMemberById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        memberDao.update(existing);
        return existing;
    }

    @Transactional
    public void deleteMember(Long id) {
        getMemberById(id);
        memberDao.deleteById(id);
    }
}

package org.library.dao;

import org.library.model.Member;

import java.util.List;

public interface MemberDAO {
    void addMember(Member member);
    void updateMember(Member member);
    void deleteMember(int memberId);
    List<Member> getAllMembers();
    Member getMemberById(int memberId);
}

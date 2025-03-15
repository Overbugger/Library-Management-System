package org.library.dao;

import org.library.model.Member;

import java.util.List;

public interface MemberDAO {
    void addMember(Member member);
    void updateMember(Member member);
    void deleteMember(int memberId);
    List<Member> getAllMembers();

//    Display Members with pagination
    List<Member> displayMembers(int pageNum, int pageSize);

    Member getMemberById(int memberId);
}

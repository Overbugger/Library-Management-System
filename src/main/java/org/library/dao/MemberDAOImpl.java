package org.library.dao;

import org.library.model.Member;
import org.library.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {

    @Override
    public void addMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setString(1, member.getName());
            statement.setString(2, member.getEmail());
            statement.setString(3, member.getPhone());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMember(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, phone = ? WHERE member_id = ?";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setString(1, member.getName());
            statement.setString(2, member.getEmail());
            statement.setString(3, member.getPhone());
            statement.setInt(4, member.getMemberId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setInt(1, memberId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection connect = DbConnection.getConnection();
             Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("member_id"));
                member.setName(rs.getString("name"));
                member.setEmail(rs.getString("email"));
                member.setPhone(rs.getString("phone"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public Member getMemberById(int memberId) {
        Member member = null;
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try(Connection connect = DbConnection.getConnection();
        PreparedStatement statement = connect.prepareStatement(sql)
        ) {
           statement.setInt(1, memberId);
           try(ResultSet res = statement.executeQuery()){
               if(res.next()) {
                   member = new Member();
                   member.setMemberId(res.getInt("member_id"));
                   member.setName(res.getString("name"));
                   member.setEmail(res.getString("email"));
                   member.setPhone(res.getString("phone"));
               }
           }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }
}

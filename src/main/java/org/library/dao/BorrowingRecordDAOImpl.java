package org.library.dao;

import org.library.model.BorrowingRecord;
import org.library.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingRecordDAOImpl implements BorrowingRecordDAO {

    @Override
    public void addBorrowingRecord(BorrowingRecord record) {
        String sql = "INSERT INTO borrowing_records (book_id, member_id, borrow_date, return_date) VALUES (?::uuid, ?, ?, ?)";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setString(1, record.getBookId());
            statement.setInt(2, record.getMemberId());
            statement.setDate(3, new java.sql.Date(record.getBorrowDate().getTime()));
            if (record.getReturnDate() != null) {
                statement.setDate(4, new java.sql.Date(record.getReturnDate().getTime()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBorrowingRecord(BorrowingRecord record) {
        String sql = "UPDATE borrowing_records SET book_id = ?::uuid, member_id = ?, borrow_date = ?, return_date = ? WHERE record_id = ?";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setString(1, record.getBookId());
            statement.setInt(2, record.getMemberId());
            statement.setDate(3, new java.sql.Date(record.getBorrowDate().getTime()));
            if (record.getReturnDate() != null) {
                statement.setDate(4, new java.sql.Date(record.getReturnDate().getTime()));
            } else {
                statement.setNull(4, Types.DATE);
            }
            statement.setInt(5, record.getRecordId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BorrowingRecord> getAllBorrowingRecords() {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrowing_records";
        try (Connection connect = DbConnection.getConnection();
             Statement statement = connect.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                BorrowingRecord record = new BorrowingRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setBookId(rs.getString("book_id"));
                record.setMemberId(rs.getInt("member_id"));
                record.setBorrowDate(rs.getDate("borrow_date"));
                record.setReturnDate(rs.getDate("return_date"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    public BorrowingRecord getBorrowingRecordById(int recordId) {
        BorrowingRecord record = null;
        String sql = "SELECT * FROM borrowing_records WHERE record_id = ?";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setInt(1, recordId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    record = new BorrowingRecord();
                    record.setRecordId(rs.getInt("record_id"));
                    record.setBookId(rs.getString("book_id"));
                    record.setMemberId(rs.getInt("member_id"));
                    record.setBorrowDate(rs.getDate("borrow_date"));
                    record.setReturnDate(rs.getDate("return_date"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
    }
}

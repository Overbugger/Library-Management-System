package org.library.dao;

import org.library.model.BorrowingRecord;
import java.util.List;

public interface BorrowingRecordDAO {
    void addBorrowingRecord(BorrowingRecord record);
    void updateBorrowingRecord(BorrowingRecord record);
    List<BorrowingRecord> getAllBorrowingRecords();
    BorrowingRecord getBorrowingRecordById(int recordId);
}

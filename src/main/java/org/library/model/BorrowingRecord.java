package org.library.model;

import java.util.Date;

public class BorrowingRecord {
    private int recordId;
    private String bookId;
    private int memberId;
    private Date borrowDate;
    private Date returnDate;

    // No-arg constructor
    public BorrowingRecord() {}

    // Parameterized constructor
    public BorrowingRecord(int recordId, String bookId, int memberId, Date borrowDate, Date returnDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    // Getters and Setters
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    // toString() method for debugging purposes
    @Override
    public String toString() {
        return "BorrowingRecord {" +
                "recordId: " + recordId +
                ", bookId: " + bookId +
                ", memberId: " + memberId +
                ", borrowDate: " + borrowDate +
                ", returnDate: " + returnDate +
                '}';
    }
}

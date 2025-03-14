package org.library.service;

import org.library.dao.BookDAO;
import org.library.dao.BookDAOImpl;
import org.library.dao.MemberDAO;
import org.library.dao.MemberDAOImpl;
import org.library.dao.BorrowingRecordDAO;
import org.library.dao.BorrowingRecordDAOImpl;
import org.library.model.Book;
import org.library.model.Member;
import org.library.model.BorrowingRecord;
import org.library.utils.LogManager;

import java.util.Date;
import java.util.List;

public class LibraryService {

    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private BorrowingRecordDAO borrowingRecordDAO;

    public LibraryService() {
        // Instantiating DAO implementations
        this.bookDAO = new BookDAOImpl();
        this.memberDAO = new MemberDAOImpl();
        this.borrowingRecordDAO = new BorrowingRecordDAOImpl();
    }

    // Book Operations

    public void addBook(Book book) {
        bookDAO.addBook(book);
        LogManager.log(book.getAvailableCopies() + " Copies of " + book.getTitle() + " Added");
    }

    public void updateBook(Book book) {
        bookDAO.updateBook(book);
        LogManager.log("Updated book: " + book.getTitle() + " (ID: " + book.getBookId() + ")");
    }

    public void deleteBook(String bookId) {
        bookDAO.deleteBook(bookId);
        LogManager.log("Deleted book with ID: " + bookId);
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public Book getBookById(String bookId) {
        return bookDAO.getBookById(bookId);
    }

    // Member Operations

    public void addMember(Member member) {
        memberDAO.addMember(member);
        LogManager.log(member.getName() + " Added");

    }

    public void updateMember(Member member) {
        memberDAO.updateMember(member);
        LogManager.log("Updated member: " + member.getName() + " (ID: " + member.getMemberId() + ")");

    }

    public void deleteMember(int memberId) {
        memberDAO.deleteMember(memberId);
        LogManager.log("Deleted member with ID: " + memberId);

    }

    public List<Member> getAllMembers() {
        return memberDAO.getAllMembers();
    }


    // Borrowing Operations

    public boolean borrowBook(String bookId, int memberId) {
        // Check if the book exists and if copies are available
        LogManager.log("Attempting to borrow book with ID: " + bookId + " for member with ID: " + memberId);

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            LogManager.log("Borrowing failed: Book with ID " + bookId + " not found.");
            System.out.println("Book not found.");
            return false;
        }
        if (book.getAvailableCopies() <= 0) {
            LogManager.log("Borrowing failed: No available copies for book with ID " + bookId);

            System.out.println("No available copies for this book.");
            return false;
        }

        // Update available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDAO.updateBook(book);

        // Create a new borrowing record
        BorrowingRecord record = new BorrowingRecord();
        record.setBookId(bookId);
        record.setMemberId(memberId);
        record.setBorrowDate(new Date());
        record.setReturnDate(null);
        borrowingRecordDAO.addBorrowingRecord(record);

        LogManager.log("Book with ID " + bookId + " successfully borrowed by member with ID " + memberId);

        return true;
    }

    public boolean returnBook(int recordId) {
        // Retrieve the borrowing record

        LogManager.log("Attempting to return book for borrowing record ID: " + recordId);

        BorrowingRecord record = borrowingRecordDAO.getBorrowingRecordById(recordId);
        if (record == null) {
            LogManager.log("Return failed: Borrowing record with ID " + recordId + " not found.");

            System.out.println("Borrowing record not found.");
            return false;
        }
        if (record.getReturnDate() != null) {
            LogManager.log("Return failed: Book for record ID " + recordId + " has already been returned.");

            System.out.println("Book has already been returned.");
            return false;
        }

        // Mark the book as returned
        record.setReturnDate(new Date());
        borrowingRecordDAO.updateBorrowingRecord(record);

        // Update the available copies for the returned book
        Book book = bookDAO.getBookById(record.getBookId());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookDAO.updateBook(book);
        }

        LogManager.log("Book with ID " + record.getBookId() + " successfully returned for record ID " + recordId);

        return true;
    }

    public List<BorrowingRecord> getAllBorrowingRecords() {
        return borrowingRecordDAO.getAllBorrowingRecords();
    }
}

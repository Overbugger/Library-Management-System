package org.library.dao;

import org.library.model.Book;
import java.util.List;

public interface BookDAO {
    void addBook(Book book);
    void updateBook(Book book);
    void deleteBook(String bookId);
    List<Book> getAllBooks();

//    Display Books with pagination
    List<Book> displayBooks(int pageNum, int pageSize);

    Book getBookById(String bookId);

}
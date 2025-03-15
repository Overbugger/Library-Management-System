package org.library.dao;

import org.library.model.Book;
import org.library.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {

    @Override
    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, genre, available_copies) VALUES (?, ?, ?, ?)";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql);
        ) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getGenre());
            statement.setInt(4, book.getAvailableCopies());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, genre = ?, available_copies = ? WHERE book_id = ?::uuid";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql);
        ) {

            statement.setString(1, book.getTitle());
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getGenre());
            statement.setInt(4, book.getAvailableCopies());
            statement.setString(5, book.getBookId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBook(String bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?::uuid";
        try (
                Connection connect = DbConnection.getConnection();
                PreparedStatement statement = connect.prepareStatement(sql)
        ) {

            statement.setString(1, bookId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection connect = DbConnection.getConnection();
             Statement statement = connect.createStatement();
             ResultSet res = statement.executeQuery(sql)) {
            while (res.next()) {
                Book book = new Book();
                book.setBookId(res.getString("book_id"));
                book.setTitle(res.getString("title"));
                book.setAuthor(res.getString("author"));
                book.setGenre(res.getString("genre"));
                book.setAvailableCopies(res.getInt("available_copies"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public List<Book> displayBooks(int pageNum, int pageSize) {
        List<Book> books = new ArrayList<>();
        int offset = (pageNum - 1) * pageSize;

        String sql = "SELECT * FROM books ORDER BY title LIMIT ? OFFSET ?";
        try (Connection connect = DbConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement(sql)){
            statement.setInt(1, pageSize);
            statement.setInt(2, offset);

            try(ResultSet res = statement.executeQuery()){
                while (res.next()) {
                    Book book = new Book();
                    book.setBookId(res.getString("book_id"));
                    book.setTitle(res.getString("title"));
                    book.setAuthor(res.getString("author"));
                    book.setGenre(res.getString("genre"));
                    book.setAvailableCopies(res.getInt("available_copies"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Book getBookById(String bookId) {
        Book book = null;

        String sql = "SELECT * FROM books WHERE book_id = ?::uuid";
        try (Connection connect = DbConnection.getConnection()) {
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, bookId);
            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    book = new Book();
                    book.setTitle(res.getString("title"));
                    book.setBookId(res.getString("book_id"));
                    book.setTitle(res.getString("title"));
                    book.setAuthor(res.getString("author"));
                    book.setGenre(res.getString("genre"));
                    book.setAvailableCopies(res.getInt("available_copies"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

}

package org.library.model;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int availableCopies;

    // No-arg constructor
    public Book() {
        this.availableCopies = 1;
    }

    // Parameterized constructor
    public Book(String bookId, String title, String author, String genre, Integer availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.availableCopies = (availableCopies != null) ? availableCopies : 1;
    }

    // Getters and Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // Override toString() for better logging
    @Override
    public String toString() {
        return "Book {" +
                "bookId: " + bookId +
                ", title: '" + title + '\'' +
                ", author: '" + author + '\'' +
                ", genre: '" + genre + '\'' +
                ", availableCopies: " + availableCopies +
                '}';
    }
}

package org.library;

import org.library.model.Book;
import org.library.model.Member;
import org.library.service.LibraryService;
import org.library.utils.CSVProvider;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Library Management System ===");
            System.out.println("1. Add Book");
            System.out.println("2. List Books");
            System.out.println("3. Add Member");
            System.out.println("4. List Members");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. Export Books to CSV");
            System.out.println("8. Export Members to CSV");
            System.out.println("9. Exit");
            System.out.print("Enter an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("\nEnter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter book genre: ");
                    String genre = scanner.nextLine();
                    System.out.print("Enter available copies: ");
                    int copies = Integer.parseInt(scanner.nextLine());

                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setGenre(genre);
                    book.setAvailableCopies(copies);

                    libraryService.addBook(book);
                    System.out.println("\nBook added successfully.");
                    break;
                case 2:
                    int booksPageNum = 1;
                    int booksPageSize = 5;

                    while (true) {
                        List<Book> books = libraryService.displayBooks(booksPageNum, booksPageSize);
                        System.out.println("\nList of Books (Page " + booksPageNum + "):");
                        System.out.println("---------------------------------------------");

                        for (Book bk : books) {
                            System.out.println("id: " + bk.getBookId());
                            System.out.println("Title: " + bk.getTitle());
                            System.out.println("Author: " + bk.getAuthor());
                            System.out.println("Genre: " + bk.getGenre());
                            System.out.println("Available Copy: " + bk.getAvailableCopies());
                            System.out.println("---------------------------------------------");
                        }

                        // Check if this is the first page and/or last page
                        boolean isFirstPage = (booksPageNum == 1);
                        boolean isLastPage = (books.size() < booksPageSize);

                        System.out.print("\nEnter 'n' for next page, 'p' for previous page, or any other key to exit: ");
                        String option = scanner.nextLine();

                        if (option.equalsIgnoreCase("n")) {
                            if (isLastPage) {
                                System.out.println("\nYou are at the last page. No more records available.");
                            } else {
                                booksPageNum++;
                            }
                        } else if (option.equalsIgnoreCase("p")) {
                            if (!isFirstPage) {
                                booksPageNum--;
                            } else {
                                System.out.println("\nYou are already at the first page.");
                            }
                        } else {
                            break;
                        }
                    }
                    break;
                case 3:
                    System.out.print("\nEnter member name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter member email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter member phone: ");
                    String phone = scanner.nextLine();

                    Member member = new Member();
                    member.setName(name);
                    member.setEmail(email);
                    member.setPhone(phone);

                    libraryService.addMember(member);
                    System.out.println("\nMember added successfully.");
                    break;
                case 4:
                    int membersPageNum = 1;
                    int membersPageSize = 5;

                    while (true) {
                        List<Member> members = libraryService.displayMembers(membersPageNum, membersPageSize);
                        System.out.println("\nList of Members (Page " + membersPageNum + "):");
                        System.out.println("---------------------------------------------");

                        for (Member mem : members) {
                            System.out.println("id: " + mem.getMemberId());
                            System.out.println("Name: " + mem.getName());
                            System.out.println("Email: " + mem.getEmail());
                            System.out.println("Phone: " + mem.getPhone());
                            System.out.println("---------------------------------------------");
                        }

                        // Check if this is the first page and/or last page
                        boolean isFirstPage = (membersPageNum == 1);
                        boolean isLastPage = (members.size() < membersPageSize);

                        System.out.print("\nEnter 'n' for next page, 'p' for previous page, or any other key to exit: ");
                        String option = scanner.nextLine();

                        if (option.equalsIgnoreCase("n")) {
                            if (isLastPage) {
                                System.out.println("\nYou are at the last page. No more records available.");
                            } else {
                                membersPageNum++;
                            }
                        } else if (option.equalsIgnoreCase("p")) {
                            if (!isFirstPage) {
                                membersPageNum--;
                            } else {
                                System.out.println("\nYou are already at the first page.");
                            }
                        } else {
                            break;
                        }
                    }
                    break;
                case 5:
                    System.out.print("Enter book ID to borrow: ");
                    String bookId = scanner.nextLine();
                    System.out.print("Enter member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine());

                    if (libraryService.borrowBook(bookId, memberId)) {
                        System.out.println("\nBook borrowed successfully.");
                    } else {
                        System.out.println("\nFailed to borrow book.");
                    }
                    break;
                case 6:
                    System.out.print("Enter borrowing record ID to return: ");
                    int recordId = Integer.parseInt(scanner.nextLine());

                    if (libraryService.returnBook(recordId)) {
                        System.out.println("\nBook returned successfully.");
                    } else {
                        System.out.println("\nFailed to return book.");
                    }
                    break;
                case 7:
                    CSVProvider.exportBooksToCSV(libraryService.getAllBooks(), "books.csv");
                    System.out.println("\nBooks exported to books.csv successfully.");
                    break;
                case 8:
                    CSVProvider.exportMembersToCSV(libraryService.getAllMembers(), "members.csv");
                    System.out.println("\nMembers exported to members.csv successfully.");
                    break;
                case 9:
                    exit = true;
                    System.out.println("\nExiting system...");
                    break;
                default:
                    System.out.println("\nInvalid choice. Try again.");
                    break;
            }
        }
        scanner.close();
    }
}

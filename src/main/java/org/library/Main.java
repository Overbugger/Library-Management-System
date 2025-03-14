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
                    System.out.print("Enter book title: ");
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
                    System.out.println("Book added successfully.");
                    break;
                case 2:
                    List<Book> books = libraryService.getAllBooks();
                    System.out.println("\nList of Books:");
                    for (Book b : books) {
                        System.out.println(b);
                    }
                    break;
                case 3:
                    System.out.print("Enter member name: ");
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
                    System.out.println("Member added successfully.");
                    break;
                case 4:
                    List<Member> members = libraryService.getAllMembers();
                    System.out.println("\nList of Members:");
                    for (Member m : members) {
                        System.out.println(m);
                    }
                    break;
                case 5:
                    System.out.print("Enter book ID to borrow: ");
                    String bookId = scanner.nextLine();
                    System.out.print("Enter member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine());

                    if (libraryService.borrowBook(bookId, memberId)) {
                        System.out.println("Book borrowed successfully.");
                    } else {
                        System.out.println("Failed to borrow book.");
                    }
                    break;
                case 6:
                    System.out.print("Enter borrowing record ID to return: ");
                    int recordId = Integer.parseInt(scanner.nextLine());

                    if (libraryService.returnBook(recordId)) {
                        System.out.println("Book returned successfully.");
                    } else {
                        System.out.println("Failed to return book.");
                    }
                    break;
                case 7:
                    CSVProvider.exportBooksToCSV(libraryService.getAllBooks(), "books.csv");
                    System.out.println("Books exported to books.csv successfully.");
                    break;
                case 8:
                    CSVProvider.exportMembersToCSV(libraryService.getAllMembers(), "members.csv");
                    System.out.println("Members exported to members.csv successfully.");
                    break;
                case 9:
                    exit = true;
                    System.out.println("Exiting system.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
        scanner.close();
    }
}

package org.library.utils;

import org.library.model.Book;
import org.library.model.Member;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVProvider {

    public static void exportBooksToCSV(List<Book> books, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write CSV header
            writer.write("bookId,title,author,genre,availableCopies");
            writer.newLine();
            // Write book details
            for (Book book : books) {
                writer.write(String.format("%s,%s,%s,%s,%d",
                        book.getBookId(),
                        book.getTitle().replace(",", " "),
                        book.getAuthor().replace(",", " "),
                        book.getGenre().replace(",", " "),
                        book.getAvailableCopies()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportMembersToCSV(List<Member> members, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write CSV header
            writer.write("memberId,name,email,phone");
            writer.newLine();
            // Write member details
            for (Member member : members) {
                writer.write(String.format("%d,%s,%s,%s",
                        member.getMemberId(),
                        member.getName().replace(",", " "),
                        member.getEmail().replace(",", " "),
                        member.getPhone().replace(",", " ")));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

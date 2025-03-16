package org.library.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.library.model.Book;
import org.library.service.LibraryService;
import org.library.model.BorrowingRecord;
import java.util.List;

public class DashboardPage extends BorderPane {

    private final LibraryService libraryService;

    public DashboardPage(LibraryService libraryService) {
        this.libraryService = libraryService;
        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(30));

        // Top Header
        Label header = new Label("Welcome to the DreamDevs Library");
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        // Cards Row
        HBox cardsRow = new HBox(20);
        cardsRow.setAlignment(Pos.CENTER);
        cardsRow.setPadding(new Insets(20, 0, 20, 0));

        // Retrieve statistics from the service
        int totalBooks = libraryService.getAllBooks()
                .stream()
                .mapToInt(Book::getAvailableCopies)
                .sum();
        int totalMembers = libraryService.getAllMembers().size();
        List<BorrowingRecord> borrowingRecords = libraryService.getAllBorrowingRecords();
        int borrowedBooks = borrowingRecords.size();
        int returnedBooks = (int) borrowingRecords.stream()
                .filter(r -> r.getReturnDate() != null)
                .count();

        // Create stat cards
        VBox booksCard = createCard("Total Books", String.valueOf(totalBooks));
        VBox membersCard = createCard("Total Members", String.valueOf(totalMembers));
        VBox borrowedCard = createCard("Borrowed Books", String.valueOf(borrowedBooks));
        VBox returnedCard = createCard("Returned Books", String.valueOf(returnedBooks));

        cardsRow.getChildren().addAll(booksCard, membersCard, borrowedCard, returnedCard);

        // Instruction Message
        Label instruction = new Label("Use the navigation panel on the left "
                + "to manage books, members, transactions, and more.");
        instruction.setStyle("-fx-font-size: 16px; -fx-alignment: CENTER;");
        instruction.setWrapText(true);
        instruction.setPadding(new Insets(20, 0, 0, 0));
        instruction.setAlignment(Pos.CENTER);

        // Center Content
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getChildren().addAll(cardsRow, instruction);
        setCenter(centerContent);
    }

    private VBox createCard(String title, String value) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #ffffff; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); "
                + "-fx-background-radius: 8;");
        card.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #555;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
}

package org.library.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.library.model.BorrowingRecord;
import org.library.service.LibraryService;

import java.util.List;

public class BorrowReturnPage extends BorderPane {

    private final LibraryService libraryService;
    private TableView<BorrowingRecord> tableView;
    private Pagination pagination;
    private final int pageSize = 10;

    public BorrowReturnPage(LibraryService libraryService) {
        this.libraryService = libraryService;
        initializeUI();
    }

    private void initializeUI() {
        // Set overall padding for the page
        setPadding(new Insets(30));

        // Top bar: HBox with "Borrow/Return" title and two buttons for Borrow and Return
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 0, 20, 0));

        Label titleLabel = new Label("Borrow/Return");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button borrowBtn = new Button("Borrow Book");
        Button returnBtn = new Button("Return Book");
        borrowBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        returnBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");

        borrowBtn.setOnAction(e -> showBorrowModal());
        returnBtn.setOnAction(e -> showReturnModal());

        topBar.getChildren().addAll(titleLabel, spacer, borrowBtn, returnBtn);
        setTop(topBar);

        // Center: VBox with TableView and Pagination (aligned to top-left)
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20, 0, 0, 0));
        centerBox.setAlignment(Pos.TOP_LEFT);

        tableView = createTableView();
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);

        centerBox.getChildren().addAll(tableView, pagination);
        setCenter(centerBox);

        updateTableData(0);
    }

    // Creates the TableView for Borrowing Records

    private TableView<BorrowingRecord> createTableView() {
        TableView<BorrowingRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        table.setPadding(new Insets(10));

        TableColumn<BorrowingRecord, Integer> recordCol = new TableColumn<>("Record ID");
        recordCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        recordCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<BorrowingRecord, String> bookCol = new TableColumn<>("Book ID");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<BorrowingRecord, Integer> memberCol = new TableColumn<>("Member ID");
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        memberCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<BorrowingRecord, String> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        borrowDateCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<BorrowingRecord, String> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        returnDateCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(recordCol, bookCol, memberCol, borrowDateCol, returnDateCol);
        return table;
    }

    // Called by the Pagination control to create a page.
    private VBox createPage(int pageIndex) {
        updateTableData(pageIndex);
        return new VBox(); // Not used since tableView is updated directly
    }

    private void updateTableData(int pageIndex) {
        List<BorrowingRecord> allRecords = libraryService.getAllBorrowingRecords();
        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, allRecords.size());
        if (start >= allRecords.size()) {
            tableView.setItems(FXCollections.emptyObservableList());
            return;
        }
        List<BorrowingRecord> pageData = allRecords.subList(start, end);
        ObservableList<BorrowingRecord> data = FXCollections.observableArrayList(pageData);
        tableView.setItems(data);

        int totalPages = (int) Math.ceil((double) allRecords.size() / pageSize);
        pagination.setPageCount(totalPages == 0 ? 1 : totalPages);
        pagination.setCurrentPageIndex(pageIndex);
    }

    // MODAL DIALOGS FOR BORROW/RETURN OPERATIONS

    // Modal for borrowing a book

    private void showBorrowModal() {
        Stage modal = createModalStage("Borrow Book");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");
        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");

        Button submitBtn = new Button("Borrow");
        submitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        submitBtn.setOnAction(e -> {
            try {
                String bookId = bookIdField.getText();
                int memberId = Integer.parseInt(memberIdField.getText());
                boolean success = libraryService.borrowBook(bookId, memberId);
                if (!success) {
                    showAlert("Error", "Failed to borrow book (no copies left or invalid data).");
                }
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid Member ID.");
            }
        });

        root.getChildren().addAll(new Label("Book ID:"), bookIdField,
                new Label("Member ID:"), memberIdField, submitBtn);
        Scene scene = new Scene(root, 320, 220);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // Modal for returning a book

    private void showReturnModal() {
        Stage modal = createModalStage("Return Book");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField recordIdField = new TextField();
        recordIdField.setPromptText("Borrowing Record ID");

        Button submitBtn = new Button("Return");
        submitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        submitBtn.setOnAction(e -> {
            try {
                int recordId = Integer.parseInt(recordIdField.getText());
                boolean success = libraryService.returnBook(recordId);
                if (!success) {
                    showAlert("Error", "Failed to return book. Check the record ID or book status.");
                }
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid Record ID.");
            }
        });

        root.getChildren().addAll(new Label("Borrowing Record ID:"), recordIdField, submitBtn);
        Scene scene = new Scene(root, 320, 180);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // Utility Methods

    private Stage createModalStage(String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        return stage;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

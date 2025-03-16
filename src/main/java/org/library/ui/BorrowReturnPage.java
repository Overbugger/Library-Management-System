package org.library.ui;

import javafx.application.Platform;
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
import org.library.model.Book;
import org.library.model.BorrowingRecord;
import org.library.service.LibraryService;
import org.library.utils.DateUtil;

import java.sql.Date;
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
        setPadding(new Insets(30));

        // Top bar with "Borrow/Return" header and two buttons for Borrow and Return
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 0, 20, 0));

        Label titleLabel = new Label("Borrow/Return");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button borrowBtn = new Button("Borrow Book");
        borrowBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        borrowBtn.setOnAction(e -> showBorrowModal());

        Button returnBtn = new Button("Return Book");
        returnBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        returnBtn.setOnAction(e -> showReturnModal());

        HBox rightPane = new HBox(10, borrowBtn, returnBtn);
        rightPane.setAlignment(Pos.CENTER_RIGHT);

        topBar.getChildren().addAll(titleLabel, spacer, rightPane);
        setTop(topBar);

        // Center: VBox with TableView and Pagination
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20, 0, 0, 0));
        centerBox.setAlignment(Pos.TOP_LEFT);

        Label copyTip = new Label("Right-click on the Book ID to copy it.");
        copyTip.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        tableView = createTableView();
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);

        centerBox.getChildren().addAll(copyTip, tableView, pagination);
        setCenter(centerBox);

        updateTableData(0);
    }

    private TableView<BorrowingRecord> createTableView() {
        TableView<BorrowingRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        table.setPadding(new Insets(10));

        // Record ID column
        TableColumn<BorrowingRecord, Integer> recordCol = new TableColumn<>("Record ID");
        recordCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        recordCol.setStyle("-fx-alignment: CENTER;");

        // Book ID column (correct generic type: BorrowingRecord)
        TableColumn<BorrowingRecord, String> bookCol = new TableColumn<>("Book ID");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookCol.setStyle("-fx-alignment: CENTER;");
        bookCol.setCellFactory(col -> {
            TableCell<BorrowingRecord, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item);
                }
            };
            // Create a context menu with a "Copy" option.
            ContextMenu contextMenu = new ContextMenu();
            MenuItem copyItem = new MenuItem("Copy");
            copyItem.setOnAction(e -> {
                javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(cell.getText());
                clipboard.setContent(content);
            });
            contextMenu.getItems().add(copyItem);
            cell.setContextMenu(contextMenu);
            return cell;
        });

        // Member ID column
        TableColumn<BorrowingRecord, Integer> memberCol = new TableColumn<>("Member ID");
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        memberCol.setStyle("-fx-alignment: CENTER;");

        // Borrow Date column (using java.sql.Date and converting to LocalDate)
        TableColumn<BorrowingRecord, java.sql.Date> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        borrowDateCol.setCellFactory(col -> new TableCell<BorrowingRecord, java.sql.Date>() {
            @Override
            protected void updateItem(java.sql.Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText("");
                } else {
                    // Convert java.sql.Date to LocalDate for formatting.
                    setText(DateUtil.formatDate(date.toLocalDate()));
                }
            }
        });
        borrowDateCol.setStyle("-fx-alignment: CENTER;");

        // Return Date column
        TableColumn<BorrowingRecord, java.sql.Date> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        returnDateCol.setCellFactory(col -> new TableCell<BorrowingRecord, java.sql.Date>() {
            @Override
            protected void updateItem(java.sql.Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText("");
                } else {
                    setText(DateUtil.formatDate(date.toLocalDate()));
                }
            }
        });
        returnDateCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(recordCol, bookCol, memberCol, borrowDateCol, returnDateCol);
        return table;
    }


    private VBox createPage(int pageIndex) {
        updateTableData(pageIndex);
        return new VBox();
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

    private void showBorrowModal() {
        Stage modal = createModalStage("Borrow Book");
        modal.setResizable(false);

        Label header = new Label("Borrow Book");
        header.getStyleClass().add("modal-form-title");

        // Use a GridPane to place Book ID and Member ID on separate rows
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label bookIdLabel = new Label("Book ID:");
        bookIdLabel.getStyleClass().add("modal-form-label");
        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Enter Book ID");
        bookIdField.getStyleClass().add("modal-form-input-field");

        Label memberIdLabel = new Label("Member ID:");
        memberIdLabel.getStyleClass().add("modal-form-label");
        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Enter Member ID");
        memberIdField.getStyleClass().add("modal-form-input-field");

        grid.add(bookIdLabel, 0, 0);
        grid.add(bookIdField, 1, 0);
        grid.add(memberIdLabel, 0, 1);
        grid.add(memberIdField, 1, 1);

        Button borrowBtn = new Button("Borrow");
        borrowBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        HBox buttonBox = new HBox(borrowBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox card = new VBox(20, header, grid, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 4);");

        ProgressIndicator modalIndicator = new ProgressIndicator();
        modalIndicator.setVisible(false);

        StackPane modalRoot = new StackPane(card, modalIndicator);
        StackPane.setAlignment(modalIndicator, Pos.CENTER);

        borrowBtn.setOnAction(e -> {
            networkOp(() -> {
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
                    showAlert("Error", "Please enter valid data.");
                }
            }, modalIndicator);
        });

        Scene scene = new Scene(modalRoot, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showReturnModal() {
        Stage modal = createModalStage("Return Book");
        modal.setResizable(false);

        Label header = new Label("Return Book");
        header.getStyleClass().add("modal-form-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label recordLabel = new Label("Record ID:");
        recordLabel.getStyleClass().add("modal-form-label");
        TextField recordField = new TextField();
        recordField.setPromptText("Enter Record ID");
        recordField.getStyleClass().add("modal-form-input-field");

        grid.add(recordLabel, 0, 0);
        grid.add(recordField, 1, 0);

        Button returnBtn = new Button("Return");
        returnBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        HBox buttonBox = new HBox(returnBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox card = new VBox(20, header, grid, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 4);");

        ProgressIndicator modalIndicator = new ProgressIndicator();
        modalIndicator.setVisible(false);

        StackPane modalRoot = new StackPane(card, modalIndicator);
        StackPane.setAlignment(modalIndicator, Pos.CENTER);

        returnBtn.setOnAction(e -> {
            networkOp(() -> {
                try {
                    int recordId = Integer.parseInt(recordField.getText());
                    boolean success = libraryService.returnBook(recordId);
                    if (!success) {
                        showAlert("Error", "Failed to return book. Check the record ID or book status.");
                    }
                    modal.close();
                    updateTableData(pagination.getCurrentPageIndex());
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Please enter a valid Record ID.");
                }
            }, modalIndicator);
        });

        Scene scene = new Scene(modalRoot, 400, 250);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
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

    private void networkOp(Runnable action, ProgressIndicator indicator) {
        indicator.getStyleClass().add("my-progress-indicator");
        Platform.runLater(() -> indicator.setVisible(true));
        new Thread(() -> {
            try {
                Thread.sleep(500); // simulate delay
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            Platform.runLater(() -> {
                action.run();
                indicator.setVisible(false);
            });
        }).start();
    }
}

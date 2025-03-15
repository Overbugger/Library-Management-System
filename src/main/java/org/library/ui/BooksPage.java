package org.library.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.library.model.Book;
import org.library.service.LibraryService;
import org.library.utils.CSVProvider;

import java.io.File;
import java.util.List;

public class BooksPage extends BorderPane {

    private final LibraryService libraryService;
    private TableView<Book> tableView;
    private Pagination pagination;
    private final int pageSize = 10;

    public BooksPage(LibraryService libraryService) {
        this.libraryService = libraryService;
        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(30));

        // HBox with "Books" title, "Add Book" and "Export CSV" buttons
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 0, 20, 0));

        Label titleLabel = new Label("Books");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBookBtn = new Button("Add Book");
        Button exportCSVBtn = new Button("Export CSV");
        addBookBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        exportCSVBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");

        addBookBtn.setOnAction(e -> showAddBookModal());
        exportCSVBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File file = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (file != null) {
                CSVProvider.exportBooksToCSV(libraryService.getAllBooks(), file.getAbsolutePath());
                showAlert("Export Success", "Books exported to " + file.getAbsolutePath());
            }
        });

        topBar.getChildren().addAll(titleLabel, spacer, addBookBtn, exportCSVBtn);
        setTop(topBar);

        // Create a VBox for the table and pagination.
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20, 0, 0, 0));
        centerBox.setAlignment(Pos.TOP_LEFT);  // Left-align the content

        tableView = createTableView();
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);

        centerBox.getChildren().addAll(tableView, pagination);
        setCenter(centerBox);

        updateTableData(0);
    }

    // Creates the TableView with columns, including the "..." menu button in each row.

    private TableView<Book> createTableView() {
        TableView<Book> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        table.setPadding(new Insets(10));

        TableColumn<Book, String> idCol = new TableColumn<>("Book ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setStyle("-fx-alignment: CENTER;");


        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Book, Integer> copiesCol = new TableColumn<>("Copies");
        copiesCol.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        copiesCol.setStyle("-fx-alignment: CENTER;");

        // Action column with vertical
        TableColumn<Book, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setStyle("-fx-alignment: CENTER;");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final MenuButton menuButton = new MenuButton("...");

            {
                MenuItem viewItem = new MenuItem("View");
                viewItem.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showViewBookModal(book);
                });
                MenuItem updateItem = new MenuItem("Update");
                updateItem.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showUpdateBookModal(book);
                });
                MenuItem borrowItem = new MenuItem("Borrow");
                borrowItem.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showBorrowBookModal(book);
                });
                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(book);
                });
                menuButton.getItems().addAll(viewItem, updateItem, borrowItem, deleteItem);
                menuButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-min-width: 100");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : menuButton);
            }
        });

        table.getColumns().addAll(idCol, titleCol, authorCol, copiesCol, actionCol);
        return table;
    }

    // Called by the Pagination control to create a page

    private VBox createPage(int pageIndex) {
        updateTableData(pageIndex);
        return new VBox();
    }

    private void updateTableData(int pageIndex) {
        List<Book> allBooks = libraryService.getAllBooks();
        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, allBooks.size());
        if (start >= allBooks.size()) {
            tableView.setItems(FXCollections.emptyObservableList());
            return;
        }
        List<Book> pageData = allBooks.subList(start, end);
        ObservableList<Book> data = FXCollections.observableArrayList(pageData);
        tableView.setItems(data);

        int totalPages = (int) Math.ceil((double) allBooks.size() / pageSize);
        pagination.setPageCount(totalPages == 0 ? 1 : totalPages);
        pagination.setCurrentPageIndex(pageIndex);
    }

    // MODAL DIALOGS FOR BOOK OPERATIONS

    private void showAddBookModal() {
        Stage modal = createModalStage("Add Book");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        TextField genreField = new TextField();
        genreField.setPromptText("Genre");
        TextField copiesField = new TextField();
        copiesField.setPromptText("Copies");

        Button submitBtn = new Button("Add");
        submitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        submitBtn.setOnAction(e -> {
            try {
                String title = titleField.getText();
                String author = authorField.getText();
                String genre = genreField.getText();
                int copies = Integer.parseInt(copiesField.getText());

                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(author);
                book.setGenre(genre);
                book.setAvailableCopies(copies);

                libraryService.addBook(book);
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            } catch (NumberFormatException ex) {
                showAlert("Error", "Copies must be a valid integer.");
            }
        });

        root.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("Genre:"), genreField,
                new Label("Copies:"), copiesField,
                submitBtn
        );
        Scene scene = new Scene(root, 320, 350);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showViewBookModal(Book book) {
        Stage modal = createDarkOverlayModal("View Book");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label infoLabel = new Label("Book Details");
        infoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label details = new Label(
                "ID: " + book.getBookId() + "\n" +
                        "Title: " + book.getTitle() + "\n" +
                        "Author: " + book.getAuthor() + "\n" +
                        "Genre: " + book.getGenre() + "\n" +
                        "Copies: " + book.getAvailableCopies()
        );
        details.setStyle("-fx-font-size: 14px;");

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> modal.close());

        root.getChildren().addAll(infoLabel, details, closeBtn);
        Scene scene = new Scene(root, 350, 250);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showUpdateBookModal(Book book) {
        Stage modal = createModalStage("Update Book");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField genreField = new TextField(book.getGenre());
        TextField copiesField = new TextField(String.valueOf(book.getAvailableCopies()));

        Button updateBtn = new Button("Update");
        updateBtn.setOnAction(e -> {
            try {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setGenre(genreField.getText());
                book.setAvailableCopies(Integer.parseInt(copiesField.getText()));

                libraryService.updateBook(book);
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            } catch (NumberFormatException ex) {
                showAlert("Error", "Copies must be a valid integer.");
            }
        });

        root.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("Genre:"), genreField,
                new Label("Copies:"), copiesField,
                updateBtn
        );
        Scene scene = new Scene(root, 320, 350);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showBorrowBookModal(Book book) {
        Stage modal = createModalStage("Borrow Book");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label infoLabel = new Label("Borrowing Book: " + book.getTitle());
        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");

        Button borrowBtn = new Button("Borrow");
        borrowBtn.setOnAction(e -> {
            try {
                int memberId = Integer.parseInt(memberIdField.getText());
                boolean success = libraryService.borrowBook(book.getBookId(), memberId);
                if (!success) {
                    showAlert("Error", "Failed to borrow book (no copies left or invalid data).");
                }
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid Member ID.");
            }
        });

        root.getChildren().addAll(infoLabel, new Label("Member ID:"), memberIdField, borrowBtn);
        Scene scene = new Scene(root, 320, 220);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showDeleteConfirmation(Book book) {
        Stage modal = createDarkOverlayModal("Delete Book");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label confirmLabel = new Label("Are you sure you want to delete:\n" + book.getTitle() + "?");
        Button deleteBtn = new Button("Delete");
        Button cancelBtn = new Button("Cancel");

        deleteBtn.setOnAction(e -> {
            libraryService.deleteBook(book.getBookId());
            modal.close();
            updateTableData(pagination.getCurrentPageIndex());
        });
        cancelBtn.setOnAction(e -> modal.close());

        HBox btnBox = new HBox(10, deleteBtn, cancelBtn);
        btnBox.setAlignment(Pos.CENTER);
        root.getChildren().addAll(confirmLabel, btnBox);
        Scene scene = new Scene(root, 320, 180);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // Utility methods for creating modals and alerts
    private Stage createModalStage(String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        return stage;
    }
    private Stage createDarkOverlayModal(String title) {
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

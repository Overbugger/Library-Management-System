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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ProgressIndicator;
import org.library.model.Book;
import org.library.service.LibraryService;
import org.library.utils.CSVProvider;

import java.io.File;
import java.util.List;

import static org.library.utils.UI.*;

public class BooksPage extends BorderPane {

    private final LibraryService libraryService;
    private TableView<Book> tableView;
    private Pagination pagination;
    private ProgressIndicator loadingIndicator;
    final int pageSize = 5;

    // A reference to the search TextField
    private TextField searchField;
    private List<Book> searchResults = null;

    public BooksPage(LibraryService libraryService) {
        this.libraryService = libraryService;
        initializeUI();
    }

    private void initializeUI() {
        // Overall padding
        setPadding(new Insets(30));

        // Top bar with header, search field, and Add/Export buttons
        setTop(createTopBar());

        // Create a VBox for the table and pagination.
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20, 0, 0, 0));
        centerBox.setAlignment(Pos.TOP_LEFT);
        tableView = createTableView();
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);
        centerBox.getChildren().addAll(tableView, pagination);

        // The centerBox and global loading indicator
        StackPane centerStack = new StackPane();
        centerStack.getChildren().add(centerBox);

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.getStyleClass().add("my-progress-indicator");

        loadingIndicator.setVisible(false);
        centerStack.getChildren().add(loadingIndicator);
        StackPane.setAlignment(loadingIndicator, Pos.CENTER);

        setCenter(centerStack);

        // Load initial data with pagination
        updateTableData(0);
    }

    // Top bar with a "Books" header on the left, a single search field in the center, and "Add Book" and "Export CSV" buttons on the right.
    private BorderPane createTopBar() {
        Label headerLabel = new Label("Books");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        searchField = new TextField();
        searchField.setPromptText("Search by title, author, or genre");
        searchField.setStyle("-fx-pref-width: 300px; -fx-pref-height: 35px;");
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        HBox searchPane = new HBox(10, searchField, searchButton);
        searchPane.setAlignment(Pos.CENTER);

        searchButton.setOnAction(e -> {
            String searchText = searchField.getText().trim();
            networkOp(() -> {
                List<Book> results = libraryService.searchBooks(searchText);
                Platform.runLater(() -> {
                    if (results.isEmpty()) {
                        showAlert("No Results", "No book found matching the criteria.");
                        searchResults = null;
                        updateTableData(0);
                    } else {
                        searchResults = results;
                        updateTableData(0);
                    }
                });
            }, loadingIndicator);
        });

        Button addBookBtn = new Button("Add Book");
        addBookBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        addBookBtn.setOnAction(e -> showAddBookModal());
        Button exportCSVBtn = new Button("Export CSV");
        exportCSVBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
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


        HBox rightPane = new HBox(10, addBookBtn, exportCSVBtn);
        rightPane.setAlignment(Pos.CENTER_RIGHT);

        // Assemble the top bar using a BorderPane layout
        BorderPane topBar = new BorderPane();
        topBar.setLeft(headerLabel);
        topBar.setCenter(searchPane);
        topBar.setRight(rightPane);
        topBar.setPadding(new Insets(10, 0, 20, 0));
        return topBar;
    }

    // The TableView with columns for Book ID, Title, Author, Copies, and an Actions column containing a MenuButton

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

        TableColumn<Book, Integer> copiesCol = new TableColumn<>("Available Copies");
        copiesCol.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        copiesCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Book, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setStyle("-fx-alignment: CENTER;");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final MenuButton menuButton = new MenuButton("...");

            {
                // Apply custom CSS class for styling the action menu.
                menuButton.getStyleClass().add("action-menu");

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

    // Called by the Pagination control to create a page.

    private VBox createPage(int pageIndex) {
        updateTableData(pageIndex);
        return new VBox();
    }

    // Updates the table with paginated data from the complete list of books.

    private void updateTableData(int pageIndex) {
        List<Book> books;
        if (searchResults != null) {
            books = searchResults;
        } else {
            books = libraryService.getAllBooks();
        }
        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, books.size());
        if (start >= books.size()) {
            tableView.setItems(FXCollections.emptyObservableList());
            return;
        }
        List<Book> pageData = books.subList(start, end);
        ObservableList<Book> data = FXCollections.observableArrayList(pageData);
        tableView.setItems(data);

        int totalPages = (int) Math.ceil((double) books.size() / pageSize);
        pagination.setPageCount(totalPages == 0 ? 1 : totalPages);
        pagination.setCurrentPageIndex(pageIndex);
    }


    // MODAL DIALOGS FOR BOOK OPERATIONS

    private void showAddBookModal() {
        Stage modal = createModalStage("Add Book");
        modal.setResizable(false);

        Label header = new Label("Add New Book");
        header.getStyleClass().add("modal-form-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label titleLabel = new Label("Title:");
        titleLabel.getStyleClass().add("modal-form-label");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter book title");
        titleField.getStyleClass().add("modal-form-input-field");

        Label authorLabel = new Label("Author:");
        authorLabel.getStyleClass().add("modal-form-label");
        TextField authorField = new TextField();
        authorField.setPromptText("Enter author name");

        Label genreLabel = new Label("Genre:");
        genreLabel.getStyleClass().add("modal-form-label");
        TextField genreField = new TextField();
        genreField.setPromptText("Enter genre");

        Label copiesLabel = new Label("Copies:");
        copiesLabel.getStyleClass().add("modal-form-label");
        TextField copiesField = new TextField();
        copiesField.setPromptText("Enter number of copies");

        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(authorLabel, 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(genreLabel, 0, 2);
        grid.add(genreField, 1, 2);
        grid.add(copiesLabel, 0, 3);
        grid.add(copiesField, 1, 3);

        Button submitBtn = new Button("Add Book");
        submitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        HBox buttonBox = new HBox(submitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox modalContent = new VBox(20, header, grid, buttonBox);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setPadding(new Insets(20));
        modalContent.setStyle("-fx-background-color: #ffffff; "
                + "-fx-background-radius: 8; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);");

        ProgressIndicator modalIndicator = new ProgressIndicator();
        modalIndicator.setVisible(false);

        StackPane modalRoot = new StackPane(modalContent, modalIndicator);
        StackPane.setAlignment(modalIndicator, Pos.CENTER);

        submitBtn.setOnAction(e -> {
            networkOp(() -> {
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
            }, modalIndicator);
        });

        Scene scene = new Scene(modalRoot, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showViewBookModal(Book book) {
        Stage modal = createModalStage("Book Details");
        modal.setResizable(false);

        Label header = new Label("Book Details");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label idLabel = new Label("ID:");
        Label idValue = new Label(book.getBookId());

        Label titleLabel = new Label("Title:");
        Label titleValue = new Label(book.getTitle());

        Label authorLabel = new Label("Author:");
        Label authorValue = new Label(book.getAuthor());

        Label genreLabel = new Label("Genre:");
        Label genreValue = new Label(book.getGenre());

        Label copiesLabel = new Label("Copies:");
        Label copiesValue = new Label(String.valueOf(book.getAvailableCopies()));

        idLabel.setStyle("-fx-font-weight: bold;");
        titleLabel.setStyle("-fx-font-weight: bold;");
        authorLabel.setStyle("-fx-font-weight: bold;");
        genreLabel.setStyle("-fx-font-weight: bold;");
        copiesLabel.setStyle("-fx-font-weight: bold;");

        grid.add(idLabel, 0, 0);
        grid.add(idValue, 1, 0);
        grid.add(titleLabel, 0, 1);
        grid.add(titleValue, 1, 1);
        grid.add(authorLabel, 0, 2);
        grid.add(authorValue, 1, 2);
        grid.add(genreLabel, 0, 3);
        grid.add(genreValue, 1, 3);
        grid.add(copiesLabel, 0, 4);
        grid.add(copiesValue, 1, 4);

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        closeBtn.setOnAction(e -> modal.close());
        HBox buttonBox = new HBox(closeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 20, 0));

        VBox card = new VBox(15, header, grid, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        StackPane modalRoot = new StackPane(card);

        Scene scene = new Scene(modalRoot, 400, 350);
        modal.setScene(scene);
        modal.showAndWait();
    }


    private void showUpdateBookModal(Book book) {
        Stage modal = createModalStage("Update Book");
        modal.setResizable(false);

        Label header = new Label("Update Book");
        header.getStyleClass().add("modal-form-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label titleLabel = new Label("Title:");
        titleLabel.getStyleClass().add("modal-form-label");
        TextField titleField = new TextField(book.getTitle());
        titleField.getStyleClass().add("modal-form-input-field");


        Label authorLabel = new Label("Author:");
        authorLabel.getStyleClass().add("modal-form-label");
        TextField authorField = new TextField(book.getAuthor());

        Label genreLabel = new Label("Genre:");
        genreLabel.getStyleClass().add("modal-form-label");
        TextField genreField = new TextField(book.getGenre());

        Label copiesLabel = new Label("Copies:");
        copiesLabel.getStyleClass().add("modal-form-label");
        TextField copiesField = new TextField(String.valueOf(book.getAvailableCopies()));

        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(authorLabel, 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(genreLabel, 0, 2);
        grid.add(genreField, 1, 2);
        grid.add(copiesLabel, 0, 3);
        grid.add(copiesField, 1, 3);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        HBox buttonBox = new HBox(updateBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        VBox card = new VBox(20, header, grid, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        ProgressIndicator modalIndicator = new ProgressIndicator();
        modalIndicator.setVisible(false);

        StackPane modalRoot = new StackPane(card, modalIndicator);
        StackPane.setAlignment(modalIndicator, Pos.CENTER);

        updateBtn.setOnAction(e -> {
            networkOp(() -> {
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
            }, modalIndicator);
        });

        Scene scene = new Scene(modalRoot, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showBorrowBookModal(Book book) {
        Stage modal = createModalStage("Borrow Book");
        modal.setResizable(false);

        Label header = new Label("Borrow " + book.getTitle());
        header.getStyleClass().add("modal-form-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label memberIdLabel = new Label("Member ID:");
        memberIdLabel.getStyleClass().add("modal-form-label");

        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Enter Member ID");
        memberIdField.getStyleClass().add("modal-form-input-field");

        // Place the label and field in one row.
        grid.add(memberIdLabel, 0, 0);
        grid.add(memberIdField, 1, 0);

        // Borrow button container.
        Button borrowBtn = new Button("Borrow");
        borrowBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        HBox buttonBox = new HBox(borrowBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox card = new VBox(20, header, grid, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        ProgressIndicator modalIndicator = new ProgressIndicator();
        modalIndicator.setVisible(false);
        modalIndicator.getStyleClass().add("my-progress-indicator");

        StackPane modalRoot = new StackPane(card, modalIndicator);
        StackPane.setAlignment(modalIndicator, Pos.CENTER);

        borrowBtn.setOnAction(e -> {
            networkOp(() -> {
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
            }, modalIndicator);
        });

        Scene scene = new Scene(modalRoot, 400, 250);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showDeleteConfirmation(Book book) {
        Stage modal = createModalStage("Delete Book");
        modal.setResizable(false);

        Label header = new Label("Delete Book");
        header.getStyleClass().add("modal-form-title");

        Label confirmLabel = new Label("Are you sure you want to delete:\n" + book.getTitle() + "?");
        confirmLabel.getStyleClass().add("modal-form-label");

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("modal-warning");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");

        HBox buttonBox = new HBox(10, deleteBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox card = new VBox(20, header, confirmLabel, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        ProgressIndicator modalIndicator = new ProgressIndicator();
        modalIndicator.setVisible(false);

        StackPane modalRoot = new StackPane(card, modalIndicator);
        modalRoot.setPadding(new Insets(20));

        deleteBtn.setOnAction(e -> {
            networkOp(() -> {
                libraryService.deleteBook(book.getBookId());
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            }, modalIndicator);
        });
        cancelBtn.setOnAction(e -> modal.close());

        Scene scene = new Scene(modalRoot, 350, 200);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        modal.setScene(scene);
        modal.showAndWait();
    }


}

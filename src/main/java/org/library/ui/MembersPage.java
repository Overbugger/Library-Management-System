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
import javafx.stage.Stage;
import org.library.model.Member;
import org.library.service.LibraryService;
import org.library.utils.CSVProvider;

import java.io.File;
import java.util.List;

import static org.library.utils.UI.*;

public class MembersPage extends BorderPane {

    private final LibraryService libraryService;
    private TableView<Member> tableView;
    private Pagination pagination;
    private final int pageSize = 5;

    public MembersPage(LibraryService libraryService) {
        this.libraryService = libraryService;
        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(30));

        // Top bar with "Members" header and buttons.
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 0, 20, 0));

        Label titleLabel = new Label("Members");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addMemberBtn = new Button("Add Member");
        addMemberBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        addMemberBtn.setOnAction(e -> showAddMemberModal());
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
                CSVProvider.exportMembersToCSV(libraryService.getAllMembers(), file.getAbsolutePath());
                showAlert("Export Success", "Members exported to " + file.getAbsolutePath());
            }
        });

        HBox rightPane = new HBox(10, addMemberBtn, exportCSVBtn);
        rightPane.setAlignment(Pos.CENTER_RIGHT);
        topBar.getChildren().addAll(titleLabel, spacer, rightPane);
        setTop(topBar);

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

    private TableView<Member> createTableView() {
        TableView<Member> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        table.setPadding(new Insets(10));

        TableColumn<Member, Integer> idCol = new TableColumn<>("Member ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Member, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Member, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setStyle("-fx-alignment: CENTER;");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final MenuButton menuButton = new MenuButton("...");

            {
                menuButton.getStyleClass().add("action-menu");

                MenuItem viewItem = new MenuItem("View");
                viewItem.setOnAction(e -> {
                    Member member = getTableView().getItems().get(getIndex());
                    showViewMemberModal(member);
                });
                MenuItem updateItem = new MenuItem("Update");
                updateItem.setOnAction(e -> {
                    Member member = getTableView().getItems().get(getIndex());
                    showUpdateMemberModal(member);
                });
                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction(e -> {
                    Member member = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(member);
                });
                menuButton.getItems().addAll(viewItem, updateItem, deleteItem);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : menuButton);
            }
        });

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, actionCol);
        return table;
    }

    private VBox createPage(int pageIndex) {
        updateTableData(pageIndex);
        return new VBox();
    }

    private void updateTableData(int pageIndex) {
        List<Member> allMembers = libraryService.getAllMembers();
        int start = pageIndex * pageSize;
        int end = Math.min(start + pageSize, allMembers.size());
        if (start >= allMembers.size()) {
            tableView.setItems(FXCollections.emptyObservableList());
            return;
        }
        List<Member> pageData = allMembers.subList(start, end);
        ObservableList<Member> data = FXCollections.observableArrayList(pageData);
        tableView.setItems(data);

        int totalPages = (int) Math.ceil((double) allMembers.size() / pageSize);
        pagination.setPageCount(totalPages == 0 ? 1 : totalPages);
        pagination.setCurrentPageIndex(pageIndex);
    }

    // MODAL DIALOGS FOR MEMBER OPERATIONS

    private void showAddMemberModal() {
        Stage modal = createModalStage("Add Member");
        modal.setResizable(false);

        Label header = new Label("Add New Member");
        header.getStyleClass().add("modal-form-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("modal-form-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter member name");
        nameField.getStyleClass().add("modal-form-input-field");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("modal-form-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter member email");
        emailField.getStyleClass().add("modal-form-input-field");

        Label phoneLabel = new Label("Phone:");
        phoneLabel.getStyleClass().add("modal-form-label");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter member phone");
        phoneField.getStyleClass().add("modal-form-input-field");

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);

        Button submitBtn = new Button("Add Member");
        submitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        HBox buttonBox = new HBox(submitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        VBox card = new VBox(20, header, grid, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        ProgressIndicator modalIndicator = new ProgressIndicator();
        modalIndicator.setVisible(false);

        StackPane modalRoot = new StackPane(card, modalIndicator);
        StackPane.setAlignment(modalIndicator, Pos.CENTER);

        submitBtn.setOnAction(e -> {
            networkOp(() -> {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();

                Member member = new Member();
                member.setName(name);
                member.setEmail(email);
                member.setPhone(phone);

                libraryService.addMember(member);
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            }, modalIndicator);
        });

        Scene scene = new Scene(modalRoot, 400, 320);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showViewMemberModal(Member member) {
        Stage modal = createModalStage("View Member");
        modal.setResizable(false);

        Label header = new Label("Member Details");
        header.getStyleClass().add("modal-form-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label idLabel = new Label("ID:");
        idLabel.getStyleClass().add("modal-form-label");
        Label idValue = new Label(String.valueOf(member.getMemberId()));

        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("modal-form-label");
        Label nameValue = new Label(member.getName());

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("modal-form-label");
        Label emailValue = new Label(member.getEmail());

        Label phoneLabel = new Label("Phone:");
        phoneLabel.getStyleClass().add("modal-form-label");
        Label phoneValue = new Label(member.getPhone());

        grid.add(idLabel, 0, 0);
        grid.add(idValue, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameValue, 1, 1);
        grid.add(emailLabel, 0, 2);
        grid.add(emailValue, 1, 2);
        grid.add(phoneLabel, 0, 3);
        grid.add(phoneValue, 1, 3);

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        closeBtn.setOnAction(e -> modal.close());
        HBox buttonBox = new HBox(closeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 20, 0));

        VBox card = new VBox(20, header, grid, buttonBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        StackPane modalRoot = new StackPane(card);
        modalRoot.setPadding(new Insets(20));

        Scene scene = new Scene(modalRoot, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showUpdateMemberModal(Member member) {
        Stage modal = createModalStage("Update Member");
        modal.setResizable(false);

        Label header = new Label("Update Member");
        header.getStyleClass().add("modal-form-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("modal-form-label");
        TextField nameField = new TextField(member.getName());
        nameField.getStyleClass().add("modal-form-input-field");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("modal-form-label");
        TextField emailField = new TextField(member.getEmail());
        emailField.getStyleClass().add("modal-form-input-field");

        Label phoneLabel = new Label("Phone:");
        phoneLabel.getStyleClass().add("modal-form-label");
        TextField phoneField = new TextField(member.getPhone());
        phoneField.getStyleClass().add("modal-form-input-field");

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);

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
                member.setName(nameField.getText());
                member.setEmail(emailField.getText());
                member.setPhone(phoneField.getText());

                libraryService.updateMember(member);
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            }, modalIndicator);
        });

        Scene scene = new Scene(modalRoot, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showDeleteConfirmation(Member member) {
        Stage modal = createModalStage("Delete Member");
        modal.setResizable(false);

        Label header = new Label("Delete Member");
        header.getStyleClass().add("modal-form-title");

        Label confirmLabel = new Label("Are you sure you want to delete:\n" + member.getName() + "?");
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
                libraryService.deleteMember(member.getMemberId());
                modal.close();
                updateTableData(pagination.getCurrentPageIndex());
            }, modalIndicator);
        });
        cancelBtn.setOnAction(e -> modal.close());

        Scene scene = new Scene(modalRoot, 400, 200);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        modal.setScene(scene);
        modal.showAndWait();
    }
}

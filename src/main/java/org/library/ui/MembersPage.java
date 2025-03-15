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
import org.library.model.Member;
import org.library.service.LibraryService;
import org.library.utils.CSVProvider;

import java.io.File;
import java.util.List;

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

        // HBox with "Members" title, "Add Member" and "Export CSV" buttons.
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 0, 20, 0));

        Label titleLabel = new Label("Members");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addMemberBtn = new Button("Add Member");
        Button exportCSVBtn = new Button("Export CSV");
        addMemberBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
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

        topBar.getChildren().addAll(titleLabel, spacer, addMemberBtn, exportCSVBtn);
        setTop(topBar);

        // VBox with TableView and Pagination, aligned to top-left.
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

    // Create the TableView for members

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

        // vertical "..." button that opens a menu with View, Update, Delete.
        TableColumn<Member, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setStyle("-fx-alignment: CENTER;");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final MenuButton menuButton = new MenuButton("...");

            {
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
                menuButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
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

    // Called by the Pagination control to create a page
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
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        Button submitBtn = new Button("Add");
        submitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        submitBtn.setOnAction(e -> {
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
        });

        root.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Phone:"), phoneField,
                submitBtn
        );
        Scene scene = new Scene(root, 320, 320);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showViewMemberModal(Member member) {
        Stage modal = createDarkOverlayModal("View Member");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label infoLabel = new Label("Member Details");
        infoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label details = new Label(
                "ID: " + member.getMemberId() + "\n" +
                        "Name: " + member.getName() + "\n" +
                        "Email: " + member.getEmail() + "\n" +
                        "Phone: " + member.getPhone()
        );
        details.setStyle("-fx-font-size: 14px;");

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> modal.close());

        root.getChildren().addAll(infoLabel, details, closeBtn);
        Scene scene = new Scene(root, 350, 250);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showUpdateMemberModal(Member member) {
        Stage modal = createModalStage("Update Member");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField nameField = new TextField(member.getName());
        TextField emailField = new TextField(member.getEmail());
        TextField phoneField = new TextField(member.getPhone());

        Button updateBtn = new Button("Update");
        updateBtn.setOnAction(e -> {
            member.setName(nameField.getText());
            member.setEmail(emailField.getText());
            member.setPhone(phoneField.getText());

            libraryService.updateMember(member);
            modal.close();
            updateTableData(pagination.getCurrentPageIndex());
        });

        root.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Phone:"), phoneField,
                updateBtn
        );
        Scene scene = new Scene(root, 320, 320);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showDeleteConfirmation(Member member) {
        Stage modal = createDarkOverlayModal("Delete Member");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label confirmLabel = new Label("Are you sure you want to delete:\n" + member.getName() + "?");
        Button deleteBtn = new Button("Delete");
        Button cancelBtn = new Button("Cancel");

        deleteBtn.setOnAction(e -> {
            libraryService.deleteMember(member.getMemberId());
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

    // Utility methods for modals and alerts

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
        // For a more realistic dark overlay effect, you might apply additional CSS.
        return stage;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

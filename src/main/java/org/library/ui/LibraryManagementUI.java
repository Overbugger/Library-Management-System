package org.library.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.library.service.LibraryService;
import static org.library.utils.UI.networkOp;

public class LibraryManagementUI extends Application {

    private BorderPane root;
    private VBox sidebar;
    private StackPane centerContent;
    private ProgressIndicator loadingIndicator;
    private final LibraryService libraryService = new LibraryService();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DreamDevs Library Management Dashboard");

        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create sidebar with active/hover indicators
        sidebar = createSidebar();
        root.setLeft(sidebar);

        // Create center content area with a loading indicator overlay
        centerContent = new StackPane();
        centerContent.setPadding(new Insets(10));
        // Set the initial content to the DashboardPage
        DashboardPage dashboardPage = new DashboardPage(libraryService);
        centerContent.getChildren().add(dashboardPage);
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        centerContent.getChildren().add(loadingIndicator);
        StackPane.setAlignment(loadingIndicator, Pos.CENTER);
        root.setCenter(centerContent);

        // Set initial active sidebar button (Dashboard)
        // Assuming the first button in the sidebar is Dashboard
        if (!sidebar.getChildren().isEmpty() && sidebar.getChildren().get(0) instanceof Button) {
            setActiveSidebarButton((Button) sidebar.getChildren().get(0));
        }

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create the sidebar with navigation buttons
    private VBox createSidebar() {
        VBox side = new VBox(15);
        side.setPadding(new Insets(10));
        side.getStyleClass().add("sidebar");
        side.setPrefWidth(200);

        Button btnDashboard = createSidebarButton("Dashboard");
        Button btnBooks = createSidebarButton("Books");
        Button btnMembers = createSidebarButton("Members");
        Button btnBorrowReturn = createSidebarButton("Borrow/Return");
        Button btnExit = createSidebarButton("Exit");

        // Sidebar actions with active style and loading indicator
        btnDashboard.setOnAction(e -> {
            setActiveSidebarButton(btnDashboard);
           networkOp(() -> {
                DashboardPage dashboardPage = new DashboardPage(libraryService);
                updateCenterContent(dashboardPage);
            }, loadingIndicator);
        });
        btnBooks.setOnAction(e -> {
            setActiveSidebarButton(btnBooks);
            networkOp(() -> {
                BooksPage booksPage = new BooksPage(libraryService);
                updateCenterContent(booksPage);
            }, loadingIndicator);
        });
        btnMembers.setOnAction(e -> {
            setActiveSidebarButton(btnMembers);
            networkOp(() -> {
                MembersPage membersPage = new MembersPage(libraryService);
                updateCenterContent(membersPage);
            }, loadingIndicator);
        });
        btnBorrowReturn.setOnAction(e -> {
            setActiveSidebarButton(btnBorrowReturn);
            networkOp(() -> {
                BorrowReturnPage borrowReturnPage = new BorrowReturnPage(libraryService);
                updateCenterContent(borrowReturnPage);
            }, loadingIndicator);
        });
        btnExit.setOnAction(e -> Platform.exit());

        side.getChildren().addAll(btnDashboard, btnBooks, btnMembers, btnBorrowReturn, btnExit);
        return side;
    }

    // Helper to create a styled sidebar button
    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    // Update the center content using a Region (like a DashboardPage, BooksPage, etc.)
    private void updateCenterContent(Region node) {
        Platform.runLater(() -> {
            centerContent.getChildren().removeIf(n -> !(n instanceof ProgressIndicator));
            centerContent.getChildren().add(0, node);
        });
    }

    // Set the active style on the sidebar buttons
    private void setActiveSidebarButton(Button activeButton) {
        for (javafx.scene.Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("active");
            }
        }
        activeButton.getStyleClass().add("active");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

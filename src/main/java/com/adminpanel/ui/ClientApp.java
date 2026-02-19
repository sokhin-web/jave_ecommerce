package com.adminpanel.ui;

import com.adminpanel.model.Category;
import com.adminpanel.model.User;
import com.adminpanel.ui.client.*;
import com.adminpanel.util.ApplicationState;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private BorderPane mainLayout;
    private Category selectedCategory = null;
    private int userId;
    private ProductGridView productGridView;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Get the current logged-in user or use default
            User currentUser = ApplicationState.getCurrentUser();
            userId = (currentUser != null) ? currentUser.getId() : 3;

            mainLayout = new BorderPane();

            // Top Header
            HBox header = createHeader();
            mainLayout.setTop(header);

            // Center Content Area
            productGridView = new ProductGridView(userId, () -> {
                // Optional: Update cart badge or similar if needed
            });
            
            ScrollPane scrollPane = new ScrollPane(productGridView.getView());
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f5f5f5;");
            mainLayout.setCenter(scrollPane);

            // Left Sidebar with Categories
            updateSidebar();

            Scene scene = new Scene(mainLayout, 1400, 800);
            primaryStage.setTitle("Client Panel - Shopping");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting ClientApp: " + e.getMessage());
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setStyle("-fx-background-color: #4FB3E8;");

        Label title = new Label("🛒 Client Shopping Panel");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Cart button
        Button cartButton = new Button("🛒 My Cart");
        cartButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        cartButton.setStyle("-fx-background-color: white; -fx-text-fill: #4FB3E8; " +
                "-fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        cartButton.setOnMouseEntered(e -> cartButton.setStyle("-fx-background-color: #e0e0e0; " +
                "-fx-text-fill: #4FB3E8; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        cartButton.setOnMouseExited(e -> cartButton.setStyle("-fx-background-color: white; " +
                "-fx-text-fill: #4FB3E8; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        cartButton.setOnAction(e -> new ClientCartView(userId).show());

        // Settings button
        Button settingsBtn = new Button("⚙️ Settings");
        settingsBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        settingsBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4FB3E8; " +
                "-fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        settingsBtn.setOnMouseEntered(e -> settingsBtn.setStyle("-fx-background-color: #e0e0e0; " +
                "-fx-text-fill: #4FB3E8; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        settingsBtn.setOnMouseExited(e -> settingsBtn.setStyle("-fx-background-color: white; " +
                "-fx-text-fill: #4FB3E8; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        settingsBtn.setOnAction(e -> new ClientSettingsView().show());

        // Logout button
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logoutBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #d32f2f; " +
                "-fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: #f44336; " +
                "-fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        logoutBtn.setOnAction(e -> handleClientLogout());

        header.getChildren().addAll(title, spacer, cartButton, settingsBtn, logoutBtn);
        return header;
    }

    private void updateSidebar() {
        CategorySidebar sidebar = new CategorySidebar(category -> {
            this.selectedCategory = category;
            updateSidebar();
            productGridView.setCategory(category);
        });
        mainLayout.setLeft(sidebar.getView(selectedCategory));
    }

    private void handleClientLogout() {
        // Clear current user
        ApplicationState.logout();

        // Go back to login screen
        Stage window = (Stage) mainLayout.getScene().getWindow();
        try {
            com.adminpanel.auth.LoginApp loginApp = new com.adminpanel.auth.LoginApp();
            loginApp.start(window);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Logout Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to logout. Please restart the application.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

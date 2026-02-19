package com.adminpanel.ui;

import com.adminpanel.model.Cart;
import com.adminpanel.model.Category;
import com.adminpanel.model.Product;
import com.adminpanel.model.User;
import com.adminpanel.store.DataStore;
import com.adminpanel.model.CartItem;
import com.adminpanel.ui.client.*;
import com.adminpanel.util.ApplicationState;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
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
    private DataStore dataStore = DataStore.getInstance();

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
        Button settingsBtn = new Button("👤 Profile");
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

    private VBox createOrderCard(Cart cart) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // Order header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label orderIdLabel = new Label("Order #" + cart.getId());
        orderIdLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalLabel = new Label(String.format("$%.2f", cart.getTotalPrice()));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalLabel.setTextFill(Color.web("#4FB3E8"));

        header.getChildren().addAll(orderIdLabel, spacer, totalLabel);

        // Order items
        ObservableList<CartItem> items = dataStore.getCartItemsByCartId(cart.getId());
        VBox itemsList = new VBox(5);

        for (CartItem item : items) {
            Product product = dataStore.getProductById(item.getProductId());
            if (product != null) {
                HBox itemRow = new HBox(10);
                itemRow.setAlignment(Pos.CENTER_LEFT);
                itemRow.setPadding(new Insets(5, 0, 5, 10));

                Label itemLabel = new Label(String.format("%s %s × %d",
                        product.getImage(), product.getName(), item.getQuantity()));
                itemLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));

                Region itemSpacer = new Region();
                HBox.setHgrow(itemSpacer, Priority.ALWAYS);

                Label itemPrice = new Label(String.format("$%.2f", item.getTotal()));
                itemPrice.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
                itemPrice.setTextFill(Color.web("#666666"));

                itemRow.getChildren().addAll(itemLabel, itemSpacer, itemPrice);
                itemsList.getChildren().add(itemRow);
            }
        }

        // Status badge
        Label statusLabel = new Label("✓ Completed");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.web("#2E7D32"));
        statusLabel.setStyle("-fx-background-color: #E8F5E9; -fx-background-radius: 12; " +
                "-fx-padding: 5 12 5 12;");

        card.getChildren().addAll(header, new Separator(), itemsList, statusLabel);
        return card;
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

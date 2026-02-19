package com.adminpanel.ui;

import com.adminpanel.store.DataStore;
import com.adminpanel.model.Cart;
import com.adminpanel.ui.admin.*;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardApp extends Application {

    private DataStore dataStore;
    private BorderPane mainLayout;
    private VBox contentArea;
    private Timeline autoRefreshTimeline;
    private Button lastClickedButton;
    private CartView activeCartView;

    @Override
    public void start(Stage primaryStage) {
        try {
            dataStore = DataStore.getInstance();

            mainLayout = new BorderPane();

            // Top Header
            HBox header = createHeader();
            mainLayout.setTop(header);

            // Left Sidebar
            VBox sidebar = createSidebar();
            mainLayout.setLeft(sidebar);

            // Center Content Area
            contentArea = new VBox(20);
            contentArea.setPadding(new Insets(30));
            contentArea.setStyle("-fx-background-color: #f5f5f5;");

            // Show dashboard by default
            showDashboard();

            ScrollPane scrollPane = new ScrollPane(contentArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f5f5f5;");
            mainLayout.setCenter(scrollPane);

            Scene scene = new Scene(mainLayout, 1200, 700);
            primaryStage.setTitle("Admin Panel");
            primaryStage.setScene(scene);
            
            // Setup auto-refresh for tables every 2 seconds
            setupAutoRefresh();
            
            primaryStage.setOnCloseRequest(e -> {
                if (autoRefreshTimeline != null) {
                    autoRefreshTimeline.stop();
                }
            });
            
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting DashboardApp: " + e.getMessage());
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #4FB3E8;");

        Label title = new Label("Admin Panel");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.BLACK);

        header.getChildren().add(title);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #4FB3E8;");

        Button dashboardBtn = createSidebarButton("Dashboard");
        Button userBtn = createSidebarButton("User");
        Button categoryBtn = createSidebarButton("Category");
        Button productBtn = createSidebarButton("Product");
        Button userCartBtn = createSidebarButton("User cart");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button settingBtn = createSidebarButton("Setting");

        dashboardBtn.setOnAction(e -> { setActiveButton(dashboardBtn); showDashboard(); });
        userBtn.setOnAction(e -> { setActiveButton(userBtn); showUserManagement(); });
        categoryBtn.setOnAction(e -> { setActiveButton(categoryBtn); showCategoryManagement(); });
        productBtn.setOnAction(e -> { setActiveButton(productBtn); showProductManagement(); });
        userCartBtn.setOnAction(e -> { setActiveButton(userCartBtn); showUserCartManagement(); });
        settingBtn.setOnAction(e -> { setActiveButton(settingBtn); showSettings(); });

        setActiveButton(dashboardBtn);  // Set dashboard as active by default

        sidebar.getChildren().addAll(dashboardBtn, userBtn, categoryBtn,
            productBtn, userCartBtn, spacer, settingBtn);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(15, 20, 15, 20));
        btn.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: black; " +
                "-fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; " +
                "-fx-background-radius: 8; -fx-cursor: hand;"));

        return btn;
    }

    private void setActiveButton(Button activeBtn) {
        if (lastClickedButton != null) {
            lastClickedButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;");
        }
        activeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: black; " +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        lastClickedButton = activeBtn;
    }

    private void setupAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (activeCartView != null && activeCartView.getCartTable() != null && activeCartView.getCartTable().isVisible()) {
                activeCartView.getCartTable().refresh();
            }
        }));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void showDashboard() {
        contentArea.getChildren().clear();
        activeCartView = null;
        contentArea.getChildren().add(new HomeView().getView());
    }

    private void showUserManagement() {
        contentArea.getChildren().clear();
        activeCartView = null;
        contentArea.getChildren().add(new UserView().getView());
    }

    private void showCategoryManagement() {
        contentArea.getChildren().clear();
        activeCartView = null;
        contentArea.getChildren().add(new CategoryView().getView());
    }

    private void showProductManagement() {
        contentArea.getChildren().clear();
        activeCartView = null;
        contentArea.getChildren().add(new ProductView().getView());
    }

    private void showUserCartManagement() {
        contentArea.getChildren().clear();
        activeCartView = new CartView();
        contentArea.getChildren().add(activeCartView.getView());
    }

    private void showSettings() {
        contentArea.getChildren().clear();
        activeCartView = null;
        contentArea.getChildren().add(new SettingsView().getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

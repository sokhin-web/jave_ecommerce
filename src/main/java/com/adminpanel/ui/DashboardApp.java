package com.adminpanel.ui;

import java.util.ArrayList;

import com.adminpanel.store.DataStore;
import com.adminpanel.model.*;
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

public class DashboardApp extends Application {

    private DataStore dataStore;
    private BorderPane mainLayout;
    private VBox contentArea;

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

        dashboardBtn.setOnAction(e -> showDashboard());
        userBtn.setOnAction(e -> showUserManagement());
        categoryBtn.setOnAction(e -> showCategoryManagement());
        productBtn.setOnAction(e -> showProductManagement());
        userCartBtn.setOnAction(e -> showUserCartManagement());
        settingBtn.setOnAction(e -> showSettings());

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

    private void showDashboard() {
        contentArea.getChildren().clear();

        // Statistics Cards
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("User amount", String.valueOf(dataStore.getUsers().size())),
                createStatCard("Category", String.valueOf(dataStore.getCategories().size())),
                createStatCard("Product", String.valueOf(dataStore.getProducts().size())),
                createStatCard("User Cart", String.valueOf(dataStore.getCarts().size())));

        // Recent Users Table
        Label recentLabel = new Label("User Recently");
        recentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TableView<User> recentUsersTable = createRecentUsersTable();

        contentArea.getChildren().addAll(statsBox, recentLabel, recentUsersTable);
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(250);
        card.setPrefHeight(100);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private TableView<User> createRecentUsersTable() {
        TableView<User> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        idCol.setPrefWidth(80);

        TableColumn<User, String> nameCol = new TableColumn<>("Full name");
        nameCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(200);

        TableColumn<User, String> phoneCol = new TableColumn<>("phone");
        phoneCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        phoneCol.setPrefWidth(150);

        TableColumn<User, String> positionCol = new TableColumn<>("position");
        positionCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPosition()));
        positionCol.setPrefWidth(150);

        TableColumn<User, String> imageCol = new TableColumn<>("image");
        imageCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImage()));
        imageCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, nameCol, phoneCol, positionCol, imageCol);

        // Show only first 5 users
        int count = Math.min(5, dataStore.getUsers().size());
        for (int i = 0; i < count; i++) {
            table.getItems().add(dataStore.getUsers().get(i));
        }

        return table;
    }

    private void showUserManagement() {
        contentArea.getChildren().clear();

        Label title = new Label("Manage of User");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Search and Create section
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search ....");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createBtn = new Button("Create  ➕");
        createBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> showUserCreateDialog());

        topBar.getChildren().addAll(searchField, spacer, createBtn);

        // Users Table
        TableView<User> table = createUserTable();

        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().clear();
            for (User user : dataStore.getUsers()) {
                if (user.getFullName().toLowerCase().contains(newVal.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(newVal.toLowerCase()) ||
                        user.getPhone().contains(newVal)) {
                    table.getItems().add(user);
                }
            }
        });

        contentArea.getChildren().addAll(title, topBar, table);
    }

    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<User, String> emailCol = new TableColumn<>("email");
        emailCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, String> phoneCol = new TableColumn<>("phone");
        phoneCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        TableColumn<User, String> addressCol = new TableColumn<>("address");
        addressCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));

        TableColumn<User, String> positionCol = new TableColumn<>("position");
        positionCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPosition()));

        TableColumn<User, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");

            {
                viewBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                viewBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserViewDialog(user);
                });

                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserEditDialog(user);
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    dataStore.deleteUser(user);
                    getTableView().getItems().remove(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol,
                phoneCol, addressCol, positionCol, actionCol);
        table.getItems().addAll(dataStore.getUsers());

        return table;
    }

    private void showCategoryManagement() {
        contentArea.getChildren().clear();

        Label title = new Label("Manage of Category");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search ....");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createBtn = new Button("Create  ➕");
        createBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> showCategoryCreateDialog());

        topBar.getChildren().addAll(searchField, spacer, createBtn);

        TableView<Category> table = createCategoryTable();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().clear();
            for (Category category : dataStore.getCategories()) {
                if (category.getName().toLowerCase().contains(newVal.toLowerCase())) {
                    table.getItems().add(category);
                }
            }
        });

        contentArea.getChildren().addAll(title, topBar, table);
    }

    private TableView<Category> createCategoryTable() {
        TableView<Category> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Category, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);

        TableColumn<Category, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        descCol.setPrefWidth(400);

        TableColumn<Category, String> iconCol = new TableColumn<>("Icon");
        iconCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIcon()));

        TableColumn<Category, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");

            {
                viewBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                viewBtn.setOnAction(e -> {
                    Category category = getTableView().getItems().get(getIndex());
                    showCategoryViewDialog(category);
                });

                editBtn.setOnAction(e -> {
                    Category category = getTableView().getItems().get(getIndex());
                    showCategoryEditDialog(category);
                });

                deleteBtn.setOnAction(e -> {
                    Category category = getTableView().getItems().get(getIndex());
                    dataStore.deleteCategory(category);
                    getTableView().getItems().remove(category);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, descCol, iconCol, actionCol);
        table.getItems().addAll(dataStore.getCategories());

        return table;
    }

    private void showProductManagement() {
        contentArea.getChildren().clear();

        Label title = new Label("Manage of Product");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search ....");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createBtn = new Button("Create  ➕");
        createBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> showProductCreateDialog());

        topBar.getChildren().addAll(searchField, spacer, createBtn);

        TableView<Product> table = createProductTable();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().clear();
            for (Product product : dataStore.getProducts()) {
                if (product.getName().toLowerCase().contains(newVal.toLowerCase())) {
                    table.getItems().add(product);
                }
            }
        });

        contentArea.getChildren().addAll(title, topBar, table);
    }

    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(150);

        TableColumn<Product, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        descCol.setPrefWidth(250);

        TableColumn<Product, String> imageCol = new TableColumn<>("Image");
        imageCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImage()));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrice()));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> {
            Category cat = dataStore.getCategoryById(data.getValue().getCategoryId());
            return new javafx.beans.property.SimpleStringProperty(
                    cat != null ? cat.getName() : "Unknown");
        });

        TableColumn<Product, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");

            {
                viewBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                viewBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showProductViewDialog(product);
                });

                editBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showProductEditDialog(product);
                });

                deleteBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    dataStore.deleteProduct(product);
                    getTableView().getItems().remove(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, descCol, imageCol, priceCol, categoryCol, actionCol);
        table.getItems().addAll(dataStore.getProducts());

        return table;
    }

    private void showUserCartManagement() {
        contentArea.getChildren().clear();

        Label title = new Label("Manage of User Cart");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search ....");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-font-weight: bold;");

        Button createBtn = new Button("Create  ➕");
        createBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> showCartCreateDialog());

        topBar.getChildren().addAll(searchField, spacer, refreshBtn, createBtn);

        TableView<Cart> table = createCartTable();

        refreshBtn.setOnAction(e -> {
            table.getItems().clear();
            table.getItems().addAll(dataStore.getCarts());
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().clear();
            for (Cart cart : dataStore.getCarts()) {
                User user = dataStore.getUserById(cart.getUserId());
                if (user != null && user.getFullName().toLowerCase().contains(newVal.toLowerCase())) {
                    table.getItems().add(cart);
                }
            }
        });

        contentArea.getChildren().addAll(title, topBar, table);
    }

    private TableView<Cart> createCartTable() {
        TableView<Cart> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        TableColumn<Cart, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Cart, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(data -> {
            User user = dataStore.getUserById(data.getValue().getUserId());
            return new javafx.beans.property.SimpleStringProperty(
                    user != null ? user.getFullName() : "Unknown");
        });
        userCol.setPrefWidth(200);

        TableColumn<Cart, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setPrefWidth(150);

        TableColumn<Cart, Double> totalCol = new TableColumn<>("Total Price");
        totalCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalPrice()));
        totalCol.setCellFactory(col -> new TableCell<Cart, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });
        totalCol.setPrefWidth(150);

        TableColumn<Cart, Integer> itemsCol = new TableColumn<>("Items Count");
        itemsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
                dataStore.getCartItemsByCartId(data.getValue().getId()).size()));

        TableColumn<Cart, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");

            {
                viewBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                viewBtn.setOnAction(e -> {
                    Cart cart = getTableView().getItems().get(getIndex());
                    showCartViewDialog(cart);
                });

                editBtn.setOnAction(e -> {
                    Cart cart = getTableView().getItems().get(getIndex());
                    showCartEditDialog(cart);
                });

                deleteBtn.setOnAction(e -> {
                    Cart cart = getTableView().getItems().get(getIndex());
                    dataStore.deleteCart(cart);
                    getTableView().getItems().remove(cart);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(idCol, userCol, statusCol, totalCol, itemsCol, actionCol);
        table.getItems().addAll(dataStore.getCarts());

        return table;
    }

    private void showSettings() {
        contentArea.getChildren().clear();

        Label title = new Label("Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Get current logged-in user
        User currentUser = ApplicationState.getCurrentUser();

        VBox settingsBox = new VBox(20);
        settingsBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 30;");
        settingsBox.setPrefWidth(600);

        if (currentUser != null) {
            // User Information Card
            Label userInfoTitle = new Label("User Information");
            userInfoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            GridPane userGrid = new GridPane();
            userGrid.setHgap(20);
            userGrid.setVgap(15);
            userGrid.setPadding(new Insets(20));
            userGrid.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

            Label userIconLabel = new Label(currentUser.getImage());
            userIconLabel.setFont(Font.font(48));
            GridPane.setRowSpan(userIconLabel, 4);

            userGrid.add(userIconLabel, 0, 0);
            userGrid.add(new Label("Name:"), 1, 0);
            userGrid.add(new Label(currentUser.getFullName()), 2, 0);
            userGrid.add(new Label("Email:"), 1, 1);
            userGrid.add(new Label(currentUser.getEmail()), 2, 1);
            userGrid.add(new Label("Position:"), 1, 2);
            userGrid.add(new Label(currentUser.getPosition()), 2, 2);
            userGrid.add(new Label("Phone:"), 1, 3);
            userGrid.add(new Label(currentUser.getPhone()), 2, 3);

            // Logout Button
            Button logoutBtn = new Button("Logout");
            logoutBtn.setPrefWidth(150);
            logoutBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            logoutBtn.setStyle("-fx-padding: 12; -fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 8;");
            logoutBtn.setOnAction(e -> handleLogout());

            settingsBox.getChildren().addAll(userInfoTitle, userGrid, logoutBtn);
        } else {
            Label info = new Label("No user logged in. Please login first.");
            info.setFont(Font.font("Arial", 14));
            settingsBox.getChildren().add(info);
        }

        contentArea.getChildren().addAll(title, settingsBox);
    }

    private void handleLogout() {
        // Clear current user
        ApplicationState.logout();

        // Get the current stage and reset to login screen
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

    // Dialog methods for User
    private void showUserCreateDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Create User");
        dialog.setHeaderText("Enter new user information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        TextField passwordField = new TextField();
        TextField positionField = new TextField();
        TextField addressField = new TextField();
        TextField phoneField = new TextField();
        TextField imageField = new TextField();
        imageField.setText("👤");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Position:"), 0, 4);
        grid.add(positionField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);
        grid.add(new Label("Phone:"), 0, 6);
        grid.add(phoneField, 1, 6);
        grid.add(new Label("Image:"), 0, 7);
        grid.add(imageField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new User(firstNameField.getText(), lastNameField.getText(),
                        emailField.getText(), passwordField.getText(),
                        positionField.getText(), addressField.getText(),
                        phoneField.getText(), imageField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            dataStore.addUser(user);
            showUserManagement();
        });
    }

    private void showUserViewDialog(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Details");
        alert.setHeaderText("User Information");
        alert.setContentText(String.format(
                "ID: %d\nName: %s\nEmail: %s\nPhone: %s\nPosition: %s\nAddress: %s",
                user.getId(), user.getFullName(), user.getEmail(),
                user.getPhone(), user.getPosition(), user.getAddress()));
        alert.showAndWait();
    }

    private void showUserEditDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField firstNameField = new TextField(user.getFirstName());
        TextField lastNameField = new TextField(user.getLastName());
        TextField emailField = new TextField(user.getEmail());
        PasswordField passwordField = new PasswordField();
        TextField positionField = new TextField(user.getPosition());
        TextField addressField = new TextField(user.getAddress());
        TextField phoneField = new TextField(user.getPhone());
        TextField imageField = new TextField(user.getImage());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Position:"), 0, 4);
        grid.add(positionField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);
        grid.add(new Label("Phone:"), 0, 6);
        grid.add(phoneField, 1, 6);
        grid.add(new Label("Image:"), 0, 7);
        grid.add(imageField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                user.setFirstName(firstNameField.getText());
                user.setLastName(lastNameField.getText());
                user.setEmail(emailField.getText());
                if (!passwordField.getText().isEmpty()) {
                    user.setPassword(passwordField.getText());
                }
                user.setPosition(positionField.getText());
                user.setAddress(addressField.getText());
                user.setPhone(phoneField.getText());
                user.setImage(imageField.getText());
                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> showUserManagement());
    }

    // Dialog methods for Category
    private void showCategoryCreateDialog() {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Create Category");
        dialog.setHeaderText("Enter new category information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField iconField = new TextField();
        iconField.setText("📦");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Icon:"), 0, 2);
        grid.add(iconField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Category(nameField.getText(), descField.getText(), iconField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(category -> {
            dataStore.addCategory(category);
            showCategoryManagement();
        });
    }

    private void showCategoryViewDialog(Category category) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Category Details");
        alert.setHeaderText("Category Information");
        alert.setContentText(String.format(
                "ID: %d\nName: %s\nDescription: %s\nIcon: %s",
                category.getId(), category.getName(), category.getDescription(), category.getIcon()));
        alert.showAndWait();
    }

    private void showCategoryEditDialog(Category category) {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit category information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(category.getName());
        TextField descField = new TextField(category.getDescription());
        TextField iconField = new TextField(category.getIcon());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Icon:"), 0, 2);
        grid.add(iconField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                category.setName(nameField.getText());
                category.setDescription(descField.getText());
                category.setIcon(iconField.getText());
                return category;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(c -> showCategoryManagement());
    }

    // Dialog methods for Product
    private void showProductCreateDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Create Product");
        dialog.setHeaderText("Enter new product information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField imageField = new TextField();
        imageField.setText("📦");
        TextField priceField = new TextField();
        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(dataStore.getCategories());
        categoryCombo.setConverter(new javafx.util.StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Image:"), 0, 2);
        grid.add(imageField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType && categoryCombo.getValue() != null) {
                return new Product(nameField.getText(), descField.getText(), imageField.getText(),
                        Double.parseDouble(priceField.getText()),
                        categoryCombo.getValue().getId());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            dataStore.addProduct(product);
            showProductManagement();
        });
    }

    private void showProductViewDialog(Product product) {
        Category category = dataStore.getCategoryById(product.getCategoryId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText("Product Information");
        alert.setContentText(String.format(
                "ID: %d\nName: %s\nDescription: %s\nPrice: $%.2f\nCategory: %s",
                product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), category != null ? category.getName() : "Unknown"));
        alert.showAndWait();
    }

    private void showProductEditDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit product information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(product.getName());
        TextField descField = new TextField(product.getDescription());
        TextField imageField = new TextField(product.getImage());
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(dataStore.getCategories());
        categoryCombo.setValue(dataStore.getCategoryById(product.getCategoryId()));
        categoryCombo.setConverter(new javafx.util.StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Image:"), 0, 2);
        grid.add(imageField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType && categoryCombo.getValue() != null) {
                product.setName(nameField.getText());
                product.setDescription(descField.getText());
                product.setImage(imageField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setCategoryId(categoryCombo.getValue().getId());
                return product;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> showProductManagement());
    }

    // Dialog methods for Cart
    private void showCartCreateDialog() {
        Dialog<Cart> dialog = new Dialog<>();
        dialog.setTitle("Create Cart");
        dialog.setHeaderText("Enter new cart information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<User> userCombo = new ComboBox<>();
        userCombo.getItems().addAll(dataStore.getUsers());
        userCombo.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() : "";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Completed", "Cancelled");
        statusCombo.setValue("Active");

        grid.add(new Label("User:"), 0, 0);
        grid.add(userCombo, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType && userCombo.getValue() != null) {
                return new Cart(userCombo.getValue().getId(), statusCombo.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(cart -> {
            dataStore.addCart(cart);
            showUserCartManagement();
        });
    }

    private void showCartViewDialog(Cart cart) {
        User user = dataStore.getUserById(cart.getUserId());
        ArrayList<CartItem> items = dataStore.getCartItemsByCartId(cart.getId());

        StringBuilder itemsText = new StringBuilder();
        for (CartItem item : items) {
            Product product = dataStore.getProductById(item.getProductId());
            if (product != null) {
                itemsText.append(String.format("\n- %s (x%d): $%.2f",
                        product.getName(), item.getQuantity(), item.getTotal()));
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cart Details");
        alert.setHeaderText("Cart Information");
        alert.setContentText(String.format(
                "ID: %d\nUser: %s\nStatus: %s\nTotal Price: $%.2f\nItems:%s",
                cart.getId(), user != null ? user.getFullName() : "Unknown",
                cart.getStatus(), cart.getTotalPrice(), itemsText.toString()));
        alert.showAndWait();
    }

    private void showCartEditDialog(Cart cart) {
        Dialog<Cart> dialog = new Dialog<>();
        dialog.setTitle("Edit Cart");
        dialog.setHeaderText("Edit cart information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Completed", "Cancelled");
        statusCombo.setValue(cart.getStatus());

        grid.add(new Label("Status:"), 0, 0);
        grid.add(statusCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                cart.setStatus(statusCombo.getValue());
                return cart;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(c -> showUserCartManagement());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

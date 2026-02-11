package com.adminpanel.ui;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

public class ClientApp extends Application {

    private DataStore dataStore;
    private BorderPane mainLayout;
    private VBox contentArea;
    private TextField searchField;
    private Category selectedCategory = null;
    private Cart userCart;

    @Override
    public void start(Stage primaryStage) {
        try {
            dataStore = DataStore.getInstance();
            
            // Get the current logged-in user or use default
            User currentUser = ApplicationState.getCurrentUser();
            int userId = (currentUser != null) ? currentUser.getId() : 3;
            
            // Create or get active cart for current user
            userCart = getOrCreateUserCart(userId);

            mainLayout = new BorderPane();

            // Top Header
            HBox header = createHeader();
            mainLayout.setTop(header);

            // Left Sidebar with Categories
            VBox sidebar = createCategorySidebar();
            mainLayout.setLeft(sidebar);

            // Center Content Area
            contentArea = new VBox(20);
            contentArea.setPadding(new Insets(30));
            contentArea.setStyle("-fx-background-color: #f5f5f5;");

            // Show all products by default
            showProductGrid(null, "");

            ScrollPane scrollPane = new ScrollPane(contentArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f5f5f5;");
            mainLayout.setCenter(scrollPane);

            Scene scene = new Scene(mainLayout, 1400, 800);
            primaryStage.setTitle("Client Panel - Shopping");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting ClientApp: " + e.getMessage());
        }
    }

    private Cart getOrCreateUserCart(int userId) {
        // Find existing active cart for user
        for (Cart cart : dataStore.getCarts()) {
            if (cart.getUserId() == userId && cart.getStatus().equals("Active")) {
                return cart;
            }
        }
        // Create new cart if none exists
        Cart newCart = new Cart(userId, "Active");
        dataStore.addCart(newCart);
        return newCart;
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
        cartButton.setOnAction(e -> showCartView());

        // Settings button
        Button settingsBtn = new Button("⚙️ Settings");
        settingsBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        settingsBtn.setStyle("-fx-background-color: white; -fx-text-fill: #4FB3E8; " +
                "-fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        settingsBtn.setOnMouseEntered(e -> settingsBtn.setStyle("-fx-background-color: #e0e0e0; " +
                "-fx-text-fill: #4FB3E8; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        settingsBtn.setOnMouseExited(e -> settingsBtn.setStyle("-fx-background-color: white; " +
                "-fx-text-fill: #4FB3E8; -fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;"));
        settingsBtn.setOnAction(e -> showClientSettings());

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

    private VBox createCategorySidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");

        Label categoriesTitle = new Label("Categories");
        categoriesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        categoriesTitle.setPadding(new Insets(0, 0, 10, 0));

        // All Products button
        Button allProductsBtn = createCategoryButton("All Products", "🏪", true);
        allProductsBtn.setOnAction(e -> {
            selectedCategory = null;
            showProductGrid(null, searchField.getText());
            updateCategoryButtons();
        });

        VBox categoryButtons = new VBox(5);
        categoryButtons.getChildren().add(allProductsBtn);

        // Category buttons
        for (Category category : dataStore.getCategories()) {
            Button categoryBtn = createCategoryButton(category.getName(), category.getIcon(), false);
            categoryBtn.setOnAction(e -> {
                selectedCategory = category;
                showProductGrid(category, searchField.getText());
                updateCategoryButtons();
            });
            categoryButtons.getChildren().add(categoryBtn);
        }

        sidebar.getChildren().addAll(categoriesTitle, new Separator(), categoryButtons);
        return sidebar;
    }

    private Button createCategoryButton(String text, String icon, boolean isSelected) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(15, 20, 15, 20));
        btn.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        
        if (isSelected) {
            btn.setStyle("-fx-background-color: #4FB3E8; -fx-text-fill: white; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;");
        }

        btn.setOnMouseEntered(e -> {
            if (!isSelected) {
                btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!isSelected) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");
            }
        });

        return btn;
    }

    private void updateCategoryButtons() {
        // Refresh the sidebar to update selected state
        VBox sidebar = createCategorySidebar();
        mainLayout.setLeft(sidebar);
    }

    private void showProductGrid(Category category, String searchQuery) {
        contentArea.getChildren().clear();

        // Title
        String categoryName = category != null ? category.getName() : "All Products";
        Label title = new Label(categoryName);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        // Search Bar
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10, 0, 10, 0));

        searchField = new TextField(searchQuery);
        searchField.setPromptText("Search products...");
        searchField.setPrefWidth(400);
        searchField.setFont(Font.font("Arial", 14));
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10 20 10 20;");

        Button searchBtn = new Button("🔍 Search");
        searchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        searchBtn.setStyle("-fx-background-color: #4FB3E8; -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> showProductGrid(selectedCategory, searchField.getText()));

        searchBox.getChildren().addAll(searchField, searchBtn);

        // Filter products
        ArrayList<Product> filteredProducts = filterProducts(category, searchQuery);

        // Products Grid
        FlowPane productGrid = new FlowPane();
        productGrid.setHgap(20);
        productGrid.setVgap(20);
        productGrid.setPadding(new Insets(20, 0, 20, 0));

        if (filteredProducts.isEmpty()) {
            Label noProducts = new Label("No products found");
            noProducts.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            noProducts.setTextFill(Color.GRAY);
            productGrid.getChildren().add(noProducts);
        } else {
            for (Product product : filteredProducts) {
                VBox productCard = createProductCard(product);
                productGrid.getChildren().add(productCard);
            }
        }

        contentArea.getChildren().addAll(title, searchBox, new Separator(), productGrid);
    }

    private ArrayList<Product> filterProducts(Category category, String searchQuery) {
        ArrayList<Product> products = dataStore.getProducts();
        
        // Filter by category
        if (category != null) {
            products = products.stream()
                    .filter(p -> p.getCategoryId() == category.getId())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        // Filter by search query
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(query) ||
                            p.getDescription().toLowerCase().contains(query))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return products;
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(250);
        card.setPrefHeight(320);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); -fx-cursor: hand;");

        // Product Image (using emoji as placeholder)
        Label imageLabel = new Label(product.getImage());
        imageLabel.setFont(Font.font(60));
        imageLabel.setAlignment(Pos.CENTER);
        imageLabel.setPrefHeight(100);

        // Product Name
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(220);
        nameLabel.setAlignment(Pos.CENTER);

        // Product Description
        Label descLabel = new Label(product.getDescription());
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.GRAY);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(220);
        descLabel.setMaxHeight(40);
        descLabel.setAlignment(Pos.CENTER);

        // Price
        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        priceLabel.setTextFill(Color.web("#4FB3E8"));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // View Details Button
        Button detailsBtn = new Button("View Details");
        detailsBtn.setMaxWidth(Double.MAX_VALUE);
        detailsBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        detailsBtn.setStyle("-fx-background-color: #4FB3E8; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        detailsBtn.setOnAction(e -> showProductDetail(product));

        // Hover effects
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 4); -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); -fx-cursor: hand;"));

        card.getChildren().addAll(imageLabel, nameLabel, descLabel, priceLabel, spacer, detailsBtn);
        return card;
    }

    private void showProductDetail(Product product) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Product Details");
        dialog.setHeaderText(null);

        // Create custom content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setPrefWidth(500);

        // Product Image
        Label imageLabel = new Label(product.getImage());
        imageLabel.setFont(Font.font(80));

        // Product Name
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Product Description
        Label descLabel = new Label(product.getDescription());
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descLabel.setTextFill(Color.GRAY);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(450);

        // Category
        Category category = dataStore.getCategoryById(product.getCategoryId());
        Label categoryLabel = new Label("Category: " + (category != null ? category.getName() : "Unknown"));
        categoryLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        // Price
        Label priceLabel = new Label(String.format("Price: $%.2f", product.getPrice()));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        priceLabel.setTextFill(Color.web("#4FB3E8"));

        // Quantity selector
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER);

        Label qtyLabel = new Label("Quantity:");
        qtyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button minusBtn = new Button("−");
        minusBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        minusBtn.setPrefSize(40, 40);
        minusBtn.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 20; -fx-cursor: hand;");

        Label quantityLabel = new Label("1");
        quantityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        quantityLabel.setPrefWidth(50);
        quantityLabel.setAlignment(Pos.CENTER);

        Button plusBtn = new Button("+");
        plusBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        plusBtn.setPrefSize(40, 40);
        plusBtn.setStyle("-fx-background-color: #4FB3E8; -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-cursor: hand;");

        // Quantity control logic
        final int[] quantity = {1};
        minusBtn.setOnAction(e -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                quantityLabel.setText(String.valueOf(quantity[0]));
            }
        });
        plusBtn.setOnAction(e -> {
            quantity[0]++;
            quantityLabel.setText(String.valueOf(quantity[0]));
        });

        quantityBox.getChildren().addAll(qtyLabel, minusBtn, quantityLabel, plusBtn);

        // Total price
        Label totalLabel = new Label(String.format("Total: $%.2f", product.getPrice()));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        totalLabel.setTextFill(Color.web("#2E7D32"));

        // Update total when quantity changes
        quantityLabel.textProperty().addListener((obs, oldVal, newVal) -> {
            double total = product.getPrice() * Integer.parseInt(newVal);
            totalLabel.setText(String.format("Total: $%.2f", total));
        });

        content.getChildren().addAll(imageLabel, nameLabel, descLabel, categoryLabel,
                new Separator(), priceLabel, quantityBox, totalLabel);

        dialog.getDialogPane().setContent(content);

        // Add buttons
        ButtonType buyButtonType = new ButtonType("🛒 Add to Cart", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, cancelButtonType);

        // Style the buy button
        Button buyButton = (Button) dialog.getDialogPane().lookupButton(buyButtonType);
        buyButton.setStyle("-fx-background-color: #4FB3E8; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 20 10 20;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buyButtonType) {
                return quantity[0];
            }
            return null;
        });

        dialog.showAndWait().ifPresent(qty -> {
            addToCart(product, qty);
            showSuccessAlert("Product added to cart successfully!", 
                    String.format("Added %d x %s to your cart", qty, product.getName()));
        });
    }

    private void addToCart(Product product, int quantity) {
        // Check if product already exists in cart
        ArrayList<CartItem> existingItems = dataStore.getCartItemsByCartId(userCart.getId());
        CartItem existingItem = null;
        
        for (CartItem item : existingItems) {
            if (item.getProductId() == product.getId()) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            dataStore.getCartItems().set(dataStore.getCartItems().indexOf(existingItem), existingItem);
            // Recalculate cart total after updating quantity
            dataStore.updateCartTotalPrice(userCart.getId());
        } else {
            // Add new cart item
            CartItem newItem = new CartItem(userCart.getId(), product.getId(), product.getPrice(), quantity);
            dataStore.addCartItem(newItem);
        }
    }

    private void showCartView() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("My Shopping Cart");
        dialog.setHeaderText(null);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);

        // Cart title
        Label titleLabel = new Label("🛒 Your Cart");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        ArrayList<CartItem> cartItems = dataStore.getCartItemsByCartId(userCart.getId());

        if (cartItems.isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty");
            emptyLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            emptyLabel.setTextFill(Color.GRAY);
            content.getChildren().addAll(titleLabel, emptyLabel);
        } else {
            VBox itemsList = new VBox(10);

            for (CartItem item : cartItems) {
                Product product = dataStore.getProductById(item.getProductId());
                if (product != null) {
                    HBox itemBox = createCartItemBox(item, product);
                    itemsList.getChildren().add(itemBox);
                }
            }

            // Total
            HBox totalBox = new HBox();
            totalBox.setAlignment(Pos.CENTER_RIGHT);
            totalBox.setPadding(new Insets(15, 0, 0, 0));
            totalBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

            Label totalLabel = new Label(String.format("Total: $%.2f", userCart.getTotalPrice()));
            totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            totalLabel.setTextFill(Color.web("#4FB3E8"));

            totalBox.getChildren().add(totalLabel);

            ScrollPane scrollPane = new ScrollPane(itemsList);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(300);
            scrollPane.setStyle("-fx-background-color: transparent;");

            content.getChildren().addAll(titleLabel, new Separator(), scrollPane, totalBox);
        }

        dialog.getDialogPane().setContent(content);

        ButtonType checkoutButtonType = new ButtonType("Checkout", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        if (cartItems.isEmpty()) {
            dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(checkoutButtonType, closeButtonType);
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == checkoutButtonType) {
                checkout();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private HBox createCartItemBox(CartItem item, Product product) {
        HBox itemBox = new HBox(15);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

        // Product image
        Label imageLabel = new Label(product.getImage());
        imageLabel.setFont(Font.font(40));

        // Product details
        VBox detailsBox = new VBox(5);
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label priceLabel = new Label(String.format("$%.2f each", item.getPrice()));
        priceLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        priceLabel.setTextFill(Color.GRAY);

        detailsBox.getChildren().addAll(nameLabel, priceLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Quantity
        Label qtyLabel = new Label("Qty: " + item.getQuantity());
        qtyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Item total
        Label totalLabel = new Label(String.format("$%.2f", item.getTotal()));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.web("#4FB3E8"));

        // Remove button
        Button removeBtn = new Button("❌");
        removeBtn.setFont(Font.font(12));
        removeBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; " +
                "-fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 5 10 5 10;");
        removeBtn.setOnAction(e -> {
            dataStore.deleteCartItem(item);
            showCartView();
        });

        itemBox.getChildren().addAll(imageLabel, detailsBox, spacer, qtyLabel, totalLabel, removeBtn);
        return itemBox;
    }

    private void checkout() {
        userCart.setStatus("Completed");
        showSuccessAlert("Order Completed!", 
                String.format("Your order has been placed successfully!\nTotal: $%.2f", userCart.getTotalPrice()));
        
        // Create new cart for user
        userCart = new Cart(userCart.getUserId(), "Active");
        dataStore.addCart(userCart);
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showClientSettings() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("User Settings");
        dialog.setHeaderText(null);

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setPrefWidth(500);

        User currentUser = ApplicationState.getCurrentUser();

        if (currentUser != null) {
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
            userGrid.add(new Label("Phone:"), 1, 2);
            userGrid.add(new Label(currentUser.getPhone()), 2, 2);
            userGrid.add(new Label("Address:"), 1, 3);
            userGrid.add(new Label(currentUser.getAddress()), 2, 3);

            content.getChildren().addAll(userInfoTitle, userGrid);
        } else {
            Label info = new Label("No user logged in");
            info.setFont(Font.font("Arial", 14));
            content.getChildren().add(info);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
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

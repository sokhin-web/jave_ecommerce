package com.adminpanel.ui.client;

import com.adminpanel.model.Cart;
import com.adminpanel.model.CartItem;
import com.adminpanel.model.Category;
import com.adminpanel.model.Product;
import com.adminpanel.store.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

import java.util.List;
import java.util.stream.Collectors;

public class ProductGridView {
    private final DataStore dataStore;
    private final int userId;
    private final Runnable onCartUpdate;
    
    private VBox view;
    private Label titleLabel;
    private TextField searchField;
    private FlowPane productGrid;
    private Category currentCategory;

    public ProductGridView(int userId, Runnable onCartUpdate) {
        this.dataStore = DataStore.getInstance();
        this.userId = userId;
        this.onCartUpdate = onCartUpdate;
        initializeView();
    }

    private void initializeView() {
        view = new VBox(20);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        titleLabel = new Label("All Products");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        // Search Bar
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10, 0, 10, 0));

        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setPrefWidth(400);
        searchField.setFont(Font.font("Arial", 14));
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10 20 10 20;");

        Button searchBtn = new Button("🔍 Search");
        searchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        searchBtn.setStyle("-fx-background-color: #4FB3E8; -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        
        // Search actions
        searchBtn.setOnAction(e -> refreshGrid());
        searchField.setOnAction(e -> refreshGrid());

        searchBox.getChildren().addAll(searchField, searchBtn);

        // Products Grid
        productGrid = new FlowPane();
        productGrid.setHgap(20);
        productGrid.setVgap(20);
        productGrid.setPadding(new Insets(20, 0, 20, 0));

        view.getChildren().addAll(titleLabel, searchBox, new Separator(), productGrid);
        
        // Initial load
        refreshGrid();
    }

    public VBox getView() {
        return view;
    }

    public void setCategory(Category category) {
        this.currentCategory = category;
        titleLabel.setText(category != null ? category.getName() : "All Products");
        refreshGrid();
    }

    private void refreshGrid() {
        String searchQuery = searchField.getText();
        List<Product> filteredProducts = filterProducts(currentCategory, searchQuery);

        productGrid.getChildren().clear();

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
    }

    private List<Product> filterProducts(Category category, String searchQuery) {
        List<Product> products = dataStore.getProducts();
        
        // Filter by category
        if (category != null) {
            products = products.stream()
                    .filter(p -> p.getCategoryId() == category.getId())
                    .collect(Collectors.toList());
        }

        // Filter by search query
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(query) ||
                            p.getDescription().toLowerCase().contains(query))
                    .collect(Collectors.toList());
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
        Label imageLabel = new Label();
        imageLabel.setAlignment(Pos.CENTER);
        imageLabel.setPrefHeight(100);
        
        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.startsWith("file:") && !imageUrl.startsWith("http")) {
            File file = new File(imageUrl);
            if (file.exists()) imageUrl = file.toURI().toString();
        }

        if (imageUrl != null && (imageUrl.startsWith("file:") || imageUrl.startsWith("http"))) {
            try {
                ImageView iv = new ImageView(new Image(imageUrl));
                iv.setFitHeight(100); iv.setFitWidth(100); iv.setPreserveRatio(true);
                imageLabel.setGraphic(iv);
            } catch (Exception e) { imageLabel.setText(product.getImage()); imageLabel.setFont(Font.font(60)); }
        } else {
            imageLabel.setText(product.getImage()); imageLabel.setFont(Font.font(60));
        }

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

        // ... (Content creation logic same as original)
        Label imageLabel = new Label();
        
        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.startsWith("file:") && !imageUrl.startsWith("http")) {
            File file = new File(imageUrl);
            if (file.exists()) imageUrl = file.toURI().toString();
        }

        if (imageUrl != null && (imageUrl.startsWith("file:") || imageUrl.startsWith("http"))) {
            try {
                ImageView iv = new ImageView(new Image(imageUrl));
                iv.setFitHeight(150); iv.setFitWidth(150); iv.setPreserveRatio(true);
                imageLabel.setGraphic(iv);
            } catch (Exception e) { imageLabel.setText(product.getImage()); imageLabel.setFont(Font.font(80)); }
        } else {
            imageLabel.setText(product.getImage()); imageLabel.setFont(Font.font(80));
        }

        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label descLabel = new Label(product.getDescription());
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descLabel.setTextFill(Color.GRAY);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(450);
        Category category = dataStore.getCategoryById(product.getCategoryId());
        Label categoryLabel = new Label("Category: " + (category != null ? category.getName() : "Unknown"));
        categoryLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        Label priceLabel = new Label(String.format("Price: $%.2f", product.getPrice()));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        priceLabel.setTextFill(Color.web("#4FB3E8"));

        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER);
        Label qtyLabel = new Label("Quantity:");
        Button minusBtn = new Button("−");
        Label quantityLabel = new Label("1");
        Button plusBtn = new Button("+");
        // Styling...
        minusBtn.setPrefSize(40, 40);
        plusBtn.setPrefSize(40, 40);
        
        final int[] quantity = {1};
        minusBtn.setOnAction(e -> { if (quantity[0] > 1) { quantity[0]--; quantityLabel.setText(String.valueOf(quantity[0])); }});
        plusBtn.setOnAction(e -> { quantity[0]++; quantityLabel.setText(String.valueOf(quantity[0])); });
        
        quantityBox.getChildren().addAll(qtyLabel, minusBtn, quantityLabel, plusBtn);
        
        content.getChildren().addAll(imageLabel, nameLabel, descLabel, categoryLabel, new Separator(), priceLabel, quantityBox);
        dialog.getDialogPane().setContent(content);
        
        ButtonType buyButtonType = new ButtonType("🛒 Add to Cart", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> dialogButton == buyButtonType ? quantity[0] : null);

        dialog.showAndWait().ifPresent(qty -> {
            addToCart(product, qty);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(String.format("Added %d x %s to your cart", qty, product.getName()));
            alert.showAndWait();
        });
    }

    private void addToCart(Product product, int quantity) {
        Cart userCart = dataStore.getCarts().stream()
                .filter(c -> c.getUserId() == userId && "Active".equals(c.getStatus()))
                .findFirst()
                .orElseGet(() -> {
                    Cart newCart = new Cart(userId, "Active");
                    dataStore.addCart(newCart);
                    return newCart;
                });

        List<CartItem> existingItems = dataStore.getCartItemsByCartId(userCart.getId());
        existingItems.stream().filter(i -> i.getProductId() == product.getId()).findFirst()
                .ifPresentOrElse(
                        item -> { item.setQuantity(item.getQuantity() + quantity); dataStore.updateCartTotalPrice(userCart.getId()); },
                        () -> dataStore.addCartItem(new CartItem(userCart.getId(), product.getId(), product.getPrice(), quantity))
                );
        
        if (onCartUpdate != null) onCartUpdate.run();
    }
}
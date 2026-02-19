package com.adminpanel.ui.client;

import com.adminpanel.model.Cart;
import com.adminpanel.model.CartItem;
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

public class ClientCartView {
    private final DataStore dataStore;
    private final int userId;

    public ClientCartView(int userId) {
        this.dataStore = DataStore.getInstance();
        this.userId = userId;
    }

    public void show() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("My Shopping Cart");
        dialog.setHeaderText(null);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);

        Label titleLabel = new Label("🛒 Your Cart");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Cart userCart = getActiveCart();
        List<CartItem> cartItems = dataStore.getCartItemsByCartId(userCart.getId());

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
                    itemsList.getChildren().add(createCartItemBox(item, product, dialog));
                }
            }

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
        
        if (!cartItems.isEmpty()) dialog.getDialogPane().getButtonTypes().add(checkoutButtonType);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == checkoutButtonType) checkout(userCart);
            return null;
        });

        dialog.showAndWait();
    }

    private Cart getActiveCart() {
        return dataStore.getCarts().stream()
                .filter(c -> c.getUserId() == userId && "Active".equals(c.getStatus()))
                .findFirst()
                .orElseGet(() -> {
                    Cart newCart = new Cart(userId, "Active");
                    dataStore.addCart(newCart);
                    return newCart;
                });
    }

    private HBox createCartItemBox(CartItem item, Product product, Dialog<Void> dialog) {
        HBox itemBox = new HBox(15);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

        Label imageLabel = new Label();
        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.startsWith("file:") && !imageUrl.startsWith("http")) {
            File file = new File(imageUrl);
            if (file.exists()) imageUrl = file.toURI().toString();
        }

        if (imageUrl != null && (imageUrl.startsWith("file:") || imageUrl.startsWith("http"))) {
            try {
                ImageView iv = new ImageView(new Image(imageUrl));
                iv.setFitHeight(50); iv.setFitWidth(50); iv.setPreserveRatio(true);
                imageLabel.setGraphic(iv);
            } catch (Exception e) { imageLabel.setText(product.getImage()); imageLabel.setFont(Font.font(40)); }
        } else {
            imageLabel.setText(product.getImage()); imageLabel.setFont(Font.font(40));
        }

        VBox detailsBox = new VBox(5);
        detailsBox.getChildren().addAll(
            new Label(product.getName()), 
            new Label(String.format("$%.2f each", item.getPrice()))
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeBtn = new Button("❌");
        removeBtn.setOnAction(e -> {
            dataStore.deleteCartItem(item);
            dialog.close();
            show(); // Refresh
        });

        itemBox.getChildren().addAll(imageLabel, detailsBox, spacer, new Label("Qty: " + item.getQuantity()), 
                new Label(String.format("$%.2f", item.getTotal())), removeBtn);
        return itemBox;
    }

    private void checkout(Cart userCart) {
        userCart.setStatus("Completed");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Completed");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Your order has been placed successfully!\nTotal: $%.2f", userCart.getTotalPrice()));
        alert.showAndWait();
        dataStore.addCart(new Cart(userId, "Active")); // Create new active cart
    }
}
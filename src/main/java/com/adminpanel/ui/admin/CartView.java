package com.adminpanel.ui.admin;

import com.adminpanel.model.Cart;
import com.adminpanel.model.CartItem;
import com.adminpanel.model.Product;
import com.adminpanel.model.User;
import com.adminpanel.store.DataStore;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class CartView {
    private final DataStore dataStore;
    private TableView<Cart> cartTable;

    public CartView() {
        this.dataStore = DataStore.getInstance();
    }

    public VBox getView() {
        VBox content = new VBox(20);

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

        cartTable = createCartTable();

        refreshBtn.setOnAction(e -> {
            cartTable.refresh();
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                cartTable.setItems(dataStore.getCartsObservable());
            } else {
                javafx.collections.ObservableList<Cart> filtered = FXCollections.observableArrayList();
                for (Cart cart : dataStore.getCartsObservable()) {
                    User user = dataStore.getUserById(cart.getUserId());
                    if (user != null && user.getFullName().toLowerCase().contains(newVal.toLowerCase())) {
                        filtered.add(cart);
                    }
                }
                cartTable.setItems(filtered);
            }
        });

        content.getChildren().addAll(title, topBar, cartTable);
        return content;
    }

    public TableView<Cart> getCartTable() {
        return cartTable;
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
        table.setItems(dataStore.getCartsObservable());

        return table;
    }

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

        dialog.showAndWait().ifPresent(dataStore::addCart);
    }

    private void showCartViewDialog(Cart cart) {
        User user = dataStore.getUserById(cart.getUserId());
        List<CartItem> items = dataStore.getCartItemsByCartId(cart.getId());

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

        dialog.showAndWait();
        cartTable.refresh();
    }
}
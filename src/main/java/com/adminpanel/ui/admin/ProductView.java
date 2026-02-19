package com.adminpanel.ui.admin;

import com.adminpanel.model.Category;
import com.adminpanel.model.Product;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class ProductView {
    private final DataStore dataStore;
    private TableView<Product> table;

    public ProductView() {
        this.dataStore = DataStore.getInstance();
    }

    public VBox getView() {
        VBox content = new VBox(20);

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

        table = createProductTable();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                table.setItems(dataStore.getProductsObservable());
            } else {
                javafx.collections.ObservableList<Product> filtered = FXCollections.observableArrayList();
                for (Product product : dataStore.getProductsObservable()) {
                    if (product.getName().toLowerCase().contains(newVal.toLowerCase())) {
                        filtered.add(product);
                    }
                }
                table.setItems(filtered);
            }
        });

        content.getChildren().addAll(title, topBar, table);
        return content;
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
        imageCol.setCellFactory(col -> new TableCell<Product, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String imageUrl = item;
                    if (!item.startsWith("file:") && !item.startsWith("http")) {
                        File file = new File(item);
                        if (file.exists()) {
                            imageUrl = file.toURI().toString();
                        }
                    }

                    if (imageUrl.startsWith("file:") || imageUrl.startsWith("http")) {
                        try { imageView.setImage(new Image(imageUrl)); setGraphic(imageView); setText(null); }
                        catch (Exception e) { setGraphic(null); setText(item); }
                    } else {
                        setGraphic(null);
                        setText(item);
                    }
                }
            }
        });

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
        table.setItems(dataStore.getProductsObservable());

        return table;
    }

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

        ImageView preview = new ImageView();
        preview.setFitHeight(100);
        preview.setFitWidth(100);
        preview.setPreserveRatio(true);

        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String uri = file.toURI().toString();
                imageField.setText(uri);
                preview.setImage(new Image(uri));
            }
        });
        HBox imageBox = new HBox(10, browseBtn, preview);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Image:"), 0, 2);
        grid.add(imageBox, 1, 2);
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

        dialog.showAndWait().ifPresent(dataStore::addProduct);
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

        ImageView preview = new ImageView();
        preview.setFitHeight(100);
        preview.setFitWidth(100);
        preview.setPreserveRatio(true);
        if (product.getImage() != null && (product.getImage().startsWith("file:") || product.getImage().startsWith("http"))) {
            try { preview.setImage(new Image(product.getImage())); } catch (Exception ignored) {}
        }

        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String uri = file.toURI().toString();
                imageField.setText(uri);
                preview.setImage(new Image(uri));
            }
        });
        HBox imageBox = new HBox(10, browseBtn, preview);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Image:"), 0, 2);
        grid.add(imageBox, 1, 2);
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

        dialog.showAndWait();
        table.refresh();
    }
}
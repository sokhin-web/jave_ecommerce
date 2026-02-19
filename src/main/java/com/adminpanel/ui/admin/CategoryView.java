package com.adminpanel.ui.admin;

import com.adminpanel.model.Category;
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

public class CategoryView {
    private final DataStore dataStore;
    private TableView<Category> table;

    public CategoryView() {
        this.dataStore = DataStore.getInstance();
    }

    public VBox getView() {
        VBox content = new VBox(20);

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

        table = createCategoryTable();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                table.setItems(dataStore.getCategoriesObservable());
            } else {
                javafx.collections.ObservableList<Category> filtered = FXCollections.observableArrayList();
                for (Category category : dataStore.getCategoriesObservable()) {
                    if (category.getName().toLowerCase().contains(newVal.toLowerCase())) {
                        filtered.add(category);
                    }
                }
                table.setItems(filtered);
            }
        });

        content.getChildren().addAll(title, topBar, table);
        return content;
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
        iconCol.setCellFactory(col -> new TableCell<Category, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
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
        table.setItems(dataStore.getCategoriesObservable());

        return table;
    }

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

        ImageView preview = new ImageView();
        preview.setFitHeight(50);
        preview.setFitWidth(50);
        preview.setPreserveRatio(true);

        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Icon");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String uri = file.toURI().toString();
                iconField.setText(uri);
                preview.setImage(new Image(uri));
            }
        });
        HBox iconBox = new HBox(10, browseBtn, preview);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Icon:"), 0, 2);
        grid.add(iconBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Category(nameField.getText(), descField.getText(), iconField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dataStore::addCategory);
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

        ImageView preview = new ImageView();
        preview.setFitHeight(50);
        preview.setFitWidth(50);
        preview.setPreserveRatio(true);
        if (category.getIcon() != null && (category.getIcon().startsWith("file:") || category.getIcon().startsWith("http"))) {
            try { preview.setImage(new Image(category.getIcon())); } catch (Exception ignored) {}
        }

        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Icon");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String uri = file.toURI().toString();
                iconField.setText(uri);
                preview.setImage(new Image(uri));
            }
        });
        HBox iconBox = new HBox(10, browseBtn, preview);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Icon:"), 0, 2);
        grid.add(iconBox, 1, 2);

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

        dialog.showAndWait();
        table.refresh();
    }
}
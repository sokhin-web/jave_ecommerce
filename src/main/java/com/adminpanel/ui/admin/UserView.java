package com.adminpanel.ui.admin;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

public class UserView {
    private final DataStore dataStore;
    private TableView<User> table;

    public UserView() {
        this.dataStore = DataStore.getInstance();
    }

    public VBox getView() {
        VBox content = new VBox(20);

        Label title = new Label("Manage of User");
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
        createBtn.setOnAction(e -> showUserCreateDialog());

        topBar.getChildren().addAll(searchField, spacer, createBtn);

        table = createUserTable();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                table.setItems(dataStore.getUsersObservable());
            } else {
                javafx.collections.ObservableList<User> filtered = FXCollections.observableArrayList();
                for (User user : dataStore.getUsersObservable()) {
                    if (user.getFullName().toLowerCase().contains(newVal.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(newVal.toLowerCase()) ||
                            user.getPhone().contains(newVal)) {
                        filtered.add(user);
                    }
                }
                table.setItems(filtered);
            }
        });

        content.getChildren().addAll(title, topBar, table);
        return content;
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
        
        TableColumn<User, String> imageCol = new TableColumn<>("Image");
        imageCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImage()));
        imageCol.setCellFactory(col -> new TableCell<User, String>() {
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
                phoneCol, addressCol, positionCol, imageCol, actionCol);
        table.setItems(dataStore.getUsersObservable());

        return table;
    }

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
        grid.add(imageBox, 1, 7);

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

        dialog.showAndWait().ifPresent(dataStore::addUser);
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

        ImageView preview = new ImageView();
        preview.setFitHeight(100);
        preview.setFitWidth(100);
        preview.setPreserveRatio(true);
        if (user.getImage() != null && (user.getImage().startsWith("file:") || user.getImage().startsWith("http"))) {
            try { preview.setImage(new Image(user.getImage())); } catch (Exception ignored) {}
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
        grid.add(imageBox, 1, 7);

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

        dialog.showAndWait();
        table.refresh();
    }
}
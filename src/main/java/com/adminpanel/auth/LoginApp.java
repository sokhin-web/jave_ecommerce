package com.adminpanel.auth;

import com.adminpanel.model.User;
import com.adminpanel.store.DataStore;
import com.adminpanel.util.ApplicationState;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class LoginApp extends Application {

    private DataStore dataStore;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        dataStore = DataStore.getInstance();

        primaryStage.setTitle("E-Commerce Login");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);

        Label title = new Label("Welcome Back");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Please sign in to continue");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(400);
        emailField.setStyle("-fx-padding: 15; -fx-font-size: 14; -fx-background-radius: 8; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(400);
        passwordField.setStyle("-fx-padding: 15; -fx-font-size: 14; -fx-background-radius: 8; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1;");

        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(190);
        loginBtn.setPrefHeight(45);
        loginBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 10, 0, 0, 3);");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.4), 12, 0, 0, 4);"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 10, 0, 0, 3);"));


        // Add keyboard shortcut - Enter to login
        passwordField.setOnAction(e -> loginBtn.fire());
        
        Button registerBtn = new Button("Create Account");
        registerBtn.setPrefWidth(190);
        registerBtn.setPrefHeight(45);
        registerBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498db; " +
                "-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;");
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #3498db; " +
                "-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498db; " +
                "-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;"));

        HBox actions = new HBox(15, loginBtn, registerBtn);
        actions.setAlignment(Pos.CENTER);

        VBox formBox = new VBox(20);
        formBox.getChildren().addAll(title, subtitle, emailField, passwordField, actions);
        formBox.setPadding(new Insets(50));
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(550);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        BorderPane root = new BorderPane(formBox);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #ecf0f1, #dfe6e9);");

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Please enter email and password");
                return;
            }

            User found = null;
            for (User u : dataStore.getUsers()) {
                if (u.getEmail().equalsIgnoreCase(email) && u.verifyPassword(password)) {
                    found = u;
                    break;
                }
            }

            if (found == null) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password");
                return;
            }

            // Set current user in application state
            ApplicationState.setCurrentUser(found);

            // Navigate based on position
            String pos = found.getPosition() != null ? found.getPosition().toLowerCase() : "";
            try {
                if (pos.contains("admin") || pos.contains("manager")) {
                    new com.adminpanel.ui.DashboardApp().start(primaryStage);
                } else {
                    new com.adminpanel.ui.ClientApp().start(primaryStage);
                }
            } catch (Exception ex) {
                System.err.println("Launch Error: " + ex.getMessage());
                showAlert(Alert.AlertType.ERROR, "Launch Error", "Cannot open application window");
            }
        });

        registerBtn.setOnAction(e -> showRegisterDialog());

        Scene scene = new Scene(root, 800, 650);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void showRegisterDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Register New User");
        dialog.setHeaderText("Create a new account");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30));
        grid.setPrefWidth(500);


        TextField firstName = new TextField();
        firstName.setPromptText("First name");
        firstName.setStyle("-fx-padding: 10;");
        TextField lastName = new TextField();
        lastName.setPromptText("Last name");
        lastName.setStyle("-fx-padding: 10;");
        TextField email = new TextField();
        email.setPromptText("Email");
        email.setStyle("-fx-padding: 10;");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setStyle("-fx-padding: 10;");
        TextField address = new TextField();
        address.setPromptText("Address");
        address.setStyle("-fx-padding: 10;");
        TextField phone = new TextField();
        phone.setPromptText("Phone");
        phone.setStyle("-fx-padding: 10;");
        
        // Hidden field to store the image URL
        TextField imageField = new TextField();
        
        ImageView preview = new ImageView();
        preview.setFitHeight(60);
        preview.setFitWidth(60);
        preview.setPreserveRatio(true);
        try { preview.setImage(new Image("https://cdn-icons-png.flaticon.com/512/149/149071.png")); } catch (Exception ignored) {}
        
        Button browseBtn = new Button("Upload Photo");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String uri = file.toURI().toString();
                imageField.setText(uri);
                try { preview.setImage(new Image(uri)); } catch (Exception ex) {}
            }
        });
        
        HBox imageBox = new HBox(15, browseBtn, preview);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        grid.add(new Label("First name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(password, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(address, 1, 4);
        grid.add(new Label("Phone:"), 0, 5);
        grid.add(phone, 1, 5);
        grid.add(new Label("Image:"), 0, 6);
        grid.add(imageBox, 1, 6);

        dialog.getDialogPane().setContent(grid);
        ButtonType createBtn = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == createBtn) {
                String imageUrl = imageField.getText().trim();
                if (imageUrl.isEmpty()) {
                    imageUrl = "https://cdn-icons-png.flaticon.com/512/149/149071.png";
                }
                return new User(firstName.getText().trim(), lastName.getText().trim(),
                    email.getText().trim(), password.getText(),
                    "Customer", address.getText().trim(), phone.getText().trim(), imageUrl);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> {
            // simple duplicate email check
            for (User ex : dataStore.getUsers()) {
                if (ex.getEmail().equalsIgnoreCase(u.getEmail())) {
                    showAlert(Alert.AlertType.ERROR, "Register", "Email already exists");
                    return;
                }
            }
            dataStore.addUser(u);
            showAlert(Alert.AlertType.INFORMATION, "Register", "User registered successfully");
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}

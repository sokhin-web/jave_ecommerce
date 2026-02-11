package com.adminpanel.auth;

import com.adminpanel.store.DataStore;
import com.adminpanel.model.User;
import com.adminpanel.util.ApplicationState;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginApp extends Application {

    private DataStore dataStore;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        dataStore = DataStore.getInstance();

        primaryStage.setTitle("Login / Register");

        Label title = new Label("Welcome — Please sign in or register");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(350);
        emailField.setStyle("-fx-padding: 12; -fx-font-size: 14;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(350);
        passwordField.setStyle("-fx-padding: 12; -fx-font-size: 14;");

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(150);
        loginBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loginBtn.setStyle("-fx-padding: 12; -fx-background-color: #4FB3E8; -fx-text-fill: white; -fx-background-radius: 8;");
        
        Button registerBtn = new Button("Register");
        registerBtn.setPrefWidth(150);
        registerBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        registerBtn.setStyle("-fx-padding: 12; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8;");

        HBox actions = new HBox(15, loginBtn, registerBtn);
        actions.setAlignment(Pos.CENTER);

        VBox formBox = new VBox(15, title, emailField, passwordField, actions);
        formBox.setPadding(new Insets(40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setPrefWidth(500);
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");

        BorderPane root = new BorderPane(formBox);
        root.setStyle("-fx-background-color: #f5f5f5;");

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
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Launch Error", "Cannot open application window");
            }
        });

        registerBtn.setOnAction(e -> showRegisterDialog());

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
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
        TextField position = new TextField();
        position.setPromptText("Position (Admin/Customer)");
        position.setStyle("-fx-padding: 10;");
        TextField address = new TextField();
        address.setPromptText("Address");
        address.setStyle("-fx-padding: 10;");
        TextField phone = new TextField();
        phone.setPromptText("Phone");
        phone.setStyle("-fx-padding: 10;");
        TextField image = new TextField("👤");
        image.setStyle("-fx-padding: 10;");

        grid.add(new Label("First name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(password, 1, 3);
        grid.add(new Label("Position:"), 0, 4);
        grid.add(position, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(address, 1, 5);
        grid.add(new Label("Phone:"), 0, 6);
        grid.add(phone, 1, 6);
        grid.add(new Label("Image:"), 0, 7);
        grid.add(image, 1, 7);

        dialog.getDialogPane().setContent(grid);
        ButtonType createBtn = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == createBtn) {
                return new User(firstName.getText().trim(), lastName.getText().trim(),
                        email.getText().trim(), password.getText(),
                        position.getText().trim(), address.getText().trim(), phone.getText().trim(), image.getText().trim());
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

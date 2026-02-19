package com.adminpanel.ui.admin;

import com.adminpanel.model.User;
import com.adminpanel.util.ApplicationState;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class SettingsView {

    public VBox getView() {
        VBox content = new VBox(20);

        Label title = new Label("Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        User currentUser = ApplicationState.getCurrentUser();

        VBox settingsBox = new VBox(20);
        settingsBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 30;");
        settingsBox.setPrefWidth(600);

        if (currentUser != null) {
            Label userInfoTitle = new Label("User Information");
            userInfoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            GridPane userGrid = new GridPane();
            userGrid.setHgap(20);
            userGrid.setVgap(15);
            userGrid.setPadding(new Insets(20));
            userGrid.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

            Label userIconLabel = new Label();
            String imageUrl = currentUser.getImage();
            if (imageUrl != null && !imageUrl.startsWith("file:") && !imageUrl.startsWith("http")) {
                File file = new File(imageUrl);
                if (file.exists()) imageUrl = file.toURI().toString();
            }

            if (imageUrl != null && (imageUrl.startsWith("file:") || imageUrl.startsWith("http"))) {
                try {
                    ImageView iv = new ImageView(new Image(imageUrl));
                    iv.setFitHeight(80); iv.setFitWidth(80); iv.setPreserveRatio(true);
                    userIconLabel.setGraphic(iv);
                } catch (Exception e) { userIconLabel.setText(currentUser.getImage()); userIconLabel.setFont(Font.font(48)); }
            } else {
                userIconLabel.setText(currentUser.getImage()); userIconLabel.setFont(Font.font(48));
            }
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

            Button logoutBtn = new Button("Logout");
            logoutBtn.setPrefWidth(150);
            logoutBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            logoutBtn.setStyle("-fx-padding: 12; -fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 8;");
            logoutBtn.setOnAction(e -> handleLogout(logoutBtn));

            settingsBox.getChildren().addAll(userInfoTitle, userGrid, logoutBtn);
        } else {
            Label info = new Label("No user logged in. Please login first.");
            info.setFont(Font.font("Arial", 14));
            settingsBox.getChildren().add(info);
        }

        content.getChildren().addAll(title, settingsBox);
        return content;
    }

    private void handleLogout(Button sourceBtn) {
        ApplicationState.logout();

        Stage window = (Stage) sourceBtn.getScene().getWindow();
        
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
}
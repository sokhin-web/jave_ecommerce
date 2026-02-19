package com.adminpanel.ui.client;

import com.adminpanel.model.User;
import com.adminpanel.util.ApplicationState;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class ClientSettingsView {

    public void show() {
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
            userGrid.add(new Label("Name: " + currentUser.getFullName()), 1, 0);
            userGrid.add(new Label("Email: " + currentUser.getEmail()), 1, 1);
            userGrid.add(new Label("Phone: " + currentUser.getPhone()), 1, 2);
            userGrid.add(new Label("Address: " + currentUser.getAddress()), 1, 3);

            content.getChildren().addAll(userInfoTitle, userGrid);
        }
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
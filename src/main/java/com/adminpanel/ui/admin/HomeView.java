package com.adminpanel.ui.admin;

import com.adminpanel.model.User;
import com.adminpanel.store.DataStore;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;

public class HomeView {
    private final DataStore dataStore;

    public HomeView() {
        this.dataStore = DataStore.getInstance();
    }

    public VBox getView() {
        VBox content = new VBox(20);
        
        // Statistics Cards
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("User amount", String.valueOf(dataStore.getUsers().size())),
                createStatCard("Category", String.valueOf(dataStore.getCategories().size())),
                createStatCard("Product", String.valueOf(dataStore.getProducts().size())),
                createStatCard("User Cart", String.valueOf(dataStore.getCarts().size())));

        // Recent Users Table
        Label recentLabel = new Label("User Recently");
        recentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TableView<User> recentUsersTable = createRecentUsersTable();

        content.getChildren().addAll(statsBox, recentLabel, recentUsersTable);
        return content;
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(250);
        card.setPrefHeight(100);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private TableView<User> createRecentUsersTable() {
        TableView<User> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        idCol.setPrefWidth(80);

        TableColumn<User, String> nameCol = new TableColumn<>("Full name");
        nameCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(200);

        TableColumn<User, String> phoneCol = new TableColumn<>("phone");
        phoneCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        phoneCol.setPrefWidth(150);

        TableColumn<User, String> positionCol = new TableColumn<>("position");
        positionCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPosition()));
        positionCol.setPrefWidth(150);

        TableColumn<User, String> imageCol = new TableColumn<>("image");
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
        imageCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, nameCol, phoneCol, positionCol, imageCol);

        javafx.collections.ObservableList<User> recentUsers = FXCollections.observableArrayList();
        int size = dataStore.getUsersObservable().size();
        int start = Math.max(0, size - 5);
        recentUsers.addAll(dataStore.getUsersObservable().subList(start, size));
        table.setItems(recentUsers);
        return table;
    }
}
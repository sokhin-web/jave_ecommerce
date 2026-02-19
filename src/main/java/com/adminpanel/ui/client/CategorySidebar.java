package com.adminpanel.ui.client;

import com.adminpanel.model.Category;
import com.adminpanel.store.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

import java.util.function.Consumer;

public class CategorySidebar {
    private final DataStore dataStore;
    private final Consumer<Category> onCategorySelected;

    public CategorySidebar(Consumer<Category> onCategorySelected) {
        this.dataStore = DataStore.getInstance();
        this.onCategorySelected = onCategorySelected;
    }

    public VBox getView(Category selectedCategory) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");

        Label categoriesTitle = new Label("Categories");
        categoriesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        categoriesTitle.setPadding(new Insets(0, 0, 10, 0));

        VBox categoryButtons = new VBox(5);
        
        // All Products button
        categoryButtons.getChildren().add(createCategoryButton("All Products", "🏪", selectedCategory == null, null));

        // Category buttons
        for (Category category : dataStore.getCategories()) {
            categoryButtons.getChildren().add(createCategoryButton(category.getName(), category.getIcon(), 
                    selectedCategory != null && selectedCategory.getId() == category.getId(), category));
        }

        sidebar.getChildren().addAll(categoriesTitle, new Separator(), categoryButtons);
        return sidebar;
    }

    private Button createCategoryButton(String text, String icon, boolean isSelected, Category category) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(15, 20, 15, 20));
        btn.setFont(Font.font("Arial", isSelected ? FontWeight.BOLD : FontWeight.SEMI_BOLD, 14));
        
        String baseStyle = "-fx-background-radius: 8; -fx-cursor: hand;";
        String activeStyle = "-fx-background-color: #4FB3E8; -fx-text-fill: white; -fx-font-weight: bold; " + baseStyle;
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: black; " + baseStyle;
        String hoverStyle = "-fx-background-color: #f0f0f0; -fx-text-fill: black; " + baseStyle;

        btn.setStyle(isSelected ? activeStyle : inactiveStyle);
        btn.setOnMouseEntered(e -> { if (!isSelected) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e -> btn.setStyle(isSelected ? activeStyle : inactiveStyle));
        
        String imageUrl = icon;
        if (icon != null && !icon.startsWith("file:") && !icon.startsWith("http")) {
            File file = new File(icon);
            if (file.exists()) {
                imageUrl = file.toURI().toString();
            }
        }

        if (imageUrl != null && (imageUrl.startsWith("file:") || imageUrl.startsWith("http"))) {
            try {
                ImageView iv = new ImageView(new Image(imageUrl));
                iv.setFitHeight(20); iv.setFitWidth(20); iv.setPreserveRatio(true);
                btn.setGraphic(iv);
            } catch (Exception e) { btn.setText(icon + "  " + text); }
        } else {
            btn.setText(icon + "  " + text);
        }

        btn.setOnAction(e -> onCategorySelected.accept(category));
        return btn;
    }
}
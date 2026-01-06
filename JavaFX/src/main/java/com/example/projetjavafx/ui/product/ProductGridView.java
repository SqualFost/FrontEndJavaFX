package com.example.projetjavafx.ui.product;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.BiConsumer;

public class ProductGridView {

    private final ScrollPane scrollPane;
    private final GridPane grid;
    private final String colorAccent;
    private final BiConsumer<String, Double> onShowDetails;

    public ProductGridView(String colorAccent, BiConsumer<String, Double> onShowDetails) {
        this.colorAccent = colorAccent;
        this.onShowDetails = onShowDetails;
        this.grid = new GridPane();
        this.scrollPane = new ScrollPane(grid);

        setupGrid();
    }

    private void setupGrid() {
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.TOP_LEFT);

        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    public void updateContent(String category) {
        grid.getChildren().clear();
        // Simulation de 16 plats par catégorie
        for (int i = 0; i < 16; i++) {
            VBox card = createProductCard(category + " #" + (i + 1), 12.0 + i);
            grid.add(card, i % 4, i / 4); // 4 colonnes
        }
    }

    private VBox createProductCard(String nom, double prix) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(260, 240);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 4);");

        Circle img = new Circle(45, Color.web("#F9F9F9"));
        Text t = new Text(nom); t.setFont(Font.font("System", FontWeight.BOLD, 14));
        Text p = new Text(String.format("%.2f€", prix));

        Button btn = new Button("PERSONNALISER");
        btn.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05); -fx-font-weight: bold; -fx-text-fill: " + colorAccent + ";");
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setOnAction(e -> onShowDetails.accept(nom, prix));

        card.getChildren().addAll(img, t, p, btn);
        return card;
    }

    public Node getView() {
        return scrollPane;
    }
}
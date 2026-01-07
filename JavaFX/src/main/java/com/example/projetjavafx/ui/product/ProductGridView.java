package com.example.projetjavafx.ui.product;

import com.example.projetjavafx.config.ApiConfig;
import com.example.projetjavafx.model.Product;
import com.example.projetjavafx.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.BiConsumer;

public class ProductGridView {

    private final GridPane grid;
    private final String colorAccent;
    private final BiConsumer<String, Double> onShowDetails;
    private final ApiService apiService;

    public ProductGridView(String colorAccent, BiConsumer<String, Double> onShowDetails) {
        this.colorAccent = colorAccent;
        this.onShowDetails = onShowDetails;
        this.grid = new GridPane();
        this.apiService = new ApiService();

        setupGrid();
    }

    private void setupGrid() {
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.TOP_LEFT);
    }

    public void updateContent(String category) {
        grid.getChildren().clear();
        
        // Afficher un indicateur de chargement
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(50, 50);
        grid.add(loadingIndicator, 0, 0);
        
        // Charger les données depuis le backend de manière asynchrone
        new Thread(() -> {
            List<Product> products = apiService.getPlatsByCategorie(category);
            
            // Mettre à jour l'UI sur le thread JavaFX
            Platform.runLater(() -> {
                grid.getChildren().clear();
                
                if (products.isEmpty()) {
                    // Aucun produit trouvé
                    Text noProductsText = new Text("Aucun produit disponible pour cette catégorie");
                    noProductsText.setFont(Font.font("System", FontWeight.NORMAL, 14));
                    noProductsText.setFill(Color.GRAY);
                    grid.add(noProductsText, 0, 0);
                } else {
                    // Afficher les produits
                    for (int i = 0; i < products.size(); i++) {
                        Product product = products.get(i);
                        VBox card = createProductCard(product);
                        grid.add(card, i % 4, i / 4); // 4 colonnes
                    }
                }
            });
        }).start();
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(260, 240);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 4);");

        // Image du produit (ou placeholder si pas d'image)
        Node imageNode;
        if (product.getPhotoUrl() != null && !product.getPhotoUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView();
                Image image = new Image(ApiConfig.getImageUrl(product.getPhotoUrl()), true);
                imageView.setImage(image);
                imageView.setFitWidth(90);
                imageView.setFitHeight(90);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageNode = imageView;
            } catch (Exception e) {
                // En cas d'erreur de chargement d'image, utiliser un placeholder
                imageNode = new Circle(45, Color.web("#F9F9F9"));
            }
        } else {
            imageNode = new Circle(45, Color.web("#F9F9F9"));
        }
        
        // Nom du produit (tronquer si trop long)
        String nom = product.getNom();
        if (nom.length() > 25) {
            nom = nom.substring(0, 22) + "...";
        }
        Text t = new Text(nom);
        t.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Prix
        Text p = new Text(String.format("%.2f€", product.getPrix()));
        p.setFill(Color.web(colorAccent));

        Button btn = new Button("PERSONNALISER");
        btn.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 0); -fx-font-weight: bold; -fx-text-fill: " + colorAccent + ";");
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setOnAction(e -> onShowDetails.accept(product.getNom(), (double) product.getPrix()));

        card.getChildren().addAll(imageNode, t, p, btn);
        return card;
    }

    public Node getView() {
        return grid;
    }
}
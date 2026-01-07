package com.example.projetjavafx.ui.carousel;

import com.example.projetjavafx.config.ApiConfig;
import com.example.projetjavafx.model.Product;
import com.example.projetjavafx.service.ApiService;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.function.BiConsumer;

public class CarouselView {

    private final String colorAccent;
    private final AnchorPane root;
    private final HBox cardsContainer;
    private final ScrollPane scrollPane;

    private final BiConsumer<String, Double> onAddToCart;
    private final BiConsumer<String, Double> onShowDetails;
    private final ApiService apiService;

    private final double CARD_SPACING = 15.0;
    private final double CARD_WIDTH = 200.0; // Encore plus petit

    public CarouselView(String initialCategory,
                        String colorAccent,
                        BiConsumer<String, Double> onAddToCart,
                        BiConsumer<String, Double> onShowDetails) {
        this.colorAccent = colorAccent;
        this.onAddToCart = onAddToCart;
        this.onShowDetails = onShowDetails;
        this.apiService = new ApiService();

        this.root = new AnchorPane();
        this.cardsContainer = new HBox(CARD_SPACING);
        this.scrollPane = new ScrollPane();

        buildLayout();
        initPlats(initialCategory);
    }

    public Node getView() {
        return root;
    }

    public void setCategory(String category) {
        initPlats(category);
    }

    private void buildLayout() {
        scrollPane.setContent(cardsContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setFitToHeight(true);

        // HAUTEUR RÉDUITE : On passe à 240px pour libérer de l'espace en bas
        scrollPane.setPrefHeight(240);

        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 35.0);
        AnchorPane.setRightAnchor(scrollPane, 35.0);

        Button btnLeft = createArrowButton("<");
        Button btnRight = createArrowButton(">");

        AnchorPane.setLeftAnchor(btnLeft, 0.0);
        AnchorPane.setTopAnchor(btnLeft, 90.0); // Ajusté pour la nouvelle hauteur

        AnchorPane.setRightAnchor(btnRight, 0.0);
        AnchorPane.setTopAnchor(btnRight, 90.0);

        btnLeft.setOnAction(e -> scrollSmoothly(-1));
        btnRight.setOnAction(e -> scrollSmoothly(1));

        root.getChildren().addAll(scrollPane, btnLeft, btnRight);
    }

    private Button createArrowButton(String text) {
        Button b = new Button(text);
        b.setPrefSize(35, 35);
        b.setStyle("-fx-background-color: white; -fx-background-radius: 18; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 0); " +
                "-fx-font-weight: bold;");
        b.setCursor(Cursor.HAND);
        return b;
    }

    private void initPlats(String cat) {
        cardsContainer.getChildren().clear();
        cardsContainer.setPadding(new Insets(5));
        
        // Charger les données depuis le backend de manière asynchrone
        new Thread(() -> {
            List<Product> products = apiService.getPlatsByCategorie(cat);
            
            // Limiter à 8 produits pour le carousel
            if (products.size() > 8) {
                products = products.subList(0, 8);
            }
            
            // Mettre à jour l'UI sur le thread JavaFX
            List<Product> finalProducts = products;
            Platform.runLater(() -> {
                cardsContainer.getChildren().clear();
                for (Product product : finalProducts) {
                    VBox card = createMiniCard(product);
                    cardsContainer.getChildren().add(card);
                }
            });
        }).start();
    }

    private void scrollSmoothly(int direction) {
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double contentWidth = cardsContainer.getBoundsInLocal().getWidth();
        double hStep = (CARD_WIDTH + CARD_SPACING) / (contentWidth - viewportWidth);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(scrollPane.hvalueProperty(),
                Math.max(0, Math.min(1, scrollPane.getHvalue() + (direction * hStep))), Interpolator.EASE_BOTH);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), kv));
        timeline.play();
    }

    private VBox createMiniCard(Product product) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(CARD_WIDTH, 220);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 8, 0, 0, 2);");

        // Image du produit (ou placeholder si pas d'image)
        Node imageNode;
        if (product.getPhotoUrl() != null && !product.getPhotoUrl().isEmpty()) {
            try {
                ImageView imageView = new ImageView();
                Image image = new Image(ApiConfig.getImageUrl(product.getPhotoUrl()), true);
                imageView.setImage(image);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCursor(Cursor.HAND);
                imageView.setOnMouseClicked(e -> { 
                    if (onShowDetails != null) onShowDetails.accept(product.getNom(), (double) product.getPrix()); 
                });
                imageNode = imageView;
            } catch (Exception e) {
                // En cas d'erreur de chargement d'image, utiliser un placeholder
                Circle img = new Circle(40, Color.web("#F1F1F1"));
                img.setCursor(Cursor.HAND);
                img.setOnMouseClicked(mouseEvent -> {
                    if (onShowDetails != null) onShowDetails.accept(product.getNom(), (double) product.getPrix()); 
                });
                imageNode = img;
            }
        } else {
            Circle img = new Circle(40, Color.web("#F1F1F1"));
            img.setCursor(Cursor.HAND);
            img.setOnMouseClicked(e -> { 
                if (onShowDetails != null) onShowDetails.accept(product.getNom(), (double) product.getPrix()); 
            });
            imageNode = img;
        }

        // Nom du produit (tronquer si trop long)
        String nom = product.getNom();
        if (nom.length() > 20) {
            nom = nom.substring(0, 17) + "...";
        }
        Text t = new Text(nom);
        t.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Text p = new Text(String.format("%.2f€", product.getPrix()));
        p.setFill(Color.web(colorAccent));

        Button btn = new Button("GO");
        btn.setStyle("-fx-background-color: " + colorAccent + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 10;");
        btn.setOnAction(e -> { 
            if (onAddToCart != null) onAddToCart.accept(product.getNom(), (double) product.getPrix()); 
        });

        card.getChildren().addAll(imageNode, t, p, btn);
        return card;
    }
}
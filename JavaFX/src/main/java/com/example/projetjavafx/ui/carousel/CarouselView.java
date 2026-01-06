package com.example.projetjavafx.ui.carousel;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Vue responsable de l'affichage du carrousel de plats
 * et des interactions associées (drag, inertie, etc.).
 */
public class CarouselView {

    // Config carrousel
    private double angleRotation = 0;
    private double lastMouseX;
    private double velocity = 0;
    private final double FRICTION = 0.95;
    private final double RAYON = 380;
    private final int nElements = 8;
    private boolean isDragging = false;

    private final String colorAccent;
    private final AnchorPane root;
    private final Pane carouselContainer;
    private final VBox filterMenu;

    private final List<VBox> platsNodes = new ArrayList<>();

    private final BiConsumer<String, Double> onAddToCart;
    private final BiConsumer<String, Double> onShowDetails;

    public CarouselView(String initialCategory,
                        String colorAccent,
                        BiConsumer<String, Double> onAddToCart,
                        BiConsumer<String, Double> onShowDetails) {
        this.colorAccent = colorAccent;
        this.onAddToCart = onAddToCart;
        this.onShowDetails = onShowDetails;

        this.root = new AnchorPane();
        this.carouselContainer = new Pane();
        this.filterMenu = createFilterMenu();

        buildLayout();
        initPlats(initialCategory);
        setupInteractions();
        startAnimation();
    }

    public Node getView() {
        return root;
    }

    public void setCategory(String category) {
        initPlats(category);
    }

    // --- Construction ---

    private void buildLayout() {
        carouselContainer.setPickOnBounds(false);
        AnchorPane.setTopAnchor(carouselContainer, 0.0);
        AnchorPane.setBottomAnchor(carouselContainer, 0.0);
        AnchorPane.setLeftAnchor(carouselContainer, 0.0);
        AnchorPane.setRightAnchor(carouselContainer, 0.0);

        AnchorPane.setTopAnchor(filterMenu, 20.0);
        AnchorPane.setRightAnchor(filterMenu, 40.0);

        root.getChildren().addAll(carouselContainer, filterMenu);
    }

    private VBox createFilterMenu() {
        VBox menu = new VBox(8);
        menu.setPadding(new Insets(15));
        menu.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        menu.getChildren().add(new Text("FILTRES"));
        String[] f = {"🌿 Vegan", "🌶️ Épicé", "⭐ Populaire"};
        for (String s : f) {
            Button b = new Button(s);
            b.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            menu.getChildren().add(b);
        }
        return menu;
    }

    // --- Logique carrousel ---

    private void initPlats(String cat) {
        carouselContainer.getChildren().clear();
        platsNodes.clear();
        for (int i = 1; i <= nElements; i++) {
            VBox card = createPlateCard(cat + " " + i, 12.0 + i, (i % 3 == 0), (i % 2 == 0), (i != 5));
            platsNodes.add(card);
            carouselContainer.getChildren().add(card);
        }
        updatePlatsPosition();
    }

    private VBox createPlateCard(String nom, double prix, boolean epice, boolean veg, boolean dispo) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(300, 420);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 10);");

        HBox iconBar = new HBox(8);
        iconBar.setPadding(new Insets(15, 20, 0, 20));
        iconBar.setAlignment(Pos.CENTER_RIGHT);
        Circle dot = new Circle(5, dispo ? Color.web("#4CD964") : Color.web("#FF3B30"));
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        iconBar.getChildren().addAll(dot, s);
        if (epice) iconBar.getChildren().add(new Text("🌶️"));
        if (veg) iconBar.getChildren().add(new Text("🌿"));

        Circle img = new Circle(80, Color.web("#F9F9F9"));
        img.setCursor(Cursor.HAND);
        img.setOnMouseClicked(e -> {
            if (onShowDetails != null) {
                onShowDetails.accept(nom, prix);
            }
        });

        Text t = new Text(nom);
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        Text p = new Text(String.format("%.2f€", prix));
        p.setFill(Color.web(colorAccent));

        Button btn = new Button(dispo ? "AJOUTER" : "ÉPUISÉ");
        btn.setDisable(!dispo);
        btn.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 20; -fx-padding: 8 25; -fx-font-weight: bold;");
        btn.setOnAction(e -> {
            if (dispo && onAddToCart != null) {
                onAddToCart.accept(nom, prix);
            }
        });

        card.getChildren().addAll(iconBar, img, t, p, btn);
        return card;
    }

    private void updatePlatsPosition() {
        double w = (carouselContainer.getWidth() > 0) ? carouselContainer.getWidth() : (1920 - 450);
        double h = (carouselContainer.getHeight() > 0) ? carouselContainer.getHeight() : (1080 - 100);
        double cX = w / 2.0;
        double cY = (h / 2.0) - 60;
        for (int i = 0; i < platsNodes.size(); i++) {
            double rad = Math.toRadians(angleRotation + (i * (360.0 / nElements)) + 90);
            platsNodes.get(i).setLayoutX(cX + Math.cos(rad) * RAYON - 150);
            platsNodes.get(i).setLayoutY(cY + Math.sin(rad) * (RAYON * 0.45) - 210);
            double sin = Math.sin(rad);
            platsNodes.get(i).getProperties().put("depth", sin);
            if (sin < -0.2) {
                platsNodes.get(i).setVisible(false);
            } else {
                platsNodes.get(i).setVisible(true);
                double s = 0.7 + (sin * 0.3);
                platsNodes.get(i).setScaleX(s);
                platsNodes.get(i).setScaleY(s);
                platsNodes.get(i).setOpacity(Math.min(1.0, (sin + 0.2) * 4));
            }
        }
        List<VBox> sorted = new ArrayList<>(platsNodes);
        sorted.sort((a, b) -> Double.compare(
                (Double) a.getProperties().getOrDefault("depth", -1.0),
                (Double) b.getProperties().getOrDefault("depth", -1.0)
        ));
        carouselContainer.getChildren().setAll(sorted);
    }

    // --- Interactions & animation ---

    private void setupInteractions() {
        root.setOnMousePressed(e -> {
            if (e.getX() < root.getWidth() - 180) {
                isDragging = true;
                velocity = 0;
                lastMouseX = e.getSceneX();
                root.setCursor(Cursor.CLOSED_HAND);
            }
        });

        root.setOnMouseDragged(e -> {
            if (isDragging) {
                double deltaX = e.getSceneX() - lastMouseX;
                velocity = deltaX * 0.4;
                angleRotation += velocity;
                updatePlatsPosition();
                lastMouseX = e.getSceneX();
            }
        });

        root.setOnMouseReleased(e -> {
            isDragging = false;
            root.setCursor(Cursor.DEFAULT);
        });
    }

    private void startAnimation() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isDragging) {
                    if (Math.abs(velocity) > 0.1) {
                        angleRotation += velocity;
                        velocity *= FRICTION;
                    } else {
                        double step = 360.0 / nElements;
                        double target = Math.round(angleRotation / step) * step;
                        angleRotation += (target - angleRotation) * 0.1;
                    }
                    updatePlatsPosition();
                }
            }
        }.start();
    }
}



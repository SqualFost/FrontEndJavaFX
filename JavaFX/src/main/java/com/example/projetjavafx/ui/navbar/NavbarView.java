package com.example.projetjavafx.ui.navbar;

import com.example.projetjavafx.model.Categorie;
import com.example.projetjavafx.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class NavbarView {

    private final String colorText;
    private final String colorAccent;
    private String categorieActuelle;
    private final Consumer<String> onCategoryChange;
    private final ApiService apiService;

    private final Runnable onKitchenAction;
    private HBox root;
    private HBox categoriesBox;

    public NavbarView(String initialCategory,
                      String colorText,
                      String colorAccent,
                      Consumer<String> onCategoryChange,
                      Runnable onKitchenAction) {
        this.categorieActuelle = initialCategory;
        this.colorText = colorText;
        this.colorAccent = colorAccent;
        this.onCategoryChange = onCategoryChange;
        this.apiService = new ApiService();
        this.onKitchenAction = onKitchenAction;
        this.root = createNavbar();
    }

    public HBox getView() {
        return root;
    }

    private HBox createNavbar() {
        HBox navbar = new HBox(30);
        navbar.setPrefHeight(100);
        navbar.setPadding(new Insets(0, 50, 0, 50));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        Text logo = new Text("Click&Wok");
        logo.setFill(Color.web(colorText));
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        categoriesBox = new HBox(25);
        categoriesBox.setAlignment(Pos.CENTER);
        rebuildCategories();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnKitchen = new Button("👨‍🍳");
        btnKitchen.setStyle("-fx-background-color: #EEE; -fx-cursor: hand; -fx-font-size: 16; -fx-background-radius: 50;");
        btnKitchen.setOnAction(e -> {
            if (onKitchenAction != null) onKitchenAction.run();
        });
        Button btnLogin = new Button("CONNEXION");
        btnLogin.setStyle("-fx-background-color: " + colorText + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 25;");

        navbar.getChildren().addAll(logo, categoriesBox, spacer, btnKitchen, btnLogin);
        return navbar;
    }

    private void rebuildCategories() {
        categoriesBox.getChildren().clear();

        // Charger les catégories depuis le backend de manière asynchrone
        new Thread(() -> {
            List<Categorie> categories = apiService.getAllCategories();

            // Mettre à jour l'UI sur le thread JavaFX
            Platform.runLater(() -> {
                categoriesBox.getChildren().clear();

                if (categories.isEmpty()) {
                    // Si aucune catégorie n'est disponible, utiliser des catégories par défaut
                    String[] defaultTypes = {"ENTRÉES", "NOUILLES", "SUSHIS", "FRITURES", "DESSERTS", "BOISSONS"};
                    for (String type : defaultTypes) {
                        categoriesBox.getChildren().add(createNavButton(type));
                    }
                } else {
                    // Afficher les catégories du backend
                    for (Categorie categorie : categories) {
                        categoriesBox.getChildren().add(createNavButton(categorie.getNom()));
                    }
                }
            });
        }).start();
    }

    /**
     * Normalise une chaîne pour la comparaison (supprime accents, espaces, met en majuscules).
     */
    private String normalizeString(String str) {
        if (str == null) return "";
        return str.toUpperCase()
                .replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E")
                .replace("Ë", "E")
                .replace("À", "A")
                .replace("Â", "A")
                .replace("Ä", "A")
                .replace("Ù", "U")
                .replace("Û", "U")
                .replace("Ü", "U")
                .replace("Ô", "O")
                .replace("Ö", "O")
                .replace("Ç", "C")
                .replace(" ", "")
                .trim();
    }

    private VBox createNavButton(String nom) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);

        // Comparaison normalisée pour gérer les accents et la casse
        boolean isActive = normalizeString(nom).equals(normalizeString(categorieActuelle));

        Text txt = new Text(nom);
        txt.setFill(isActive ? Color.web(colorAccent) : Color.web("#888"));
        txt.setFont(Font.font("System", FontWeight.BOLD, 14));

        Rectangle line = new Rectangle(30, 3);
        line.setFill(isActive ? Color.web(colorAccent) : Color.TRANSPARENT);

        container.getChildren().addAll(txt, line);
        container.setCursor(Cursor.HAND);

        container.setOnMouseClicked(e -> {
            categorieActuelle = nom;
            rebuildCategories();
            if (onCategoryChange != null) {
                onCategoryChange.accept(nom);
            }
        });

        return container;
    }
}



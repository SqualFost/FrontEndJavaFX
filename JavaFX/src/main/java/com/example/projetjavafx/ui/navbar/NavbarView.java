package com.example.projetjavafx.ui.navbar;

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

import java.util.function.Consumer;

/**
 * Vue responsable de l'affichage de la barre de navigation
 * et de la sélection de catégorie.
 */
public class NavbarView {

    private final String colorText;
    private final String colorAccent;
    private String categorieActuelle;
    private final Consumer<String> onCategoryChange;

    private HBox root;
    private HBox categoriesBox;

    public NavbarView(String initialCategory,
                      String colorText,
                      String colorAccent,
                      Consumer<String> onCategoryChange) {
        this.categorieActuelle = initialCategory;
        this.colorText = colorText;
        this.colorAccent = colorAccent;
        this.onCategoryChange = onCategoryChange;
        this.root = createNavbar();
    }

    public HBox getView() {
        return root;
    }

    // --- Construction de la barre ---

    private HBox createNavbar() {
        HBox navbar = new HBox(30);
        navbar.setPrefHeight(100);
        navbar.setPadding(new Insets(0, 50, 0, 50));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        Text logo = new Text("O'ZenLihn");
        logo.setFill(Color.web(colorText));
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        categoriesBox = new HBox(25);
        categoriesBox.setAlignment(Pos.CENTER);
        rebuildCategories();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogin = new Button("CONNEXION");
        btnLogin.setStyle("-fx-background-color: " + colorText + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 25;");

        navbar.getChildren().addAll(logo, categoriesBox, spacer, btnLogin);
        return navbar;
    }

    private void rebuildCategories() {
        categoriesBox.getChildren().clear();
        String[] types = {"ENTRÉES", "NOUILLES", "SUSHIS", "FRITURES", "DESSERTS", "BOISSONS"};
        for (String type : types) {
            categoriesBox.getChildren().add(createNavButton(type));
        }
    }

    private VBox createNavButton(String nom) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);

        boolean isActive = nom.equals(categorieActuelle);

        Text txt = new Text(nom);
        txt.setFill(isActive ? Color.web(colorAccent) : Color.web("#888"));
        txt.setFont(Font.font("System", FontWeight.BOLD, 14));

        Rectangle line = new Rectangle(30, 3);
        line.setFill(isActive ? Color.web(colorAccent) : Color.TRANSPARENT);

        container.getChildren().addAll(txt, line);
        container.setCursor(Cursor.HAND);

        container.setOnMouseClicked(e -> {
            categorieActuelle = nom;
            rebuildCategories(); // met à jour le style actif
            if (onCategoryChange != null) {
                onCategoryChange.accept(nom);
            }
        });

        return container;
    }
}



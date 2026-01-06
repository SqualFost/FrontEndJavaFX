package com.example.projetjavafx;

import com.example.projetjavafx.model.CartItem;
import com.example.projetjavafx.service.CartService;
import com.example.projetjavafx.ui.cart.CartSidebarView;
import com.example.projetjavafx.ui.carousel.CarouselView;
import com.example.projetjavafx.ui.dialog.ProductDetailsDialog;
import com.example.projetjavafx.ui.navbar.NavbarView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AsianKioskApp extends Application {

    // --- UI Components ---
    private StackPane mainRoot;
    private CartSidebarView cartSidebarView;
    private NavbarView navbarView;
    private CarouselView carouselView;
    private String categorieActuelle = "SUSHIS";
    private final List<VBox> platsNodes = new ArrayList<>();
    private final CartService cartService = new CartService();

    // Thème Jour
    private final String COLOR_BG = "#F5F5F7";
    private final String COLOR_ACCENT = "#D4AF37";
    private final String COLOR_TEXT = "#1D1D1F";

    @Override
    public void start(Stage stage) {
        mainRoot = new StackPane();
        BorderPane contentLayout = new BorderPane();
        contentLayout.setStyle("-fx-background-color: " + COLOR_BG + ";");

        navbarView = new NavbarView(
                categorieActuelle,
                COLOR_TEXT,
                COLOR_ACCENT,
                cat -> {
                    categorieActuelle = cat;
                    carouselView.setCategory(cat);
                }
        );
        contentLayout.setTop(navbarView.getView());

        cartSidebarView = new CartSidebarView(
                cartService,
                COLOR_ACCENT,
                item -> showProductDetails(item.getNom(), item.getPrixUnitaire(), item)
        );
        contentLayout.setRight(cartSidebarView.getView());

        carouselView = new CarouselView(
                categorieActuelle,
                COLOR_ACCENT,
                (nom, prix) -> ajouterAuPanier(nom, prix, new ArrayList<>()),
                (nom, prix) -> showProductDetails(nom, prix, null)
        );
        contentLayout.setCenter(carouselView.getView());

        mainRoot.getChildren().add(contentLayout);
        Scene scene = new Scene(mainRoot, 1920, 1080);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);
        stage.show();
    }

    // --- POPUP DETAILS & PERSONNALISATION MODIFIABLE ---
    private void showProductDetails(String nom, double prix, CartItem itemAModifier) {
        ProductDetailsDialog.show(
                mainRoot,
                COLOR_ACCENT,
                nom,
                prix,
                itemAModifier,
                selectedOptions -> {
                    if (itemAModifier != null) {
                        // On met à jour l'item existant
                        itemAModifier.setOptions(selectedOptions);
                    } else {
                        // On ajoute normalement
                        ajouterAuPanier(nom, prix, selectedOptions);
                    }
                    cartSidebarView.refresh();
                }
        );
    }

    // --- LOGIQUE PANIER (déléguée à CartService + CartSidebarView) ---
    private void ajouterAuPanier(String nom, double prix, List<String> options) {
        cartService.ajouterAuPanier(nom, prix, options);
        cartSidebarView.refresh();
    }

    private VBox createFilterMenu() {
        VBox menu = new VBox(8); menu.setPadding(new Insets(15));
        menu.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        menu.getChildren().add(new Text("FILTRES"));
        String[] f = {"🌿 Vegan", "🌶️ Épicé", "⭐ Populaire"};
        for(String s : f) { Button b = new Button(s); b.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"); menu.getChildren().add(b); }
        return menu;
    }

    public static void main(String[] args) { launch(args); }
}
package com.example.projetjavafx;

import com.example.projetjavafx.model.CartItem;
import com.example.projetjavafx.service.CartService;
import com.example.projetjavafx.ui.cart.CartSidebarView;
import com.example.projetjavafx.ui.carousel.CarouselView;
import com.example.projetjavafx.ui.product.ProductGridView;
import com.example.projetjavafx.ui.dialog.ProductDetailsDialog;
import com.example.projetjavafx.ui.navbar.NavbarView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AsianKioskApp extends Application {

    private StackPane mainRoot;
    private CartSidebarView cartSidebarView;
    private NavbarView navbarView;
    private CarouselView carouselView;
    private ProductGridView productGridView;
    private Text menuTitle; // Pour le changement dynamique

    private String categorieActuelle = "SUSHIS";
    private final CartService cartService = new CartService();

    private final String COLOR_BG = "#F5F5F7";
    private final String COLOR_ACCENT = "#D4AF37";
    private final String COLOR_TEXT = "#1D1D1F";

    @Override
    public void start(Stage stage) {
        mainRoot = new StackPane();
        BorderPane contentLayout = new BorderPane();
        contentLayout.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // 1. Navbar avec écouteur de changement
        navbarView = new NavbarView(categorieActuelle, COLOR_TEXT, COLOR_ACCENT, cat -> {
            this.categorieActuelle = cat;
            carouselView.setCategory(cat);
            productGridView.updateContent(cat);
            menuTitle.setText("NOTRE CARTE : " + cat); // Mise à jour du titre
        });
        contentLayout.setTop(navbarView.getView());

        // 2. Panier latéral
        cartSidebarView = new CartSidebarView(cartService, COLOR_ACCENT,
                item -> showProductDetails(item.getNom(), item.getPrixUnitaire(), item));
        contentLayout.setRight(cartSidebarView.getView());

        // 3. Zone Centrale
        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20, 40, 20, 40));

        // --- SECTION HAUT : TENDANCES ---
        Text trendLabel = new Text("NOS SUGGESTIONS 🔥");
        trendLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        carouselView = new CarouselView(categorieActuelle, COLOR_ACCENT,
                (nom, prix) -> ajouterAuPanier(nom, prix, new ArrayList<>()),
                (nom, prix) -> showProductDetails(nom, prix, null));

        // --- SECTION BAS : GRILLE DE PRODUITS ---
        menuTitle = new Text("NOTRE CARTE : " + categorieActuelle);
        menuTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        menuTitle.setFill(Color.web(COLOR_TEXT));

      ush  productGridView = new ProductGridView(COLOR_ACCENT, (nom, prix) -> showProductDetails(nom, prix, null));
        productGridView.updateContent(categorieActuelle);

        centerLayout.getChildren().addAll(trendLabel, carouselView.getView(), menuTitle, productGridView.getView());
        contentLayout.setCenter(centerLayout);

        mainRoot.getChildren().add(contentLayout);
        Scene scene = new Scene(mainRoot, 1920, 1080);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }

    private void showProductDetails(String nom, double prix, CartItem itemAModifier) {
        ProductDetailsDialog.show(mainRoot, COLOR_ACCENT, nom, prix, itemAModifier,
                selectedOptions -> {
                    if (itemAModifier != null) itemAModifier.setOptions(selectedOptions);
                    else ajouterAuPanier(nom, prix, selectedOptions);
                    cartSidebarView.refresh();
                });
    }

    private void ajouterAuPanier(String nom, double prix, List<String> options) {
        cartService.ajouterAuPanier(nom, prix, options);
        cartSidebarView.refresh();
    }

    public static void main(String[] args) { launch(args); }
}
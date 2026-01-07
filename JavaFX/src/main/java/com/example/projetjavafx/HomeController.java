package com.example.projetjavafx;

import com.example.projetjavafx.model.CartItem;
import com.example.projetjavafx.service.CartService;
import com.example.projetjavafx.ui.cart.CartSidebarView;
import com.example.projetjavafx.ui.carousel.CarouselView;
import com.example.projetjavafx.ui.dialog.ProductDetailsDialog;
import com.example.projetjavafx.ui.navbar.NavbarView;
import com.example.projetjavafx.ui.product.ProductGridView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    // --- VARIABLES GLOBALES ---
    private StackPane rootKiosk;
    private CartSidebarView cartSidebarView;
    private NavbarView navbarView;
    private CarouselView carouselView;
    private ProductGridView productGridView;
    private Text menuTitle;
    private String categorieActuelle = "SUSHIS";
    private final CartService cartService = new CartService();

    // Couleurs de l'interface
    private final String COLOR_BG = "#F5F5F7";
    private final String COLOR_ACCENT = "#D4AF37"; // Or
    private final String COLOR_TEXT = "#1D1D1F";

    @FXML
    protected void onStartClick(ActionEvent event) {
        System.out.println("Lancement de l'interface Kiosk...");

        // 1. Récupérer la fenêtre actuelle
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        // 2. CONSTRUIRE L'INTERFACE
        rootKiosk = new StackPane();
        BorderPane contentLayout = new BorderPane();
        contentLayout.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // A. Navbar
        navbarView = new NavbarView(categorieActuelle, COLOR_TEXT, COLOR_ACCENT, cat -> {
            this.categorieActuelle = cat;
            carouselView.setCategory(cat);
            productGridView.updateContent(cat);
            menuTitle.setText("NOTRE CARTE : " + cat);
        });
        contentLayout.setTop(navbarView.getView());
        cartSidebarView = new CartSidebarView(cartService, COLOR_ACCENT,
                item -> showProductDetails(item.getNom(), item.getPrixUnitaire(), item),
                () -> allerVersPaiement() // La nouvelle action !
        );
        contentLayout.setRight(cartSidebarView.getView());

        // C. Zone Centrale (Carousel + Grille + Scroll)
        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20, 40, 20, 40));
        centerLayout.setStyle("-fx-background-color: " + COLOR_BG + ";");

        Text trendLabel = new Text("NOS SUGGESTIONS 🔥");
        trendLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Carousel
        carouselView = new CarouselView(categorieActuelle, COLOR_ACCENT,
                (nom, prix) -> ajouterAuPanier(nom, prix, new ArrayList<>()),
                (nom, prix) -> showProductDetails(nom, prix, null));

        // Titre catégorie
        menuTitle = new Text("NOTRE CARTE : " + categorieActuelle);
        menuTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        menuTitle.setFill(Color.web(COLOR_TEXT));

        // Grille de produits
        productGridView = new ProductGridView(COLOR_ACCENT, (nom, prix) -> showProductDetails(nom, prix, null));
        productGridView.updateContent(categorieActuelle);

        // On ajoute tout dans la VBox verticale
        centerLayout.getChildren().addAll(trendLabel, carouselView.getView(), menuTitle, productGridView.getView());

        // --- SCROLLPANE GLOBAL ---
        javafx.scene.control.ScrollPane globalScroll = new javafx.scene.control.ScrollPane(centerLayout);
        globalScroll.setFitToWidth(true);
        globalScroll.setFitToHeight(false);
        globalScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        globalScroll.setPannable(true);

        contentLayout.setCenter(globalScroll);

        rootKiosk.getChildren().add(contentLayout);

        // 3. Afficher la nouvelle scène (C'est ce bout qu'il te manquait aussi !)
        Scene scene = new Scene(rootKiosk, 1920, 1080);
        stage.setScene(scene);
        stage.setFullScreen(true);
    }

    // --- C'EST ICI QUE TU AVAIS DES ERREURS : IL FALLAIT AJOUTER CES MÉTHODES ---
    private void allerVersPaiement() {
        try {
            System.out.println("Navigation vers le récapitulatif...");
            // On charge le fichier recap.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("recap.fxml"));

            // On récupère la fenêtre actuelle depuis le rootKiosk
            Stage stage = (Stage) rootKiosk.getScene().getWindow();

            // On change la scène
            Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
            stage.setScene(scene);
            stage.setFullScreen(true); // On reste en plein écran

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur impossible de charger recap.fxml");
        }
    }
    private void showProductDetails(String nom, double prix, CartItem itemAModifier) {
        ProductDetailsDialog.show(rootKiosk, COLOR_ACCENT, nom, prix, itemAModifier,
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
}
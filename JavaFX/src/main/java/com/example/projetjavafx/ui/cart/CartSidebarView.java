package com.example.projetjavafx.ui.cart;

import com.example.projetjavafx.model.CartItem;
import com.example.projetjavafx.service.CartService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.Consumer;

/**
 * Vue responsable de l'affichage du panneau panier (sidebar)
 * et de la mise à jour de son contenu à partir du CartService.
 */
public class CartSidebarView {

    private final CartService cartService;
    private final String colorAccent;
    private final Consumer<CartItem> onEditItem;
    private final Runnable onPayAction;
    private VBox root;
    private VBox itemList;
    private Text totalAmountDisplay;

    public CartSidebarView(CartService cartService,
                           String colorAccent,
                           Consumer<CartItem> onEditItem,
                           Runnable onPayAction) {
        this.cartService = cartService;
        this.colorAccent = colorAccent;
        this.onEditItem = onEditItem;
        this.onPayAction = onPayAction; // On l'enregistre bien maintenant
        this.root = createOrderSidebar();
        refresh();
    }

    public VBox getView() {
        return root;
    }

    /**
     * Reconstruit la liste des items et le total.
     * À appeler après toute modification du panier.
     */
    public void refresh() {
        itemList.getChildren().clear();

        for (CartItem item : cartService.getItems()) {
            HBox row = new HBox(10);
            row.setPadding(new Insets(12));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #FAFAFA; -fx-background-radius: 12; -fx-border-color: #EEE;");

            VBox info = new VBox(2);
            Text n = new Text(item.getNom());
            n.setFont(Font.font("System", FontWeight.BOLD, 13));

            Text optsTxt = new Text(item.getOptions().isEmpty() ? "Standard" : String.join(", ", item.getOptions()));
            optsTxt.setFont(Font.font(10));
            optsTxt.setFill(javafx.scene.paint.Color.GRAY);

            Text p = new Text(String.format("%.2f€", item.getPrixUnitaire() * item.getQuantite()));
            info.getChildren().addAll(n, optsTxt, p);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox actions = new HBox(5);
            actions.setAlignment(Pos.CENTER);

            // Bouton Modifier ✏️
            Button edit = new Button("✏");
            edit.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            edit.setOnAction(e -> {
                if (onEditItem != null) {
                    onEditItem.accept(item);
                }
            });

            // Boutons +/-
            Button minus = new Button("-");
            minus.setOnAction(e -> {
                if (item.getQuantite() > 1) {
                    item.setQuantite(item.getQuantite() - 1);
                    refresh();
                }
            });

            Text quantityText = new Text(String.valueOf(item.getQuantite()));

            Button plus = new Button("+");
            plus.setOnAction(e -> {
                item.setQuantite(item.getQuantite() + 1);
                refresh();
            });

            // Bouton Supprimer 🗑
            Button delete = new Button("🗑");
            delete.setStyle("-fx-text-fill: red; -fx-background-color: transparent;");
            delete.setOnAction(e -> {
                cartService.supprimerItem(item);
                refresh();
            });

            actions.getChildren().addAll(edit, minus, quantityText, plus, delete);
            row.getChildren().addAll(info, spacer, actions);
            itemList.getChildren().add(row);
        }

        double total = cartService.calculerTotal();
        totalAmountDisplay.setText(String.format("%.2f€", Math.max(0, total)));
    }

    // --- Construction du panneau ---

    private VBox createOrderSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(320);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #EEE; -fx-border-width: 0 0 0 1;");

        Text title = new Text("VOTRE PANIER");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));

        itemList = new VBox(10);
        ScrollPane scroll = new ScrollPane(itemList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(650);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox totalRow = new HBox();
        totalAmountDisplay = new Text("0.00€");
        totalAmountDisplay.setFont(Font.font("System", FontWeight.BOLD, 24));

        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        totalRow.getChildren().addAll(new Text("TOTAL"), s, totalAmountDisplay);

        Button btnPay = new Button("PAYER");
        btnPay.setMaxWidth(Double.MAX_VALUE);
        btnPay.setPrefHeight(60);
        btnPay.setStyle("-fx-background-color: " + colorAccent + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;");
        btnPay.setOnAction(e -> {
            if (onPayAction != null) {
                onPayAction.run();
            }
        });
        sidebar.getChildren().addAll(title, scroll, spacer, totalRow, btnPay);
        return sidebar;
    }
}



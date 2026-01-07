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
        this.onPayAction = onPayAction;
        this.root = createOrderSidebar();
        refresh();
    }

    public VBox getView() {
        return root;
    }

    public void refresh() {
        itemList.getChildren().clear();

        for (int i = 0; i < cartService.getItems().size(); i++) {
            CartItem item = cartService.getItems().get(i);

            HBox row = new HBox(10);
            row.setPadding(new Insets(12));
            row.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(2);
            Text n = new Text(item.getNom());
            n.setFont(Font.font("System", FontWeight.BOLD, 13));
            Text optsTxt = new Text(item.getOptions().isEmpty() ? "Standard" : String.join("\n", item.getOptions()));
            optsTxt.setFont(Font.font(10));
            optsTxt.setFill(javafx.scene.paint.Color.GRAY);

            Text p = new Text(String.format("%.2f€", item.getPrixUnitaire() * item.getQuantite()));
            info.getChildren().addAll(n, optsTxt, p);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox actions = new HBox(5);
            actions.setAlignment(Pos.CENTER);

            Button edit = new Button("✏️ ");
            edit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: blue;");
            edit.setOnAction(e -> {
                if (onEditItem != null) {
                    onEditItem.accept(item);
                }
            });

            Button minus = new Button("-");
            minus.setStyle("-fx-background-color: #FAFAFA;  -fx-cursor: hand; -fx-border-radius: 10;");
            minus.setOnAction(e -> {
                if (item.getQuantite() > 1) {
                    item.setQuantite(item.getQuantite() - 1);
                    refresh();
                }
                else if (item.getQuantite() == 1) {
                    cartService.supprimerItem(item);
                    refresh();
                }
            });

            Text quantityText = new Text(String.valueOf(item.getQuantite()));

            Button plus = new Button("+");
            plus.setStyle("-fx-background-color: #FAFAFA; -fx-cursor: hand; -fx-border-radius: 10;");
            plus.setOnAction(e -> {
                item.setQuantite(item.getQuantite() + 1);
                refresh();
            });

            actions.getChildren().addAll(edit, minus, quantityText, plus);
            row.getChildren().addAll(info, spacer, actions);
            itemList.getChildren().add(row);

            if (i < cartService.getItems().size() - 1) {
                Region sep = new Region();
                sep.setStyle("-fx-pref-height: 1; -fx-min-height: 1; -fx-max-height: 1; -fx-background-color: #E5E5E5; -fx-opacity: 1; -fx-margin: 6 0 6 0;");
                sep.getStyleClass().add("cart-separator");
                itemList.getChildren().add(sep);
            }
        }

        double total = cartService.calculerTotal();
        totalAmountDisplay.setText(String.format("%.2f€", Math.max(0, total)));
    }


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
            if (onPayAction != null && cartService.calculerTotal() > 0) {
                onPayAction.run();
            }
            else
                System.out.print("Aucun plat a payer.");
        });
        sidebar.getChildren().addAll(title, scroll, spacer, totalRow, btnPay);
        return sidebar;
    }
}



package com.example.projetjavafx.ui.dialog;

import com.example.projetjavafx.model.CartItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Popup de personnalisation d'un produit (détails + options).
 * Ne connaît pas la logique métier du panier : elle est transmise via un callback.
 */
public class ProductDetailsDialog {

    private ProductDetailsDialog() {
        // utilitaire statique
    }

    public static void show(StackPane root,
                            String colorAccent,
                            String nom,
                            double prix,
                            CartItem itemAModifier,
                            Consumer<List<String>> onConfirm) {

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        VBox modal = new VBox(20);
        modal.setMaxSize(500, 700);
        modal.setPadding(new Insets(30));
        modal.setAlignment(Pos.TOP_CENTER);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 30;");

        Text title = new Text(itemAModifier != null ? "Modifier : " + nom : nom);
        title.setFont(Font.font("System", FontWeight.BOLD, 26));

        VBox customBox = new VBox(10);
        customBox.setPadding(new Insets(20, 0, 20, 0));
        Text customTitle = new Text("PERSONNALISATION");
        customTitle.setFill(Color.web(colorAccent));
        customTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        String[] opts = {"Sans coriandre", "Extra piment", "Sauce sucrée", "Suppléments baguettes"};
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String o : opts) {
            CheckBox c = new CheckBox(o);
            // Si on modifie, on pré-coche les options déjà présentes
            if (itemAModifier != null && itemAModifier.getOptions().contains(o)) {
                c.setSelected(true);
            }
            checkBoxes.add(c);
            customBox.getChildren().add(c);
        }

        Button confirmBtn = new Button(itemAModifier != null ? "METTRE À JOUR" : "AJOUTER AU PANIER");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setStyle("-fx-background-color: " + colorAccent + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-padding: 12;");
        confirmBtn.setOnAction(e -> {
            List<String> selected = checkBoxes.stream()
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .collect(Collectors.toList());

            if (onConfirm != null) {
                onConfirm.accept(selected);
            }
            root.getChildren().remove(overlay);
        });

        Button close = new Button("ANNULER");
        close.setOnAction(e -> root.getChildren().remove(overlay));

        modal.getChildren().addAll(
                new Circle(60, Color.web("#EEE")),
                title,
                new Text("Options de préparation :"),
                customTitle,
                customBox,
                confirmBtn,
                close
        );

        overlay.getChildren().add(modal);
        root.getChildren().add(overlay);
    }
}



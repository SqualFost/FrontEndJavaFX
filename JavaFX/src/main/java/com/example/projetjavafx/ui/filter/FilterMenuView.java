package com.example.projetjavafx.ui.filter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

public class FilterMenuView {

    private final HBox container;
    private final String colorAccent;
    private final Consumer<String> onFilterChanged;

    public FilterMenuView(String colorAccent, Consumer<String> onFilterChanged) {
        this.colorAccent = colorAccent;
        this.onFilterChanged = onFilterChanged;
        this.container = new HBox(15);
        this.container.setPadding(new Insets(10, 0, 10, 0));
        this.container.setAlignment(Pos.CENTER_LEFT);

        buildFilters();
    }

    private void buildFilters() {
        ToggleGroup group = new ToggleGroup();

        String[] filters = {"Tout", "🌿 Vegan", "🌶️ Épicé", "⭐ Populaire", "💎 Premium"};

        for (String filter : filters) {
            ToggleButton btn = new ToggleButton(filter);
            btn.setToggleGroup(group);
            btn.setFont(Font.font("System", FontWeight.MEDIUM, 13));

            String baseStyle = "-fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand; -fx-border-width: 1; ";
            String idleStyle = baseStyle + "-fx-background-color: white; -fx-text-fill: #555; -fx-border-color: #DDD;";
            String activeStyle = baseStyle + "-fx-background-color: " + colorAccent + "; -fx-text-fill: white; -fx-border-color: " + colorAccent + ";";

            btn.setStyle(idleStyle);

            btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
                btn.setStyle(newVal ? activeStyle : idleStyle);
                if (newVal && onFilterChanged != null) {
                    onFilterChanged.accept(filter);
                }
            });

            if (filter.equals("Tout")) btn.setSelected(true);

            container.getChildren().add(btn);
        }
    }

    public Node getView() {
        return container;
    }
}
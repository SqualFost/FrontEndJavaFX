package com.example.projetjavafx;

import com.example.projetjavafx.model.ClientOrder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CuisineController {

    @FXML private VBox containerEnPrepa;
    @FXML private VBox containerPret;

    @FXML
    public void initialize() {

        List<ClientOrder> fakeOrders = new ArrayList<>();
        fakeOrders.add(new ClientOrder("#CMD-001", Arrays.asList("Sushi Saumon x6", "Coca Zéro"), "EN_PREPA", "12:05"));
        fakeOrders.add(new ClientOrder("#CMD-002", Arrays.asList("Maki Avocat x6", "Soupe Miso"), "PRET", "11:58"));
        fakeOrders.add(new ClientOrder("#CMD-003", Arrays.asList("Menu Duo", "Eau Minérale"), "EN_PREPA", "12:10"));
        fakeOrders.add(new ClientOrder("#CMD-004", Arrays.asList("Brochettes Boeuf x4"), "PRET", "11:55"));

        for (ClientOrder order : fakeOrders) {
            ajouterCarteCommande(order);
        }
    }

    private void ajouterCarteCommande(ClientOrder order) {
        VBox card = new VBox(10);

        card.getStyleClass().add("carte-produit");

        HBox header = new HBox();

        Label idLbl = new Label(order.getId());
        idLbl.getStyleClass().add("nom-produit");
        idLbl.setStyle("-fx-font-size: 20px;");

        Label timeLbl = new Label(order.getTime());
        timeLbl.getStyleClass().add("sous-titre");
        timeLbl.setStyle("-fx-font-size: 16px;");

        HBox spacer = new HBox();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(idLbl, spacer, timeLbl);

        VBox itemsBox = new VBox(5);
        for (String item : order.getItems()) {
            Label l = new Label("• " + item);
            l.getStyleClass().add("sous-titre");
            l.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50;");
            itemsBox.getChildren().add(l);
        }

        card.getChildren().addAll(header, itemsBox);

        if ("EN_PREPA".equals(order.getStatus())) {
            containerEnPrepa.getChildren().add(card);
        } else {
            containerPret.getChildren().add(card);
        }
    }

    @FXML
    protected void retourBorne(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("home.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1920, 1080));
        stage.setFullScreen(true);
    }
}
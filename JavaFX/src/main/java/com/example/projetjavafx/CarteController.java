package com.example.projetjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class CarteController {

    @FXML
    protected void ouvrirDetail() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("detail.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Détail du produit");
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    @FXML
    protected void allerAuPanier(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("panier.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1920, 1080));
    }

    @FXML
    protected void retourAccueil(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("home.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1920, 1080));
    }
}
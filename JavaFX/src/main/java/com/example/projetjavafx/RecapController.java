package com.example.projetjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RecapController {
    @FXML
    protected void retourAccueil(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("home.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1920, 1080));
    }
}
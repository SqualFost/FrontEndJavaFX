package com.example.projetjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;


    @FXML
    protected void onHelloButtonClick() throws IOException {
        URL url = new URL("http://localhost:7001");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println(status);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine = "";
        StringBuffer content = new StringBuffer();
        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        System.out.println(content);

        in.close();
        welcomeText.setText("Welcome to JavaFX Application! ");
    }
}

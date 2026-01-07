module com.example.projetjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens com.example.projetjavafx to javafx.fxml;
    opens com.example.projetjavafx.model to com.fasterxml.jackson.databind;
    exports com.example.projetjavafx;
}
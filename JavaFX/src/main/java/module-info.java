module com.example.projetjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projetjavafx to javafx.fxml;
    exports com.example.projetjavafx;
}
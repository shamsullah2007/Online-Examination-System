module com.example.olineexaminationsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.olineexaminationsystem to javafx.fxml;
    exports com.example.olineexaminationsystem;
}
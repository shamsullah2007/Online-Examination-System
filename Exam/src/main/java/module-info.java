module com.exam {
    requires javafx.controls;
    requires java.sql;
    requires bcrypt;

    opens com.exam to javafx.fxml;
    exports com.exam;
}
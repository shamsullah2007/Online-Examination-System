module com.exam {
    requires javafx.controls;
    requires java.sql;
    requires at.favre.lib.bcrypt;

    opens com.exam to javafx.graphics;
    exports com.exam;
}
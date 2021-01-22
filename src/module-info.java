module ConnorGriggSchedulingApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires mysql.connector.java;

    opens SchedulingApp to javafx.fxml, javafx.graphics;
    opens View_Controller to javafx.fxml, javafx.graphics;
    opens Model to javafx.base,javafx.fxml;
}
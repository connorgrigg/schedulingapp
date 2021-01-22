package View_Controller;

import Database.DBConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * //class to control main menu
 */
public class MainMenuController implements Initializable {

    @Override
/**
 *     //fulfils requirement to check upon login if appointment is in 15 minutes
  */
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     *     //launches customers menu
      * @param actionEvent
     * @throws IOException
     */
    public void handleMainMenuCustomersButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/CustomersMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //launches appointments menu
      * @param actionEvent
     * @throws IOException
     */
    public void handleMainMenuAppointmentsButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/AppointmentsMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //launches reports menu
      * @param actionEvent
     * @throws IOException
     */
    public void handleMainMenuReportsButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/Reports.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //exits application
      * @param actionEvent
     * @throws SQLException
     */
    public void handleMainMenuExitButton(ActionEvent actionEvent) throws SQLException {
        DBConnect.closeConnection();
        System.exit(0);
    }
}

package View_Controller;
import Database.DBConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * //class to control reports menu
 */
public class ReportsController {
    public Button AppointmentCountButton;
    public Button ContactScheduleButton;
    public Button CustomerCountButton;
    public Button ReturnMainMenuButton;
    public Connection connection = DBConnect.getConnection();

    /**
     *     //first report; counts monthly and unique appointments; contains second lambda that builds report string
      * @param event
     * @throws SQLException
     */
    public void handleAppointmentCount(ActionEvent event) throws IOException {

        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/ReportOne.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //launches contact schedule menu
      * @param event
     * @throws IOException
     */
    public void handleContactSchedule(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/ContactSchedule.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //counts total customers and displays in popup
      * @param event
     * @throws SQLException
     */
    public void handleCustomerCount(ActionEvent event) throws SQLException{
        int distinctCustomer = 0;
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(DISTINCT Customer_ID) FROM customers");
        ResultSet rs = ps.executeQuery();
        while(rs.next())
            distinctCustomer = rs.getInt(1);
        if(LoginScreenController.getLocale().equals("en")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Customer Count");
            alert.setHeaderText("Total Number of Distinct Customers: " + distinctCustomer);
            Optional<ButtonType> option = alert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nombre de Clients");
            alert.setHeaderText("Nombre total de clients distincts: " + distinctCustomer);
            Optional<ButtonType> option = alert.showAndWait();
        }
    }

    /**
     *     //launches main menu screen
      * @param event
     * @throws IOException
     */
    public void handleReturnMainMenu(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/MainMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }


}

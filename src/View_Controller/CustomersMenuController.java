package View_Controller;

import Database.DBConnect;
import Model.Customer;
import Model.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * //class to control the customers menu
 */
public class CustomersMenuController implements Initializable {

    public TableColumn<Customer, String> CustomerNameCol;
    public TableColumn<Customer, Integer> CustomerIDCol;
    public TableColumn<Customer, String> CustomerCountryCol;
    public TableColumn<Customer, String> CustomerPhoneCol;
    public Button CustomerMenuAddButton;
    public TableView<Customer> CustomerTable;
    public int indexCustomerID;
    private static Customer modCustomer;
    public Connection connection = DBConnect.getConnection();

    /**
     *     //initializes display
      * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        CustomerIDCol.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        CustomerNameCol.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        CustomerCountryCol.setCellValueFactory(new PropertyValueFactory<>("CustomerCountry"));
        CustomerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("CustomerPhone"));

        CustomerTable.setItems(Schedule.getCustomerList());
    }

    /**
     *     //adds customer
      * @param actionEvent
     * @throws IOException
     */
    public void handleCustomerMenuAddButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/AddCustomer.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //selects customer for modification, stores selected customer (returned below)
      * @param actionEvent
     * @throws IOException
     */
    public void handleCustomerMenuModifyButton(ActionEvent actionEvent) throws IOException {
        modCustomer = CustomerTable.getSelectionModel().getSelectedItem();
        indexCustomerID = modCustomer.getCustomerID();
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/ModifyCustomer.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //deletes selected customer, confirms deletion
      * @param actionEvent
     * @throws SQLException
     */
    public void handleCustomerMenuDeleteButton(ActionEvent actionEvent) throws SQLException {
        if (LoginScreenController.getLocale().equals("en")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Confirm Delete Customer");
            alert.setContentText("Should this customer be deleted?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM appointments WHERE Customer_ID = " + CustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
                PreparedStatement ps2 = connection.prepareStatement("DELETE FROM customers WHERE Customer_ID = " + CustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
                int rs = ps.executeUpdate();
                int rs2 = ps2.executeUpdate();
                CustomerTable.setItems(Schedule.getCustomerList());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmer");
            alert.setHeaderText("Confirmer la suppression du client");
            alert.setContentText("Ce client doit-il être supprimé?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM appointments WHERE Customer_ID = " + CustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
                PreparedStatement ps2 = connection.prepareStatement("DELETE FROM customers WHERE Customer_ID = " + CustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
                int rs = ps.executeUpdate();
                rs = ps2.executeUpdate();
                CustomerTable.setItems(Schedule.getCustomerList());
            }
        }
    }

    /**
     *     //launches main menu
      * @param actionEvent
     * @throws IOException
     */
    public void handleCustomerMenuReturnButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/MainMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //returns customer for modification
      * @return
     */
    public static Customer getModCustomer(){
        return modCustomer;
    }
}

package View_Controller;

import Database.DBConnect;
import Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * //class to add customer to application
 */
public class AddCustomerController implements Initializable {
    public TextField AddCustomerNameField;
    public TextField AddCustomerAddressOneField;
    public TextField AddCustomerPostalCodeField;
    public TextField AddCustomerPhoneField;
    public Button CustomerSaveButton;
    public Button CustomerCancelButton;
    public TextField AddCustomerIDField;
    public ComboBox<String> AddCustomerDivisionCombo;
    public ComboBox<String> AddCustomerCountryCombo;
    public int divisionID = 0;
    private int customerID;
    public Connection connection = DBConnect.getConnection();

    /**
     *     //initializes menu options
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT MAX(Customer_ID) FROM customers");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                    customerID = rs.getInt(1) + 1;
                    System.out.println(customerID);
                }
        }
        catch(SQLException s){
            s.printStackTrace();
        }

        AddCustomerIDField.setText("ID: " + customerID);
        AddCustomerIDField.setEditable(false);

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT country FROM countries");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                AddCustomerCountryCombo.getItems().add(rs.getString("country"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     *     //saves customer to server
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void handleCustomerSaveButton(ActionEvent actionEvent) throws IOException, SQLException {
        String name = AddCustomerNameField.getText();
        String addressOne = AddCustomerAddressOneField.getText();
        String division = AddCustomerDivisionCombo.getValue();
        String country = AddCustomerCountryCombo.getValue();
        String postal = AddCustomerPostalCodeField.getText();
        String phone = AddCustomerPhoneField.getText();
        int ID = customerID;
        if(!name.equals("") && !addressOne.equals("") && AddCustomerCountryCombo.getValue() != null && AddCustomerDivisionCombo.getValue() != null && !postal.equals("") && !phone.equals("")) {
            Customer customer = new Customer(ID, name, country, phone, division, addressOne, postal);
            customer.setCustomerID(ID);
            customer.setCustomerName(name);
            customer.setPrimaryAddress(addressOne);
            customer.setCustomerDivision(division);
            customer.setCountry(country);
            customer.setPostalCode(postal);
            customer.setPhone(phone);

            String saveCustomer = "INSERT INTO customers VALUES(" + ID + ", " + "'" + name + "'" + ", " + "'" + addressOne + "'" + ", " + "'" + postal + "'" + ", " + "'" + phone + "'"
                    + ", NOW(), 'app', NOW(), 'app', " + divisionID + ");";
            PreparedStatement ps = connection.prepareStatement(saveCustomer);
            int rs = ps.executeUpdate();

            Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/CustomersMenu.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
        else {
            if(LoginScreenController.getLocale().equals("en")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Heads Up!");
                alert.setHeaderText("invalid input");
                alert.setContentText("no fields may be left blank");
                Optional<ButtonType> option = alert.showAndWait();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("la tête haute!");
                alert.setHeaderText("entrée invalide");
                alert.setContentText("aucun champ ne peut être laissé vide");
                Optional<ButtonType> option = alert.showAndWait();
            }
        }
    }

    /**
     *     //cancels add customer
     * @param actionEvent
     * @throws IOException
     */
    public void handleCustomerCancelButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/CustomersMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //converts selected country for division combobox
     * @param actionEvent
     */
    public void handleCountrySelection(ActionEvent actionEvent) {
        if(AddCustomerCountryCombo.getValue().equals("US")){
            try {
                AddCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID < 60");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    AddCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
        else if(AddCustomerCountryCombo.getValue().equals("UK")){
            try {
                AddCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID > 100");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    AddCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
        else if(AddCustomerCountryCombo.getValue().equals("Canada")){
            try {
                AddCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division_ID, Division FROM first_level_divisions WHERE Division_ID BETWEEN 60 AND 100");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    AddCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
    }

    /**
     *     //converts division name to division id
     * @param actionEvent
     * @throws SQLException
     */
    public void handleDivisionSelection(ActionEvent actionEvent) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Division_ID FROM first_level_divisions WHERE Division LIKE " + "'" + AddCustomerDivisionCombo.getValue() + "'");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                divisionID = rs.getInt(1);
        }
        catch(SQLException s){
            s.printStackTrace();
        }
    }
}

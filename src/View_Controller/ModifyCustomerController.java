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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * //class to modify customers and control menu
 */
public class ModifyCustomerController implements Initializable {
    public TextField ModCustomerIDField;
    public TextField ModCustomerNameField;
    public TextField ModCustomerAddressOneField;
    public TextField ModCustomerPostalCodeField;
    public Button CustomerSaveButton;
    public Button CustomerCancelButton;
    public TextField ModCustomerPhoneField;
    public ComboBox<String> ModCustomerCountryCombo;
    public ComboBox<String> ModCustomerDivisionCombo;
    public Connection connection = DBConnect.getConnection();
    public Customer modCustomer = CustomersMenuController.getModCustomer();
    public int divisionID;


    @Override
/**
 *     //initializes menu
  */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ModCustomerIDField.setText("ID: " + modCustomer.getCustomerID());
        ModCustomerIDField.setEditable(false);
        ModCustomerNameField.setText(modCustomer.getCustomerName());
        ModCustomerAddressOneField.setText(modCustomer.getCustomerPrimaryAddress());
        ModCustomerPhoneField.setText(modCustomer.getCustomerPhone());
        ModCustomerPostalCodeField.setText(modCustomer.getCustomerPostalCode());
        ModCustomerCountryCombo.getSelectionModel().select(modCustomer.getCustomerCountry());
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID = " + modCustomer.getCustomerDivision());
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                ModCustomerDivisionCombo.getSelectionModel().select(rs.getString(1));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        populateCountries();
        populateDivisions();
        handleDivisionSelection(null);
    }

    /**
     *     //finds all countries
      */
    public void populateCountries(){
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT country FROM countries");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ModCustomerCountryCombo.getItems().add(rs.getString("country"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     *     //populates divisions based upon country selection
      */
    public void populateDivisions(){
        if(ModCustomerCountryCombo.getValue().equals("US")){
            try {
                ModCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID < 60");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    ModCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
        else if(ModCustomerCountryCombo.getValue().equals("UK")){
            try {
                ModCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID > 100");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    ModCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
        else if(ModCustomerCountryCombo.getValue().equals("Canada")){
            try {
                ModCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division_ID, Division FROM first_level_divisions WHERE Division_ID BETWEEN 60 AND 100");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    ModCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
    }

    /**
     *     //saves customer to server
      * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void handleCustomerSaveButton(ActionEvent actionEvent) throws SQLException, IOException {
        String name = ModCustomerNameField.getText();
        String addressOne = ModCustomerAddressOneField.getText();
        String division = ModCustomerDivisionCombo.getValue();
        String country = ModCustomerCountryCombo.getValue();
        String postal = ModCustomerPostalCodeField.getText();
        String phone = ModCustomerPhoneField.getText();
        int ID = modCustomer.getCustomerID();

        if(!name.equals("") && !addressOne.equals("") && ModCustomerCountryCombo.getValue() != null && ModCustomerDivisionCombo.getValue() != null && !postal.equals("") && !phone.equals("")) {

            Customer customer = new Customer(ID, name, country, phone, division, addressOne, postal);

            customer.setCustomerID(ID);
            customer.setCustomerName(name);
            customer.setPrimaryAddress(addressOne);
            customer.setCustomerDivision(division);
            customer.setCountry(country);
            customer.setPostalCode(postal);
            customer.setPhone(phone);
            Schedule.addCustomer(customer);
            String saveCustomer = "UPDATE customers SET Customer_Name = " + "'" + name + "', Address = " + "'" + addressOne + "', Postal_Code = '" + postal + "', Phone = '" + phone +
                    "', Last_Update = NOW(), Division_ID = '" + divisionID + "' WHERE (Customer_ID = " + modCustomer.getCustomerID() + ");";
            PreparedStatement ps = connection.prepareStatement(saveCustomer);
            int rs = ps.executeUpdate();

            Schedule.updateCustomer(modCustomer.getCustomerID(), modCustomer);

            Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/CustomersMenu.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
        else{
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
     *     //cancels modify customer, launches customers menu
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
     *     //converts division name to disvision id
      * @param actionEvent
     */
    public void handleDivisionSelection(ActionEvent actionEvent) {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Division_ID FROM first_level_divisions WHERE Division LIKE " + "'" + ModCustomerDivisionCombo.getValue() + "'");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                divisionID = rs.getInt(1);
        }
        catch(SQLException s){
            s.printStackTrace();
        }
    }

    /**
     *     //selects divisions based upon country
      * @param actionEvent
     */
    public void handleCountrySelection(ActionEvent actionEvent) {
        if(ModCustomerCountryCombo.getValue().equals("US")){
            try {
                ModCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID < 60");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    ModCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
        else if(ModCustomerCountryCombo.getValue().equals("UK")){
            try {
                ModCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE Division_ID > 100");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    ModCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
        else if(ModCustomerCountryCombo.getValue().equals("Canada")){
            try {
                ModCustomerDivisionCombo.getItems().clear();
                PreparedStatement ps = connection.prepareStatement("SELECT Division_ID, Division FROM first_level_divisions WHERE Division_ID BETWEEN 60 AND 100");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    ModCustomerDivisionCombo.getItems().add(rs.getString("Division"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
    }
}

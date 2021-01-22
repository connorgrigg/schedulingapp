package View_Controller;

import Database.DBConnect;
import Model.Appointment;
import Model.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * //class controlling contact schedule for report 3
 */
public class ContactScheduleController implements Initializable {
    public Connection connection = DBConnect.getConnection();
    public TableView<Appointment> AppointmentsTableView;
    public TableColumn<Appointment, Integer> AppointmentIDCol;
    public TableColumn<Appointment, Integer> CustomerIDCol;
    public TableColumn<Appointment, String> TitleCol;
    public TableColumn<Appointment, String> DescriptionCol;
    public TableColumn<Appointment, String> TypeCol;
    public TableColumn<Appointment, LocalDateTime> StartCol;
    public TableColumn<Appointment, LocalDateTime> EndCol;
    public Button AppointmentsMenuReturnButton;
    public ComboBox<String> ContactScheduleCombo;
    public String selectedContact = "";

    @Override
/**
 *     //initializes display
  */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Contact_Name FROM contacts");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                ContactScheduleCombo.getItems().add(rs.getString(1));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }

        AppointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentID"));
        CustomerIDCol.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        TitleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        DescriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
        TypeCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentType"));
        StartCol.setCellValueFactory(new PropertyValueFactory<>("Start"));
        EndCol.setCellValueFactory(new PropertyValueFactory<>("End"));
    }

    /**
     *     //launches reports menu
      * @param event
     * @throws IOException
     */
    public void handleAppointmentsMenuReturnButton(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/Reports.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //updates table based upon contact selection
      * @param event
     */
    public void handleContactSelection(ActionEvent event) {
        selectedContact = ContactScheduleCombo.getValue();
        try {
            AppointmentsTableView.setItems(Schedule.getContactSchedule(selectedContact));
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }
}
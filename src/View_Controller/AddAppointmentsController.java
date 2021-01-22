package View_Controller;

import Database.DBConnect;
import Model.Appointment;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * //class to control the add appointments menu
 */
public class AddAppointmentsController implements Initializable {
    public TextField AddAppointmentIDField;
    public TextField AddAppointmentTitleField;
    public TextField AddAppointmentDescriptionField;
    public TextField AddAppointmentLocationField;
    public ComboBox AddAppointmentContactCombo;
    public TextField AddAppointmentTypeField;
    public Button AddAppointmentSaveButton;
    public Button AddAppointmentCancelButton;
    public ComboBox<LocalTime> AddAppointmentStartTimeCombo;
    public ComboBox<LocalTime> AddAppointmentEndTimeCombo;
    public DatePicker AddAppointmentStartDatePicker;
    public DatePicker AddAppointmentEndDatePicker;
    public Connection connection = DBConnect.getConnection();
    public ComboBox AddAppointmentCustomerIDCombo;
    public ComboBox<String> AddAppointmentUserCombo;
    private int appointmentID;
    private int customerID = -1;
    private int contactID = -1;
    private int userID = -1;
    private String startError = "";
    private String endError = "";
    private final TimeZone userTimeZone = TimeZone.getDefault();
    public ZoneId userZoneId = userTimeZone.toZoneId();
    public TimeZone targetTimeZone = TimeZone.getTimeZone("UTC");
    public TimeZone officeTimeZone = TimeZone.getTimeZone("US/Eastern");
    public ZoneId targetZoneId = targetTimeZone.toZoneId();
    public ZoneId officeZoneId = officeTimeZone.toZoneId();
    public ZonedDateTime userZDT = ZonedDateTime.now(userZoneId);
    public ZonedDateTime targetZDT = userZDT.withZoneSameInstant(targetZoneId);
    public ZonedDateTime officeZDT = targetZDT.withZoneSameInstant(officeZoneId);
    int offset = officeZDT.getOffset().compareTo(userZDT.getOffset())/60/60;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalTime startTimeOptions = LocalTime.of(8,0).plusHours(offset);
    private final LocalTime endTimeOptions = LocalTime.of(21,45).plusHours(offset);

    /**
     *     //initializes the various data entry options, ex limiting the time selections to EST 08-22 equivalent from user time
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        try {

            PreparedStatement ps = connection.prepareStatement("SELECT MAX(Appointment_ID) FROM appointments");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                appointmentID = rs.getInt(1) + 1;
            }
        }
        catch(SQLException s){
            s.printStackTrace();
        }
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT Customer_Name FROM customers");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                AddAppointmentCustomerIDCombo.getItems().add(rs.getString(1));
            }

        } catch (SQLException s) {
            s.printStackTrace();
        }
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT Contact_Name FROM contacts");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                AddAppointmentContactCombo.getItems().add(rs.getString(1));
            }

        } catch (SQLException s) {
            s.printStackTrace();
        }
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT User_Name FROM users");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                AddAppointmentUserCombo.getItems().add(rs.getString(1));
            }

        } catch (SQLException s) {
            s.printStackTrace();
        }
        AddAppointmentIDField.setText("Auto Gen: " + appointmentID);
        AddAppointmentIDField.setEditable(false);

        while(startTimeOptions.isBefore(endTimeOptions.plusSeconds(1))){
            AddAppointmentStartTimeCombo.getItems().add(startTimeOptions);
            startTimeOptions = startTimeOptions.plusMinutes(15);
            AddAppointmentEndTimeCombo.getItems().add(startTimeOptions);
        }
    }

    /**
     *     //saves the appointment to server, translating localtime to UTC
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void handleAddAppointmentSaveButton(ActionEvent actionEvent) throws SQLException, IOException {

        String title = AddAppointmentTitleField.getText();
        String description = AddAppointmentDescriptionField.getText();
        String location = AddAppointmentLocationField.getText();
        String type = AddAppointmentTypeField.getText();
        handleSelectedUser(actionEvent);
        LocalDate startDate = AddAppointmentStartDatePicker.getValue();
        LocalTime startTime = AddAppointmentStartTimeCombo.getValue();
        LocalDate endDate = AddAppointmentEndDatePicker.getValue();
        LocalTime endTime = AddAppointmentEndTimeCombo.getValue();
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);
        ZonedDateTime userStartZone = start.atZone(userZoneId);
        ZonedDateTime targetStartTime = userStartZone.withZoneSameInstant(targetZoneId);
        ZonedDateTime userEndZone = end.atZone(userZoneId);
        ZonedDateTime targetEndTime = userEndZone.withZoneSameInstant(targetZoneId);
        LocalDateTime targetStart = targetStartTime.toLocalDateTime();
        LocalDateTime targetEnd = targetEndTime.toLocalDateTime();
        if (!title.equals("") && !description.equals("") && !location.equals("") && !type.equals("") && userID != -1 && customerID != -1 && contactID != -1 && !targetStart.equals("") && !targetEnd.equals("")) {
            if (validateStart(targetStart, targetEnd)) { System.out.println("Start validated");
                if (validateEnd(targetStart, targetEnd)) {System.out.println("End validated");
                    Appointment appointment = new Appointment(appointmentID, customerID, title, description, location, contactID, type, targetStart, targetEnd, userID);
                    appointment.setUserID(userID);
                    appointment.setAppointmentID(appointmentID);
                    appointment.setCustomerID(customerID);
                    appointment.setTitle(title);
                    appointment.setDescription(description);
                    appointment.setLocation(location);
                    appointment.setContactID(contactID);
                    appointment.setAppointmentType(type);
                    appointment.setStart(targetStart);
                    appointment.setEnd(targetEnd);
                    String saveAppointment = "INSERT INTO appointments VALUES(" + appointmentID + ", " + "'" + title + "'" + ", " + "'" + description + "'" + ", " + "'" + location + "', " +
                            "'" + type + "'" + ", " + "'" + targetStart + "'" + ", '" + targetEnd + "', " + "NOW(), 'app', NOW(), 'app', " + customerID + ", " + userID + ", " + contactID + ");";
                    System.out.println(saveAppointment);
                    PreparedStatement ps = connection.prepareStatement(saveAppointment);
                    int rs = ps.executeUpdate();

                    Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/AppointmentsMenu.fxml"));
                    Scene scene = new Scene(loader);
                    Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    window.setScene(scene);
                    window.show();

                } else {
                    if (LoginScreenController.getLocale().equals("en")) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("Invalid Entry");
                        alert.setContentText("Date/Time overlap with existing appointment");
                        Optional<ButtonType> option = alert.showAndWait();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Entrée invalide");
                        alert.setContentText("Chevauchement de date / heure avec \nle rendez-vous existant");
                        Optional<ButtonType> option = alert.showAndWait();
                    }
                }
            }
            else {
                if (LoginScreenController.getLocale().equals("en")) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Entry");
                    alert.setContentText("Date/Time overlap with existing appointment");
                    Optional<ButtonType> option = alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Entrée invalide");
                    alert.setContentText("Chevauchement de date / heure avec le rendez-vous existant");
                    Optional<ButtonType> option = alert.showAndWait();
                }
            }
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
     *     //cancels add appointment
     * @param actionEvent
     * @throws IOException
     */
    public void handleAddAppointmentCancelButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/AppointmentsMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //converts customer name into customer id
     * @param actionEvent
     * @throws SQLException
     */
    public void handleSelectedCustomer(ActionEvent actionEvent) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Customer_ID FROM customers WHERE (Customer_Name = '" + AddAppointmentCustomerIDCombo.getValue() + "')");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                customerID = rs.getInt(1);
            }
        }
        catch(SQLException s){
            s.printStackTrace();
        }
    }

    /**
     *     //converts contact name to ID
     * @param actionEvent
     */
    public void handleSelectedContact(ActionEvent actionEvent) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Contact_ID FROM contacts WHERE (Contact_Name = '" + AddAppointmentContactCombo.getValue() + "')");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                contactID = rs.getInt(1);
            }
        }
        catch(SQLException s){
            s.printStackTrace();
        }
    }

    /**
     *     //converts user name to id
     * @param actionEvent
     */
    public void handleSelectedUser(ActionEvent actionEvent) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT User_ID FROM users WHERE User_Name = '" + AddAppointmentUserCombo.getValue() + "'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userID = rs.getInt(1);
            }
        }
        catch(SQLException s){
            s.printStackTrace();
        }
    }

    /**
     *     //validates that start time does not overlap
     * @param start
     * @return
     * @throws SQLException
     */
    public boolean validateStart(LocalDateTime start, LocalDateTime end) throws SQLException{
        boolean returnBoolean = false;
        LocalDateTime now = LocalDateTime.now();
        PreparedStatement ps = connection.prepareStatement("SELECT Start, End FROM appointments");
        ResultSet rs = ps.executeQuery();
        if(start.isAfter(now)){
            returnBoolean = true;

        }
        while(rs.next())
        {
            LocalDateTime tempStart = LocalDateTime.parse(rs.getString(1), dateTimeFormatter);
            LocalDateTime tempEnd = LocalDateTime.parse(rs.getString(2),dateTimeFormatter);
            System.out.println("tempstart: " + tempStart + "\n temp end: " + tempEnd);
            if (tempStart.isEqual(start)){
                returnBoolean = false;
                break;
            }
            if(start.isBefore(tempStart) && end.isBefore(tempEnd) && end.isAfter(tempStart)){
                System.out.println("fml");
                returnBoolean = false;
                break;
            }
        }

        return returnBoolean;
    }

    /**
     *     //validates end time does not overlap
     * @param start
     * @param end
     * @return
     * @throws SQLException
     */
    public boolean validateEnd(LocalDateTime start, LocalDateTime end) throws SQLException{
        boolean returnBoolean = false;
        PreparedStatement ps = connection.prepareStatement("SELECT Start, End FROM appointments");
        ResultSet rs = ps.executeQuery();
        if(end.isAfter(start))
            returnBoolean = true;
        while(rs.next())
        {
            LocalDateTime tempStart = LocalDateTime.parse(rs.getString(1),dateTimeFormatter);
            LocalDateTime tempEnd = LocalDateTime.parse(rs.getString(2), dateTimeFormatter);
            if(tempStart.isBefore(start) && tempEnd.isAfter(start)){
                returnBoolean = false;
                break;
            }
        }
        return returnBoolean;
    }
}

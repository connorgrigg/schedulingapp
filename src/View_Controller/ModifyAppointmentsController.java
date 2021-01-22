package View_Controller;

import Database.DBConnect;
import Model.Appointment;
import Model.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
 * //class that modifies appointment and controls modify appointment menu
 */
public class ModifyAppointmentsController implements Initializable {

    @FXML
    private TextField ModAppointmentIDField;
    @FXML
    private TextField ModAppointmentTitleField;
    @FXML
    private TextField ModAppointmentDescriptionField;
    @FXML
    private TextField ModAppointmentLocationField;
    @FXML
    private TextField ModAppointmentTypeField;
    @FXML
    private ComboBox<LocalTime> ModAppointmentStartTimeCombo;
    @FXML
    private ComboBox<LocalTime> ModAppointmentEndTimeCombo;
    @FXML
    private DatePicker ModAppointmentStartDatePicker;
    @FXML
    private DatePicker ModAppointmentEndDatePicker;
    @FXML
    private ComboBox ModAppointmentCustomerIDCombo;
    @FXML
    private ComboBox ModAppointmentContactCombo;
    @FXML
    private ComboBox<String> ModAppointmentUserCombo;

    Connection connection = DBConnect.getConnection();
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

    LocalTime startTimeOptions = LocalTime.of(8,0).plusHours(offset);
    LocalTime endTimeOptions = LocalTime.of(21,45).plusHours(offset);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    Appointment modAppointment = AppointmentsMenuController.getModAppointment();

    private int appointmentID;
    private int customerID = -1;
    private int contactID = -1;
    private int userID = -1;
    private ActionEvent event;

    /**
     *     //initializes menu options
      * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT MAX(Appointment_ID) FROM appointments");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                appointmentID = rs.getInt(1);
            }
        }
        catch(SQLException s){
            s.printStackTrace();
        }
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT Customer_Name FROM customers");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ModAppointmentCustomerIDCombo.getItems().add(rs.getString(1));
            }

        } catch (SQLException s) {
            s.printStackTrace();
        }
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT Contact_Name FROM contacts");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ModAppointmentContactCombo.getItems().add(rs.getString(1));
            }

        } catch (SQLException s) {
            s.printStackTrace();
        }
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT User_Name FROM users");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ModAppointmentUserCombo.getItems().add(rs.getString(1));
            }

        } catch (SQLException s) {
            s.printStackTrace();
        }
        ModAppointmentIDField.setText("Auto Gen: " + modAppointment.getAppointmentID());
        ModAppointmentIDField.setEditable(false);

        while(startTimeOptions.isBefore(endTimeOptions.plusSeconds(1))){
            ModAppointmentStartTimeCombo.getItems().add(startTimeOptions);
            startTimeOptions = startTimeOptions.plusMinutes(15);
            ModAppointmentEndTimeCombo.getItems().add(startTimeOptions);
        }
        try {
            ModAppointmentCustomerIDCombo.getSelectionModel().select(Schedule.convertCustomer(modAppointment.getCustomerID()));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        ModAppointmentTitleField.setText(modAppointment.getTitle());
        ModAppointmentDescriptionField.setText(modAppointment.getDescription());
        ModAppointmentLocationField.setText(modAppointment.getLocation());
        try {
            ModAppointmentContactCombo.getSelectionModel().select(Schedule.convertContact(modAppointment.getContactID()));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        handleSelectedContact(event);
        ModAppointmentTypeField.setText(modAppointment.getAppointmentType());
        try {
            ModAppointmentStartTimeCombo.getSelectionModel().select(Appointment.getStartTime(modAppointment));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        try {
            ModAppointmentEndTimeCombo.getSelectionModel().select(Appointment.getEndTime(modAppointment));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        try {
            ModAppointmentStartDatePicker.setValue(Appointment.getStartDate(modAppointment));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        try {
            ModAppointmentEndDatePicker.setValue(Appointment.getEndDate(modAppointment));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        try {
            ModAppointmentUserCombo.getSelectionModel().select(initUserID(modAppointment.getAppointmentID()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            handleSelectedCustomer(null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        handleSelectedUser(null);
        handleSelectedContact(null);
    }

    /**
     *     //saves modified appointment to server
      * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void handleModAppointmentSaveButton(ActionEvent actionEvent) throws SQLException, IOException {
        String title = ModAppointmentTitleField.getText();
        String description = ModAppointmentDescriptionField.getText();
        String location = ModAppointmentLocationField.getText();
        String type = ModAppointmentTypeField.getText();
        LocalDate startDate = ModAppointmentStartDatePicker.getValue();
        LocalTime startTime = ModAppointmentStartTimeCombo.getValue();
        LocalDate endDate = ModAppointmentEndDatePicker.getValue();
        LocalTime endTime = ModAppointmentEndTimeCombo.getValue();
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);
        ZonedDateTime userStartZone = start.atZone(userZoneId);
        ZonedDateTime targetStartTime = userStartZone.withZoneSameInstant(targetZoneId);
        ZonedDateTime userEndZone = end.atZone(userZoneId);
        ZonedDateTime targetEndTime = userEndZone.withZoneSameInstant(targetZoneId);
        LocalDateTime targetStart = targetStartTime.toLocalDateTime();
        LocalDateTime targetEnd = targetEndTime.toLocalDateTime();
        if (!title.equals("") && !description.equals("") && !location.equals("") && !type.equals("") && userID != -1 && customerID != -1 && contactID != -1 && !targetStart.equals("") && !targetEnd.equals("")) {
            if (validateStart(targetStart, targetEnd)) {
                if (validateEnd(targetStart, targetEnd)) {

                    Appointment appointment = new Appointment(appointmentID, customerID, title, description, location, contactID, type, targetStart, targetEnd, userID);

                    appointment.setAppointmentID(appointmentID);
                    appointment.setCustomerID(customerID);
                    appointment.setTitle(title);
                    appointment.setDescription(description);
                    appointment.setLocation(location);
                    appointment.setContactID(contactID);
                    appointment.setAppointmentType(type);
                    appointment.setStart(targetStart);
                    appointment.setEnd(targetEnd);
                    appointment.setUserID(userID);

                    String saveAppointment = "UPDATE appointments SET Title = '" + title + "'" + ", Description = " + "'" + description + "'" +
                            ", Location = " + "'" + location + "', Type = " + "'" + type + "'" + ", Start = " + "'" + targetStart + "'" + ", End = '" + targetEnd + "', " +
                            "Last_Update = NOW(), Last_Updated_By = 'app', Customer_ID = " + customerID + ", User_ID = " + userID + ", Contact_ID = " + contactID
                            + "  WHERE Appointment_ID = '" + modAppointment.getAppointmentID() + "';";
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
                        alert.setContentText("Incorrect End date or time");
                        Optional<ButtonType> option = alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Entrée invalide");
                        alert.setContentText("Date ou heure de fin non valide");
                        Optional<ButtonType> option = alert.showAndWait();
                    }
                }
            } else {
                if (LoginScreenController.getLocale().equals("en")) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Entry");
                    alert.setContentText("Incorrect Start date or time");
                    Optional<ButtonType> option = alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Entrée invalide");
                    alert.setContentText("Date ou heure de début incorrecte");
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
     *     //cancels modified appointment and launches appointment menu
      * @param actionEvent
     * @throws IOException
     */
    public void handleModAppointmentCancelButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/AppointmentsMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     *     //converts customer name to customer id
      * @param actionEvent
     * @throws SQLException
     */
    public void handleSelectedCustomer(ActionEvent actionEvent) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Customer_ID FROM customers WHERE (Customer_Name = '" + ModAppointmentCustomerIDCombo.getValue() + "')");
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
     *     //converts contact name to contact id
      * @param actionEvent
     */
    public void handleSelectedContact(ActionEvent actionEvent) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT Contact_ID FROM contacts WHERE (Contact_Name = '" + ModAppointmentContactCombo.getValue() + "')");
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
     *     //converts user name to user id
      * @param actionEvent
     */
    public void handleSelectedUser(ActionEvent actionEvent) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT User_ID FROM users WHERE (User_Name = '" + ModAppointmentUserCombo.getValue() + "')");
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
     * validates start time to prevent overlapping appointments
     * @param start
     * @param end
     * @return
     * @throws SQLException
     */
    public boolean validateStart(LocalDateTime start, LocalDateTime end) throws SQLException{
        boolean returnBoolean = false;
        LocalDateTime now = LocalDateTime.now();
        PreparedStatement ps = connection.prepareStatement("SELECT Start, End FROM appointments WHERE Appointment_ID != " + modAppointment.getAppointmentID());
        ResultSet rs = ps.executeQuery();
        if(start.isAfter(now)){
            returnBoolean = true;

        }
        while(rs.next())
        {
            LocalDateTime tempStart = LocalDateTime.parse(rs.getString(1), dateTimeFormatter);
            LocalDateTime tempEnd = LocalDateTime.parse(rs.getString(2),dateTimeFormatter);
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
     *     //validates end time
      * @param start
     * @param end
     * @return
     */
    public boolean validateEnd(LocalDateTime start, LocalDateTime end) throws SQLException{
        boolean returnBoolean = false;
        PreparedStatement ps = connection.prepareStatement("SELECT Start, End FROM appointments WHERE Appointment_ID != " + modAppointment.getAppointmentID());
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

    /**
     *     //initializes userID
      * @param appointmentID
     * @return
     * @throws SQLException
     */
    public String initUserID(int appointmentID) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT User_ID FROM appointments WHERE Appointment_ID = " + appointmentID);
        int i = 0;
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            i = rs.getInt(1);
        }
        return Schedule.convertUser(i);
    }
}

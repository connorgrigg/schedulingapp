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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * //class controlling appointments menu
 */
public class AppointmentsMenuController implements Initializable {
    public Button AppointmentMenuAddButton;
    public Button AppointmentsMenuDeleteButton;
    public Button AppointmentsMenuModifyButton;
    public Button AppointmentsMenuReturnButton;
    public RadioButton AppointmentsMenuWeeklyView;
    public RadioButton AppointmentsMenuMonthlyView;
    public RadioButton AppointmentsMenuDailyView;
    public RadioButton AppointmentsMenuAllAppointmentsButton;
    public TableView<Appointment> AppointmentsTableView;
    public TableColumn<Appointment, Integer> AppointmentIDCol;
    public TableColumn<Appointment, Integer> CustomerIDCol;
    public TableColumn<Appointment, String> TitleCol;
    public TableColumn<Appointment, String> DescriptionCol;
    public TableColumn<Appointment, String> LocationCol;
    public TableColumn<Appointment, String> ContactCol;
    public TableColumn<Appointment, String> TypeCol;
    public TableColumn<Appointment, LocalDateTime> StartCol;
    public TableColumn<Appointment, LocalDateTime> EndCol;
    public static Connection connection = DBConnect.getConnection();
    private static Appointment modAppointment;
    public int viewSelection = 0;
    private static final TimeZone userTimeZone = TimeZone.getDefault();
    public static ZoneId userZoneId = userTimeZone.toZoneId();

    public static TimeZone targetTimeZone = TimeZone.getTimeZone("UTC");
    public static ZoneId targetZoneId = targetTimeZone.toZoneId();
    public static ZonedDateTime userZDT = ZonedDateTime.now(userZoneId);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    /**
     *     //initializes display options
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentID"));
        CustomerIDCol.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        TitleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        DescriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
        LocationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
        ContactCol.setCellValueFactory(new PropertyValueFactory<>("ContactID"));
        TypeCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentType"));
        StartCol.setCellValueFactory(new PropertyValueFactory<>("Start"));
        EndCol.setCellValueFactory(new PropertyValueFactory<>("End"));
        try {
            AppointmentsTableView.setItems(Schedule.getAppointmentList(viewSelection));
        } catch (SQLException s) {
            s.printStackTrace();
        }
        AppointmentsMenuAllAppointmentsButton.setSelected(true);
    }

    /**
     * //launches add appointment menu
     *
     * @param actionEvent
     * @throws IOException
     */
    public void handleAppointmentMenuAddButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/AddAppointments.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * //deletes selected appointment, updates table
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void handleAppointmentsMenuDeleteButton(ActionEvent actionEvent) throws SQLException {
        if (LoginScreenController.getLocale().equals("en")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Appointment Cancelled");
            alert.setHeaderText("Cancelling Appointment with ID: " + AppointmentsTableView.getSelectionModel().getSelectedItem().getAppointmentID());
            alert.setContentText("Appointment: " + AppointmentsTableView.getSelectionModel().getSelectedItem().getAppointmentID() + " of type: " + AppointmentsTableView.getSelectionModel().getSelectedItem().getAppointmentType() + " cancelled");
            Optional<ButtonType> option = alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Erreur");
            alert.setHeaderText("Entrée invalide");
            alert.setContentText("Chevauchement de date / heure avec \nle rendez-vous existant");
            Optional<ButtonType> option = alert.showAndWait();
        }
        PreparedStatement ps = connection.prepareStatement("DELETE FROM appointments WHERE Appointment_ID = " + AppointmentsTableView.getSelectionModel().getSelectedItem().getAppointmentID());
        int rs = ps.executeUpdate();
        AppointmentsTableView.setItems(Schedule.getAppointmentList(viewSelection));

    }

    /**
     * //saves selected appointment for modification (returned in getModAppointment) and launches modify appointment menu
     *
     * @param actionEvent
     * @throws IOException
     */
    public void handleAppointmentsMenuModifyButton(ActionEvent actionEvent) throws IOException {
        modAppointment = AppointmentsTableView.getSelectionModel().getSelectedItem();
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/ModifyAppointments.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * //launches main menu
     *
     * @param actionEvent
     * @throws IOException
     */
    public void handleAppointmentsMenuReturnButton(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/MainMenu.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * //modifies table to select appointments daily view
     *
     * @param event
     */
    public void handleAppointmentsMenuDailyView(ActionEvent event) {
        AppointmentsMenuWeeklyView.setSelected(false);
        AppointmentsMenuMonthlyView.setSelected(false);
        AppointmentsMenuAllAppointmentsButton.setSelected(false);
        viewSelection = 1;
        try {
            AppointmentsTableView.setItems(Schedule.getAppointmentList(viewSelection));
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     * //modifies table to select appointments weekly view
     *
     * @param event
     */
    public void handleAppointmentsMenuWeeklyView(ActionEvent event) {
        AppointmentsMenuDailyView.setSelected(false);
        AppointmentsMenuMonthlyView.setSelected(false);
        AppointmentsMenuAllAppointmentsButton.setSelected(false);
        viewSelection = 2;
        try {
            AppointmentsTableView.setItems(Schedule.getAppointmentList(viewSelection));
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     * //modifies table to select appointments monthly view
     *
     * @param event
     */
    public void handleAppointmentsMenuMonthlyView(ActionEvent event) {
        AppointmentsMenuDailyView.setSelected(false);
        AppointmentsMenuWeeklyView.setSelected(false);
        AppointmentsMenuAllAppointmentsButton.setSelected(false);
        viewSelection = 3;
        try {
            AppointmentsTableView.setItems(Schedule.getAppointmentList(viewSelection));
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     * //modifies table to select all appointments regardless as to date (default)
     *
     * @param event
     */
    public void handleAllAppointments(ActionEvent event) {
        AppointmentsMenuDailyView.setSelected(false);
        AppointmentsMenuWeeklyView.setSelected(false);
        AppointmentsMenuMonthlyView.setSelected(false);
        viewSelection = 0;
        try {
            AppointmentsTableView.setItems(Schedule.getAppointmentList(viewSelection));
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     * //returns selected appointment for modification
     *
     * @return
     */
    public static Appointment getModAppointment() {
        return modAppointment;
    }

    /**
     * //checks if there is an appointment within 15 minutes of current time
     *
     * @throws SQLException
     */
    public static void checkNearestAppointment() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT Appointment_ID, Start FROM appointments");
        ResultSet rs = ps.executeQuery();
        int holder = 0;
        while (rs.next()) {
            int id = rs.getInt(1);
            LocalDateTime temp = LocalDateTime.parse(rs.getString(2), dateTimeFormatter);
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime userZDT = now.atZone(userZoneId);
            ZonedDateTime UTC = userZDT.withZoneSameInstant(targetZoneId);
            LocalDateTime nowFinal = UTC.toLocalDateTime();
            if (nowFinal.isBefore(temp) && nowFinal.plusMinutes(15).isAfter(temp)) {
                holder++;
                if (LoginScreenController.getLocale().equals("en")) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Heads Up!");
                    alert.setHeaderText("Upcoming Appointment");
                    alert.setContentText("next appointment is within 15 minutes\n ID: " + id + " at " + temp);
                    Optional<ButtonType> option = alert.showAndWait();
                    break;
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("La tête haute");
                    alert.setHeaderText("rendez-vous à venir");
                    alert.setContentText("15 minutes jusqu'au prochain rendez-vous\n ID: " + " à " + temp);
                    Optional<ButtonType> option = alert.showAndWait();
                    break;
                }
            }
        }
        if(holder == 0)
            noUpcomingAppointments();
    }

    /**
     * if there are no upcoming appointments, this sends appropriate message
     */
    public static void noUpcomingAppointments()
    {
        if (LoginScreenController.getLocale().equals("en")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Free Time");
            alert.setHeaderText("No Upcoming Appointments");
            alert.setContentText("Next 15 minutes free");
            Optional<ButtonType> option = alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("temps libre");
            alert.setHeaderText("pas de rendez-vous à venir");
            alert.setContentText("15 minutes de repos");
            Optional<ButtonType> option = alert.showAndWait();
        }
    }
}

package View_Controller;

import Model.Appointment;
import Model.Schedule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * class implementing the first report
 */
public class ReportOneController implements Initializable {
    public Button ReportOneSubmitButton;
    public Button ReportOneReturnButton;
    public ComboBox<String> SelectMonthCombo;
    public ComboBox SelectTypeCombo;

    public ObservableList<String> ol = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointmentsHolder = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointmentsFinal = FXCollections.observableArrayList();
    public List<String> appointmentTypes = FXCollections.observableArrayList();

    /**
     * initializes the class and viewables
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ol.addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        for(int i = 0; i < 12; i++){
            SelectMonthCombo.getItems().add(ol.get(i));
        }
    }

    /**
     * handles the report generation upon clicking the select button; also contains the second lambda which builds the string for the report based upon
     * the selected month, and that months total appointments and total unique appointments by combining the strings together to form the report
     * @param event
     * @throws SQLException
     */
        public void handleSubmitReport(ActionEvent event) throws SQLException {
            appointmentsHolder.clear();
            appointmentsFinal.clear();
            appointmentTypes.clear();
            String month = SelectMonthCombo.getValue().toUpperCase();
            appointmentsHolder = Schedule.getAppointmentList(0);
            for(int i = 0; i < appointmentsHolder.size(); i++){
                if(Schedule.getMonth(appointmentsHolder.get(i)).equals(month)) {
                    appointmentsFinal.add(appointmentsHolder.get(i));
                    appointmentTypes.add(appointmentsHolder.get(i).getAppointmentType());
                }
            }
            int countTotal = appointmentsFinal.size();

            int countUniqueTypes = getUniqueTypes(appointmentTypes).size();
                ReportOneInterface firstReport = (s1, i2, i3) -> "For month: " + s1 + "\n Total appointments: " + i2 + "\n Total distinct types: " + i3;
                String s = firstReport.firstReport(month, countTotal, countUniqueTypes);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Metrics");
            alert.setHeaderText(s);
            Optional<ButtonType> option = alert.showAndWait();
        }

    public void handleReturnButton(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/Reports.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public List<String> getUniqueTypes(List<String> list){
        return list.stream().distinct().collect(Collectors.toList());
    }

    public int getElementCount(String s, List<String> list){
        int returnInt = 0;
        for (String value : list) {
            if (value.equals(s))
                returnInt++;
        }
        return returnInt;
    }
}

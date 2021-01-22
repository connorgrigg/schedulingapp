package View_Controller;

import Database.DBConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * //class to control login screen
 */
public class LoginScreenController implements Initializable {
    public TextField LoginScreenUserField;
    public PasswordField LoginScreenPasswordField;
    public Button LoginScreenLoginButton;
    public Text ZoneIDZone;
    public TimeZone timeZone = TimeZone.getDefault();
    public ZoneId zoneId = timeZone.toZoneId();
    public static Locale locale = Locale.getDefault();
    /**
     *     lambda to detect user language setting; this Sets the LanguageSelectionInterface by getting the language code from the default Locale, to be return with
     *     the getLocale method
      */
    public static LanguageSelectionInterface LSI = l -> Locale.getDefault().getLanguage();
    public static String lang = LSI.languageDetection(locale);
    public Text LoginText;
    public Text UsernameText;
    public Text PasswordText;
    public Text ZoneIDText;

    @Override
/**
 *     //initializes form and generates text
  */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ZoneIDZone.setText(zoneId.toString());
        if(getLocale().equals("fr")) {
            LoginText.setText("Page de connexion");
            UsernameText.setText("Nom d'utilisateur");
            PasswordText.setText("Mot de passe");
            LoginScreenLoginButton.setText("soumettre");
            ZoneIDText.setText("Zone:");
            System.out.println(lang);
        }
    }

    /**
     *     //takes user login parameters, sends them to DBConnect for confirmation, and logs login attempts
      * @param actionEvent
     * @throws SQLException
     */
    public void handleLoginScreenLoginButton(ActionEvent actionEvent) throws SQLException {
        try {
                LocalDateTime localDateTime = LocalDateTime.now();
                FileWriter writer = new FileWriter("login_activity.txt", true);
                writer.write("Login Attempt from user: " + LoginScreenUserField.getText() + ", at: " + localDateTime + ";\n");
            if(lang.equals("en")) {
                if (DBConnect.handleLogin(LoginScreenUserField.getText(), LoginScreenPasswordField.getText())) {
                    writer.write("Login successful\n\n");
                    Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/MainMenu.fxml"));
                    Scene scene = new Scene(loader);
                    Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    window.setTitle("Scheduling Service");
                    window.setScene(scene);
                    window.show();
                    AppointmentsMenuController.checkNearestAppointment();
                } else {
                    writer.write("Login failed\n\n");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Login");
                    alert.setContentText("Invalid Username or Password");
                    Optional<ButtonType> option = alert.showAndWait();
                }
            }
            else {
                if (DBConnect.handleLogin(LoginScreenUserField.getText(), LoginScreenPasswordField.getText())) {
                    writer.write("Login successful\n\n");
                    Parent loader = FXMLLoader.load(getClass().getResource("/View_Controller/MainMenu.fxml"));
                    Scene scene = new Scene(loader);
                    Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    window.setTitle("Service de Planification");
                    window.setScene(scene);
                    window.show();
                    AppointmentsMenuController.checkNearestAppointment();
                } else {
                    writer.write("Login failed\n\n");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Identifiant Invalide");
                    alert.setContentText("nom d'utilisateur ou mot de passe invalide");
                    Optional<ButtonType> option = alert.showAndWait();
                }
            }
            writer.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns user locale
     * @return
     */
    public static String getLocale(){
        return lang;
    }
}

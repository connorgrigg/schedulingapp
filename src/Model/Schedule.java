package Model;

import Database.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.TimeZone;

/**
 * class for connecting customers and appointments and manipulating data between them
 */
public class Schedule {
    private static final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> contactSchedule = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    public static Connection connection = DBConnect.getConnection();

    private static final TimeZone userTimeZone = TimeZone.getDefault();
    public static ZoneId userZoneId = userTimeZone.toZoneId();
    public static TimeZone targetTimeZone = TimeZone.getTimeZone("UTC");
    public static ZoneId targetZoneId = targetTimeZone.toZoneId();
    public static ZonedDateTime userZDT = ZonedDateTime.now(userZoneId);
    public static ZonedDateTime targetZDT = userZDT.withZoneSameInstant(targetZoneId);
    public static int offset = targetZDT.getOffset().compareTo(userZDT.getOffset())/60/60;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * adds customer to relevant list
     * @param customer
     */
    public static void addCustomer(Customer customer){
        customerList.add(customer);
    }

    /**
     * adds appointment to relevant list
     * @param appointment
     */
    public static void addAppointment(Appointment appointment){
        appointmentList.add(appointment);
    }

    /**
     * adds appointment to contact list
     * @param appointment
     */
    public static void addContactAppointment(Appointment appointment){
        contactSchedule.add(appointment);
    }

    /**
     * updates customer
     * @param index
     * @param customer
     */
    public static void updateCustomer(int index, Customer customer){
        customerList.set(index, customer);
    }

    /**
     * returns customer list
     * @return
     */
    public static ObservableList<Customer> getCustomerList() {
        customerList.clear();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customers");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int ID = rs.getInt(1);
                String name = rs.getString(2);
                String addressOne = rs.getString(3);
                String postal = rs.getString(4);
                String phone = rs.getString(5);
                String division = rs.getString(10);
                String country = "";

                if (rs.getInt(10) < 60) {
                    country = "US";
                } else if (rs.getInt(10) > 59 && rs.getInt(10) < 100) {
                    country = "Canada";
                } else if (rs.getInt(10) > 99) {
                    country = "UK";
                }

                Customer customer = new Customer(ID, name, country, phone, division, addressOne, postal);

                customer.setCustomerID(ID);
                customer.setCustomerName(name);
                customer.setPrimaryAddress(addressOne);
                customer.setCustomerDivision(division);
                customer.setCountry(country);
                customer.setPostalCode(postal);
                customer.setPhone(phone);
                addCustomer(customer);

            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return customerList;
    }

    /**
     * returns appointment list
     * @param viewSelection
     * @return
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAppointmentList(int viewSelection) throws SQLException {
        appointmentList.clear();
        LocalDate thisDay = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int curWeek = thisDay.get(weekFields.weekOfWeekBasedYear());
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM appointments");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int appointmentID = rs.getInt(1);
                    String title = rs.getString(2);
                    String description = rs.getString(3);
                    String location = rs.getString(4);
                    String appointmentType = rs.getString(5);
                    LocalDateTime start = LocalDateTime.parse(rs.getString(6), dateTimeFormatter);
                    LocalDateTime end = LocalDateTime.parse(rs.getString(7), dateTimeFormatter);
                    start = start.plusHours(offset);
                    end = end.plusHours(offset);
                    int customerID = rs.getInt(12);
                    int userID = rs.getInt(13);
                    int contactID = rs.getInt(14);
                    if(viewSelection == 0) {
                        Appointment appointment = new Appointment(appointmentID, customerID, title, description, location, contactID, appointmentType, start, end, userID);
                        appointment.setAppointmentID(appointmentID);
                        appointment.setCustomerID(customerID);
                        appointment.setTitle(title);
                        appointment.setDescription(description);
                        appointment.setLocation(location);
                        appointment.setContactID(contactID);
                        appointment.setAppointmentType(appointmentType);
                        appointment.setStart(start);
                        appointment.setEnd(end);

                        addAppointment(appointment);
                    }

                    else if(viewSelection == 1)
                    {
                        if(start.toLocalDate().equals(thisDay))
                        {
                            Appointment appointment = new Appointment(appointmentID, customerID, title, description, location, contactID, appointmentType, start, end, userID);
                            appointment.setAppointmentID(appointmentID);
                            appointment.setCustomerID(customerID);
                            appointment.setTitle(title);
                            appointment.setDescription(description);
                            appointment.setLocation(location);
                            appointment.setContactID(contactID);
                            appointment.setAppointmentType(appointmentType);
                            appointment.setStart(start);
                            appointment.setEnd(end);

                            addAppointment(appointment);
                        }
                        else
                            continue;
                    }
                    else if(viewSelection == 2){
                        if(start.get(weekFields.weekOfWeekBasedYear()) == curWeek){
                            Appointment appointment = new Appointment(appointmentID, customerID, title, description, location, contactID, appointmentType, start, end, userID);
                            appointment.setAppointmentID(appointmentID);
                            appointment.setCustomerID(customerID);
                            appointment.setTitle(title);
                            appointment.setDescription(description);
                            appointment.setLocation(location);
                            appointment.setContactID(contactID);
                            appointment.setAppointmentType(appointmentType);
                            appointment.setStart(start);
                            appointment.setEnd(end);

                            addAppointment(appointment);
                        }
                        else
                            continue;
                    }
                    else if(viewSelection == 3){
                        if(start.toLocalDate().getMonthValue() == thisDay.getMonthValue()){
                            Appointment appointment = new Appointment(appointmentID, customerID, title, description, location, contactID, appointmentType, start, end, userID);
                            appointment.setAppointmentID(appointmentID);
                            appointment.setCustomerID(customerID);
                            appointment.setTitle(title);
                            appointment.setDescription(description);
                            appointment.setLocation(location);
                            appointment.setContactID(contactID);
                            appointment.setAppointmentType(appointmentType);
                            appointment.setStart(start);
                            appointment.setEnd(end);

                            addAppointment(appointment);
                        }
                        else
                            continue;
                    }
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
            return appointmentList;
    }

    /**
     * returns contact specific appointment schedule for report 3
     * @param selectedContact
     * @return
     * @throws SQLException
     */
    public static ObservableList<Appointment> getContactSchedule(String selectedContact) throws SQLException {
        contactSchedule.clear();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM appointments WHERE Contact_ID = " + convertContactID(selectedContact));
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            int appointmentID = rs.getInt(1);
            String title = rs.getString(2);
            String description = rs.getString(3);
            String location = rs.getString(4);
            String appointmentType = rs.getString(5);
            LocalDateTime start = LocalDateTime.parse(rs.getString(6), dateTimeFormatter);
            start = start.plusHours(offset);
            LocalDateTime end = LocalDateTime.parse(rs.getString(7), dateTimeFormatter);
            end = end.plusHours(offset);
            int customerID = rs.getInt(12);
            int userID = rs.getInt(13);
            int contactID = rs.getInt(14);

            Appointment appointment = new Appointment(appointmentID, customerID, title, description, location, contactID, appointmentType, start, end, userID);
            appointment.setAppointmentID(appointmentID);
            appointment.setCustomerID(customerID);
            appointment.setTitle(title);
            appointment.setDescription(description);
            appointment.setLocation(location);
            appointment.setContactID(contactID);
            appointment.setAppointmentType(appointmentType);
            appointment.setStart(start);
            appointment.setEnd(end);

            addContactAppointment(appointment);
        }

        return contactSchedule;
    }

    /**
     * converts contact name to contact ID; this and following methods useful to allow for more human friendly selections
     * @param contactName
     * @return
     * @throws SQLException
     */
    public static int convertContactID(String contactName) throws SQLException{
        int returnInt = 0;
        PreparedStatement ps = connection.prepareStatement("SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + contactName + "'");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            returnInt = rs.getInt(1);
        }
        return returnInt;
    }

    /**
     * converts contact id to contact name
     * @param contactID
     * @return
     * @throws SQLException
     */
    public static String convertContact(int contactID) throws SQLException {
        String returnString = "";
        PreparedStatement ps = connection.prepareStatement("SELECT Contact_Name FROM contacts WHERE Contact_ID = " + contactID);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
            returnString = rs.getString(1);
        return returnString;
    }

    /**
     * converts user id into user name
     * @param userID
     * @return
     * @throws SQLException
     */
    public static String convertUser(int userID) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT User_Name FROM users WHERE User_ID = " + userID);
        ResultSet rs = ps.executeQuery();
        String returnString = "";
        while(rs.next()){
            returnString = rs.getString(1);
        }
        return returnString;
    }

    /**
     * converts customer id into customer name
     * @param customerID
     * @return
     * @throws SQLException
     */
    public static String convertCustomer(int customerID) throws SQLException {
        String returnString = "";
        PreparedStatement ps = connection.prepareStatement("SELECT Customer_Name FROM customers WHERE Customer_ID = " + customerID);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
            returnString = rs.getString(1);
        return returnString;
    }

    public static String getMonth(Appointment appointment){
        LocalDateTime ldt = appointment.getStart();
        String returnString = ldt.getMonth().toString();
        return returnString;
    }
}

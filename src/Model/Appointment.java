package Model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * class for appointment object setup
 */
public class Appointment {
    private int appointmentID;
    private int customerID;
    private String title;
    private String description;
    private String location;
    private int userID;
    private int contactID;
    private String appointmentType;
    private LocalDateTime start;
    private LocalDateTime end;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");

    /**
     * appointment constructor
     * @param appointmentID
     * @param customerID
     * @param title
     * @param description
     * @param location
     * @param contactID
     * @param appointmentType
     * @param start
     * @param end
     * @param userID
     */
    public Appointment(int appointmentID, int customerID, String title, String description, String location, int contactID, String appointmentType, LocalDateTime start, LocalDateTime end, int userID){

    }

    /**
     * returns appointment start date
     * @param appointment
     * @return
     * @throws SQLException
     */
    public static LocalDate getStartDate(Appointment appointment) throws SQLException {
        LocalDateTime localDatetime = appointment.getStart();
        return localDatetime.toLocalDate();
    }

    /**
     * returns appointment end date
     * @param appointment
     * @return
     * @throws SQLException
     */
    public static LocalDate getEndDate(Appointment appointment) throws SQLException {
        LocalDateTime localDateTime = appointment.getEnd();
        return localDateTime.toLocalDate();
    }

    /**
     * returns appointment start time
     * @param appointment
     * @return
     * @throws SQLException
     */
    public static LocalTime getStartTime(Appointment appointment) throws SQLException {
        LocalDateTime localDateTime = appointment.getStart();
        LocalTime localTime = localDateTime.toLocalTime();
        localTime.format(timeFormatter);
        return localTime;
    }

    /**
     * returns appointment end time
     * @param appointment
     * @return
     * @throws SQLException
     */
    public static LocalTime getEndTime(Appointment appointment) throws SQLException {
        LocalDateTime localDateTime = appointment.getEnd();
        LocalTime localTime = localDateTime.toLocalTime();
        localTime.format(timeFormatter);
        return localTime;
    }

    /**
     * appointment id setter
     * @param appointmentID
     */
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    /**
     * customer id setter
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * title setter
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * description setter
     * @param description
     */
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * location setter
     * @param location
     */
    public void setLocation(String location){
        this.location = location;
    }

    /**
     * contact id setter
     * @param contactID
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     * type setter
     * @param appointmentType
     */
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    /**
     * start setter
     * @param start
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * end setter
     * @param end
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * appointment id getter
     * @return
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     * customer ID getter
     * @return
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * title getter
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * description getter
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * location getter
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * contact id getter
     * @return
     */
    public int getContactID(){
        return contactID;
    }

    /**
     * appointment type getter
     * @return
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * start time getter
     * @return
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * end time getter
     * @return
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * user id getter
     * @param userID
     */
    public void setUserID(int userID){
        this.userID = userID;
    }
}
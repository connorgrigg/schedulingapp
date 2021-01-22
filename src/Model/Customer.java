package Model;

/**
 *
 */
//class for customer object setup
public class Customer {
    private String customerName;
    private String customerPrimaryAddress;
    private String customerDivision;
    private String customerCountry;
    private String customerPhone;
    private int customerID;
    private String customerPostalCode;

    /**
     * customer constructor
     * @param customerID
     * @param customerName
     * @param customerCountry
     * @param customerPhone
     * @param customerDivision
     * @param customerPrimaryAddress
     * @param customerPostalCode
     */
    public Customer(int customerID, String customerName, String customerCountry, String customerPhone, String customerDivision,
                    String customerPrimaryAddress, String customerPostalCode){
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerCountry = customerCountry;
        this.customerPhone = customerPhone;
        this.customerDivision = customerDivision;
        this.customerPrimaryAddress = customerPrimaryAddress;
        this.customerPostalCode = customerPostalCode;
    }

    /**
     * customer name setter
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * customer address setter
     * @param customerPrimaryAddress
     */
    public void setPrimaryAddress(String customerPrimaryAddress) {
        this.customerPrimaryAddress = customerPrimaryAddress;
    }

    /**
     * customer division setter
     * @param customerDivision
     */
    public void setCustomerDivision(String customerDivision) {
        this.customerDivision = customerDivision;
    }

    /**
     * customer country setter
     * @param customerCountry
     */
    public void setCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    /**
     * customer postal code setter
     * @param customerPostalCode
     */
    public void setPostalCode(String customerPostalCode) {
        this.customerPostalCode = customerPostalCode;
    }

    /**
     * customer phone setter
     * @param customerPhone
     */
    public void setPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    /**
     * customer id setter
     * @param customerID
     */
    public void setCustomerID(int customerID){
        this.customerID = customerID;
    }

    /**
     * customer name getter
     * @return
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * customer address getter
     * @return
     */
    public String getCustomerPrimaryAddress() {
        return customerPrimaryAddress;
    }

    /**
     * customer division getter
     * @return
     */
    public String getCustomerDivision() {
        return customerDivision;
    }

    /**
     * customer country getter
     * @return
     */
    public String getCustomerCountry() {
        return customerCountry;
    }

    /**
     * customer postal code getter
     * @return
     */
    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    /**
     * customer phone getter
     * @return
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * customer id getter
     * @return
     */
    public int getCustomerID(){
        return customerID;
    }
}
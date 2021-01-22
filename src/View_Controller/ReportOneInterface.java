package View_Controller;

/**
 * //interface for lambda in reports controller
 */
public interface ReportOneInterface {
    /**
     * lambda building the string for the first report
     * @param inputCount
     * @param inputUnique
     * @return
     */
    String firstReport(String month, int inputCount, int inputUnique);

}

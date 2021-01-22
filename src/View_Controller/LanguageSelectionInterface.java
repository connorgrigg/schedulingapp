package View_Controller;

import java.util.Locale;

/**
 * //interface for lambda in login controller
 */
public interface LanguageSelectionInterface {
    /**
     *     //lambda to detect user language from locale
      * @param locale
     * @return
     */
    String languageDetection(Locale locale);
}

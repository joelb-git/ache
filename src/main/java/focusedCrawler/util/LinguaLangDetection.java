package focusedCrawler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pemistahl.lingua.api.IsoCode639_1;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;

import focusedCrawler.target.model.Page;

public class LinguaLangDetection implements ILangDetection {

    private static final Logger logger = LoggerFactory.getLogger(LangDetection.class);

    public boolean isSupported(String langCode) {
        // ugh, no api for this?
        try {
            IsoCode639_1 iso = IsoCode639_1.valueOf(langCode.toUpperCase());
            Language lang = Language.getByIsoCode639_1(iso);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * Try to detect the language of the text in the String.
     *
     * @param page
     * @return true if the String contains English language, false otherwise
     */
    public Boolean isEnglish(String content) {
        return isLanguage(content, "en");
    }

    public boolean isLanguage(String content, String lang) {
        try {
            if (content == null || content.isEmpty()) {
                return false;
            }
            LanguageDetector linguaDetector = LanguageDetectorBuilder.fromAllLanguages().build();
            Language detectedLanguage = linguaDetector.detectLanguageOf(content);
            return lang.equals(detectedLanguage.getIsoCode639_1().toString());
        } catch (Exception ex) {
            logger.warn("Problem while detecting language in text: " + content, ex);
            return false;
        }
    }

    public String getLanguage(String content) {
        try {
            if (content == null || content.isEmpty()) {
                return null;
            }
            LanguageDetector linguaDetector = LanguageDetectorBuilder.fromAllLanguages().build();
            Language detectedLanguage = linguaDetector.detectLanguageOf(content);
            return detectedLanguage.getIsoCode639_1().toString();
        } catch (Exception ex) {
            logger.warn("Problem while detecting language in text: " + content, ex);
            return null;
        }
    }

    /**
     * Try to detect the language of contents of the page.
     *
     * @param page
     * @param lang - two-letter lang code
     * @return true if the page is in English language, false otherwise
     */
    public Boolean isLanguage(Page page, String lang) {
        try {
            return isLanguage(page.getParsedData().getCleanText(), lang);
        } catch (Exception e) {
            System.out.println("Exception in detect_page");
            return false;
        }
    }

    public String getLanguage(Page page) {
        try {
            String text = page.getParsedData().getCleanText();
            return this.getLanguage(text);
        } catch (Exception e) {
            System.out.println("Exception in detect_page");
            return null;
        }
    }
}

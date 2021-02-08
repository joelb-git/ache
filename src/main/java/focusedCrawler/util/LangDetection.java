package focusedCrawler.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.Language;

import focusedCrawler.target.model.Page;

public class LangDetection implements ILangDetection {

    private static final Logger logger = LoggerFactory.getLogger(LangDetection.class);

    /**
     *  Loads language profiles from resources folder
     */
    private static final Set<String> supportedLanguages = new HashSet<>();
    static {
        // If not supported, consider adding new profile.  See:
        //   https://github.com/shuyo/language-detection/blob/wiki/Tools.md#generate-language-profile
        // TODO: grab list from resources, under `src/main/resources/profiles/` instead of hardcoded list
        String[] languages = {"af", "ar", "bg", "bn", "cs", "da", "de", "el", "en", "es", "et",
                "fa", "fi", "fr", "gu", "he", "hi", "hr", "hu", "id", "it", "ja", "kn", "ko", "kk", "lt",
                "lv", "mk", "ml", "mr", "ne", "nl", "no", "pa", "pl", "pt", "ro", "ru", "sk", "sl",
                "so", "sq", "sv", "sw", "ta", "te", "th", "tl", "tr", "uk", "ur", "vi", "zh-cn",
                "zh-tw"};
        supportedLanguages.addAll(Arrays.asList(languages));
        try {
            List<String> profiles = new ArrayList<String>();

            for (String language : languages) {
                String filename = "profiles/" + language;
                InputStream is = LangDetection.class.getClassLoader().getResourceAsStream(filename);
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String jsonProfile = br.readLine();
                profiles.add(jsonProfile);
            }

            DetectorFactory.loadProfile(profiles);

        } catch (Exception e) {
            throw new IllegalStateException("Could not load language profiles.");
        }
    }

    public boolean isSupported(String langCode) {
        return LangDetection.supportedLanguages.contains(langCode);
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

            Detector detector = DetectorFactory.create();
            detector.append(content);
            ArrayList<Language> langs = detector.getProbabilities();

            if (langs.size() == 0) {
                return false;
            }

            if (langs.get(0).lang.equals(lang)) {
                return true;
            }

            return false;
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

            Detector detector = DetectorFactory.create();
            detector.append(content);
            ArrayList<Language> langs = detector.getProbabilities();

            if (langs.size() == 0) {
                return null;
            }
            return langs.get(0).lang;
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

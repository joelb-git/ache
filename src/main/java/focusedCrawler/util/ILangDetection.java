package focusedCrawler.util;

import focusedCrawler.target.model.Page;

// Abstract over cybozu and lingua detectors
public interface ILangDetection {
    boolean isSupported(String langCode);
    Boolean isEnglish(String content);
    boolean isLanguage(String content, String lang);
    String getLanguage(String content);
    Boolean isLanguage(Page page, String lang);
    String getLanguage(Page page);
}

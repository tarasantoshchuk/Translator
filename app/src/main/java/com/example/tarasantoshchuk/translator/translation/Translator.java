package com.example.tarasantoshchuk.translator.translation;

import android.content.res.Resources;

import com.example.tarasantoshchuk.translator.R;
import com.memetix.mst.language.Language;
import com.memetix.mst.language.SpokenDialect;
import com.memetix.mst.speak.Speak;
import com.memetix.mst.translate.Translate;

import java.util.ArrayList;
import java.util.HashMap;

public class Translator {
    private static Resources sResources;

    /**
     * bing API doesn't provide conversion from Language to SpokenDialect
     * we provide conversion using sLangToDialect hashMap
     */
    private static HashMap<Language, SpokenDialect> sLangToDialect =
            new HashMap<Language, SpokenDialect>();

    static {
        sLangToDialect.put(Language.CATALAN, SpokenDialect.CATALAN_SPAIN);
        sLangToDialect.put(Language.DANISH, SpokenDialect.DANISH_DENMARK);
        sLangToDialect.put(Language.GERMAN, SpokenDialect.GERMAN_GERMANY);
        sLangToDialect.put(Language.ENGLISH, SpokenDialect.ENGLISH_UNITED_KINGDOM);
        sLangToDialect.put(Language.SPANISH, SpokenDialect.SPANISH_SPAIN);
        sLangToDialect.put(Language.FINNISH, SpokenDialect.FINNISH_FINLAND);
        sLangToDialect.put(Language.FRENCH, SpokenDialect.FRENCH_FRANCE);
        sLangToDialect.put(Language.ITALIAN, SpokenDialect.ITALIAN_ITALY);
        sLangToDialect.put(Language.JAPANESE, SpokenDialect.JAPANESE_JAPAN);
        sLangToDialect.put(Language.KOREAN, SpokenDialect.KOREAN_KOREA);
        sLangToDialect.put(Language.NORWEGIAN, SpokenDialect.NORWEGIAN_NORWAY);
        sLangToDialect.put(Language.DUTCH, SpokenDialect.DUTCH_NETHERLANDS);
        sLangToDialect.put(Language.POLISH, SpokenDialect.POLISH_POLAND);
        sLangToDialect.put(Language.PORTUGUESE, SpokenDialect.PORTUGUESE_PORTUGAL);
        sLangToDialect.put(Language.RUSSIAN, SpokenDialect.RUSSIAN_RUSSIA);
        sLangToDialect.put(Language.SWEDISH, SpokenDialect.SWEDISH_SWEDEN);
        sLangToDialect.put(Language.CHINESE_SIMPLIFIED,
                SpokenDialect.CHINESE_SIMPLIFIED_PEOPLES_REPUBLIC_OF_CHINA);
        sLangToDialect.put(Language.CHINESE_TRADITIONAL,
                SpokenDialect.CHINESE_TRADITIONAL_HONG_KONG_SAR);
    }

    public static void Init(Resources resources) {
        Translate.setClientId(resources.getString(R.string.client_id));
        Translate.setClientSecret(resources.getString(R.string.client_secret));
        sResources = resources;
    }

    public static String getTranslation(String input, String sourceLang, String targetLang) {
        Language source = languageFromName(sourceLang);
        Language target = languageFromName(targetLang);
        String result;
        if(target == Language.AUTO_DETECT) {
            result = sResources.getString(R.string.txt_set_target_lang);
        } else {
            try {
                result = Translate.execute(input, source, target);
            } catch (NullPointerException e) {
                result = sResources.getString(R.string.txt_empty);
            } catch (Exception e) {
                result = sResources.getString(R.string.txt_check_connection);
            }
        }
        return result;
    }

    private static Language languageFromName(String langName) {
        for (Language l : Language.values()) {
            try {
                if (l.getName(Language.ENGLISH).equals(langName)) {
                    return l;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static ArrayList<String> getAllLanguages() {
        Language[] languages = Language.values();
        ArrayList<String> langNames = new ArrayList<String>(languages.length);

        for(Language lang: languages) {
            try {
                langNames.add(lang.getName(Language.ENGLISH));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

        return langNames;
    }

    public static String getSound(String input, String lang) {
        try {
            return Speak.execute(input, dialectFromString(lang));
        } catch(Exception e) {
            return null;
        }
    }

    private static SpokenDialect dialectFromString(String lang) {
        Language l = languageFromName(lang);
        return sLangToDialect.get(l);
    }
}

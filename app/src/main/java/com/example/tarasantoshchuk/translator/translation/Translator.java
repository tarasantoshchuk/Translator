package com.example.tarasantoshchuk.translator.translation;

import android.content.res.Resources;

import com.example.tarasantoshchuk.translator.R;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.util.ArrayList;

public class Translator {
    private static Resources mResources;

    public static void Init(Resources resources) {
        Translate.setClientId(resources.getString(R.string.client_id));
        Translate.setClientSecret(resources.getString(R.string.client_secret));
        mResources = resources;
    }

    public static String getTranslation(String input, String sourceLang, String targetLang) {
        Language source = fromName(sourceLang);
        Language target = fromName(targetLang);
        String result;
        if(target == Language.AUTO_DETECT) {
            result = mResources.getString(R.string.txt_set_target_lang);
        } else {
            try {
                result = Translate.execute(input, source, target);
            } catch (NullPointerException e) {
                result = mResources.getString(R.string.txt_empty);
            } catch (Exception e) {
                result = mResources.getString(R.string.txt_check_connection);
            }
        }
        return result;
    }

    private static Language fromName(String langName) {
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
}

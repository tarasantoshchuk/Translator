package com.example.tarasantoshchuk.translator.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.example.tarasantoshchuk.translator.activity.MainActivity;
import com.example.tarasantoshchuk.translator.translation.Translator;
import com.example.tarasantoshchuk.translator.word.WordInfo;

import java.util.ArrayList;

public class TranslationService extends IntentService {

    private static final String TARGET_LANGUAGE = "TargetLanguage";
    private static final String SOURCE_LANGUAGE = "SourceLanguage";

    private static final String INTENT_TYPE = "IntentType";

    private static final String INPUT = "Input";

    private static final String RESULT_RECEIVER = "ResultReceiver";

    public TranslationService(String name) {
        super(name);
    }

    public TranslationService() {
        super("");
    }

    public enum IntentType {
        TRANSLATION, ALL_LANGUAGES, DETAIL
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch((IntentType)intent.getSerializableExtra(INTENT_TYPE)) {
            case TRANSLATION:
                translate(intent);
                break;
            case ALL_LANGUAGES:
                getLanguages(intent);
                break;
            case DETAIL:
                getWordInfo(intent);
                break;
        }
    }

    private void getWordInfo(Intent intent) {

        String input = intent.getStringExtra(INPUT);
        String sourceLang = intent.getStringExtra(SOURCE_LANGUAGE);
        String targetLang = intent.getStringExtra(TARGET_LANGUAGE);

        String sourceSound = Translator.getSound(input, sourceLang);

        String result = Translator.getTranslation(input, sourceLang, targetLang);

        String targetSound = Translator.getSound(result, targetLang);

        WordInfo info =
                new WordInfo(sourceLang, targetLang, input, result, sourceSound, targetSound);

        Bundle bundle = MainActivity.getDetailedTranslationBundle(info);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void getLanguages(Intent intent) {
        ArrayList<String> langNames = Translator.getAllLanguages();

        Bundle bundle = MainActivity.getLanguagesBundle(langNames);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void translate(Intent intent) {

        String input = intent.getStringExtra(INPUT);
        String sourceLang = intent.getStringExtra(SOURCE_LANGUAGE);
        String targetLang = intent.getStringExtra(TARGET_LANGUAGE);

        String result = Translator.getTranslation(input, sourceLang, targetLang);

        Bundle bundle = MainActivity.getTranslationBundle(result);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        receiver.send(Activity.RESULT_OK, bundle);
    }

    public static Bundle getTranslationBundle(String input, String source, String target,
                                       ResultReceiver receiver) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(INTENT_TYPE, IntentType.TRANSLATION);

        bundle.putString(INPUT, input);

        bundle.putString(SOURCE_LANGUAGE, source);
        bundle.putString(TARGET_LANGUAGE, target);

        bundle.putParcelable(RESULT_RECEIVER, receiver);

        return bundle;
    }

    public static Bundle getLanguagesBundle(ResultReceiver receiver) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(INTENT_TYPE, IntentType.ALL_LANGUAGES);

        bundle.putParcelable(RESULT_RECEIVER, receiver);

        return bundle;
    }


    public static Bundle getDetailedTranslationBundle(
            String input, String source, String target, ResultReceiver receiver) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(INTENT_TYPE, IntentType.DETAIL);

        bundle.putString(INPUT, input);

        bundle.putString(SOURCE_LANGUAGE, source);
        bundle.putString(TARGET_LANGUAGE, target);

        bundle.putParcelable(RESULT_RECEIVER, receiver);

        return bundle;
    }
}

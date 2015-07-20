package com.example.tarasantoshchuk.translator.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.translation.Translator;
import com.example.tarasantoshchuk.translator.detailed.WordInfo;

import java.util.ArrayList;

public class TranslationService extends IntentService {
    public static final String RESULT_TYPE = "ResultType";

    public enum ResultType {
        WORD_INFO, LANGUAGES, TRANSLATION, DEFAULTS, DETECTION
    }

    public static final String WORD_INFO = "WordInfo";
    public static final String LANGUAGES = "Languages";
    public static final String TRANSLATION = "Translation";
    public static final String DETECTED_LANGUAGE = "DetectedLanguage";
    public static final String AUTO_DETECT_LANGUAGE = "AutoDetectLanguage";

    public static final String DEFAULT_SRC_LANG = "DefaultSrcLang";
    public static final String DEFAULT_TRGT_LANG = "DefulatTrgtLang";

    private static final String TARGET_LANGUAGE = "TargetLanguage";
    private static final String SOURCE_LANGUAGE = "SourceLanguage";

    private static final String INTENT_TYPE = "IntentType";

    private static final String INPUT = "Input";
    private static final String RESULT = "Result";

    private static final String RESULT_RECEIVER = "ResultReceiver";

    public TranslationService(String name) {
        super(name);
    }

    public TranslationService() {
        super("");
    }

    public enum IntentType {
        TRANSLATION, ALL_LANGUAGES, DETAIL, DEFAULTS, DETECTION
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch((IntentType)intent.getSerializableExtra(INTENT_TYPE)) {
            case TRANSLATION:
                getTranslation(intent);
                break;
            case ALL_LANGUAGES:
                getLanguages(intent);
                break;
            case DETAIL:
                getWordInfo(intent);
                break;
            case DEFAULTS:
                getDefaultLanguages(intent);
                break;
            case DETECTION:
                getDetectedLanguage(intent);
                break;
        }
    }

    private void getDetectedLanguage(Intent intent) {

        String input = intent.getStringExtra(INPUT);

        String detectedLanguage = Translator.getDetectedLanguage(input);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_TYPE, ResultType.DETECTION);

        bundle.putString(DETECTED_LANGUAGE, detectedLanguage);

        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void getWordInfo(Intent intent) {

        String input = intent.getStringExtra(INPUT);
        String result = intent.getStringExtra(RESULT);
        String sourceLang = intent.getStringExtra(SOURCE_LANGUAGE);
        String targetLang = intent.getStringExtra(TARGET_LANGUAGE);

        if(sourceLang.equals(Translator.getAutoDetectLang())) {
            sourceLang = Translator.getDetectedLanguage(input);
        }

        String sourceSound = Translator.getSound(input, sourceLang);

        if(result.equals(getString(R.string.txt_empty))) {
            result = Translator.getTranslation(input, sourceLang, targetLang);
        }

        String targetSound = Translator.getSound(result, targetLang);

        WordInfo info =
                new WordInfo(sourceLang, targetLang, input, result, sourceSound, targetSound);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_TYPE, ResultType.WORD_INFO);
        bundle.putParcelable(WORD_INFO, info);

        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void getLanguages(Intent intent) {
        ArrayList<String> langNames = Translator.getAllLanguages();
        String autoDetectLang = Translator.getAutoDetectLang();

        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_TYPE, ResultType.LANGUAGES);
        bundle.putStringArrayList(LANGUAGES, langNames);
        bundle.putString(AUTO_DETECT_LANGUAGE, autoDetectLang);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void getTranslation(Intent intent) {

        String input = intent.getStringExtra(INPUT);
        String sourceLang = intent.getStringExtra(SOURCE_LANGUAGE);
        String targetLang = intent.getStringExtra(TARGET_LANGUAGE);

        if(sourceLang.equals(Translator.getAutoDetectLang())) {
            sourceLang = Translator.getDetectedLanguage(input);
        }

        String result = Translator.getTranslation(input, sourceLang, targetLang);

        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_TYPE, ResultType.TRANSLATION);
        bundle.putString(TRANSLATION, result);
        bundle.putString(DETECTED_LANGUAGE, sourceLang);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        receiver.send(Activity.RESULT_OK, bundle);
    }

    private void getDefaultLanguages(Intent intent) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_TYPE, ResultType.DEFAULTS);

        bundle.putString(DEFAULT_SRC_LANG, Translator.getDefaultSourceLang());
        bundle.putString(DEFAULT_TRGT_LANG, Translator.getDefaultTargetLang());

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


    public static Bundle getDefaultLangsBundle(ResultReceiver receiver) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(INTENT_TYPE, IntentType.DEFAULTS);

        bundle.putParcelable(RESULT_RECEIVER, receiver);

        return bundle;
    }


    public static Bundle getDetailedTranslationBundle(
            String input, String result, String source, String target, ResultReceiver receiver) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(INTENT_TYPE, IntentType.DETAIL);

        bundle.putString(INPUT, input);
        bundle.putString(RESULT, result);

        bundle.putString(SOURCE_LANGUAGE, source);
        bundle.putString(TARGET_LANGUAGE, target);

        bundle.putParcelable(RESULT_RECEIVER, receiver);

        return bundle;
    }


    public static Bundle getAutoDetectBundle(String input, ResultReceiver receiver) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(INTENT_TYPE, IntentType.DETECTION);

        bundle.putString(INPUT, input);

        bundle.putParcelable(RESULT_RECEIVER, receiver);

        return bundle;
    }
}

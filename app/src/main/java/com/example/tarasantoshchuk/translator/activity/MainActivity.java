package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.service.TranslationService;
import com.example.tarasantoshchuk.translator.translation.Translator;
import com.example.tarasantoshchuk.translator.word.WordInfo;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String RESULT_ID = "ResultId";

    private static final String LANGUAGE_TYPE = "LanguageType";

    private static final String ACTIVITY_TYPE = "ActivityType";

    private static final String TRANSLATED_STRING = "TranslatedString";
    private static final String LANGUAGES = "Languages";
    private static final String LANGUAGE = "Language";
    private static final String WORD_INFO = "WordInfo";

    private static final int REQUEST_CODE = 0;

    private Receiver mReceiver;

    private TextView mTxtResult;

    private Button mBtnTranslate;
    private Button mBtnSwap;
    private Button mBtnDetailed;

    private EditText mEdtInput;

    private TextView mTxtSourceLang;
    private TextView mTxtTargetLang;

    private ArrayList<String> mLanguages;

    private enum ResultId {
        TRANSLATION, ALL_LANGUAGES, DETAILED
    }

    private enum ActivityResultId {
        SET_LANG
    }

    private enum LanguageType {
        TARGET, SOURCE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new Receiver(new Handler(Looper.getMainLooper()));

        Intent intent = new Intent(this, TranslationService.class);
        intent.putExtras(TranslationService.getLanguagesBundle(mReceiver));
        startService(intent);

        Translator.Init(getResources());

        mTxtResult = (TextView) findViewById(R.id.txtResult);

        mBtnTranslate = (Button) findViewById(R.id.btnTranslate);
        mBtnSwap = (Button) findViewById(R.id.btnSwap);
        mBtnDetailed = (Button) findViewById(R.id.btnDetailed);

        mEdtInput = (EditText) findViewById(R.id.edtInput);

        mTxtSourceLang = (TextView) findViewById(R.id.txtInfoSourceLang);
        mTxtTargetLang = (TextView) findViewById(R.id.txtTargetLang);

        mBtnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEdtInput.getText().toString();

                String sourceLang = mTxtSourceLang.getText().toString();
                String targetLang = mTxtTargetLang.getText().toString();

                Bundle bundle =
                        TranslationService.getTranslationBundle(input, sourceLang, targetLang, mReceiver);

                Intent intent = new Intent(MainActivity.this, TranslationService.class);

                intent.putExtras(bundle);

                startService(intent);
            }
        });

        mBtnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence buffer = mTxtSourceLang.getText();
                mTxtSourceLang.setText(mTxtTargetLang.getText());
                mTxtTargetLang.setText(buffer);
            }
        });

        mBtnDetailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEdtInput.getText().toString();

                String sourceLang = mTxtSourceLang.getText().toString();
                String targetLang = mTxtTargetLang.getText().toString();

                Bundle bundle =
                        TranslationService.getDetailedTranslationBundle(input, sourceLang,
                                targetLang, mReceiver);

                Intent intent = new Intent(MainActivity.this, TranslationService.class);

                intent.putExtras(bundle);

                startService(intent);
            }
        });

        mTxtSourceLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLanguages == null) {
                    Toast.makeText(MainActivity.this, getString(R.string.txt_wait_lang_init),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, SetLanguageActivity.class);
                intent.putExtras(SetLanguageActivity.getStartExtras(mLanguages));
                intent.putExtra(LANGUAGE_TYPE, LanguageType.SOURCE);

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mTxtTargetLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLanguages == null) {
                    Toast.makeText(MainActivity.this, getString(R.string.txt_wait_lang_init),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, SetLanguageActivity.class);

                intent.putExtras(SetLanguageActivity.getStartExtras(mLanguages));
                intent.putExtra(LANGUAGE_TYPE, LanguageType.TARGET);

                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK) {
            return;
        }

        switch((ActivityResultId)data.getSerializableExtra(ACTIVITY_TYPE)) {
            case SET_LANG:
                if(data.getSerializableExtra(LANGUAGE_TYPE) == LanguageType.SOURCE) {
                    mTxtSourceLang.setText(data.getStringExtra(LANGUAGE));
                } else {
                    mTxtTargetLang.setText(data.getStringExtra(LANGUAGE));
                }
                break;
        }
    }

    /**
     * set of methods to return appropriate bundles
     */
    public static Bundle getTranslationBundle(String result) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_ID, ResultId.TRANSLATION);
        bundle.putString(TRANSLATED_STRING, result);

        return bundle;
    }

    public static Bundle getLanguagesBundle(ArrayList<String> langNames) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_ID, ResultId.ALL_LANGUAGES);

        bundle.putStringArrayList(LANGUAGES, langNames);

        return bundle;
    }

    public static Bundle getDetailedTranslationBundle(WordInfo info) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(RESULT_ID, ResultId.DETAILED);

        bundle.putParcelable(WORD_INFO, info);

        return bundle;
    }

    public static Bundle getSetLanguageBundle(Intent intent, String language) {
        Bundle bundle = intent.getExtras();

        bundle.putString(LANGUAGE, language);
        bundle.putSerializable(ACTIVITY_TYPE, ActivityResultId.SET_LANG);

        return bundle;
    }

    /**
     * receiver to grab result from translationService
     */
    private class Receiver extends ResultReceiver {
        public Receiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode != RESULT_OK) {
                throw new RuntimeException();
            }
            switch((ResultId)resultData.getSerializable(RESULT_ID)) {
                case TRANSLATION:
                    mTxtResult.setText(resultData.getString(TRANSLATED_STRING));
                    break;
                case ALL_LANGUAGES:
                    mLanguages = resultData.getStringArrayList(LANGUAGES);
                    break;
                case DETAILED:
                    WordInfo info = resultData.getParcelable(WORD_INFO);

                    Intent intent = new Intent(MainActivity.this, WordInfoActivity.class);
                    intent.putExtras(WordInfoActivity.getStartExtras(info));
                    startActivity(intent);
                    break;
            }
        }
    }
}

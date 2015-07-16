package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.tarasantoshchuk.translator.word.TranslationHistory;
import com.example.tarasantoshchuk.translator.word.TranslationInfo;
import com.example.tarasantoshchuk.translator.word.WordInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String RESULT_ID = "ResultId";

    private static final String LANGUAGE_TYPE = "LanguageType";

    private static final String ACTIVITY_TYPE = "ActivityType";

    private static final String TRANSLATED_STRING = "TranslatedString";
    private static final String LANGUAGES = "Languages";
    private static final String LANGUAGE = "Language";
    private static final String WORD_INFO = "WordInfo";

    /**
     * TEMPORARY: will be changed after task 4 and additional task 3 is finished
     * (loading latest language combinations)
     */
    private static final String DEFAULT_SOURCE_LANG = "English";
    private static final String DEFAULT_TARGET_LANG = "Ukrainian";

    private static final int REQUEST_CODE = 0;

    private Receiver mReceiver;

    private TextView mTxtResult;

    private Button mBtnTranslate;
    private Button mBtnSwap;
    private Button mBtnDetailed;

    /**
     * TEMPORARY: button to check functionality
     * this button will be removed, feature will be available via NavigationDrawer
     */
    private Button mBtnHistory;

    private EditText mEdtInput;

    private TextView mTxtSourceLang;
    private TextView mTxtTargetLang;

    private ArrayList<String> mLanguages;

    private TranslationHistory mTranslations;

    private TranslationInfo mLastTranslation;

    private enum ResultId {
        TRANSLATION, ALL_LANGUAGES, DETAILED
    }

    private enum ActivityResultId {
        SET_LANG, DELETE_HISTORY
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

        getTranslationHistory();

        mTxtResult = (TextView) findViewById(R.id.txtResult);

        mBtnTranslate = (Button) findViewById(R.id.btnTranslate);
        mBtnSwap = (Button) findViewById(R.id.btnSwap);
        mBtnDetailed = (Button) findViewById(R.id.btnDetailed);

        mBtnHistory = (Button) findViewById(R.id.btnHistory);

        mEdtInput = (EditText) findViewById(R.id.edtInput);

        mTxtSourceLang = (TextView) findViewById(R.id.txtInfoSourceLang);
        mTxtTargetLang = (TextView) findViewById(R.id.txtTargetLang);

        /**
         * TEMPORARY: will be changed after task 4 and additional task 3 is finished
         * (loading latest language combinations)
         */
        mTxtSourceLang.setText(DEFAULT_SOURCE_LANG);
        mTxtTargetLang.setText(DEFAULT_TARGET_LANG);

        mBtnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEdtInput.getText().toString();

                String sourceLang = mTxtSourceLang.getText().toString();
                String targetLang = mTxtTargetLang.getText().toString();

                mLastTranslation = new TranslationInfo(input, sourceLang, targetLang);

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
                v.setEnabled(false);

                String input = mEdtInput.getText().toString();

                String sourceLang = mTxtSourceLang.getText().toString();
                String targetLang = mTxtTargetLang.getText().toString();

                mLastTranslation = new TranslationInfo(input, sourceLang, targetLang);

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

        mBtnHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TranslationHistoryActivity.class);

                intent.putExtras(TranslationHistoryActivity.getStartExtras(mTranslations));

                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void getTranslationHistory() {
        File history = new File(getFilesDir(), getString(R.string.file_history));

        if(history.exists()) {
            FileInputStream fileStream = null;
            ObjectInputStream stream = null;

            try {

                fileStream = new FileInputStream(history.getPath());
                stream = new ObjectInputStream(fileStream);

                mTranslations = (TranslationHistory)stream.readObject();

                return;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if(fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        mTranslations = new TranslationHistory();
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

            case DELETE_HISTORY:
                mTranslations.clear();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        File history = new File(getFilesDir(), getString(R.string.file_history));

        if(!history.exists()) {
            try {
                history.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        FileOutputStream fileStream = null;
        ObjectOutputStream stream = null;

        try {

            fileStream = new FileOutputStream(history.getPath());
            stream = new ObjectOutputStream(fileStream);

            stream.writeObject(mTranslations);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    public static Bundle getTranslationHistoryBundle() {
        Bundle bundle = new Bundle();

        bundle.putSerializable(ACTIVITY_TYPE, ActivityResultId.DELETE_HISTORY);

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
                    String result = resultData.getString(TRANSLATED_STRING);

                    mTxtResult.setText(result);
                    mLastTranslation.setmTargetWord(result);

                    mTranslations.add(mLastTranslation);

                    break;
                case ALL_LANGUAGES:
                    mLanguages = resultData.getStringArrayList(LANGUAGES);
                    break;
                case DETAILED:
                    WordInfo info = resultData.getParcelable(WORD_INFO);

                    mLastTranslation.setmTargetWord(info.getmTargetWord());

                    mTranslations.add(mLastTranslation);

                    Intent intent = new Intent(MainActivity.this, WordInfoActivity.class);
                    intent.putExtras(WordInfoActivity.getStartExtras(info));
                    startActivity(intent);

                    mBtnDetailed.setEnabled(true);

                    break;
            }
        }
    }
}

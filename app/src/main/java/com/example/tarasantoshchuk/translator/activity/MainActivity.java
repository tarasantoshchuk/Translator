package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.history.languages.LanguagesHistory;
import com.example.tarasantoshchuk.translator.history.languages.LanguagesInfo;
import com.example.tarasantoshchuk.translator.history.statistics.StatisticInfo;
import com.example.tarasantoshchuk.translator.navigation.NavigationDrawerAdapter;
import com.example.tarasantoshchuk.translator.service.TranslationService;
import com.example.tarasantoshchuk.translator.translation.Translator;
import com.example.tarasantoshchuk.translator.history.translations.TranslationHistory;
import com.example.tarasantoshchuk.translator.history.translations.TranslationInfo;
import com.example.tarasantoshchuk.translator.detailed.WordInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int REQUEST_SET_TARGET_LANG = 0;
    private static final int REQUEST_SET_SOURCE_LANG = 1;
    private static final int REQUEST_LANG_HISTORY = 2;
    private static final int REQUEST_TRANS_HISTORY = 3;
    private static final int REQUEST_STATS = 4;

    private InputMethodManager mMethodManager;

    private Receiver mReceiver;

    private DrawerLayout mDrawerLayout;

    private TextView mTxtResult;

    private Button mBtnTranslate;
    private Button mBtnSwap;
    private Button mBtnDetailed;

    private EditText mEdtInput;

    private TextView mTxtSourceLang;
    private TextView mTxtTargetLang;

    private ListView mLeftDrawer;

    private ArrayList<String> mLanguages;

    private TranslationHistory mTransHistory;
    private TranslationInfo mLastTranslation;

    private LanguagesHistory mLangHistory;

    private StatisticInfo mStats;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);

        mReceiver = new Receiver(new Handler(Looper.getMainLooper()));

        mMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mTxtResult = (TextView) findViewById(R.id.txtResult);

        mBtnTranslate = (Button) findViewById(R.id.btnTranslate);
        mBtnSwap = (Button) findViewById(R.id.btnSwap);
        mBtnDetailed = (Button) findViewById(R.id.btnDetailed);

        mEdtInput = (EditText) findViewById(R.id.edtInput);

        mTxtSourceLang = (TextView) findViewById(R.id.txtInfoSourceLang);
        mTxtTargetLang = (TextView) findViewById(R.id.txtTargetLang);

        mLeftDrawer = (ListView) findViewById(R.id.leftDrawer);

        Translator.Init(getResources());

        getTranslationHistory();
        getLanguagesHistory();
        getStats();

        getStartLanguages();

        getAllLanguages();

        setOnClickListeners();

        mEdtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTxtResult.setText(R.string.txt_empty);
            }
        });

        mLeftDrawer.setAdapter(new NavigationDrawerAdapter(this));

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                hideKeyboard();
            }
        });
    }


    private void getStartLanguages() {
        if(!mLangHistory.isEmpty()) {

            mTxtSourceLang.setText(mLangHistory.getLastSourceLang());
            mTxtTargetLang.setText(mLangHistory.getLastTargetLang());

        } else {

            Intent intent = new Intent(this, TranslationService.class);
            intent.putExtras(TranslationService.getDefaultLangsBundle(mReceiver));
            startService(intent);

        }
    }

    private void getAllLanguages() {
        Intent intent = new Intent(this, TranslationService.class);
        intent.putExtras(TranslationService.getLanguagesBundle(mReceiver));
        startService(intent);
    }

    private void setOnClickListeners() {
        mBtnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEdtInput.getText().toString();

                String sourceLang = mTxtSourceLang.getText().toString();
                String targetLang = mTxtTargetLang.getText().toString();

                mLastTranslation = new TranslationInfo(input, sourceLang, targetLang);

                mLangHistory.add(new LanguagesInfo(sourceLang, targetLang));

                Bundle bundle =
                        TranslationService.getTranslationBundle(input, sourceLang, targetLang, mReceiver);

                Intent intent = new Intent(MainActivity.this, TranslationService.class);

                intent.putExtras(bundle);

                startService(intent);

                hideKeyboard();
            }
        });

        mBtnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence buffer = mTxtSourceLang.getText();
                mTxtSourceLang.setText(mTxtTargetLang.getText());
                mTxtTargetLang.setText(buffer);

                /**
                 * if translation is on screen - just swap it with input
                 */
                if(!mTxtResult.getText().toString().equals(getString(R.string.txt_empty))) {
                    buffer = mEdtInput.getText();
                    mEdtInput.setText(mTxtResult.getText());
                    mTxtResult.setText(buffer);
                }
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

                mLangHistory.add(new LanguagesInfo(sourceLang, targetLang));

                Bundle bundle =
                        TranslationService.getDetailedTranslationBundle(input, sourceLang,
                                targetLang, mReceiver);

                Intent intent = new Intent(MainActivity.this, TranslationService.class);

                intent.putExtras(bundle);

                startService(intent);

                hideKeyboard();
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

                startActivityForResult(intent, REQUEST_SET_SOURCE_LANG);
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

                startActivityForResult(intent, REQUEST_SET_TARGET_LANG);
            }
        });
    }

    private void hideKeyboard() {
        if(getCurrentFocus()!=null) {
            mMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void startStatisticActivity() {
        mDrawerLayout.closeDrawers();

        Intent intent = new Intent(MainActivity.this, StaticticInfoActivity.class);

        intent.putExtras(StaticticInfoActivity.getStartExtras(mStats));

        startActivityForResult(intent, REQUEST_STATS);
    }

    public void startLanguageHistoryActivity() {
        mDrawerLayout.closeDrawers();

        Intent intent = new Intent(MainActivity.this, LanguagesHistoryActivity.class);

        intent.putExtras(LanguagesHistoryActivity.getStartExtras(mLangHistory));

        startActivityForResult(intent, REQUEST_LANG_HISTORY);
    }

    public void startTranslationHistoryActivity() {
        mDrawerLayout.closeDrawers();

        Intent intent = new Intent(MainActivity.this, TranslationHistoryActivity.class);

        intent.putExtras(TranslationHistoryActivity.getStartExtras(mTransHistory));

        startActivityForResult(intent, REQUEST_TRANS_HISTORY);
    }

    private void getTranslationHistory() {
        File history = new File(getFilesDir(), getString(R.string.file_trans_history));

        if(history.exists()) {
            FileInputStream fileStream = null;
            ObjectInputStream stream = null;

            try {

                fileStream = new FileInputStream(history.getPath());
                stream = new ObjectInputStream(fileStream);

                mTransHistory = (TranslationHistory)stream.readObject();

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

        mTransHistory = new TranslationHistory();
    }

    private void getLanguagesHistory() {
        File history = new File(getFilesDir(), getString(R.string.file_lang_history));

        if(history.exists()) {
            FileInputStream fileStream = null;
            ObjectInputStream stream = null;

            try {

                fileStream = new FileInputStream(history.getPath());
                stream = new ObjectInputStream(fileStream);

                mLangHistory = (LanguagesHistory)stream.readObject();

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

        mLangHistory = new LanguagesHistory();
    }

    private void getStats() {
        File stats = new File(getFilesDir(), getString(R.string.file_stats));

        if(stats.exists()) {
            FileInputStream fileStream = null;
            ObjectInputStream stream = null;

            try {

                fileStream = new FileInputStream(stats.getPath());
                stream = new ObjectInputStream(fileStream);

                mStats = (StatisticInfo)stream.readObject();

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

        mStats = new StatisticInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case REQUEST_SET_SOURCE_LANG:

                mTxtSourceLang.setText(data.getStringExtra(SetLanguageActivity.LANG_KEY));

                mTxtResult.setText(R.string.txt_empty);

                break;

            case REQUEST_SET_TARGET_LANG:

                mTxtTargetLang.setText(data.getStringExtra(SetLanguageActivity.LANG_KEY));

                mTxtResult.setText(R.string.txt_empty);

                break;

            case REQUEST_TRANS_HISTORY:

                mTransHistory.clear();

                break;

            case REQUEST_LANG_HISTORY:

                if(data.getBooleanExtra(LanguagesHistoryActivity.DELETE, false)) {
                    mLangHistory.clear();
                } else {
                    mTxtSourceLang.setText(
                            data.getStringExtra(LanguagesHistoryActivity.SOURCE_LANG));

                    mTxtTargetLang.setText(
                            data.getStringExtra(LanguagesHistoryActivity.TARGET_LANG));
                }

                break;

            case REQUEST_STATS:

                mStats = new StatisticInfo();

                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveTransHistory();
        saveLangHistory();
        saveStats();
    }

    private void saveTransHistory() {
        File history = new File(getFilesDir(), getString(R.string.file_trans_history));

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

            stream.writeObject(mTransHistory);

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

    private void saveLangHistory() {
        File history = new File(getFilesDir(), getString(R.string.file_lang_history));

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

            stream.writeObject(mLangHistory);

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

    private void saveStats() {
        File stats = new File(getFilesDir(), getString(R.string.file_stats));

        if(!stats.exists()) {
            try {
                stats.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        FileOutputStream fileStream = null;
        ObjectOutputStream stream = null;

        try {

            fileStream = new FileOutputStream(stats.getPath());
            stream = new ObjectOutputStream(fileStream);

            stream.writeObject(mStats);

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
            switch((TranslationService.ResultType)resultData
                    .getSerializable(TranslationService.RESULT_TYPE)) {
                case TRANSLATION:
                    String result = resultData.getString(TranslationService.TRANSLATION);

                    mTxtResult.setText(result);
                    mLastTranslation.setmTargetWord(result);

                    mTransHistory.add(mLastTranslation);
                    mStats.update(mLastTranslation);

                    break;
                case LANGUAGES:
                    mLanguages = resultData.getStringArrayList(TranslationService.LANGUAGES);

                    break;
                case WORD_INFO:
                    WordInfo info = resultData.getParcelable(TranslationService.WORD_INFO);

                    mLastTranslation.setmTargetWord(info.getmTargetWord());

                    mTransHistory.add(mLastTranslation);
                    mStats.update(mLastTranslation);

                    Intent intent = new Intent(MainActivity.this, WordInfoActivity.class);
                    intent.putExtras(WordInfoActivity.getStartExtras(info));
                    startActivity(intent);

                    mBtnDetailed.setEnabled(true);

                    break;
                case DEFAULTS:
                    String sourceLang = resultData.getString(TranslationService.DEFAULT_SRC_LANG);
                    String targetLang = resultData.getString(TranslationService.DEFAULT_TRGT_LANG);

                    mTxtSourceLang.setText(sourceLang);
                    mTxtTargetLang.setText(targetLang);

                    break;

            }
        }
    }
}

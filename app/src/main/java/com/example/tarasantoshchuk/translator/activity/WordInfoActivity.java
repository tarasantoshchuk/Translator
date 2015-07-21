package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.detailed.WordInfo;
import com.example.tarasantoshchuk.translator.history.languages.LanguagesInfo;
import com.example.tarasantoshchuk.translator.history.translations.TranslationInfo;
import com.example.tarasantoshchuk.translator.service.TranslationService;

import java.io.IOException;
import java.util.LinkedList;

public class WordInfoActivity extends Activity {
    private static final String INPUT = "Input";
    private static final String RESULT = "Result";
    private static final String SOURCE_LANGUAGE  = "SourceLang";
    private static final String TARGET_LANGUAGE = "TargetLang";
    private static final String AUTO_DETECT_LANGUAGE = "AutoDetectLanguage";

    public static final String TRANSLATION_INFO = "TranslationInfo";

    private Button mBtnListenSource;
    private Button mBtnListenTarget;

    private TextView mTxtInfoSourceLang;
    private TextView mTxtInfoTargetLang;

    private TextView mTxtSourceWord;
    private TextView mTxtTargetWord;

    TranslationInfo mTranslationInfo;

    private ResultReceiver mReceiver;

    public static Bundle getStartExtras(String input, String result, String sourceLang,
                                        String targetLang, String autoDetect) {
        Bundle bundle = new Bundle();

        bundle.putString(INPUT, input);
        bundle.putString(RESULT, result);

        bundle.putString(SOURCE_LANGUAGE, sourceLang);
        bundle.putString(TARGET_LANGUAGE, targetLang);

        bundle.putString(AUTO_DETECT_LANGUAGE, autoDetect);

        return bundle;
    }

    private LinkedList<AsyncTask<String, Void, Boolean>> mRunningTasks =
            new LinkedList<AsyncTask<String, Void, Boolean>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new WordInfoResultReceiver(new Handler(getMainLooper()));

        Intent startIntent = getIntent();

        String input = startIntent.getStringExtra(INPUT);
        String result = startIntent.getStringExtra(RESULT);

        String sourceLang = startIntent.getStringExtra(SOURCE_LANGUAGE);
        String targetLang = startIntent.getStringExtra(TARGET_LANGUAGE);
        String autoDetect = startIntent.getStringExtra(AUTO_DETECT_LANGUAGE);

        setContentView(R.layout.acitivty_word_info);

        mBtnListenSource = (Button) findViewById(R.id.btnListenSource);
        mBtnListenTarget = (Button) findViewById(R.id.btnListenTarget);

        mBtnListenSource.setEnabled(false);
        mBtnListenTarget.setEnabled(false);

        mTxtInfoSourceLang = (TextView) findViewById(R.id.txtInfoSourceLang);
        mTxtInfoTargetLang = (TextView) findViewById(R.id.txtInfoTargetLang);

        mTxtSourceWord = (TextView) findViewById(R.id.txtSourceWord);
        mTxtTargetWord = (TextView) findViewById(R.id.txtTargetWord);

        mTxtSourceWord.setText(input);

        mTxtInfoTargetLang.setText(targetLang);

        if(!sourceLang.equals(autoDetect)){
            mTxtInfoSourceLang.setText(sourceLang);
        }

        if(!result.equals(getString(R.string.txt_empty))) {
            mTxtTargetWord.setText(result);
        }

        Intent intent = new Intent(this, TranslationService.class);

        intent.putExtras(TranslationService.getDetailedTranslationBundle(input, result, sourceLang,
                targetLang, mReceiver));

        startService(intent);
    }

    private void setButton(Button button, final String soundUrl) {
        if(soundUrl != null) {

            button.setText(getString(R.string.btn_listen));
            button.setEnabled(true);

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

                        MediaPlayer mPlayer = new MediaPlayer();

                        @Override
                        protected Boolean doInBackground(String... params) {

                            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            try {

                                mPlayer.setDataSource(params[0]);
                                mPlayer.prepare();

                            } catch (IOException ie) {
                                return Boolean.FALSE;
                            }

                            return Boolean.TRUE;
                        }

                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            if (aBoolean) {
                                mPlayer.start();
                            } else {

                                Toast.makeText(WordInfoActivity.this,
                                        getString(R.string.sound_fail),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            mRunningTasks.remove(this);
                        }
                    };
                    mRunningTasks.add(task);

                    task.execute(soundUrl);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for(AsyncTask<String, Void, Boolean> task: mRunningTasks) {
            if(task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancel(true);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mTranslationInfo != null) {
                setResult(RESULT_OK, new Intent()
                        .putExtra(TRANSLATION_INFO, (Parcelable) mTranslationInfo));

                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WordInfoResultReceiver extends ResultReceiver {

        public WordInfoResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode != RESULT_OK) {
                throw new RuntimeException();
            }
            if(resultData.getSerializable(TranslationService.RESULT_TYPE) ==
                TranslationService.ResultType.WORD_INFO) {
                WordInfo info = resultData.getParcelable(TranslationService.WORD_INFO);

                mTxtInfoSourceLang.setText(info.getmSourceLang());
                mTxtTargetWord.setText(info.getmTargetWord());

                setButton(mBtnListenSource, info.getmSourceSoundUrl());
                setButton(mBtnListenTarget, info.getmTargetSoundUrl());

                mTranslationInfo = new TranslationInfo(info.getmSourceWord(),
                        info.getmSourceLang(), info.getmTargetWord(), info.getmTargetLang());
            }
        }
    }

}

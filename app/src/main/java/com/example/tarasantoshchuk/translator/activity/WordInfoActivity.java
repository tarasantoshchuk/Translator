package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.detailed.WordInfo;

import java.io.IOException;
import java.util.LinkedList;

public class WordInfoActivity extends Activity {
    private static final String WORD_INFO = "WordInfo";

    public static Bundle getStartExtras(WordInfo info) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(WORD_INFO, info);

        return bundle;
    }

    private LinkedList<AsyncTask<String, Void, Boolean>> mRunningTasks =
            new LinkedList<AsyncTask<String, Void, Boolean>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WordInfo info = getIntent().getParcelableExtra(WORD_INFO);

        setContentView(R.layout.acitivty_word_info);

        Button btnListenSource = (Button) findViewById(R.id.btnListenSource);
        Button btnListenTarget = (Button) findViewById(R.id.btnListenTarget);

        TextView txtInfoSourceLang = (TextView) findViewById(R.id.txtInfoSourceLang);
        TextView txtInfoTargetLang = (TextView) findViewById(R.id.txtInfoTargetLang);

        TextView txtSourceWord = (TextView) findViewById(R.id.txtSourceWord);
        TextView txtTargetWord = (TextView) findViewById(R.id.txtTargetWord);

        txtSourceWord.setText(info.getmSourceWord());
        txtTargetWord.setText(info.getmTargetWord());

        txtInfoSourceLang.setText(info.getmSourceLang());
        txtInfoTargetLang.setText(info.getmTargetLang());

        setButton(btnListenSource, info.getmSourceSoundUrl(), info.getmSourceWord());
        setButton(btnListenTarget, info.getmTargetSoundUrl(), info.getmTargetWord());

    }

    private void setButton(Button button, final String soundUrl,
                           String word) {
        if(!word.equals("") && soundUrl != null) {

            button.setEnabled(true);
            button.setVisibility(View.VISIBLE);

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
                            if(aBoolean) {
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

        } else {
            button.setEnabled(false);
            button.setVisibility(View.INVISIBLE);
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
}

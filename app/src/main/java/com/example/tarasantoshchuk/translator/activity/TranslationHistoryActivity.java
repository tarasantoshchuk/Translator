package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.history.translations.TranslationHistory;
import com.example.tarasantoshchuk.translator.history.translations.TranslationInfoAdapter;


public class TranslationHistoryActivity extends Activity {
    private static final String HISTORY = "History";

    private ListView mListHistory;
    private Button mBtnDeleteHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_translation_history);

        mBtnDeleteHistory = (Button) findViewById(R.id.btnDelTransHistory);

        mListHistory = (ListView) findViewById(R.id.listTransHistory);

        TranslationHistory history = getIntent().getParcelableExtra(HISTORY);

        final LayoutInflater inflater = getLayoutInflater();

        TranslationInfoAdapter adapter = new TranslationInfoAdapter(inflater, history);

        mListHistory.setAdapter(adapter);

        mBtnDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);

                finish();
            }
        });

        if(mListHistory.getAdapter().getCount() == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.txt_trans_history_empty)
                    .setCancelable(false)
                    .setNeutralButton(getString(R.string.txt_back),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });

            builder.create().show();
        }
    }

    public static Bundle getStartExtras(TranslationHistory history) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(HISTORY, history);

        return bundle;
    }
}

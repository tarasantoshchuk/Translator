package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
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
                Intent resultData = new Intent();

                resultData.putExtras(MainActivity.getDeleteTransHistoryBundle());

                setResult(RESULT_OK, resultData);

                finish();
            }
        });
    }

    public static Bundle getStartExtras(TranslationHistory history) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(HISTORY, history);

        return bundle;
    }
}

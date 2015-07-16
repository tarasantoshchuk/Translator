package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.history.languages.LanguagesHistory;
import com.example.tarasantoshchuk.translator.history.languages.LanguagesInfoAdapter;

import javax.xml.transform.Source;

public class LanguagesHistoryActivity extends Activity {
    public static final String DELETE = "Delete";
    public static final String SOURCE_LANG = "SourceLang";
    public static final String TARGET_LANG = "TargetLang";

    private static final String HISTORY = "History";


    private ListView mListHistory;
    private Button mBtnDeleteHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_languages_history);

        mBtnDeleteHistory = (Button) findViewById(R.id.btnDelLangHistory);

        mListHistory = (ListView) findViewById(R.id.listLangHistory);

        LanguagesHistory history = getIntent().getParcelableExtra(HISTORY);

        final LayoutInflater inflater = getLayoutInflater();

        LanguagesInfoAdapter adapter = new LanguagesInfoAdapter(inflater, history);

        mListHistory.setAdapter(adapter);

        mBtnDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultData = new Intent();

                resultData.putExtra(DELETE, true);

                setResult(RESULT_OK, resultData);

                finish();
            }
        });

        mListHistory.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                View currentView = parent.getChildAt(position);

                LanguagesInfoAdapter.LanguagesInfoViewHolder holder =
                        (LanguagesInfoAdapter.LanguagesInfoViewHolder)currentView.getTag();

                String sourceLang = holder.txtSourceLang.getText().toString();
                String targetLang = holder.txtTargetLang.getText().toString();

                Intent resultData = new Intent();

                resultData.putExtra(DELETE, false);

                resultData.putExtra(SOURCE_LANG, sourceLang);
                resultData.putExtra(TARGET_LANG, targetLang);

                setResult(RESULT_OK, resultData);

                finish();
            }
        });
    }

    public static Bundle getStartExtras(LanguagesHistory history) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(HISTORY, history);

        return bundle;
    }
}

package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.history.statistics.StatisticInfo;

public class StaticticInfoActivity extends Activity {
    private static final String STATS_INFO = "StatsInfo";

    private Button mBtnDeleteStats;

    private TextView mTxtFirstUsed;
    private TextView mTxtLastUsed;

    private TextView mTxtWordsTranslated;
    private TextView mTxtCharsTranslated;

    private TextView mTxtSrcLangsUsed;
    private TextView mTxtTrgtLangsUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistic_info);

        mBtnDeleteStats = (Button) findViewById(R.id.btnDeleteStats);

        mTxtFirstUsed = (TextView) findViewById(R.id.txtStatInfoFirstUsedVal);
        mTxtLastUsed = (TextView) findViewById(R.id.txtStatInfoLastUsedVal);

        mTxtWordsTranslated = (TextView) findViewById(R.id.txtStatInfoWordsTransVal);
        mTxtCharsTranslated = (TextView) findViewById(R.id.txtStatInfoCharsTransVal);

        mTxtSrcLangsUsed = (TextView) findViewById(R.id.txtStatInfoSrcLangsVal);
        mTxtTrgtLangsUsed = (TextView) findViewById(R.id.txtStatInfoTrgtLangVal);

        StatisticInfo info = getIntent().getParcelableExtra(STATS_INFO);

        mTxtFirstUsed.setText(info.getmFirstUsed());
        mTxtLastUsed.setText(info.getmLastUsed());

        mTxtWordsTranslated.setText(info.getmWordsTranslated());
        mTxtCharsTranslated.setText(info.getmCharsTranslated());

        mTxtSrcLangsUsed.setText(info.getmNumSourceLangs());
        mTxtTrgtLangsUsed.setText(info.getmNumTargetLangs());

        mBtnDeleteStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);

                finish();
            }
        });
    }

    public static Bundle getStartExtras(StatisticInfo info) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(STATS_INFO, info);

        return bundle;
    }
}

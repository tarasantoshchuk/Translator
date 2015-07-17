package com.example.tarasantoshchuk.translator.navigation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.activity.MainActivity;

public class NavigationDrawerAdapter extends ArrayAdapter<String> {
    private static final int TRANSLATION_HISTORY = 0;
    private static final int LANGUAGES_HISTORY = 1;
    private static final int STATISTIC = 2;


    private MainActivity mActivity;

    public NavigationDrawerAdapter(MainActivity activity) {
        super(activity.getApplicationContext(), android.R.layout.simple_list_item_1,
                activity.getResources().getStringArray(R.array.navigation_names));

        this.mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        switch(position) {
            case TRANSLATION_HISTORY:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.startTranslationHistoryActivity();
                    }
                });
                break;
            case LANGUAGES_HISTORY:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.startLanguageHistoryActivity();
                    }
                });
                break;
            case STATISTIC:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.startStatisticActivity();
                    }
                });
                break;
        }

        return view;
    }
}

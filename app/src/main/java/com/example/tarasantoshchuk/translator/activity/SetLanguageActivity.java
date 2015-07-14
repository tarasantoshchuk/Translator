package com.example.tarasantoshchuk.translator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tarasantoshchuk.translator.R;

import java.util.ArrayList;

public class SetLanguageActivity extends Activity {
    private static final String LANG_ARRAY = "LangArray";

    public static Bundle getStartExtras(ArrayList<String> langs) {
        Bundle bundle = new Bundle();

        bundle.putStringArrayList(LANG_ARRAY, langs);

        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_language);

        ArrayList<String> langs = getIntent().getStringArrayListExtra(LANG_ARRAY);

        ListView listLang = (ListView) findViewById(R.id.listLang);

        listLang.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, langs));

        listLang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String language = (String) parent.getItemAtPosition(position);

                Bundle bundle = MainActivity.getSetLanguageBundle(getIntent(), language);

                setResult(RESULT_OK, new Intent().putExtras(bundle));
                finish();
            }
        });
    }
}

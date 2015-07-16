package com.example.tarasantoshchuk.translator.history.languages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tarasantoshchuk.translator.R;
import com.example.tarasantoshchuk.translator.history.translations.TranslationInfo;

public class LanguagesInfoAdapter extends BaseAdapter {

    private LanguagesHistory mItems = new LanguagesHistory();
    private LayoutInflater mInflater;

    public LanguagesInfoAdapter(LayoutInflater inflater, LanguagesHistory items) {
        mItems = items;
        mInflater = inflater;
    }

    private LanguagesInfo getLanguagesInfo(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return getLanguagesInfo(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LanguagesInfo info = getLanguagesInfo(position);

        LanguagesInfoViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.languages_info, null);

            holder = new LanguagesInfoViewHolder();

            holder.txtSourceLang = (TextView) convertView.findViewById(R.id.txtLangInfoSrcLang);
            holder.txtTargetLang = (TextView) convertView.findViewById(R.id.txtLangInfoTrgtLang);

            convertView.setTag(holder);
        } else {
            holder = (LanguagesInfoViewHolder) convertView.getTag();
        }

        holder.txtSourceLang.setText(info.getmSourceLanguage());
        holder.txtTargetLang.setText(info.getmTargetLanguage());

        return convertView;
    }

    public static class LanguagesInfoViewHolder {
        public TextView txtSourceLang;
        public TextView txtTargetLang;
    }
}

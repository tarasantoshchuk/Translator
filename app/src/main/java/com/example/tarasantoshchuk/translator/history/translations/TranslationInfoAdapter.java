package com.example.tarasantoshchuk.translator.history.translations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tarasantoshchuk.translator.R;


public class TranslationInfoAdapter extends BaseAdapter {

    private TranslationHistory mItems= new TranslationHistory();
    private LayoutInflater mInflater;

    public TranslationInfoAdapter(LayoutInflater mInflater, TranslationHistory mItems) {
        this.mInflater = mInflater;
        this.mItems = mItems;
    }

    private TranslationInfo getTranslationInfo(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return getTranslationInfo(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TranslationInfo info = getTranslationInfo(position);

        TranslationInfoViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.translation_info, null);

            holder = new TranslationInfoViewHolder();

            holder.txtSourceLang = (TextView) convertView.findViewById(R.id.txtTransInfoSrcLang);
            holder.txtTargetLang = (TextView) convertView.findViewById(R.id.txtTransInfoTrgtLang);

            holder.txtSourceWord = (TextView) convertView.findViewById(R.id.txtTransInfoSrcWord);
            holder.txtTargetWord = (TextView) convertView.findViewById(R.id.txtTransInfoTrgtWord);

            convertView.setTag(holder);
        } else {
            holder = (TranslationInfoViewHolder) convertView.getTag();
        }

        holder.txtSourceLang.setText(info.getmSourceLang());
        holder.txtTargetLang.setText(info.getmTargetLang());

        holder.txtSourceWord.setText(info.getmSourceWord());
        holder.txtTargetWord.setText(info.getmTargetWord());

        return convertView;
    }

    private static class TranslationInfoViewHolder {
        public TextView txtSourceLang;
        public TextView txtTargetLang;

        public TextView txtSourceWord;
        public TextView txtTargetWord;
    }
}

package com.example.tarasantoshchuk.translator.word;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.LinkedList;

public class TranslationHistory implements Serializable, Parcelable{
    private static final int MAX_SIZE = 50;

    private LinkedList<TranslationInfo> list;

    public TranslationHistory() {
        list = new LinkedList<TranslationInfo>();
    }

    private TranslationHistory(Parcel source) {
        this();

        int size = source.readInt();

        for(int i = 0; i < size; i++) {
            list.addLast(
                    (TranslationInfo)source.readParcelable(TranslationInfo.class.getClassLoader()));
        }
    }

    public void add(TranslationInfo tInfo) {
        /**
         * check for duplicates
         */
        for(TranslationInfo info: list) {
            if(info.equals(tInfo)) {
                return;
            }
        }

        /**
         * remove oldest translation info, if we exceed max size of list
         */
        if(list.size() == MAX_SIZE) {
            list.removeLast();
        }

        list.addFirst(tInfo);
    }

    public void clear() {
        list.clear();
    }

    public TranslationInfo get(int position) {
        return list.get(position);
    }

    public int size() {
        return list.size();
    }

    public static final Parcelable.Creator<TranslationHistory> CREATOR =
            new Parcelable.Creator<TranslationHistory>() {

                @Override
                public TranslationHistory createFromParcel(Parcel source) {
                    return new TranslationHistory(source);
                }

                @Override
                public TranslationHistory[] newArray(int size) {
                    return new TranslationHistory[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(list.size());

        for(TranslationInfo tInfo: list) {
            dest.writeParcelable(tInfo, 0);
        }
    }
}

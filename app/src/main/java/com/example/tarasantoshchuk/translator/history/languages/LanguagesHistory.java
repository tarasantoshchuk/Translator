package com.example.tarasantoshchuk.translator.history.languages;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.LinkedList;

public class LanguagesHistory implements Serializable, Parcelable {
    private static final int MAX_SIZE = 20;

    private LinkedList<LanguagesInfo> list;

    public LanguagesHistory() {
        this.list = new LinkedList<LanguagesInfo>();
    }

    public LanguagesHistory(Parcel source) {
        this();

        int size = source.readInt();

        for(int i = 0; i < size; i++) {
            list.addLast(
                    (LanguagesInfo)source.readParcelable(LanguagesInfo.class.getClassLoader()));
        }

    }

    public void add(LanguagesInfo lInfo) {
        /**
         * check for duplicates
         */
        for(LanguagesInfo info: list) {
            if(lInfo.equals(info)) {
                list.remove(info);
                break;
            }
        }

        /**
         * remove oldest languages info, if we exceed max size of list
         */
        if(list.size() == MAX_SIZE) {
            list.removeLast();
        }

        list.addFirst(lInfo);
    }

    public void clear() {
        list.clear();
    }

    public LanguagesInfo get(int position) {
        return list.get(position);
    }

    public int size() {
        return list.size();
    }

    public static final Parcelable.Creator<LanguagesHistory> CREATOR =
            new Parcelable.Creator<LanguagesHistory>() {

                @Override
                public LanguagesHistory createFromParcel(Parcel source) {
                    return new LanguagesHistory(source);
                }

                @Override
                public LanguagesHistory[] newArray(int size) {
                    return new LanguagesHistory[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(list.size());

        for(LanguagesInfo lInfo: list) {
            dest.writeParcelable(lInfo, 0);
        }
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public String getLastSourceLang() {
        return list.getFirst().getmSourceLanguage();
    }

    public String getLastTargetLang() {
        return list.getFirst().getmTargetLanguage();
    }
}

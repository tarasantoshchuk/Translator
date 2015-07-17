package com.example.tarasantoshchuk.translator.history.statistics;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tarasantoshchuk.translator.history.translations.TranslationInfo;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;

public class StatisticInfo implements Serializable, Parcelable {
    private Date mFirstUsed;
    private Date mLastUsed;

    private int mWordsTranslated;
    private int mCharsTranslated;

    private int mNumSourceLangs;
    private int mNumTargetLangs;

    private HashSet<String> mSourceLangs;
    private HashSet<String> mTargetLangs;

    public StatisticInfo() {
        mFirstUsed = new Date(System.currentTimeMillis());
        mLastUsed = new Date(System.currentTimeMillis());;

        mWordsTranslated = 0;
        mCharsTranslated = 0;

        mNumSourceLangs = 0;
        mNumTargetLangs = 0;

        mSourceLangs = new HashSet<String>();
        mTargetLangs = new HashSet<String>();
    }

    public void update(TranslationInfo info) {
        mLastUsed = new Date(System.currentTimeMillis());

        ++mWordsTranslated;
        mCharsTranslated += info.getmSourceWord().length();

        boolean isNewSourceLang = mSourceLangs.add(info.getmSourceLang());

        if(isNewSourceLang) {
            ++mNumSourceLangs;
        }

        boolean isNewTargetLang = mTargetLangs.add(info.getmTargetLang());

        if(isNewTargetLang) {
            ++mNumTargetLangs;
        }

    }

    public static final Parcelable.Creator<StatisticInfo> CREATOR =
            new Parcelable.Creator<StatisticInfo>(){

                @Override
                public StatisticInfo createFromParcel(Parcel source) {
                    return new StatisticInfo(source);
                }

                @Override
                public StatisticInfo[] newArray(int size) {
                    return new StatisticInfo[size];
                }
            };

    public StatisticInfo(Parcel source) {
        this();

        mFirstUsed.setTime(source.readLong());
        mLastUsed.setTime(source.readLong());

        mWordsTranslated = source.readInt();
        mCharsTranslated = source.readInt();

        mNumSourceLangs = source.readInt();
        mNumTargetLangs = source.readInt();

        HashSet<String> mSourceLangs = (HashSet<String>)source.readSerializable();
        HashSet<String> mTargetLangs = (HashSet<String>)source.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mFirstUsed.getTime());
        dest.writeLong(mLastUsed.getTime());

        dest.writeInt(mWordsTranslated);
        dest.writeInt(mCharsTranslated);

        dest.writeInt(mNumSourceLangs);
        dest.writeInt(mNumTargetLangs);

        dest.writeSerializable(mSourceLangs);
        dest.writeSerializable(mTargetLangs);
    }

    public String getmFirstUsed() {
        return DateFormat.getDateTimeInstance().format(mFirstUsed);
    }

    public String getmLastUsed() {
        return DateFormat.getDateTimeInstance().format(mLastUsed);
    }

    public String getmWordsTranslated() {
        return Integer.toString(mWordsTranslated);
    }

    public String getmCharsTranslated() {
        return Integer.toString(mCharsTranslated);
    }

    public String getmNumSourceLangs() {
        return Integer.toString(mNumSourceLangs);
    }

    public String getmNumTargetLangs() {
        return Integer.toString(mNumTargetLangs);
    }
}

package com.example.tarasantoshchuk.translator.history.translations;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TranslationInfo implements Parcelable, Serializable {
    private String mSourceWord;
    private String mTargetWord;

    private String mSourceLang;
    private String mTargetLang;

    public TranslationInfo(String mSourceWord, String mSourceLang,
                           String mTargetLang) {
        this.mSourceWord = mSourceWord;
        this.mSourceLang = mSourceLang;
        this.mTargetLang = mTargetLang;
    }

    public TranslationInfo(String mSourceWord, String mSourceLang, String mTargetWord,
                           String mTargetLang) {
        this(mSourceWord, mSourceLang, mTargetLang);
        this.mTargetWord = mTargetWord;
    }

    public TranslationInfo(Parcel source) {
        mSourceWord = source.readString();
        mTargetWord = source.readString();

        mSourceLang = source.readString();
        mTargetLang = source.readString();
    }

    public String getmTargetWord() {
        return mTargetWord;
    }

    public String getmSourceWord() {
        return mSourceWord;
    }

    public String getmTargetLang() {
        return mTargetLang;
    }

    public void setmTargetWord(String mTargetWord) {
        this.mTargetWord = mTargetWord;
    }

    public String getmSourceLang() {
        return mSourceLang;
    }

    public void setmSourceLang(String mSourceLang) {
        this.mSourceLang = mSourceLang;
    }

    public static final Parcelable.Creator<TranslationInfo> CREATOR =
            new Parcelable.Creator<TranslationInfo>(){

                @Override
                public TranslationInfo createFromParcel(Parcel source) {
                    return new TranslationInfo(source);
                }

                @Override
                public TranslationInfo[] newArray(int size) {
                    return new TranslationInfo[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSourceWord);
        dest.writeString(mTargetWord);

        dest.writeString(mSourceLang);
        dest.writeString(mTargetLang);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TranslationInfo) {

            TranslationInfo other = (TranslationInfo) o;

            if((other.mSourceWord.equals(this.mSourceWord)) &&
                    other.mSourceLang.equals(this.mSourceLang) &&
                    other.mTargetLang.equals(this.mTargetLang) &&
                    other.mTargetWord.equals(this.mTargetWord)) {

                return true;

            }
        }
        return false;
    }
}

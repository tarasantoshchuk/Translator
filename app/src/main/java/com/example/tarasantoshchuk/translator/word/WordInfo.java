package com.example.tarasantoshchuk.translator.word;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class WordInfo implements Parcelable {
    private String mSourceLang;
    private String mTargetLang;

    public String getmSourceLang() {
        return mSourceLang;
    }

    public String getmTargetLang() {
        return mTargetLang;
    }

    public String getmSourceWord() {
        return mSourceWord;
    }

    public String getmTargetWord() {
        return mTargetWord;
    }

    public String getmSourceSoundUrl() {
        return mSourceSoundUrl;
    }

    public String getmTargetSoundUrl() {
        return mTargetSoundUrl;
    }

    private String mSourceWord;
    private String mTargetWord;
    private String mSourceSoundUrl;
    private String mTargetSoundUrl;

    public static final Parcelable.Creator<WordInfo> CREATOR =
            new Parcelable.Creator<WordInfo>() {

                @Override
                public WordInfo createFromParcel(Parcel source) {
                    return new WordInfo(source);
                }

                @Override
                public WordInfo[] newArray(int size) {
                    return new WordInfo[size];
                }
            };

    private WordInfo(Parcel source) {

        this.mSourceLang = source.readString();
        this.mTargetLang = source.readString();

        this.mSourceWord = source.readString();
        this.mTargetWord = source.readString();

        this.mSourceSoundUrl = source.readString();
        this.mTargetSoundUrl = source.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSourceLang);
        dest.writeString(mTargetLang);

        dest.writeString(mSourceWord);
        dest.writeString(mTargetWord);

        dest.writeString(mSourceSoundUrl);
        dest.writeString(mTargetSoundUrl);
    }

    public WordInfo(String mSourceLang, String mTargetLang, String mSourceWord, String mTargetWord,
                    String mSourceSoundUrl, String mTargetSoundUrl) {

        this.mSourceLang = mSourceLang;
        this.mTargetLang = mTargetLang;

        this.mSourceWord = mSourceWord;
        this.mTargetWord = mTargetWord;

        this.mSourceSoundUrl = mSourceSoundUrl;
        this.mTargetSoundUrl = mTargetSoundUrl;
    }

}

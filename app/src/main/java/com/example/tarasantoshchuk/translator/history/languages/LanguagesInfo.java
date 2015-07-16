package com.example.tarasantoshchuk.translator.history.languages;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class LanguagesInfo implements Parcelable, Serializable {
    private String mSourceLanguage;
    private String mTargetLanguage;

    public String getmSourceLanguage() {
        return mSourceLanguage;
    }

    public String getmTargetLanguage() {
        return mTargetLanguage;
    }

    public LanguagesInfo(String mSourceLanguage, String mTargetLanguage) {
        this.mSourceLanguage = mSourceLanguage;
        this.mTargetLanguage = mTargetLanguage;
    }

    public static final Parcelable.Creator<LanguagesInfo> CREATOR =
            new Parcelable.Creator<LanguagesInfo>() {

                @Override
                public LanguagesInfo createFromParcel(Parcel source) {
                    return new LanguagesInfo(source);
                }

                @Override
                public LanguagesInfo[] newArray(int size) {
                    return new LanguagesInfo[size];
                }
            };

    public LanguagesInfo(Parcel source) {

        mSourceLanguage = source.readString();
        mTargetLanguage = source.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mSourceLanguage);
        dest.writeString(mTargetLanguage);

    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof LanguagesInfo) {

            LanguagesInfo other = (LanguagesInfo) o;

            if(other.mSourceLanguage.equals(this.mSourceLanguage) &&
                    other.mTargetLanguage.equals(this.mTargetLanguage)) {

                return true;

            }
        }
        return false;
    }
}

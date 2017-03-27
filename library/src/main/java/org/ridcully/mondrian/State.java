package org.ridcully.mondrian;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ridcully on 27.03.2017.
 */

public class State implements Parcelable {

    public String mTag;
    // key = container's id, String = class name of view
    private Map<Integer, String> mViewClasses;

    public State(String tag) {
        this(tag, new HashMap<Integer, String>());
    }

    public State(String tag, Map<Integer, String> viewClasses) {
        mTag = tag;
        mViewClasses = viewClasses;
    }

    public String getTag() {
        return mTag;
    }

    Map<Integer, String> getViewClasses() {
        return mViewClasses;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTag);
        dest.writeInt(this.mViewClasses.size());
        for (Map.Entry<Integer, String> entry : this.mViewClasses.entrySet()) {
            dest.writeValue(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    protected State(Parcel in) {
        this.mTag = in.readString();
        int mViewClassesSize = in.readInt();
        this.mViewClasses = new HashMap<Integer, String>(mViewClassesSize);
        for (int i = 0; i < mViewClassesSize; i++) {
            Integer key = (Integer) in.readValue(Integer.class.getClassLoader());
            String value = in.readString();
            this.mViewClasses.put(key, value);
        }
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel source) {
            return new State(source);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };
}

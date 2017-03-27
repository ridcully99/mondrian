package org.ridcully.mondrian;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ridcully on 26.03.2017.
 */

public class SegmentContainer extends FrameLayout {

    public SegmentContainer(@NonNull Context context) {
        super(context);
    }

    public SegmentContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SegmentContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    public void saveToBundle(Bundle bundle, String tag) {
        ArrayList<String> classNames = new ArrayList<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            classNames.add(child.getClass().getCanonicalName());
        }
        bundle.putStringArrayList(tag, classNames);
    }

    public void rebuildFromBundle(Bundle bundle, String tag) {
        if (bundle != null && tag != null) {
            List<String> childViewClassNames = bundle.getStringArrayList(tag);
            for (String className : childViewClassNames) {
                try {
                    Class<?> clazz = getClass().getClassLoader().loadClass(className);
                    Constructor constructor = clazz.getConstructor(Context.class);
                    View childView = (View)constructor.newInstance(getContext());
                    addView(childView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

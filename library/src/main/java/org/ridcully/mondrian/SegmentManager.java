package org.ridcully.mondrian;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ridcully on 27.03.2017.
 */

public class SegmentManager {

    private final static String TAG = SegmentManager.class.getSimpleName();
    private final static String CONTAINERS_KEY = "mondrian.segmentmanager.containers";

    private SegmentActivity mSegmentActivity;
    private Set<Integer> mManagedContainerIds = new HashSet<>();

    SegmentManager(SegmentActivity segmentActivity) {
        mSegmentActivity = segmentActivity;
    }

    void onSaveInstanceState(Bundle outState) {
        Bundle containers = new Bundle();
        for (int containerId : mManagedContainerIds) {
            ArrayList<String> segmentClassNames = collectSegmentClassNames(containerId);
            containers.putStringArrayList(Integer.toString(containerId), segmentClassNames);
        }
        outState.putBundle(CONTAINERS_KEY, containers);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        Bundle containers = savedInstanceState.getBundle(CONTAINERS_KEY);
        mManagedContainerIds.clear();
        if (containers == null) return;
        for (String key : containers.keySet()) {
            ArrayList<String> segmentClassNames = containers.getStringArrayList(key);
            int containerId = Integer.parseInt(key);
            rebuildSegments(containerId, segmentClassNames);
        }
    }

    /**
     * Adds given segment to the ViewGroup identified by containerId.
     *
     * @param containerId
     * @param segment
     * @param tag An optional tag that can be used to identify the segment for further usage.
     *            For example it can be used to ccordinate state of different containers, etc.
     */
    public void push(@IdRes int containerId, Segment segment, String tag) {
        ViewGroup container = findContainer(containerId);
        segment.setTag(R.id.mondrian_segmentmanager_usertag, tag);
        container.addView(segment);
    }

    /**
     * Removes top-most Segment from the ViewGroup identified by containerId.
     *
     * @param containerId
     * @return true if a segment was removed, false if not
     */
    public boolean pop(@IdRes int containerId) {
        boolean popped = false;
        ViewGroup container = findContainer(containerId);
        if (container.getChildCount() > 0) {
            container.removeViewAt(container.getChildCount() - 1);
            popped = true;
        }
        return popped;
    }

    /**
     * Pops Segments from ViewGroup identified by containerId until Segment with given tag is found.
     * The Segment having the given tag is not popped.
     *
     * @param containerId
     * @param tag
     * @return true if any segments where popped, false if not
     */
    public boolean popTo(@IdRes int containerId, String tag) {
        boolean popped = false;
        ViewGroup container = findContainer(containerId);
        for (int pos = container.getChildCount() - 1; pos >= 0; pos--) {
            String segmentTag = (String) container.getChildAt(pos).getTag(R.id.mondrian_segmentmanager_usertag);
            if (areEqual(tag, segmentTag)) break;
            container.removeViewAt(pos);
            popped = true;
        }
        return popped;
    }

    /**
     * Pops all Segments from ViewGroup identified by containerId
     * @param containerId
     * @return true if any segments where popped, false if not
     */
    public boolean popAll(@IdRes int containerId) {
        boolean popped = false;
        ViewGroup container = findContainer(containerId);
        for (int pos = container.getChildCount() - 1; pos >= 0; pos--) {
            container.removeViewAt(pos);
            popped = true;
        }
        return popped;
    }

    public boolean clear(@IdRes int containerId) {
        return popAll(containerId);
    }

    /**
     * Returns top-most Segment of ViewGroup identified by given container, without removing it.
     * @param containerId
     * @return Topmost segment or null if ViewGroup had no children
     */
    public Segment peek(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        if (container.getChildCount() > 0) {
            return (Segment)container.getChildAt(container.getChildCount() - 1);
        }
        return null;
    }

    public boolean isEmpty(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        return container.getChildCount() == 0;
    }

    /**
     * Collects class names of all views in ViewGroup identified by given containerId.
     *
     * @param containerId
     * @return list of class names in same order as they are placed in the view group
     */
    private ArrayList<String> collectSegmentClassNames(int containerId) {
        ViewGroup container = findContainer(containerId);
        ArrayList<String> result = new ArrayList<>();
        for (int pos = 0; pos < container.getChildCount(); pos++) {
            result.add(container.getChildAt(pos).getClass().getCanonicalName());
        }
        return result;
    }

    private void rebuildSegments(int containerId, ArrayList<String> segmentClassNames) {
        ViewGroup container = findContainer(containerId);
        for (String className : segmentClassNames) {
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(className);
                Constructor constructor = clazz.getConstructor(Context.class);
                View childView = (View) constructor.newInstance(mSegmentActivity);
                container.addView(childView);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private ViewGroup findContainer(@IdRes int containerId) {
        View view = mSegmentActivity.findViewById(containerId);
        if (view == null || !(view instanceof ViewGroup)) {
            throw new IllegalArgumentException("containerId " + containerId + " must identify a ViewGroup in current contentView");
        }
        mManagedContainerIds.add(containerId);
        return (ViewGroup)view;
    }

    /**
     * Tests if a and b are equal in a null-safe way.
     * @param a
     * @param b
     * @return
     */
    private boolean areEqual(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}

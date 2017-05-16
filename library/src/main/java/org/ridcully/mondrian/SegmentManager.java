package org.ridcully.mondrian;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ridcully on 27.03.2017.
 */

public class SegmentManager {

    private final static String TAG = SegmentManager.class.getSimpleName();
    private final static String SEGMENTS_KEY = "mondrian.segmentmanager.segments";

    private SegmentActivity mSegmentActivity;
    private Set<Integer> mManagedContainerIds = new HashSet<>();

    SegmentManager(SegmentActivity segmentActivity) {
        mSegmentActivity = segmentActivity;
    }

    void onSaveInstanceState(Bundle outState) {
        ArrayList<Bundle> segmentInfos = new ArrayList<>();
        for (int containerId : mManagedContainerIds) {
            for (Segment segment : getSegments(containerId)) {
                Bundle segmentInfo = new Bundle();
                segmentInfo.putInt("containerId", containerId);
                segmentInfo.putString("className", segment.getClass().getCanonicalName());
                segmentInfo.putBundle("arguments", segment.getArguments());
                segmentInfos.add(segmentInfo);
            }
        }
        outState.putParcelableArrayList(SEGMENTS_KEY, segmentInfos);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        mManagedContainerIds.clear();
        ArrayList<Bundle> segmentInfos = savedInstanceState.getParcelableArrayList(SEGMENTS_KEY);
        if (segmentInfos == null) return;
        // clear all containers in savedInstanceState first, to avoid duplication of segments
        for (Bundle segmentInfo : segmentInfos) {
            int containerId = segmentInfo.getInt("containerId");
            // clear() adds container to managed containers, so we can check if containerId
            // is already managed to avoid calling clear multiple times for same container.
            if (!mManagedContainerIds.contains(containerId)) {
                clear(containerId);
            }
        }
        for (Bundle segmentInfo : segmentInfos) {
            rebuildSegment(segmentInfo.getInt("containerId"),
                    segmentInfo.getString("className"),
                    segmentInfo.getBundle("arguments"));
        }
    }

    /**
     * Builds an argument bundle from given nameValuePairs.
     * You should statically import this helper method into where you create Segments with arguments.
     *
     * @param nameValuePairs
     * @return Bundle that can be passed to constructors
     */
    public static Bundle bundle(Object... nameValuePairs) {
        BundleBuilder builder = BundleBuilder.instance();
        for (int i = 0; i < nameValuePairs.length - 1; i += 2) {
            String key = (String)nameValuePairs[i];
            Object value = nameValuePairs[i+1];
            builder.setArg(key, value);
        }
        return builder.build();
    }

    /**
     * Adds given segment to the ViewGroup identified by containerId.
     *
     * @param containerId
     * @param segment
     * @return SegmentManager for concatenating further operations
     */
    public SegmentManager push(@IdRes int containerId, Segment segment) {
        push(containerId, segment, null);
        return this;
    }

    /**
     * Adds given segment to the ViewGroup identified by containerId.
     *
     * @param containerId
     * @param segment
     * @param marker An optional marker that can be used to identify the segment for further usage.
     *            For example it can be used to coordinate state of different containers, etc.
     * @return SegmentManager for concatenating further operations
     */
    public SegmentManager push(@IdRes int containerId, Segment segment, String marker) {
        ViewGroup container = findContainer(containerId);
        segment.setMarker(marker);
        container.addView(segment);
        return this;
    }

    /**
     * Removes top-most Segment from the ViewGroup identified by containerId.
     *
     * @param containerId
     * @return SegmentManager for concatinating further operations
     */
    public SegmentManager pop(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        if (container.getChildCount() > 0) {
            container.removeViewAt(container.getChildCount() - 1);
        }
        return this;
    }

    /**
     * Pops Segments from ViewGroup identified by containerId until Segment with given marker is found.
     * The Segment having the given marker is not popped.
     *
     * @param containerId
     * @param marker
     * @return SegmentManager for concatenating further operations
     */
    public SegmentManager popToMarker(@IdRes int containerId, String marker) {
        ViewGroup container = findContainer(containerId);
        for (int pos = container.getChildCount() - 1; pos >= 0; pos--) {
            View view = container.getChildAt(pos);
            if (view instanceof Segment && stringsEqual(marker, ((Segment)view).getMarker())) {
                break;
            }
            container.removeViewAt(pos);
        }
        return this;
    }

    /**
     * Pops all Segments from ViewGroup identified by containerId.
     *
     * @param containerId
     * @return SegmentManager for concatinating further operations
     */
    public SegmentManager popAll(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        for (int pos = container.getChildCount() - 1; pos >= 0; pos--) {
            container.removeViewAt(pos);
        }
        return this;
    }

    /**
     * Removes all segments from ViewGroup identified by containerId.
     * This is a synonym for {@link #popAll(int)}.
     *
     * @param containerId
     * @return SegmentManager for concatenating further operations
     */
    public SegmentManager clear(@IdRes int containerId) {
        return popAll(containerId);
    }

    /**
     * Sets given segment in ViewGroup identified by given containerId, removing all other segments
     * before. This is the same as concatinating clear(containerId) and push(containerId, segment).
     *
     * @param containerId
     * @param segment
     * @return SegmentManager for concatinating further operations
     */
    public SegmentManager set(@IdRes int containerId, Segment segment) {
        return set(containerId, segment, null);
    }

    /**
     * Sets given segment in ViewGroup identified by given containerId, removing all other segments
     * before. This is the same as concatinating clear(containerId) and push(containerId, segment).
     *
     * @param containerId
     * @param segment
     * @param marker
     * @return SegmentManager for concatenating further operations
     */
    public SegmentManager set(@IdRes int containerId, Segment segment, String marker) {
        clear(containerId);
        push(containerId, segment, marker);
        return this;
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
     * Invokes onBackPressed() of top-most segment of every specified container (in specified order).
     * If it returns false, it gets popped from its container.
     *
     * @param containerIds
     * @return true if any segments where popped
     */
    public boolean onBackPressed(@IdRes int... containerIds) {
        boolean handled = false;
        if (containerIds != null) {
            for (int id : containerIds) {
                Segment segment = peek(id);
                if (segment != null && !segment.onBackPressed()) {
                    handled = true;
                    pop(id);
                }
            }
        }
        return handled;
    }


    // ---------------------------------------------------------------------------- Internal methods


    /**
     * Finds all Segments attached to window of mSegmentActivity.
     *
     * @param collectSubSegments
     * @return
     */
    List<Segment> getAttachedSegments(boolean collectSubSegments) {
        List<Segment> result = new ArrayList<>();
        View root = mSegmentActivity.getWindow().getDecorView().getRootView();
        if (root != null && root instanceof ViewGroup) {
            if (root instanceof Segment && ((Segment)root).isAttachedToWindow()) {
                result.add((Segment) root);
            }
            if (collectSubSegments || !(root instanceof Segment)) {
                collectAttachedSegments((ViewGroup)root, collectSubSegments, result);
            }
        }
        return result;
    }

    /**
     * Helper method for getAttachedSegments.
     *
     * @param parent
     * @param collectSubSegments
     * @param result
     */
    private void collectAttachedSegments(@NonNull ViewGroup parent,
                                         boolean collectSubSegments,
                                         @NonNull List<Segment> result) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof Segment && ((Segment) child).isAttachedToWindow()) {
                    result.add((Segment) child);
                }
                if (collectSubSegments || !(child instanceof Segment)) {
                    collectAttachedSegments((ViewGroup) child, collectSubSegments, result);
                }
            }
        }
    }

    /**
     * Gets segments in given container.
     *
     * @param containerId
     * @return
     */
    private List<Segment> getSegments(int containerId) {
        ViewGroup container = findContainer(containerId);
        ArrayList<Segment> segments = new ArrayList<>();
        for (int pos = 0; pos < container.getChildCount(); pos++) {
            segments.add((Segment)container.getChildAt(pos));
        }
        return segments;
    }

    /**
     * Rebuilds segment of given class name with given arguments in given container.
     *
     * @param containerId
     * @param className
     * @param arguments
     */
    private void rebuildSegment(int containerId, String className, Bundle arguments) {
        ViewGroup container = findContainer(containerId);
        try {
            View childView;
            Class<?> clazz = getClass().getClassLoader().loadClass(className);
            try {
                Constructor constructor = clazz.getConstructor(Context.class, Bundle.class);
                childView = (View) constructor.newInstance(mSegmentActivity, arguments);
            } catch (Exception e) {
                Constructor constructor = clazz.getConstructor(Context.class);
                childView = (View) constructor.newInstance(mSegmentActivity);
            }
            container.addView(childView);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Finds container viewgroup by given containerId.
     * @param containerId
     * @return
     * @throws IllegalArgumentException if not found or not a viewgroup
     */
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
     *
     * @param a
     * @param b
     * @return
     */
    private boolean stringsEqual(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

}

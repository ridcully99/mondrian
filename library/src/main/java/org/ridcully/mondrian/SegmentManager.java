package org.ridcully.mondrian;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ridcully on 27.03.2017.
 */

public class SegmentManager {

    private final static String STACK_KEY = "mondrian.segmentmanager.stack";

    private SegmentActivity mSegmentActivity;
    private ArrayList<State> mStack = new ArrayList<>();

    SegmentManager(SegmentActivity segmentActivity) {
        mSegmentActivity = segmentActivity;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        ArrayList<State> stack = savedInstanceState.getParcelableArrayList(STACK_KEY);
        if (stack != null) {
            mStack = stack;
            rebuildFromStack(stack);
        }
    }

    public StateBuilder newState() {
        return new StateBuilder(this);
    }

    private void rebuildFromStack(ArrayList<State> stack) {
        SparseArray<ViewGroup> containerCache = new SparseArray<>();
        for (State state : stack) {
            for (Map.Entry<Integer, String> entry : state.getViewClasses().entrySet())  {
                try {
                    ViewGroup container = containerCache.get(entry.getKey());
                    if (container == null) {
                        View view = mSegmentActivity.findViewById(entry.getKey());
                        if (view == null || !(view instanceof ViewGroup)) continue;
                        container = (ViewGroup) view;
                        containerCache.put(entry.getKey(), container);
                    }
                    Class<?> clazz = getClass().getClassLoader().loadClass(entry.getValue());
                    Constructor constructor = clazz.getConstructor(Context.class);
                    View childView = (View)constructor.newInstance(mSegmentActivity);
                    container.addView(childView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STACK_KEY, mStack);
    }

    /**
     *
     * @param tag
     * @param segments
     * @throws Exception if segments.key is not ID of a viewgroup etc.
     */
    public void push(String tag, Map<Integer, Segment> segments) {
        State state = new State(tag);
        for (Map.Entry<Integer, Segment> entry : segments.entrySet()) {
            ViewGroup container = (ViewGroup) mSegmentActivity.findViewById(entry.getKey());
            container.addView(entry.getValue());
            state.getViewClasses().put(entry.getKey(), entry.getValue().getClass().getCanonicalName());
        }
        mStack.add(state);
    }

}

package org.ridcully.mondrian;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity that should be used for holding Segments to correctly manage Segment's lifecycle callbacks.
 *
 * Created by ridcully on 24.03.2017.
 */

public class SegmentActivity extends AppCompatActivity {

    private boolean mIsStarted;
    private boolean mIsResumed;
    private SegmentManager mSegmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSegmentManager = new SegmentManager(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mSegmentManager.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsStarted = true;
        for (Segment s : mSegmentManager.getAttachedSegments(true)) s.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResumed = true;
        for (Segment s : mSegmentManager.getAttachedSegments(true)) s.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResumed = false;
        for (Segment s : mSegmentManager.getAttachedSegments(true)) s.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsStarted = false;
        for (Segment s : mSegmentManager.getAttachedSegments(true)) s.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSegmentManager.onSaveInstanceState(outState);
    }

    public SegmentManager getSegmentManager() {
        return mSegmentManager;
    }

    /**
     * Called by segment to notify us, that given segment has been attached. This method should
     * never be called directly, but is called by the segments automatically when they get attached
     * to the window. Depending on current status of activity, the relevant lifecycle callbacks
     * of the segment are called in following order:
     *
     * <ul>
     *     <li>{@link Segment#onAttach())</li>
     *     <li>{@link Segment#onStart()} only if this activity is started</li>
     *     <li>{@link Segment#onResume()} only if this activity is resumed</li>
     * </ul>
     *
     * @param segment
     */
    public void onAttachSegment(Segment segment) {
        segment.onAttach();
        if (mIsStarted) segment.onStart();
        if (mIsResumed) segment.onResume();
    }

    /**
     * Called by segment to notify us, that given segment has been detached. This method should
     * never be called directly, but is called by the segments automatically when they get detached
     * to the window. Depending on current status of activity, the relevant lifecycle callbacks
     * of the segment are called in following order:
     *
     * <ul>
     *     <li>{@link Segment#onPause()} only if this activity is started</li>
     *     <li>{@link Segment#onStop()} only if this activity is resumed</li>
     *     <li>{@link Segment#onDetach())</li>
     * </ul>
     *
     * @param segment
     */
    public void onDetachSegment(Segment segment) {
        if (mIsResumed) segment.onPause();
        if (mIsStarted) segment.onStop();
        segment.onDetach();
    }


    // ---------------------------------------------------------------------------------------------


}

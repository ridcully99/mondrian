package org.ridcully.mondrian;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that should be used for holding Segments to correctly manage Segment's lifecycle callbacks.
 *
 * Created by ridcully on 24.03.2017.
 */

public class SegmentActivity extends AppCompatActivity {

    private boolean mIsStarted;
    private boolean mIsResumed;

    @Override
    protected void onStart() {
        super.onStart();
        mIsStarted = true;
        for (Segment s : getSegments(true)) s.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResumed = true;
        for (Segment s : getSegments(true)) s.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResumed = false;
        for (Segment s : getSegments(true)) s.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsStarted = false;
        for (Segment s : getSegments(true)) s.onStop();
    }

    /**
     * This method is final. Override {@link #handleBackPressed()} instead.
     */
    @Override
    final public void onBackPressed() {
        if (!handleBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * Called upon backPressed event, this method invokes handleBackPressed()
     * on attached Segments (also Segments attached to Segments etc.), until as a segment
     * consumes the event. If the event is not consumed by any event, the default implementation
     * (super.onBackPressed() is called.
     *
     * @return true if backPressed event was consumed by any Segment, false if not.
     */
    public boolean handleBackPressed() {
        return performSegmentsHandleBackPressed();
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


    /**
     * TODO Segmente anders durchgehen/aufrufen -- immer ganz ans Segment-Tree Ende gehen und dort anfangen.
     * TODO Und während dem Durchgehen handleBackPressed() aufrufen
     *
     *
     *
     * @return
     */
    private boolean performSegmentsHandleBackPressed() {
        for (Segment segment : getSegments(true)) {
            boolean handled = segment.handleBackPressed();
            if (handled) return true;
        }
        return false;
    }

    private List<Segment> getSegments(boolean collectSubSegments) {
        List<Segment> result = new ArrayList<>();
        View root = getWindow().getDecorView().getRootView();
        if (root != null && root instanceof ViewGroup) {
            if (root instanceof Segment && ((Segment)root).isAttachedToWindow()) {
                result.add((Segment) root);
            }
            if (collectSubSegments || !(root instanceof Segment)) {
                collectSegments((ViewGroup)root, collectSubSegments, result);
            }
        }
        return result;
    }

    static void collectSegments(@NonNull ViewGroup parent,
                                boolean collectSubSegments,
                                @NonNull List<Segment> result) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof Segment && ((Segment) child).isAttachedToWindow()) {
                    result.add((Segment) child);
                }
                if (collectSubSegments || !(child instanceof Segment)) {
                    collectSegments((ViewGroup) child, collectSubSegments, result);
                }
            }
        }
    }
}

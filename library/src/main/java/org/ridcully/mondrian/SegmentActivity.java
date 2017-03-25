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
     * Dispatches onBackPressed event to all attached segments. Only if none of the segments
     * 'consumes' the event, the parent class' onBackPressed() is called.
     *
     * You can use {@link #onBackPressedBeforeSegments()} and {@link #onBackPressedAfterSegments()}
     * to add your own handling of onBackPressed events before or after the segments are called.
     */
    @Override
    final public void onBackPressed() {
        if (onBackPressedBeforeSegments()) return;
        if (performSegmentsOnBackPressed()) return;
        if (onBackPressedAfterSegments()) return;
        super.onBackPressed();
    }

    /**
     * Invoked when back button is pressed, before the event is passed on to direct child segments.
     *
     * @return whether the event has been consumed and should not be passed on any further
     */
    public boolean onBackPressedBeforeSegments() {
        return false;
    }

    /**
     * Invoked when back button is pressed, after calls to {@link #onBackPressedBeforeSegments()}
     * and after event has been passed on to direct child segments and if the event has not been
     * consumed by any of those.
     *
     * @return whether the event has been consumed and should not be passed on any further
     */
    public boolean onBackPressedAfterSegments() {
        return false;
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


    private boolean performSegmentsOnBackPressed() {
        boolean handled = false;
        for (Segment segment : getSegments(false)) {
            handled = segment.onBackPressed() || handled; // let all segments handle back presses
        }
        return handled;
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

package org.ridcully.mondrian;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.Serializable;

/**
 * Base Segment. Extend and use with some SegmentActivity to participate in Activity's lifecycle.
 *
 * Created by robert.brandner on 20.03.2017.
 */

public class Segment extends FrameLayout {

    private final static String TAG = Segment.class.getSimpleName();
    private final static String MARKER_KEY = "mondrian.segment.marker";

    private boolean mIsAttachedToWindow = false;
    private String mMarker;
    private Bundle mArguments;

    public Segment(@NonNull Context context) {
        this(context, null, 0, null);
    }

    /**
     * Required constructor.
     * Required by SegmentManager to rebuild segments in restoreInstanceState()
     *
     * @param context
     * @param attrs
     */
    public Segment(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public Segment(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    public Segment(@NonNull Context context, Bundle args) {
        this(context, null, 0, args);
    }

    public Segment(@NonNull Context context, @Nullable AttributeSet attrs, Bundle args) {
        this(context, attrs, 0, args);
    }

    public Segment(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, Bundle args) {
        super(context, attrs, defStyleAttr);
        mArguments = args;
        onCreate();
    }

    /**
     * Returns the arguments that were provided to the constructor, if any.
     * @return
     */
    public Bundle getArguments() {
        return mArguments;
    }

    /**
     * Invoked when component is created.
     * <p>
     * This method should inflate the layout of the component, apply Butterknife#bind, and do all
     * the other setup usually done in Activity#onCreate().
     * <p>
     * Example<br/>
     * <code>
     * inflate(getContext(), R.layout.component_layout, this);
     * ButterKnife.bind(this);
     * </code>
     */
    protected void onCreate() {
        Log.d(TAG, "create");
    }

    public void onAttach() {
        Log.d(TAG, "attach");
    }

    public void onStart() {
        Log.d(TAG, "start");
    }

    public void onResume() {
        Log.d(TAG, "resume");
    }

    public void onPause() {
        Log.d(TAG, "pause");
    }

    public void onStop() {
        Log.d(TAG, "stop");
    }

    public void onDetach() {
        Log.d(TAG, "detach");
    }

    /**
     * Implement this and call from parent view (activity/component) if segment wants to
     * handle back-presses specifically.
     *
     * @return true if back-pressed was handled here, false if you want parent to handle it
     */
    public boolean onBackPressed() {
        return false;
    }


    public void setContentView(@LayoutRes int layoutResId) {
        inflate(getContext(), layoutResId, this);
    }


    // ------------------------------------------------------------------------------ Internal stuff


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachedToWindow = true;
        if (getContext() != null && getContext() instanceof SegmentActivity) {
            ((SegmentActivity)getContext()).onAttachSegment(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachedToWindow = false;
        if (getContext() != null && getContext() instanceof SegmentActivity) {
            ((SegmentActivity)getContext()).onDetachSegment(this);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putString(MARKER_KEY, mMarker);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        mMarker = ((Bundle)state).getString(MARKER_KEY);
    }

    /**
     * Checks, if this segment is currently attached to a window.
     * This is used by the SegmentManager, so do not change it.
     *
     * @return
     */
    public boolean isAttachedToWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return super.isAttachedToWindow();
        } else {
            return mIsAttachedToWindow;
        }
    }

    /**
     * Sets internal marker, used by SegmentManager.
     *
     * @param marker
     */
    void setMarker(String marker) {
        this.mMarker = marker;
    }

    /**
     * Gets internal marker, used by SegmentManager.
     *
     * @return
     */
    String getMarker() {
        return mMarker;
    }
}

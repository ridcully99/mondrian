package org.ridcully.mondrian;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Base Segment. Extend and use with some SegmentActivity to participate in Activity's lifecycle.
 *
 * Created by robert.brandner on 20.03.2017.
 */

public class Segment extends FrameLayout {

    private final static String TAG = Segment.class.getSimpleName();

    private boolean mIsAttachedToWindow = false;

    public Segment(@NonNull Context context) {
        this(context, null, 0);
    }

    public Segment(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Segment(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
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
    public boolean handleBackPressed() {
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

    public boolean isAttachedToWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return super.isAttachedToWindow();
        } else {
            return mIsAttachedToWindow;
        }
    }
}

package org.ridcully.vragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * Base Vragment. Extend and use with some VragmentActivity to participate in Activity's lifecycle.
 */

public class Vragment extends FrameLayout implements LifecycleOwner {

    private final static String MARKER_KEY = "org.ridcully.vfragment.marker";

    private boolean mIsAttachedToWindow = false;
    private String mMarker;
    private Bundle mArguments;
    private LifecycleRegistry mLifecycle;

    public Vragment(@NonNull Context context) {
        this(context, null, 0, null);
    }

    public Vragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public Vragment(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    public Vragment(@NonNull Context context, Bundle args) {
        this(context, null, 0, args);
    }

    public Vragment(@NonNull Context context, @Nullable AttributeSet attrs, Bundle args) {
        this(context, attrs, 0, args);
    }

    public Vragment(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, Bundle args) {
        super(context, attrs, defStyleAttr);
        mArguments = args;
        mLifecycle = new LifecycleRegistry(this);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * Returns the arguments that were provided to the constructor, if any.
     * @return
     */
    public Bundle getArguments() {
        return mArguments;
    }

    /**
     * Invoked when vragment is attached to window.
     */
    public void onAttach() {

    }

    /**
     * Invoked when host activity gets started,
     * or when added programmatically and host activity is already started.
     */
    public void onStart() {

    }

    /**
     * Invoked when host activity gets resumed,
     * or when added programmatically and host activity is already resumed.
     */
    public void onResume() {

    }

    /**
     * Invoked when host activity gets paused.
     */
    public void onPause() {

    }

    /**
     * Invoked when host acitivty gets stopped.
     */
    public void onStop() {

    }

    /**
     * Invoked when vragment is detached from window.
     */
    public void onDetach() {

    }

    /**
     * Invoked when VragmentActivity to which this Vragment is attached, is destroyed.
     */
    public void onDestroy() {

    }

    /**
     * Invoked by VragmentManager, when back button is pressed and this vragment is the top-most
     * in a container and no other vragment claimed the event yet.
     *
     * @see VragmentManager#onBackPressed(int...)
     *
     * @return  true if back-pressed was consumed here,
     *          false if you want VragmentManager to pop this vragment from the container
     */
    public boolean onBackPressed() {
        return false;
    }


    // -------------------------------------------------------------------- Some convenience methods


    public CharSequence getText(int resId) {
        return getContext().getText(resId);
    }

    public String getString(int resId) {
        return getContext().getString(resId);
    }

    public String getString(int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

    public Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    public int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }


    // ------------------------------------------------------------------------------ Internal stuff


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachedToWindow = true;
        if (getContext() != null && getContext() instanceof VragmentActivity) {
            ((VragmentActivity)getContext()).onAttachVragment(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachedToWindow = false;
        if (getContext() != null && getContext() instanceof VragmentActivity) {
            ((VragmentActivity)getContext()).onDetachVragment(this);
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
     * Checks, if this vragment is currently attached to a window.
     * This is used by the VragmentManager, so do not change it.
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
     * Sets internal marker, used by VragmentManager.
     *
     * @param marker
     */
    void setMarker(String marker) {
        this.mMarker = marker;
    }

    /**
     * Gets internal marker, used by VragmentManager.
     *
     * @return
     */
    String getMarker() {
        return mMarker;
    }

    void performStart() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
        onStart();
    }

    public void performResume() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        onResume();
    }

    public void performPause() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        onPause();
    }

    public void performStop() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        onStop();
    }

    public void performDestroy() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        onDestroy();
    }

    public void performAttach() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
        onAttach();
    }

    public void performDetach() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        onDetach();
    }
}

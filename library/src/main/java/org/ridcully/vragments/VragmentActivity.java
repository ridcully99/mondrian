package org.ridcully.vragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that should be used for holding Vragments to correctly manage lifecycle callbacks.
 */

public class VragmentActivity extends AppCompatActivity {


    // ------------------------------------------------------------------------------- Member fields


    /**
     * Keep track of current state of activity, so we can invoke necessary callbacks on Vragments
     * regardless on how or when they are added.
     */
    private boolean mIsStarted;
    private boolean mIsResumed;
    /**
     * The vragment manager; should be used add or remove vragments-
     */
    private VragmentManager mVragmentManager;


    // -------------------------------------------------------------------------- Activity lifecycle


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVragmentManager = new VragmentManager(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mVragmentManager.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsStarted = true;
        for (Vragment s : mVragmentManager.getAttachedVragments(true)) s.performStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResumed = true;
        for (Vragment s : mVragmentManager.getAttachedVragments(true)) s.performResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResumed = false;
        for (Vragment s : mVragmentManager.getAttachedVragments(true)) s.performPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsStarted = false;
        for (Vragment s : mVragmentManager.getAttachedVragments(true)) s.performStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Vragment s : mVragmentManager.getAttachedVragments(true)) s.performDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mVragmentManager.onSaveInstanceState(outState);
    }


    // --------------------------------------------------------------------------------- Own methods


    /**
     * Returns the vragment manager of this activity. Use the vragment manager to programmatically
     * add and remove vragments to the activity.
     *
     * @return the vragment manager
     */
    public VragmentManager getVragmentManager() {
        return mVragmentManager;
    }

    /**
     * Called by vragment to notify us, that given vragment has been attached. This method should
     * never be called directly, but is called by the vragments automatically when they get attached
     * to the window. Depending on current status of activity, the relevant lifecycle callbacks
     * of the vragment are called in following order:
     *
     * <ul>
     *     <li>{@link Vragment#onAttach())</li>
     *     <li>{@link Vragment#onStart()} only if this activity is started</li>
     *     <li>{@link Vragment#onResume()} only if this activity is resumed</li>
     * </ul>
     *
     * @param vragment
     */
    public void onAttachVragment(Vragment vragment) {
        vragment.performAttach();
        if (mIsStarted) vragment.performStart();
        if (mIsResumed) vragment.performResume();
    }

    /**
     * Called by vragment to notify us, that given vragment has been detached. This method should
     * never be called directly, but is called by the vragments automatically when they get detached
     * from the window. Depending on current status of activity, the relevant lifecycle callbacks
     * of the vragment are called in following order:
     *
     * <ul>
     *     <li>{@link Vragment#onPause()} only if this activity is started</li>
     *     <li>{@link Vragment#onStop()} only if this activity is resumed</li>
     *     <li>{@link Vragment#onDetach())</li>
     * </ul>
     *
     * @param vragment
     */
    public void onDetachVragment(Vragment vragment) {
        if (mIsResumed) vragment.performPause();
        if (mIsStarted) vragment.performStop();
        vragment.performDetach();
    }


    // ---------------------------------------------------------------------------------------------
}

package org.ridcully.vragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */

public class VragmentManager {

    private final static String TAG = VragmentManager.class.getSimpleName();
    private final static String VRAGMENTS_KEY = "org.ridcully.vragments.vragmentmanager.vragments";

    private VragmentActivity mVragmentActivity;
    private Set<Integer> mManagedContainerIds = new HashSet<>();

    VragmentManager(VragmentActivity vragmentActivity) {
        mVragmentActivity = vragmentActivity;
    }

    void onSaveInstanceState(Bundle outState) {
        ArrayList<Bundle> vragmentInfos = new ArrayList<>();
        for (int containerId : mManagedContainerIds) {
            for (Vragment vragment : getVragments(containerId)) {
                Bundle vragmentInfo = new Bundle();
                vragmentInfo.putInt("containerId", containerId);
                vragmentInfo.putString("className", vragment.getClass().getCanonicalName());
                vragmentInfo.putBundle("arguments", vragment.getArguments());
                vragmentInfos.add(vragmentInfo);
            }
        }
        outState.putParcelableArrayList(VRAGMENTS_KEY, vragmentInfos);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        mManagedContainerIds.clear();
        ArrayList<Bundle> vragmentInfos = savedInstanceState.getParcelableArrayList(VRAGMENTS_KEY);
        if (vragmentInfos == null) return;
        // clear all containers in savedInstanceState first, to avoid duplication of vragments
        for (Bundle vragmentInfo : vragmentInfos) {
            int containerId = vragmentInfo.getInt("containerId");
            // clear() adds container to managed containers, so we can check if containerId
            // is already managed to avoid calling clear multiple times for same container.
            if (!mManagedContainerIds.contains(containerId)) {
                clear(containerId);
            }
        }
        for (Bundle vragmentInfo : vragmentInfos) {
            rebuildVragment(vragmentInfo.getInt("containerId"),
                    vragmentInfo.getString("className"),
                    vragmentInfo.getBundle("arguments"));
        }
    }

    /**
     * Adds given vragment to the ViewGroup identified by containerId.
     *
     * @param containerId
     * @param vragment
     * @return VragmentManager for concatenating further operations
     */
    public VragmentManager push(@IdRes int containerId, Vragment vragment) {
        push(containerId, vragment, null);
        return this;
    }

    /**
     * Adds given vragment to the ViewGroup identified by containerId.
     *
     * @param containerId
     * @param vragment
     * @param marker An optional marker that can be used to identify the vragment for further usage.
     *            For example it can be used to coordinate state of different containers, etc.
     * @return VragmentManager for concatenating further operations
     */
    public VragmentManager push(@IdRes int containerId, Vragment vragment, String marker) {
        ViewGroup container = findContainer(containerId);
        vragment.setMarker(marker);
        container.addView(vragment);
        return this;
    }

    /**
     * Removes top-most Vragment from the ViewGroup identified by containerId.
     *
     * @param containerId
     * @return VragmentManager for concatinating further operations
     */
    public VragmentManager pop(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        if (container.getChildCount() > 0) {
            container.removeViewAt(container.getChildCount() - 1);
        }
        return this;
    }

    /**
     * Pops Vragments from ViewGroup identified by containerId until Vragment with given marker is found.
     * The Vragment having the given marker is not popped.
     *
     * @param containerId
     * @param marker
     * @return VragmentManager for concatenating further operations
     */
    public VragmentManager popToMarker(@IdRes int containerId, String marker) {
        ViewGroup container = findContainer(containerId);
        for (int pos = container.getChildCount() - 1; pos >= 0; pos--) {
            View view = container.getChildAt(pos);
            if (view instanceof Vragment && stringsEqual(marker, ((Vragment)view).getMarker())) {
                break;
            }
            container.removeViewAt(pos);
        }
        return this;
    }

    /**
     * Pops all Vragments from ViewGroup identified by containerId.
     *
     * @param containerId
     * @return VragmentManager for concatinating further operations
     */
    public VragmentManager popAll(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        for (int pos = container.getChildCount() - 1; pos >= 0; pos--) {
            container.removeViewAt(pos);
        }
        return this;
    }

    /**
     * Removes all vragments from ViewGroup identified by containerId.
     * This is a synonym for {@link #popAll(int)}.
     *
     * @param containerId
     * @return VragmentManager for concatenating further operations
     */
    public VragmentManager clear(@IdRes int containerId) {
        return popAll(containerId);
    }

    /**
     * Sets given vragment in ViewGroup identified by given containerId, removing all other vragments
     * before. This is the same as concatinating clear(containerId) and push(containerId, vragment).
     *
     * @param containerId
     * @param vragment
     * @return VragmentManager for concatinating further operations
     */
    public VragmentManager set(@IdRes int containerId, Vragment vragment) {
        return set(containerId, vragment, null);
    }

    /**
     * Sets given vragment in ViewGroup identified by given containerId, removing all other vragments
     * before. This is the same as concatinating clear(containerId) and push(containerId, vragment).
     *
     * @param containerId
     * @param vragment
     * @param marker
     * @return VragmentManager for concatenating further operations
     */
    public VragmentManager set(@IdRes int containerId, Vragment vragment, String marker) {
        clear(containerId);
        push(containerId, vragment, marker);
        return this;
    }

    /**
     * Returns top-most Vragment of ViewGroup identified by given container, without removing it.
     * @param containerId
     * @return Topmost vragment or null if ViewGroup had no children
     */
    public Vragment peek(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        if (container.getChildCount() > 0) {
            return (Vragment)container.getChildAt(container.getChildCount() - 1);
        }
        return null;
    }

    public boolean isEmpty(@IdRes int containerId) {
        ViewGroup container = findContainer(containerId);
        return container.getChildCount() == 0;
    }

    /**
     * Invokes onBackPressed() of top-most vragment of every specified container (in specified order).
     * If it returns false, it gets popped from its container.
     *
     * @param containerIds
     * @return true if any vragments where popped
     */
    public boolean onBackPressed(@IdRes int... containerIds) {
        boolean handled = false;
        if (containerIds != null) {
            for (int id : containerIds) {
                Vragment vragment = peek(id);
                if (vragment != null && !vragment.onBackPressed()) {
                    handled = true;
                    pop(id);
                }
            }
        }
        return handled;
    }


    // ---------------------------------------------------------------------------- Internal methods


    /**
     * Finds all Vragments attached to window of mVragmentActivity.
     *
     * @param collectSubVragments
     * @return
     */
    List<Vragment> getAttachedVragments(boolean collectSubVragments) {
        List<Vragment> result = new ArrayList<>();
        View root = mVragmentActivity.getWindow().getDecorView().getRootView();
        if (root != null && root instanceof ViewGroup) {
            if (root instanceof Vragment && ((Vragment)root).isAttachedToWindow()) {
                result.add((Vragment) root);
            }
            if (collectSubVragments || !(root instanceof Vragment)) {
                collectAttachedVragments((ViewGroup)root, collectSubVragments, result);
            }
        }
        return result;
    }

    /**
     * Helper method for getAttachedVragments.
     *
     * @param parent
     * @param collectSubVragments
     * @param result
     */
    private void collectAttachedVragments(@NonNull ViewGroup parent,
                                         boolean collectSubVragments,
                                         @NonNull List<Vragment> result) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof Vragment && ((Vragment) child).isAttachedToWindow()) {
                    result.add((Vragment) child);
                }
                if (collectSubVragments || !(child instanceof Vragment)) {
                    collectAttachedVragments((ViewGroup) child, collectSubVragments, result);
                }
            }
        }
    }

    /**
     * Gets vragments in given container.
     *
     * @param containerId
     * @return
     */
    private List<Vragment> getVragments(int containerId) {
        ViewGroup container = findContainer(containerId);
        ArrayList<Vragment> vragments = new ArrayList<>();
        for (int pos = 0; pos < container.getChildCount(); pos++) {
            vragments.add((Vragment)container.getChildAt(pos));
        }
        return vragments;
    }

    /**
     * Rebuilds vragment of given class name with given arguments in given container.
     *
     * @param containerId
     * @param className
     * @param arguments
     */
    private void rebuildVragment(int containerId, String className, Bundle arguments) {
        ViewGroup container = findContainer(containerId);
        try {
            View childView;
            Class<?> clazz = getClass().getClassLoader().loadClass(className);
            try {
                Constructor constructor = clazz.getConstructor(Context.class, Bundle.class);
                childView = (View) constructor.newInstance(mVragmentActivity, arguments);
            } catch (Exception e) {
                Constructor constructor = clazz.getConstructor(Context.class);
                childView = (View) constructor.newInstance(mVragmentActivity);
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
        View view = mVragmentActivity.findViewById(containerId);
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

package org.ridcully.vragments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ridcully on 26.03.2017.
 */

public class TestFragment extends Fragment {

    private final static String TAG = TestFragment.class.getSimpleName();

    public TestFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "attach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "start");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "pause");
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

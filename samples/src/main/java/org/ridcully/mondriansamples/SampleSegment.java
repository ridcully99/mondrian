package org.ridcully.mondriansamples;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.Toast;

import org.ridcully.mondrian.Segment;

import butterknife.ButterKnife;

/**
 * Created by ridcully on 25.03.2017.
 */

class SampleSegment extends Segment {

    /** Required */
    public SampleSegment(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.segment_sample);
        ButterKnife.bind(this);
        Toast.makeText(getContext(), "Hello " + getArguments().getString("name"), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getContext(),
                "SampleSegment received onBackPressed but ignores it", Toast.LENGTH_SHORT)
                .show();
        return super.onBackPressed();
    }
}

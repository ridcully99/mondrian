package org.ridcully.mondriansamples;

import android.content.Context;
import android.widget.Toast;

import org.ridcully.mondrian.Segment;

import butterknife.ButterKnife;

/**
 * Created by ridcully on 25.03.2017.
 */

class SampleSegment extends Segment {

    public SampleSegment(Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.segment_sample);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getContext(),
                "SampleSegment received onBackPressed but ignores it", Toast.LENGTH_SHORT)
                .show();
        return super.onBackPressed();
    }
}

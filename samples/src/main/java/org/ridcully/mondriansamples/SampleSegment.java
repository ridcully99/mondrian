package org.ridcully.mondriansamples;

import android.content.Context;
import android.os.Parcelable;
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
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.segment_sample);
        ButterKnife.bind(this);
    }

    @Override
    public boolean handleBackPressed() {
        Toast.makeText(getContext(),
                "SampleSegment received onBackPressed but ignores it", Toast.LENGTH_SHORT)
                .show();
        return super.handleBackPressed();
    }
}

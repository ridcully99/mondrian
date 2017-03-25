package org.ridcully.mondriansamples;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import org.ridcully.mondrian.Segment;
import org.ridcully.mondrian.SegmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends SegmentActivity {

    @BindView(R.id.segment_container) FrameLayout mSegmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_add)
    public void addSegment() {
        mSegmentContainer.removeAllViews();
        mSegmentContainer.addView(new SampleSegment(this));
    }



    @Override
    public boolean onBackPressedAfterSegments() {
        if (getSegment() != null) {
            mSegmentContainer.removeAllViews();
            return true;
        } else {
            return false;
        }
    }

    private Segment getSegment() {
        if (mSegmentContainer.getChildCount() == 0) return null;
        View v = mSegmentContainer.getChildAt(0);
        if (v instanceof Segment) return (Segment)v;
        return null;
    }
}

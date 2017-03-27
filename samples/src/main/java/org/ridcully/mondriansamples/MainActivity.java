package org.ridcully.mondriansamples;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.FrameLayout;

import org.ridcully.mondrian.Segment;
import org.ridcully.mondrian.SegmentActivity;
import org.ridcully.mondrian.SegmentContainer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends SegmentActivity {

    @BindView(R.id.segment_container) SegmentContainer mSegmentContainer;

    private List<Parcelable> mSegmentTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        if (savedInstanceState != null) {
//            mSegmentContainer.rebuildFromBundle(savedInstanceState, "foo");
//        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mSegmentContainer.saveToBundle(outState, "foo");
//    }

    @OnClick(R.id.bt_add)
    public void addSegment() {
        getSegmentManager()
                .newState()
                    .withTag("foo")
                    .putSegment(R.id.segment_container, new SampleSegment(this))
                .push();
//        mSegmentContainer.removeAllViews();
//        mSegmentContainer.addView(new SampleSegment(this));
    }

    @Override
    public boolean handleBackPressed() {
        if (!super.handleBackPressed()) {
            if (getSegment() != null) {
                mSegmentContainer.removeAllViews();
                return true;
            }
        }
        return false;
    }

    private Segment getSegment() {
        if (mSegmentContainer.getChildCount() == 0) return null;
        View v = mSegmentContainer.getChildAt(0);
        if (v instanceof Segment) return (Segment)v;
        return null;
    }
}

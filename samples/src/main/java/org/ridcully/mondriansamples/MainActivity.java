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

    @BindView(R.id.segment_container) FrameLayout mSegmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_add)
    public void addSegment() {
        getSegmentManager().push(R.id.segment_container, new SampleSegment(this), "tag");
    }

    @Override
    public boolean handleBackPressed() {
        if (!super.handleBackPressed()) {
            if (getSegmentManager().peek(R.id.segment_container) != null) {
                getSegmentManager().popAll(R.id.segment_container);
                return true;
            }
        }
        return false;
    }
}

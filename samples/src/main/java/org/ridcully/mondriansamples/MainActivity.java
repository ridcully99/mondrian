package org.ridcully.mondriansamples;

import android.os.Bundle;
import android.widget.FrameLayout;

import org.ridcully.mondrian.SegmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.ridcully.mondrian.SegmentManager.argumentBundle;

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
        getSegmentManager().push(R.id.segment_container, new SampleSegment(this, argumentBundle("name", "Robert")), "marker");
    }

    @Override
    public void onBackPressed() {
        if (!getSegmentManager().onBackPressed(R.id.segment_container)) {
            super.onBackPressed();
        }
    }
}

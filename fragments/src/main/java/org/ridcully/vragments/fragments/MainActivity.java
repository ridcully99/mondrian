package org.ridcully.vragments.fragments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fragment_container) FrameLayout mSegmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_add)
    public void addFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new TestFragment())
                .commitNow();
    }

}

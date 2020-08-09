package org.ridcully.mondriansamples;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import org.ridcully.vragments.Vragment;

import butterknife.ButterKnife;

/**
 * Created by ridcully on 25.03.2017.
 */

@SuppressLint("ViewConstructor")
class GreenVragment extends Vragment {

    /** Required */
    public GreenVragment(Context context, Bundle args) {
        super(context, args);
        inflate(getContext(), R.layout.segment_green, this);
        ButterKnife.bind(this);
        Toast.makeText(getContext(), "Hello " + getArguments().getString("name"), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getContext(),
                "Green segment received onBackPressed but ignores it", Toast.LENGTH_SHORT)
                .show();
        return super.onBackPressed();
    }
}

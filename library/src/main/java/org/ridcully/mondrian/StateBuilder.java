package org.ridcully.mondrian;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ridcully on 27.03.2017.
 */

public class StateBuilder {

    private SegmentManager mSegmentManager;
    private String mTag;
    private Map<Integer, Segment> mSegments = new HashMap<>();

    StateBuilder(SegmentManager segmentManager) {
        mSegmentManager = segmentManager;
    }

    public StateBuilder withTag(String tag) {
        mTag = tag;
        return this;
    }

    public StateBuilder putSegment(int containerViewId, Segment segment) {
        mSegments.put(containerViewId, segment);
        return this;
    }

    public void push() {
        mSegmentManager.push(mTag, mSegments);
    }
}

package org.kb10uy.tencocoa;

import org.kb10uy.tencocoa.model.TencocoaStatus;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

public class HomeTimeLineFragment extends TimelineFragment {

    public HomeTimeLineFragment() {
        // Required empty public constructor
    }

    public void addRestStatuses(List<Status> statuses) {
        List<TencocoaStatus> org = getTimelineAdapter().getList();
        ArrayList<TencocoaStatus> ns = new ArrayList<>();
        for (Status s : statuses) ns.add(new TencocoaStatus(s));
        org.addAll(0, ns);
        sendToHandler(() -> getTimelineAdapter().setList(org));
    }

}

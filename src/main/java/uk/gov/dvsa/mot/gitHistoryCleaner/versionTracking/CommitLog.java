package uk.gov.dvsa.mot.gitHistoryCleaner.versionTracking;

import java.util.LinkedList;

public class CommitLog {
    LinkedList<CommitPair> pairs = new LinkedList<>();

    public void add(CommitPair pair) {
        pairs.addFirst(pair);
    }

    public CommitPair getNewestCommitPair() {
        return pairs.get(0);
    }
}

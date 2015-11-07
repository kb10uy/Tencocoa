package org.kb10uy.bhavaagra;

import java.util.ArrayList;
import java.util.List;

public class RhapsodyAlbum {
    private String mAlbumName;
    private long mAlbumBucketId;
    private List<RhapsodyImage> mMediaList;

    RhapsodyAlbum(String name, long id) {
        mAlbumName = name;
        mAlbumBucketId = id;
        mMediaList = new ArrayList<>();
    }

    public int getMediaCount() {
        return mMediaList.size();
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public long getAlbumBucketId() {
        return mAlbumBucketId;
    }

    public RhapsodyImage get(int index) {
        return mMediaList.get(index);
    }

    public void add(RhapsodyImage image) {
        mMediaList.add(image);
    }

    public List<RhapsodyImage> getList() {
        return mMediaList;
    }
}


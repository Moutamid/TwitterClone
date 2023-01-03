package com.android.cts.clone.Model;

public class DeleteTweetModel {
    long id;
    int position;
    boolean isDeleted;

    public DeleteTweetModel(long id, int positionList, boolean isDeleted) {
        this.id = id;
        this.position = positionList;
        this.isDeleted = isDeleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPositionList(int positionList) {
        this.position = positionList;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}

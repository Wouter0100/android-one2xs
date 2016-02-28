package nl.wouter0100.one2xs.models;

import java.io.Serializable;

public class Forum implements Serializable {

    private int mId;
    private String mName;
    private String mDescription;

    public Forum(int id, String name, String description) {
        this.mId = id;
        this.mName = name;
        this.mDescription = description;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getId() {
        return mId;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public String toString() {
        return this.mName;
    }
}

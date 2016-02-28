package nl.wouter0100.one2xs.models;

import java.io.Serializable;

public class Section implements Serializable {

    private Forum[] mSubforums;
    private String mName;

    public Section(String name, Forum[] subforums) {
        this.mName = name;
        this.mSubforums = subforums;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    public Forum[] getSubforums() {
        return mSubforums;
    }

    public void setSubforums(Forum[] mSubforums) {
        this.mSubforums = mSubforums;
    }
}

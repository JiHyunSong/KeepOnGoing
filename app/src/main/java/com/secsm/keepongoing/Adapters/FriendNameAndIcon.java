package com.secsm.keepongoing.Adapters;

import com.secsm.keepongoing.R;

public class FriendNameAndIcon {
    int icon;
    String profile_path;
    String name;
    String targetTime;

    public FriendNameAndIcon(String profile_path, String name, String targetTime) {
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        this.targetTime = targetTime;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(String targetTime) {
        this.targetTime = targetTime;
    }
}

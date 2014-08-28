package com.secsm.keepongoing.Adapters;

import com.secsm.keepongoing.R;

public class FriendNameAndIcon {
    private int icon;
    private String profile_path;
    private String name;
    private String targetTime;
    private String accomplishedTime;
    private String isMaster;
    private String score;

    public FriendNameAndIcon(String profile_path, String name, String targetTime, String isMaster, String accomplishedTime) {
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        this.targetTime = targetTime;
        this.isMaster = isMaster;
        this.accomplishedTime = accomplishedTime;
    }

    public FriendNameAndIcon(String profile_path, String name, String targetTime, String isMaster) {
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        this.targetTime = targetTime;
        this.isMaster = isMaster;
        this.score = null;
    }

    public FriendNameAndIcon(String profile_path, String name, String targetTime) {
        this.profile_path = profile_path;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
        this.targetTime = targetTime;
        this.isMaster = "false";
        this.score = null;
    }

    public String getAccomplishedTime() {
        return accomplishedTime;
    }

    public void setAccomplishedTime(String accomplishedTime) {
        this.accomplishedTime = accomplishedTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(String isMaster) {
        this.isMaster = isMaster;
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

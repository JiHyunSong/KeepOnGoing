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
}

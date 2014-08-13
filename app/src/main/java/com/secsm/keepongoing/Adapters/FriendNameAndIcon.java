package com.secsm.keepongoing.Adapters;

import com.secsm.keepongoing.R;

public class FriendNameAndIcon {
    int icon;
    String name;

    FriendNameAndIcon(int icon, String name) {
        this.icon = icon;
        // TODO : profile change
        this.icon = R.drawable.profile_default;
        this.name = name;
    }
}

package com.groupchat.jasonchesney.groupchat;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    int ntabs;

    public TabsAccessorAdapter(FragmentManager fm, int con) {
        super(fm);
        this.ntabs= con;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            //case 0: ChatsFragment chtf = new ChatsFragment();
            //return chtf;

            case 0: GroupsFragment grpf = new GroupsFragment();
            return grpf;

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return ntabs;
    }
}

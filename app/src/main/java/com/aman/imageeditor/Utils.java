package com.aman.imageeditor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

class Utils {
    public static Fragment getVisibleFragment(FragmentManager manager) {

        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
}


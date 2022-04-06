package com.atlanticcity.app.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.atlanticcity.app.Fragments.Businesses;

public class ExploreTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ExploreTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
           /* case 0:
                Deals tab1 = new Deals();
                return tab1;*/
            case 0:
                Businesses tab2 = new Businesses();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}

package com.atlanticcity.app.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.atlanticcity.app.Fragments.Businesses;
import com.atlanticcity.app.Fragments.Earned;
import com.atlanticcity.app.Fragments.InvitesAcceped;
import com.atlanticcity.app.Fragments.InvitesSent;

public class InvitesAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public InvitesAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Earned tab1 = new Earned();
                return tab1;
            case 1:
                InvitesAcceped tab2 = new InvitesAcceped();
                return tab2;

            case 2:
                InvitesSent tab3 = new InvitesSent();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}

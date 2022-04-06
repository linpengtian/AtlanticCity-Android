package com.atlanticcity.app.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.atlanticcity.app.Fragments.FavoriteAdds;
import com.atlanticcity.app.Fragments.FavoriteBusinesses;
import com.atlanticcity.app.Fragments.FavoritesDeals;

public class FavoritesTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public FavoritesTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FavoritesDeals tab1 = new FavoritesDeals();
                return tab1;
            case 1:
                FavoriteBusinesses tab2 = new FavoriteBusinesses();
                return tab2;

          /*  case 2:
                FavoriteAdds tab3 = new FavoriteAdds();
                return tab3;*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}


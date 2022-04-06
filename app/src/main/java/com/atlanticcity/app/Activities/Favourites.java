package com.atlanticcity.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dueeeke.tablayout.SegmentTabLayout;
import com.dueeeke.tablayout.listener.OnTabSelectListener;
import com.atlanticcity.app.Adapters.FavoritesTabAdapter;
import com.atlanticcity.app.R;

public class Favourites extends AppCompatActivity {

    SegmentTabLayout favourites_tab;
    private String[] mTitles = {"Deals","Businesses"};
    ImageView icon_back,cross_icon,search;
    ViewPager viewPager;
    FavoritesTabAdapter favoritesTabAdapter;
    EditText et_search;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        icon_back = findViewById(R.id.icon_back);
        cross_icon = findViewById(R.id.cross_icon);
        et_search = findViewById(R.id.et_search);
        search = findViewById(R.id.search);
        viewPager = findViewById(R.id.pager);
        favourites_tab = findViewById(R.id.favourites_tab);
        favourites_tab.setTabData(mTitles);
        type = "deals";
        favoritesTabAdapter = new FavoritesTabAdapter(getSupportFragmentManager(), favourites_tab.getTabCount());
        viewPager.setAdapter(favoritesTabAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    type = "deals";
                }else {
                    type = "business";
                }
                favourites_tab.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            et_search.setVisibility(View.VISIBLE);
            cross_icon.setVisibility(View.VISIBLE);
            icon_back.setVisibility(View.GONE);
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(Favourites.this);
                Intent i = new Intent(Favourites.this,SearchActivity.class);
                i.putExtra("type",type);
                i.putExtra("searched_value",et_search.getText().toString().trim());
                startActivity(i);
                return true;
            }
            return false;
            }
        });

        cross_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            et_search.setVisibility(View.GONE);
            cross_icon.setVisibility(View.GONE);
            icon_back.setVisibility(View.VISIBLE);
            }
        });

        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        favourites_tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if(position == 0){
                    type = "deals";
                }else {
                    type = "business";
                }
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    public static void hideKeyboard(Activity activity) {
        try{
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Favourites.this,DealsFullScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}

package net.laenredadera.full.peliculas.gratis.de.terror.Adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {

    public String TAG = "PageAdapter";

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();

    public PageAdapter(FragmentManager fm) {
        //noinspection deprecation
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    public void removeFragment(Fragment fragment){
        fragmentList.remove(fragment);
    }
}

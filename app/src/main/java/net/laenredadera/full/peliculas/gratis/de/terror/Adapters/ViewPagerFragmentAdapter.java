package net.laenredadera.full.peliculas.gratis.de.terror.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.laenredadera.full.peliculas.gratis.de.terror.Fragments.FavoritesFragment;
import net.laenredadera.full.peliculas.gratis.de.terror.Fragments.SearchFragment;
import net.laenredadera.full.peliculas.gratis.de.terror.Fragments.VideoListFragment;

import java.util.ArrayList;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> arrayList = new ArrayList<>();


    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FavoritesFragment();
            case 1:
                return new VideoListFragment();
            case 2:
                return new SearchFragment();

        }
        return null;
    }


    public void addFragment(Fragment fragment) {
        arrayList.add(fragment);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}

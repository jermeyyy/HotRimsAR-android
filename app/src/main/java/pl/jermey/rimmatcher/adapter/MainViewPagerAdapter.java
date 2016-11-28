package pl.jermey.rimmatcher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import pl.jermey.rimmatcher.fragment.PagerItemFragment_;
import pl.jermey.rimmatcher.model.RimInfo;

/**
 * Created by Jermey on 27.11.2016.
 */

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<RimInfo> list = new ArrayList<>();

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PagerItemFragment_.builder()
                .rimInfo(list.get(position))
                .build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void add(RimInfo rimInfo) {
        list.add(rimInfo);
        notifyDataSetChanged();
    }
}

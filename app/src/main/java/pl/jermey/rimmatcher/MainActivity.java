package pl.jermey.rimmatcher;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import pl.jermey.rimmatcher.adapter.MainViewPagerAdapter;
import pl.jermey.rimmatcher.api.DataProvider_;
import pl.jermey.rimmatcher.base.BaseActivity;
import pl.jermey.rimmatcher.fragment.FilterFragment;
import pl.jermey.rimmatcher.fragment.FilterFragment_;
import pl.jermey.rimmatcher.view.PagerContainer;

/**
 * Created by Jermey on 27.11.2016.
 */
@OptionsMenu(R.menu.main)
@EActivity(R.layout.main_activity)
public class MainActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;
    @ViewById
    NavigationView navigationView;
    @ViewById
    DrawerLayout drawerLayout;
    @ViewById
    PagerContainer pagerContainer;
    @ViewById
    TextView counter;

    @InstanceState
    int currentPage = 0;

    private MainViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        getWindow().setFormat(PixelFormat.RGBX_8888);
    }

    @AfterViews
    void afterViews() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setTitle(R.string.app_name);
        viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        navigationView.addHeaderView(getHeaderView());

        setupViewPager();
        DataProvider_.getAllRims()
                .compose(bindToLifecycle())
                .doOnCompleted(() -> pagerContainer.getViewPager().setCurrentItem(currentPage, true))
                .subscribe(rims -> viewPagerAdapter.addAll(rims), Throwable::printStackTrace);
        startPostponedEnterTransition();
    }

    private View getHeaderView() {
        return LayoutInflater.from(this).inflate(R.layout.drawer_header, null, false);
    }

    private void setupViewPager() {
        ViewPager viewPager = pagerContainer.getViewPager();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setClipChildren(false);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin((int) -(getResources().getDisplayMetrics().widthPixels * 0.2f));
        viewPager.setPageTransformer(true, (page, position) -> {
            float scale = 1f - Math.min(Math.abs(position) / 2, 0.5f);
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(scale);
            page.setRotationY(position * -20f);
            page.setElevation(scale * 16f);
        });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                counter.setText(String.valueOf(position + 1) + " of " + viewPagerAdapter.getCount());
            }
        });
        counter.setText(String.valueOf(viewPager.getCurrentItem() + 1) + " of " + viewPagerAdapter.getCount());
    }

    @OptionsItem(R.id.action_filter)
    void filter() {
        FilterFragment filterFragment = FilterFragment_.builder().build();
        filterFragment.show(getSupportFragmentManager(), "filterFragment");
    }

    @OptionsItem(android.R.id.home)
    void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START, true);
    }

}

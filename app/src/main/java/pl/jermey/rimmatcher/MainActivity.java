package pl.jermey.rimmatcher;

import android.app.ProgressDialog;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import pl.jermey.rimmatcher.model.RimInfo;
import pl.jermey.rimmatcher.util.FilterInterface;
import pl.jermey.rimmatcher.view.PagerContainer;

/**
 * Created by Jermey on 27.11.2016.
 */
@OptionsMenu(R.menu.main)
@EActivity(R.layout.main_activity)
public class MainActivity extends BaseActivity implements FilterInterface {

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
    @InstanceState
    Integer mMaxPrice = 1000;
    @InstanceState
    Integer mMinPrice = 1000;
    @InstanceState
    Float mMinStars = 2.5f;

    private MainViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

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

        startPostponedEnterTransition();
    }

    private View getHeaderView() {
        return LayoutInflater.from(this).inflate(R.layout.drawer_header, null, false);
    }

    private void setupViewPager() {
        viewPager = pagerContainer.getViewPager();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
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
        DataProvider_.getAllRims()
                .compose(bindToLifecycle())
                .doOnCompleted(() -> {
                    counter.setText(String.valueOf(1) + " of " + viewPagerAdapter.getCount());
                    pagerContainer.getViewPager().setCurrentItem(currentPage, true);
                })
                .subscribe(rims -> viewPagerAdapter.addAll(rims), Throwable::printStackTrace);
    }

    @OptionsItem(R.id.action_filter)
    void filter() {
        FilterFragment filterFragment = FilterFragment_.builder()
                .mMinPrice(mMinPrice)
                .mMaxPrice(mMaxPrice)
                .mMinStars(mMinStars)
                .build();
        filterFragment.show(getSupportFragmentManager(), "filterFragment");
    }

    @OptionsItem(android.R.id.home)
    void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START, true);
    }

    @Override
    public void filter(Integer minPrice, Integer maxPrice, Float minStars) {
        mMinPrice = minPrice;
        mMaxPrice = maxPrice;
        mMinStars = minStars;
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.processing);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        DataProvider_.query().select(RimInfo.class)
                .where(RimInfo.STARS.greaterThanOrEqual(minStars).and(RimInfo.PRICE.between(minPrice, maxPrice)))
                .get()
                .toObservable()
                .toList()
                .compose(bindToLifecycle())
                .doOnCompleted(() -> {
                    progressDialog.dismiss();
                    if (currentPage == 0) {
                        counter.setText(String.valueOf(1) + " of " + viewPagerAdapter.getCount());
                        pagerContainer.getViewPager().setCurrentItem(currentPage, true);
                    }
                })
                .subscribe(rims -> {
                    if (rims.size() > 1) {
                        currentPage = 0;
                        viewPagerAdapter.clear();
                        viewPagerAdapter.addAll(rims);
                    } else {
                        new AlertDialog.Builder(this).setTitle(R.string.no_results)
                                .setMessage(R.string.no_results_message)
                                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                }, Throwable::printStackTrace);
    }
}

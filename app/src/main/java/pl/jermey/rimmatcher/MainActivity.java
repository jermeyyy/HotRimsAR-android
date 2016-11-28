package pl.jermey.rimmatcher;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import pl.jermey.rimmatcher.adapter.MainViewPagerAdapter;
import pl.jermey.rimmatcher.base.BaseActivity;
import pl.jermey.rimmatcher.fragment.FilterFragment;
import pl.jermey.rimmatcher.fragment.FilterFragment_;
import pl.jermey.rimmatcher.model.RimInfo;
import pl.jermey.rimmatcher.model.RimInfoList;
import pl.jermey.rimmatcher.util.UnityPlayerWrapper;
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
    @ViewById
    FrameLayout unityPlayerContainer;

    protected UnityPlayerWrapper mUnityPlayer;
    private MainViewPagerAdapter viewPagerAdapter;
    private boolean matcherVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBX_8888);

        mUnityPlayer = new UnityPlayerWrapper(this);
    }

    @Override
    protected void onDestroy() {
        if (mUnityPlayer != null)
            mUnityPlayer.quit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (matcherVisible)
            mUnityPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (matcherVisible)
            mUnityPlayer.resume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (matcherVisible) {
            return mUnityPlayer.injectEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @AfterViews
    void afterViews() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        unityPlayerContainer.addView(mUnityPlayer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setTitle(R.string.app_name);
        viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());

        InputStream input = null;
        try {
            input = getAssets().open("rims_list.json");
            Reader reader = new InputStreamReader(input, "UTF-8");
            Gson gson = new Gson();
            List<RimInfo> rimInfoList = gson.fromJson(reader, RimInfoList.class).getResult();
            Stream.of(rimInfoList).forEach(rimInfo -> viewPagerAdapter.add(rimInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        counter.setText(String.valueOf(viewPager.getCurrentItem() + 1) + " of " + viewPagerAdapter.getCount());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                counter.setText(String.valueOf(position + 1) + " of " + viewPagerAdapter.getCount());
            }
        });
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

    public void showMatcher() {
        unityPlayerContainer.setVisibility(View.VISIBLE);
        mUnityPlayer.resume();
//        mUnityPlayer.windowFocusChanged(true);
        matcherVisible = true;
    }

    @Override
    public void onBackPressed() {
        if (matcherVisible) {
            hideMatcher();
        } else {
            super.onBackPressed();
        }
    }

    public void hideMatcher() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        unityPlayerContainer.setVisibility(View.GONE);
        mUnityPlayer.pause();
        matcherVisible = false;
    }

}

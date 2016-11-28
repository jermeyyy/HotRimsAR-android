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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import pl.jermey.rimmatcher.adapter.MainViewPagerAdapter;
import pl.jermey.rimmatcher.base.BaseActivity;
import pl.jermey.rimmatcher.fragment.FilterFragment;
import pl.jermey.rimmatcher.fragment.FilterFragment_;
import pl.jermey.rimmatcher.model.RimInfo;
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
        mUnityPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//        Gson gson = new Gson();
//        String json = getResources().getString(R.string.rimInfoList);
//        List<RimInfo> rimInfoList = gson.fromJson(json, RimInfoList.class).getResult();
//        Stream.of(rimInfoList).forEach(rimInfo -> viewPagerAdapter.add(rimInfo));
        viewPagerAdapter.add(new RimInfo("Sparco ASSETTO GARA", "Matte Graphite Silver", "Bought on sale for $109 per wheel, which included lug nuts and Sparco center caps. I dropped almost 5 pounds per wheel over the stock wheels on my 2004 BMW 330xi and, let me tell you, there is a noticeable bump in acceleration at higher speeds, and it handles like a different car--significantly tighter in the corners and the tires don't wail nearly as much (left my new Michelin Premier 205/50s on). I've done a lot of performance mods on my car, and I have to say this was the most noticeable one of all. AND, they look bad-ass (matte graphite silver on a black car)!", "http://www.tirerack.com/images/wheels/sparco/sparco_ag_mgs_pdpfull.jpg"));
        viewPagerAdapter.add(new RimInfo("BBS CS5", "Bright Silver Paint", "I purchased this wheel for a 2014 Jetta Sportwagen TDI. I went from the stock 16\\\" wheel to a 17\\\" BBS. These definitely are quality wheels. Made in Germany with a low pressure cast for better density and durability. The quality is apparent, from the paint job all the way down to the nice BBS stem covers. These wheels have a classic style and will look nice for a long time. The only issue I see if cleaning will be difficult with all of the spokes, but I recommend Sonax wheel cleaner to solve that issue.", "http://www.tirerack.com/images/wheels/bbs/bbs_cs5_bs_pdpfull.jpg"));
        viewPagerAdapter.add(new RimInfo("Enkei FUJIN", "Black Painted", "I purchased the black Enkei Fujin wheels for track and autocross use, as well as daily driving in the summer. They are incredibly lightweight and there is a noticeable improvement in handling, acceleration, and braking versus my stock Huffs. I highly recommend these wheels if you're into performance. I bought the Sumitomo HTR Z III tires with these wheels - the combination exceeded my expectations!", "http://www.tirerack.com/images/wheels/enkei_tuning/enkeitun_fujin_b_pdpfull.jpg"));
        viewPagerAdapter.add(new RimInfo("BBS CH-R", "Black w/Polished Stainless Lip", "Great Wheels - Unbeatable BBS quality - Actually 3-4lbs lighter than my 19\\\" stock wheels yet these are wider with larger tires. The M3 is already a bad-boy... these classic, German Wheels takes it to the next level. Kinda pricey, but untouchable in the price range.", "http://www.tirerack.com/images/wheels/bbs/bbs_chr_bpsl_pdpfull.jpg"));
        viewPagerAdapter.add(new RimInfo("OZ ALLEGGERITA HLT", "Black Painted", "Wheels look better \"in person\" than on the page here. Very high quality. No vibrations at all. Most aftermarket wheels come with a centering ring that just slips into the wheel to make it fit the correct hub, but these wheels have a groove that mates with a round ring on the centering ring for a positive fit that are made by OZ racing that makes a good snug fit on my hubs. This prevent the wheel from being even a little off so there is no chance of vibration. The lug bolts supplied are longer than stock but still a spherical seat like the originals. I did notice some paint flecks where the lug bolts go and this is probably the cause of some of the bolts needing to be re-torqued a few times over the course of 100 miles until the settle down and keep the proper setting.", "http://www.tirerack.com/images/wheels/oz/oz_allerghlt_blk_pdpfull.jpg"));
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

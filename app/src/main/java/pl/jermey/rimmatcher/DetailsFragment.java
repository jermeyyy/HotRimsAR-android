package pl.jermey.rimmatcher;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.rd.PageIndicatorView;
import com.rd.animation.AnimationType;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import pl.jermey.rimmatcher.adapter.GalleryPagerAdapter;
import pl.jermey.rimmatcher.model.RimInfo;

/**
 * Created by Jermey on 27.11.2016.
 */
@OptionsMenu(R.menu.details)
@EFragment(R.layout.details_fragment)
public class DetailsFragment extends RxFragment {

    @ViewById
    Toolbar toolbar;
    @ViewById
    ViewPager galleryPager;
    @ViewById
    PageIndicatorView pagerIndicatorView;
    @ViewById
    TextView color;
    @ViewById
    TextView review;
    @ViewById
    RelativeLayout match;

    @FragmentArg
    RimInfo rimInfo;

    @AfterViews
    void afterViews() {
        ((RxAppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((RxAppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((RxAppCompatActivity) getActivity()).getSupportActionBar().setTitle(rimInfo.getName());
        color.setText(rimInfo.getColorInfo());
        review.setText(rimInfo.getReview());
        List<String> photosList = new ArrayList<>();
        photosList.add(rimInfo.getImage());
        photosList.add(rimInfo.getImage());
        photosList.add(rimInfo.getImage());
        galleryPager.setAdapter(new GalleryPagerAdapter(getContext(), photosList));
        pagerIndicatorView.setViewPager(galleryPager);

    }

    @Click(R.id.match)
    void match() {
        ((MainActivity) getActivity()).showMatcher();
    }

    @OptionsItem(android.R.id.home)
    void close() {
        getActivity().onBackPressed();
    }

}

package pl.jermey.rimmatcher.fragment;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import pl.jermey.rimmatcher.MainActivity;
import pl.jermey.rimmatcher.R;
import pl.jermey.rimmatcher.adapter.GalleryPagerAdapter;
import pl.jermey.rimmatcher.model.RimInfo;
import pl.jermey.rimmatcher.util.FragmentTransitionUtil;

/**
 * Created by Jermey on 27.11.2016.
 */
@OptionsMenu(R.menu.details)
@EFragment(R.layout.details_fragment)
public class DetailsFragment extends RxFragment implements FragmentTransitionUtil.DelayedTransitionFragment, GalleryPagerAdapter.GalleryPagerInterface {

    @ViewById
    Toolbar toolbar;
    @ViewById
    ViewPager galleryPager;
    @ViewById
    PageIndicatorView pagerIndicatorView;
    @ViewById
    TextView color;
    @ViewById
    TextView price;
    @ViewById
    RatingBar rating;
    @ViewById
    TextView review;
    @ViewById
    RelativeLayout match;

    @FragmentArg
    RimInfo rimInfo;
    private boolean photosLoaded = false;

    @AfterViews
    void afterViews() {
        toolbar.setTitle(rimInfo.getName());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        color.setText(rimInfo.getColorInfo());
        review.setText(rimInfo.getReview());
        price.setText(rimInfo.getPrice() + "$");
        rating.setRating(Float.parseFloat(rimInfo.getStars()));
        List<String> images = new ArrayList<>();
        images.addAll(rimInfo.getImages());
        images.addAll(rimInfo.getImages());
        images.addAll(rimInfo.getImages());
        galleryPager.setAdapter(new GalleryPagerAdapter(getContext(), images, this));
        pagerIndicatorView.setViewPager(galleryPager);
    }

    @Click(R.id.match)
    void match() {
        ((MainActivity) getActivity()).showMatcher();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        resetTransitions();
    }

    private void resetTransitions() {
        String sharedElementName =
                FragmentTransitionUtil.getInstance(getFragmentManager()).getTransitionName();
        Slide enter = new Slide(Gravity.LEFT);
        Slide exit = new Slide(Gravity.RIGHT);
        if (sharedElementName != null) {
            enter.excludeTarget(sharedElementName, true);
            exit.excludeTarget(sharedElementName, true);
        }
        setEnterTransition(enter);
        setExitTransition(exit);
    }

    @Override
    public boolean isTransitionDelayed() {
        return photosLoaded;
    }

    @Override
    public void photoLoaded() {
        photosLoaded = true;
    }
}

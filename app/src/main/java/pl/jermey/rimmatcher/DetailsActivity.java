package pl.jermey.rimmatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rd.PageIndicatorView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pl.jermey.rimmatcher.adapter.GalleryPagerAdapter;
import pl.jermey.rimmatcher.api.DataProvider_;
import pl.jermey.rimmatcher.api.RestClient_;
import pl.jermey.rimmatcher.base.BaseActivity;
import pl.jermey.rimmatcher.model.RimInfo;

/**
 * Created by Jermey on 27.11.2016.
 */
@EActivity(R.layout.details_activity)
public class DetailsActivity extends BaseActivity implements GalleryPagerAdapter.GalleryPagerInterface {

    @ViewById
    ViewPager galleryPager;
    @ViewById
    PageIndicatorView pagerIndicatorView;
    @ViewById
    AppBarLayout appBarLayout;
    @ViewById
    TextView name;
    @ViewById
    RatingBar rating;
    @ViewById
    TextView price;
    @ViewById
    TextView color;
    @ViewById
    TextView shippingInfo;
    @ViewById
    TextView review;
    @ViewById
    ImageView authorAvatar;
    @ViewById
    TextView authorName;
    @ViewById
    RelativeLayout match;
    @ViewById
    ImageView close;
    @ViewById
    TextView like;

    @Extra
    String rimId;

    RimInfo rimInfo;
    private boolean collapsed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                fadeViews(1.0f, 250, 250, new AnimatorListenerAdapter() {
                });
            }
        });
        photoLoaded();
    }

    private void fadeViews(float alpha, long duration, long delay, AnimatorListenerAdapter listenerAdapter) {
        close.animate().alpha(alpha).setStartDelay(delay).setDuration(duration).start();
        pagerIndicatorView.animate().alpha(alpha).setStartDelay(delay).setDuration(duration).setListener(listenerAdapter).start();
    }

    @AfterViews
    void afterViews() {
        DataProvider_.getRimById(rimId)
                .compose(bindToLifecycle())
                .subscribe(rimInfo -> {
                            this.rimInfo = rimInfo;
                            setupView();
                        },
                        Throwable::printStackTrace);
    }

    private void setupView() {
        name.setText(rimInfo.getName());
        color.setText(rimInfo.getColorInfo());
        authorName.setText(rimInfo.getAuthorName());
        Glide.with(this)
                .load(rimInfo.getAuthorAvatar())
                .bitmapTransform(new CropCircleTransformation(this))
                .into(authorAvatar);
        if (rimInfo.isIsLiked() != null && rimInfo.isIsLiked()) {
            like.setEnabled(false);
            like.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(like.getContext(), R.drawable.ic_favorite_white_24dp), null, null, null);
        } else {
            like.setEnabled(true);
            like.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(like.getContext(), R.drawable.ic_favorite_border_white_24dp), null, null, null);
        }
        shippingInfo.setText(rimInfo.getShipInfo());
        review.setText(rimInfo.getReview());
        price.setText(rimInfo.getPrice() + "$");
        rating.setRating(Float.parseFloat(rimInfo.getStars()));
        like.setText(rimInfo.getLikes());
        rimInfo.getImages();
        galleryPager.setAdapter(new GalleryPagerAdapter(this, rimInfo.getImages()));
        pagerIndicatorView.setViewPager(galleryPager);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                collapsed = true;
            } else if (verticalOffset == 0) {
                collapsed = false;
            }
        });
    }

    @Click(R.id.match)
    void match() {
        MatcherActivity_.intent(this).modelName(rimInfo.getModelName()).start();
    }

    @Click(R.id.close)
    void close() {
        onBackPressed();
    }

    @Click(R.id.like)
    void like() {
        like.setEnabled(false);
        like.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_favorite_white_24dp), null, null, null);

        RestClient_.likeRim(rimInfo).subscribe(aVoid -> {
        }, throwable -> {
            Toast.makeText(this, "We coudn't like the rim :( (Check your network)", Toast.LENGTH_SHORT).show();
            like.setEnabled(true);
            like.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_favorite_border_white_24dp), null, null, null);
            throwable.printStackTrace();
        });
    }

    @Override
    public void photoLoaded() {
        startPostponedEnterTransition();
    }

    @Override
    public void onBackPressed() {
        if (collapsed) {
            super.onBackPressed();
        } else {
            fadeViews(0.0f, 150, 0, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    DetailsActivity.super.onBackPressed();
                }
            });
        }
    }


}

package pl.jermey.rimmatcher.fragment;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import pl.iterators.mobile.transitionhelper.TransitionHelper;
import pl.jermey.rimmatcher.DetailsActivity_;
import pl.jermey.rimmatcher.R;
import pl.jermey.rimmatcher.api.DataProvider_;
import pl.jermey.rimmatcher.api.RestClient_;
import pl.jermey.rimmatcher.model.RimInfo;

/**
 * Created by Jermey on 27.11.2016.
 */
@EFragment(R.layout.pager_item_fragment)
public class PagerItemFragment extends RxFragment {

    @ViewById
    LinearLayout container;
    @ViewById
    TextView name;
    @ViewById
    ImageView image;
    @ViewById
    TextView color;
    @ViewById
    TextView shippingInfo;
    @ViewById
    RelativeLayout match;
    @ViewById
    LinearLayout descriptionContainer;
    @ViewById
    RatingBar rating;
    @ViewById
    TextView like;

    @FragmentArg
    String rimId;

    private RimInfo rimInfo;

    @AfterViews
    void afterViews() {
        DataProvider_.getRimById(rimId)
                .compose(bindToLifecycle())
                .subscribe(rimInfo -> {
                    this.rimInfo = rimInfo;
                    setupView();
                }, Throwable::printStackTrace);
    }

    private void setupView() {
        name.setText(rimInfo.getName());
        color.setText(rimInfo.getColorInfo());
        shippingInfo.setText(rimInfo.getShipInfo());
        like.setText(rimInfo.getLikes());
        Glide.with(this).load(rimInfo.getImages().get(0).getUrl()).into(image);
        rating.setRating(Float.parseFloat(rimInfo.getStars()));
        if (rimInfo.isIsLiked() != null && rimInfo.isIsLiked()) {
            like.setEnabled(false);
            like.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(like.getContext(), R.drawable.ic_favorite_white_24dp), null, null, null);
        } else {
            like.setEnabled(true);
            like.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(like.getContext(), R.drawable.ic_favorite_border_white_24dp), null, null, null);
        }
    }

    @Click(R.id.like)
    void like(View v) {
        like.setEnabled(false);
        like.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_favorite_white_24dp), null, null, null);

        RestClient_.likeRim(rimInfo).subscribe(aVoid -> {
        }, throwable -> {
            Toast.makeText(v.getContext(), "We coudn't like the rim :( (Check your network)", Toast.LENGTH_SHORT).show();
            like.setEnabled(true);
            like.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_favorite_border_white_24dp), null, null, null);
            throwable.printStackTrace();
        });
    }

    @Click({R.id.container, R.id.details})
    void details() {
        Intent intent = DetailsActivity_.intent(this).rimId(rimInfo.getId()).get();
        TransitionHelper.transitionTo(this, intent, R.transition.rim_details, image, descriptionContainer, match);
    }
}

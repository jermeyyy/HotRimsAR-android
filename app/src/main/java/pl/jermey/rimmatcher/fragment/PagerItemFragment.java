package pl.jermey.rimmatcher.fragment;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import pl.jermey.rimmatcher.UnityPlayerActivity;
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
    TextView review;
    @ViewById
    RelativeLayout match;
    @ViewById
    LinearLayout descriptionContainer;

    @FragmentArg
    RimInfo rimInfo;

    @AfterViews
    void afterViews() {
        name.setText(rimInfo.getName());
        color.setText(rimInfo.getColorInfo());
        review.setText(rimInfo.getReview());
        Glide.with(this).load(rimInfo.getImage()).into(image);
    }

    @Click(R.id.match)
    void match() {
        startActivity(new Intent(getContext(), UnityPlayerActivity.class));
    }

    @Click(R.id.container)
    void details() {
        TransitionHelper.transitionTo(this, DetailsActivity_.intent(this).rimInfo(rimInfo).get(), R.transition.rim_details, image, match, name, descriptionContainer);
    }
}

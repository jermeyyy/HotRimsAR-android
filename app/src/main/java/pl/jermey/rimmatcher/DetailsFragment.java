package pl.jermey.rimmatcher;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import pl.jermey.rimmatcher.model.RimInfo;

/**
 * Created by Jermey on 27.11.2016.
 */
@EFragment(R.layout.details_fragment)
public class DetailsFragment extends RxFragment {

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

    @FragmentArg
    RimInfo rimInfo;

    @AfterViews
    void afterViews() {
        name.setText(rimInfo.getName());
        color.setText(rimInfo.getColorInfo());
        review.setText(rimInfo.getReview());
        Glide.with(this).load(rimInfo.getImage()).into(new ImageViewTarget<GlideDrawable>(image) {
            @Override
            protected void setResource(GlideDrawable resource) {
                image.setImageDrawable(resource);
            }
        });
    }

    @Click(R.id.match)
    void match() {
        ((MainActivity) getActivity()).showMatcher();
    }

    @Click(R.id.close)
    void close() {
        getActivity().onBackPressed();
    }
}

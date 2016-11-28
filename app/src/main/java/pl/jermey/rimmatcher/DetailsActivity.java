package pl.jermey.rimmatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import pl.jermey.rimmatcher.base.BaseActivity;
import pl.jermey.rimmatcher.model.RimInfo;

/**
 * Created by Jermey on 27.11.2016.
 */
@EActivity(R.layout.details_activity)
public class DetailsActivity extends BaseActivity {

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

    @Extra
    RimInfo rimInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
    }

    @AfterViews
    void afterViews() {
        name.setText(rimInfo.getName());
        color.setText(rimInfo.getColorInfo());
        review.setText(rimInfo.getReview());
        Glide.with(this).load(rimInfo.getImage()).into(new ImageViewTarget<GlideDrawable>(image) {
            @Override
            protected void setResource(GlideDrawable resource) {
                image.setImageDrawable(resource);
                startPostponedEnterTransition();
            }
        });
    }

    @Click(R.id.match)
    void match() {
        startActivity(new Intent(this, UnityPlayerActivity.class));
    }

    @Click(R.id.close)
    void close() {
        onBackPressed();
    }
}

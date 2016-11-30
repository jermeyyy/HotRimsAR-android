package pl.jermey.rimmatcher.fragment;

import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import pl.jermey.rimmatcher.MainActivity;
import pl.jermey.rimmatcher.R;
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
    TextView price;
    @ViewById
    RelativeLayout match;
    @ViewById
    LinearLayout descriptionContainer;
    @ViewById
    RatingBar rating;

    @FragmentArg
    RimInfo rimInfo;

    @AfterViews
    void afterViews() {
        name.setText(rimInfo.getName());
        color.setText(rimInfo.getColorInfo());
        price.setText(rimInfo.getPrice() + "$");
        Glide.with(this).load(rimInfo.getImages().get(0)).into(image);
        rating.setRating(Float.parseFloat(rimInfo.getStars()));
    }

    @Click(R.id.match)
    void match() {
        ((MainActivity) getActivity()).showMatcher();
    }

    @Click(R.id.container)
    void details() {
        DetailsFragment fragment = DetailsFragment_.builder().rimInfo(rimInfo).build();
        Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.rim_details);
        fragment.setSharedElementEnterTransition(transition);
        fragment.setEnterTransition(new Fade());
        setExitTransition(new Fade());
        fragment.setSharedElementReturnTransition(transition);
//        FragmentTransitionUtil.getInstance(getFragmentManager()).transition(R.id.fragmentContainer, this, fragment, image, image.getTransitionName());
        getActivity().getSupportFragmentManager().beginTransaction()
                .addSharedElement(image, image.getTransitionName())
                .addSharedElement(match, match.getTransitionName())
                .addSharedElement(name, name.getTransitionName())
                .addSharedElement(descriptionContainer, descriptionContainer.getTransitionName())
                .add(R.id.fragmentContainer, fragment, "detailsFragment")
                .addToBackStack("detailsFragment")
                .commit();
    }
}

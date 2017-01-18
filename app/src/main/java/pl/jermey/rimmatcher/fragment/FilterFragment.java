package pl.jermey.rimmatcher.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import pl.jermey.rimmatcher.R;
import pl.jermey.rimmatcher.util.FilterInterface;

import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

/**
 * Created by Jermey on 28.11.2016.
 */
@EFragment(R.layout.filter_fragment)
public class FilterFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

    @ViewById
    SeekBar minPrice;
    @ViewById
    SeekBar maxPrice;
    @ViewById
    TextView minPriceLabel;
    @ViewById
    TextView maxPriceLabel;
    @ViewById
    RatingBar minRating;

    @FragmentArg
    Integer mMaxPrice;
    @FragmentArg
    Integer mMinPrice;
    @FragmentArg
    Float mMinStars;

    private FilterInterface filterInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof FilterInterface)) {
            throw new RuntimeException(getClass().getSimpleName() + " must implement " + FilterInterface.class.getSimpleName());
        } else {
            filterInterface = ((FilterInterface) getActivity());
        }
    }

    @AfterViews
    void afterViews() {
        minPriceLabel.setText(getString(R.string.min_price_format, mMinPrice));
        minPrice.setProgress(mMinPrice);
        maxPriceLabel.setText(getString(R.string.max_price_format, mMaxPrice));
        maxPrice.setProgress(mMaxPrice);
        minRating.setRating(mMinStars);

        minPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minPriceLabel.setText(getString(R.string.min_price_format, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maxPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maxPriceLabel.setText(getString(R.string.max_price_format, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet == null)
                return;
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheet.setBackground(null);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case STATE_HIDDEN:
                            dismiss();
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        });
    }

    @Click(R.id.filter)
    void filter() {
        filterInterface.filter(minPrice.getProgress(), maxPrice.getProgress(), minRating.getRating());
        bottomSheetBehavior.setState(STATE_HIDDEN);
    }
}

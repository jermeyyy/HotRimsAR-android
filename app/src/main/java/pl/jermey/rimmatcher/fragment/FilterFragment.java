package pl.jermey.rimmatcher.fragment;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import pl.jermey.rimmatcher.R;

/**
 * Created by Jermey on 28.11.2016.
 */
@EFragment(R.layout.filter_fragment)
public class FilterFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

    @AfterViews
    void afterViews() {
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet == null)
                return;
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheet.setBackground(null);
        });
    }

    @Click(R.id.filter)
    void filter() {
        getDialog().dismiss();
    }
}

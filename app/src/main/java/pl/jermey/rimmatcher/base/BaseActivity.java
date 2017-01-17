package pl.jermey.rimmatcher.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.iterators.mobile.transitionhelper.TransitionHelper;
import pl.jermey.rimmatcher.AppPrefs_;

/**
 * Created by Jermey on 27.11.2016.
 */
@EBean
public class BaseActivity extends RxAppCompatActivity{

    @Pref
    public AppPrefs_ appPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionHelper.setupTransitions(this);
    }
}

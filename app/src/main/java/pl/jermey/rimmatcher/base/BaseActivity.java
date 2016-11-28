package pl.jermey.rimmatcher.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import pl.iterators.mobile.transitionhelper.TransitionHelper;

/**
 * Created by Jermey on 27.11.2016.
 */

public class BaseActivity extends RxAppCompatActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionHelper.setupTransitions(this);
    }
}

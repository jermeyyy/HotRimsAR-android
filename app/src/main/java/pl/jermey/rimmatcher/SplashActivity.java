package pl.jermey.rimmatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.iterators.mobile.transitionhelper.TransitionHelper;
import pl.jermey.rimmatcher.api.DataProvider_;
import pl.jermey.rimmatcher.api.RestClient_;
import pl.jermey.rimmatcher.base.BaseActivity;

/**
 * Created by Jermey on 05.12.2016.
 */
@EActivity(R.layout.splash_activity)
public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private static final long SYNC_INTERVAL = 1 * 60 * 60 * 1000; // 1h
    @ViewById
    ImageView logo;
    @ViewById
    TextView name;

    @AfterViews
    void afterViews() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - appPrefs.dbUpdateTimestamp().getOr(0L) > SYNC_INTERVAL) {
            downloadRims();
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            next();
        }
    }

    private void downloadRims() {
        RestClient_.getAllRims()
                .compose(bindToLifecycle())
                .map(rimInfoList -> rimInfoList.result)
                .subscribe(rimInfos -> DataProvider_.query().upsert(rimInfos).subscribe(r -> {
                            appPrefs.edit().dbUpdateTimestamp().put(System.currentTimeMillis()).apply();
                            next();
                        }, Throwable::printStackTrace)
                        , Throwable::printStackTrace);
    }


    private void next() {
        logo.animate()
                .rotation(360 * 5f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        logo.animate()
                                .scaleX(0f)
                                .scaleY(0f)
                                .alpha(0f)
                                .setDuration(150)
                                .setInterpolator(new AccelerateInterpolator())
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Intent intent = MainActivity_.intent(SplashActivity.this).get();
                                        TransitionHelper.transitionTo(SplashActivity.this, intent, R.transition.main);
                                        finish();
                                    }
                                })
                                .start();
                        name.animate()
                                .alpha(0f)
                                .setDuration(150)
                                .setInterpolator(new AccelerateInterpolator())
                                .start();
                    }
                }).start();
    }
}

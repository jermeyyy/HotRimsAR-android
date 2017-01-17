package pl.jermey.rimmatcher;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import pl.jermey.rimmatcher.base.BaseActivity;
import pl.jermey.rimmatcher.util.UnityPlayerWrapper;

/**
 * Created by Jermey on 01.12.2016.
 */
@EActivity
public class MatcherActivity extends BaseActivity {

    @Extra
    String modelName;

    UnityPlayerWrapper unityPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        unityPlayer = new UnityPlayerWrapper(this);
        setContentView(unityPlayer);
        unityPlayer.requestFocus();
        UnityPlayerWrapper.UnitySendMessage("ModelController", "selectModel", modelName);
    }

    @Override
    protected void onDestroy() {
        unityPlayer.quit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unityPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        unityPlayer.resume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        unityPlayer.configurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        unityPlayer.windowFocusChanged(hasFocus);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return unityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        } else {
            return unityPlayer.injectEvent(event);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        else
            return unityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return unityPlayer.injectEvent(event);
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return unityPlayer.injectEvent(event);
    }
}

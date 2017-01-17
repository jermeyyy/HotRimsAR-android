package pl.jermey.rimmatcher;

import android.app.Application;

import com.facebook.stetho.Stetho;

import org.androidannotations.annotations.EApplication;

import pl.jermey.rimmatcher.api.DataProvider_;
import pl.jermey.rimmatcher.api.RestClient_;

/**
 * Created by Jermey on 01.12.2016.
 */
@EApplication
public class RimMatcherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        DataProvider_.getInstance_(this);
        RestClient_.getInstance_(this);
    }

}

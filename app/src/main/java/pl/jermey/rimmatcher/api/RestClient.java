package pl.jermey.rimmatcher.api;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.jermey.rimmatcher.model.RimInfo;
import pl.jermey.rimmatcher.model.RimInfoList;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jermey on 29.11.2016.
 */

@EBean(scope = EBean.Scope.Singleton)
public class RestClient {

    public static final String ENDPOINT = "http://www.bellaryszarda.hekko24.pl/przemo/hotrims/index.php/";
    private static final String TAG = "RestClient";

    @RootContext
    Context context;

    private static ApiService apiService;

    @AfterInject
    public void afterInject() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ENDPOINT)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static Observable<Void> likeRim(RimInfo rimInfo) {
        rimInfo.setIsLiked(true);
        return apiService.addToFavourites(rimInfo.getId())
                .subscribeOn(Schedulers.io())
                .doOnCompleted(() -> DataProvider_.query().update(rimInfo)
                        .subscribe(rimInfo1 -> Log.d(TAG, "likeRim: " + rimInfo.getId())))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<RimInfoList> getAllRims() {
        Log.e(TAG, "getAllRims");
        return apiService.getRims()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

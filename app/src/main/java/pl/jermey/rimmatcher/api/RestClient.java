package pl.jermey.rimmatcher.api;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jermey on 29.11.2016.
 */

@EBean(scope = EBean.Scope.Singleton)
public class RestClient {

    public static final String ENDPOINT = "http://www.bellaryszarda.hekko24.pl/przemo/hotrims/index.php/";
    private static final String TAG = "RestClient";

    @RootContext
    Context context;

    public static ApiService apiService;

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


}

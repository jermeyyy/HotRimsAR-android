package pl.jermey.rimmatcher.api;

import pl.jermey.rimmatcher.model.RimInfoList;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Jermey on 29.11.2016.
 */

public interface ApiService {

    @POST("API/rims")
    Observable<RimInfoList> getRims();

    @FormUrlEncoded
    @POST("API/add_to_favourite")
    Observable<Void> addToFavourites(@Field("ID") String id);
}

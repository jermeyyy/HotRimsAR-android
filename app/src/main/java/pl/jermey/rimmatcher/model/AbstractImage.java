package pl.jermey.rimmatcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.requery.Entity;
import io.requery.Key;
import io.requery.ManyToOne;

/**
 * Created by Jermey on 18.12.2016.
 */
@Entity(stateless = true)
public abstract class AbstractImage {
    @Key
    @Expose
    @SerializedName("url")
    String url;
    @ManyToOne
    RimInfo rimInfo;
}

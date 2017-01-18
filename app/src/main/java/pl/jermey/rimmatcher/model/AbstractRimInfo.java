package pl.jermey.rimmatcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.requery.CascadeAction;
import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToMany;

/**
 * Created by Jermey on 27.11.2016.
 */
@Entity(stateless = true)
public abstract class AbstractRimInfo {
    @Key
    @Expose
    @SerializedName("id")
    String id;
    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("colorInfo")
    String colorInfo;
    @Expose
    @SerializedName("review")
    String review;
    @Expose
    @SerializedName("stars")
    String stars;
    @Expose
    @SerializedName("price")
    String price;
    @Expose
    @SerializedName("likes")
    String likes;
    @Expose
    @SerializedName("shipInfo")
    String shipInfo;
    @Expose
    @SerializedName("authorAvatar")
    String authorAvatar;
    @Expose
    @SerializedName("authorName")
    String authorName;
    @Expose
    @SerializedName("modelName")
    String modelName;
    @OneToMany(mappedBy = "rimInfo", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    @Expose
    @SerializedName("images")
    List<Image> images;
    Boolean isLiked;
}

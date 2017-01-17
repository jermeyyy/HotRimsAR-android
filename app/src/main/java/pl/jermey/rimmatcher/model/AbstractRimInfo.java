package pl.jermey.rimmatcher.model;

import java.util.List;

import io.requery.CascadeAction;
import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToMany;
import io.requery.Table;

/**
 * Created by Jermey on 27.11.2016.
 */
@Table(name = "rims")
@Entity(stateless = true)
public abstract class AbstractRimInfo {
    @Key
    String id;
    String name;
    String colorInfo;
    String review;
    String stars;
    String price;
    String likes;
    String shipInfo;
    String authorAvatar;
    String authorName;
    String modelName;
    @OneToMany(mappedBy = "rimInfo", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<Image> images;
    Boolean isLiked;

    public void setImages(List<Image> images) {
        this.images = images;
    }
}

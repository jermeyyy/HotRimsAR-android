package pl.jermey.rimmatcher.model;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jermey on 27.11.2016.
 */
@Parcel
@Getter
@Setter
public class RimInfo {

    String ID;
    String name;
    String colorInfo;
    String review;
    String image;

    public RimInfo() {
    }

    public RimInfo(String name, String colorInfo, String review, String image) {
        this.name = name;
        this.colorInfo = colorInfo;
        this.review = review;
        this.image = image;
    }
}

package pl.jermey.rimmatcher.model;

import org.parceler.Parcel;

import java.util.List;

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
    List<String> images;
    String stars;
    String price;
}

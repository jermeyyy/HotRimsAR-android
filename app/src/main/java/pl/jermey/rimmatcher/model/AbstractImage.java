package pl.jermey.rimmatcher.model;

import io.requery.Entity;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.Table;

/**
 * Created by Jermey on 18.12.2016.
 */
@Table(name = "images")
@Entity(stateless = true)
public abstract class AbstractImage {
    @Key
    String url;
    @ManyToOne
    RimInfo rimInfo;
}

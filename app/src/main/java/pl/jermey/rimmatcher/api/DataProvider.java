package pl.jermey.rimmatcher.api;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import pl.jermey.rimmatcher.model.Models;
import pl.jermey.rimmatcher.model.RimInfo;
import rx.Observable;

/**
 * Created by Jermey on 16.01.2017.
 */
@EBean(scope = EBean.Scope.Singleton)
public class DataProvider {

    @RootContext
    protected static Context context;

    private static SingleEntityStore<Persistable> dataStore;

    public static SingleEntityStore<Persistable> query() {
        if (dataStore == null) {
            DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, 1);
            Configuration configuration = source.getConfiguration();
            dataStore = RxSupport.toReactiveStore(
                    new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }

    public static Observable<List<RimInfo>> getAllRims() {
        return query().select(RimInfo.class)
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<RimInfo> getRimById(String rimId) {
        return query().findByKey(RimInfo.class, rimId)
                .toObservable();
    }
}

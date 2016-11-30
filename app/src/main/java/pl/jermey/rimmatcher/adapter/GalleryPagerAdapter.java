package pl.jermey.rimmatcher.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.List;

import pl.jermey.rimmatcher.R;

/**
 * Created by Karol on 29.11.2016.
 */

public class GalleryPagerAdapter extends PagerAdapter {

    private final Context context;
    private final GalleryPagerInterface galleryPagerInterface;
    private List<String> images;

    public GalleryPagerAdapter(Context context, List<String> images, GalleryPagerInterface galleryPagerInterface) {
        this.images = images;
        this.context = context;
        this.galleryPagerInterface = galleryPagerInterface;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = (ImageView) LayoutInflater.from(context).inflate(R.layout.gallery_item, container, false);
        Glide.with(context).load(images.get(position)).into(new ImageViewTarget<GlideDrawable>(view) {
            @Override
            protected void setResource(GlideDrawable resource) {
                view.setImageDrawable(resource);
                galleryPagerInterface.photoLoaded();
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public interface GalleryPagerInterface {
        void photoLoaded();
    }
}

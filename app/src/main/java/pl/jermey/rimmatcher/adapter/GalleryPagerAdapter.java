package pl.jermey.rimmatcher.adapter;

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
import pl.jermey.rimmatcher.base.BaseActivity;
import pl.jermey.rimmatcher.model.Image;

/**
 * Created by Karol on 29.11.2016.
 */

public class GalleryPagerAdapter extends PagerAdapter {

    private final BaseActivity context;
    private final GalleryPagerInterface galleryPagerInterface;
    private List<Image> images;

    public GalleryPagerAdapter(BaseActivity context, List<Image> images) {
        if (context instanceof GalleryPagerInterface) {
            this.images = images;
            this.context = context;
            this.galleryPagerInterface = (GalleryPagerInterface) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement " + GalleryPagerInterface.class.getSimpleName());
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = (ImageView) LayoutInflater.from(context).inflate(R.layout.gallery_item, container, false);
        view.setOnClickListener(view1 -> context.onBackPressed());
        Glide.with(context).load(images.get(position).getUrl()).into(new ImageViewTarget<GlideDrawable>(view) {
            @Override
            protected void setResource(GlideDrawable resource) {
                view.setImageDrawable(resource);
                if (position == 0)
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

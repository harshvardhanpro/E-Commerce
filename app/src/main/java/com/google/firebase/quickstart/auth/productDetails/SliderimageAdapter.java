package com.google.firebase.quickstart.auth.productDetails;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.quickstart.auth.R;

/**
 * Created by Harsh on 10-06-2017.
 */

public class SliderimageAdapter extends PagerAdapter {
    private int[] image_resources={
            R.drawable.shoe,
            R.drawable.shoe1,
            R.drawable.shoe2,
            R.drawable.shoe3,
            R.drawable.shoe4,
            R.drawable.shoe5,
            R.drawable.shoe6,
            R.drawable.shoe7,
    };

    private Context ctx;
    private LayoutInflater layoutInflater;
    public SliderimageAdapter(Context ctx){
        this.ctx=ctx;
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position){
    layoutInflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item_view = layoutInflater.inflate(R.layout.slider_image,container,false);
        ImageView imageView=(ImageView)item_view.findViewById(R.id.slider_imageView);
        imageView.setImageResource(image_resources[position]);
        container.addView(item_view);
        return item_view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}

package com.sportcityapp.sportsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sportcityapp.sportsapp.R;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    private int images[] = {
            R.drawable.city_logo,
            R.drawable.city_play,
            R.drawable.soccer
    };

    private String titles[] = {
            "WELCOME TO YOUR NEW SPORT CITY ONLINE APP",
            "MATCH DAY LIVE AND LATEST NEWS",
            "ENHANCE YOUR EXPERIENCE"
    };

    private String desc[] = {
            "Bringing you all the latest sport city online news and video, including exclusive content. All combined with our live Match day centre and daily experience.",
            "Stay up to date with the latest sport city online news and never miss a kick on match day.",
            "Register and log in to access our exclusive news and experience."
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_pager, container, false);

        //initiate views
        ImageView imageView = v.findViewById(R.id.imageViewPager);
        TextView txtTitle = v.findViewById(R.id.txtTitle);
        TextView txtDesc = v.findViewById(R.id.txtDesc);

        imageView.setImageResource(images[position]);
        txtTitle.setText(titles[position]);
        txtDesc.setText(desc[position]);

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}

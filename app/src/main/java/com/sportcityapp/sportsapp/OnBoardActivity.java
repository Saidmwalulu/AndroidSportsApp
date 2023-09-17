package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sportcityapp.sportsapp.Adapters.ViewPagerAdapter;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnLeft, btnRight;
    private ViewPagerAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        init();
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        dotsLayout = findViewById(R.id.dotsLayout);
        adapter = new ViewPagerAdapter(this);
        addDots(0);
        viewPager.addOnPageChangeListener(listener);
        viewPager.setAdapter(adapter);

        btnRight.setOnClickListener(v -> {
            if (btnRight.getText().toString().equals("Next")) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            } else {
                startActivity(new Intent(OnBoardActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnLeft.setOnClickListener(v -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+2);
        });

    }

    //dots creation using HTML code
    private void addDots(int position) {
        dotsLayout.removeAllViews();
        dots = new TextView[3];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            //html dots code
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.light_grey));
            dotsLayout.addView(dots[i]);
        }
        //change the selected color
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.grey));
        }
    }

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            //we have to change text of next button to finish at the end
            //also hide skip button if we are not in page 1
            if (position == 0) {
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            } else if (position == 1) {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            } else {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
package com.jenihaiti.com.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.Utility;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager viewPager;
    DotsIndicator dotsIndicator;
    TextView btnNext, btnSkip;
    WelcomeAdapter welcomeAdapter;
    private int[] layouts;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.fullScreen(WelcomeActivity.this);
        setContentView(R.layout.activity_welcome);
        Utility.screenCapOff(WelcomeActivity.this);

        Init();

        layouts = new int[]{R.layout.welcome_slide1, R.layout.welcome_slide2};
        welcomeAdapter = new WelcomeAdapter(WelcomeActivity.this, layouts);
        viewPager.setAdapter(welcomeAdapter);
        dotsIndicator.setViewPager(viewPager);

        if (layouts.length > 0) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    viewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % layouts.length);
                        }
                    });
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 10000, 10000);
        }
    }

    private void Init() {
        try {
            viewPager = findViewById(R.id.viewPager);
            dotsIndicator = findViewById(R.id.dots_indicator);

            btnNext = findViewById(R.id.btnNext);
            btnNext.setOnClickListener(this);

            btnSkip = findViewById(R.id.btnSkip);
            btnSkip.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception ==>", "" + e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                int current = getItem(+1);

                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
                break;

            case R.id.btnSkip:
                launchHomeScreen();
                break;
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    public static class WelcomeAdapter extends PagerAdapter {
        private int[] layouts;
        private Context context;

        public WelcomeAdapter(Context context, int[] layouts) {
            this.context = context;
            this.layouts = layouts;
            Log.e("layout size", "" + layouts.length);
        }

        @SuppressLint("LongLogTag")
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @SuppressLint("LongLogTag")
        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}

package com.example.bernardo.seguridadpersonal.PantallaPrincipal;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bernardo.seguridadpersonal.MainActivity;
import com.example.bernardo.seguridadpersonal.Principal;
import com.example.bernardo.seguridadpersonal.R;

public class HowWork extends AppCompatActivity {


    static final int TOTAL_PAGES = 6;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    boolean isOpaque = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();

        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_how_work);

        pager = (ViewPager) findViewById(R.id.pagerhw);
        pagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        // pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == TOTAL_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == TOTAL_PAGES - 1) {
                    endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //noinspection StatementWithEmptyBody
        /*if (pager != null) {
            //pager.clearOnPageChangeListeners();
        }*/
    }

    private void buildCircles() {
        circles = LinearLayout.class.cast(findViewById(R.id.circleshw));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < TOTAL_PAGES - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_brightness_1_black_24dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < TOTAL_PAGES) {
            for (int i = 0; i < TOTAL_PAGES - 1; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);
                if (i == index) {
                    int activeColor = Color.parseColor("#607D8B");
                    circle.setColorFilter(activeColor);
                } else {
                    int color = Color.parseColor("#ECF0F1");
                    circle.setColorFilter(color);
                }
            }
        }
    }

    private void endTutorial() {
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        if(getIntent().getIntExtra("comeTuto", 0)==1){
            Intent intent = new Intent(this, Principal.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlideAdapter extends FragmentStatePagerAdapter {

        public ScreenSlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TutorialFragment tutorialFragment = null;


            switch (position) {
                case 0:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen8);
                    break;
                case 1:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen9);
                    break;
                case 2:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen7);
                    break;
                case 3:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen2);
                    break;
                case 4:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen6);
                    break;
                case 5:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen4);
                    break;
                case 6:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen5);
                    break;
                case 7:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen1);
                    break;
                // Animacion final. Dejar este fragment siempre en el último case
                case 8:
                    tutorialFragment = TutorialFragment.newInstance(
                            R.layout.hw_fragment_screen4);
                    break;
            }

            return tutorialFragment;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }



}

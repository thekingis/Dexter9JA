package com.dexter9ja.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dexter9ja.android.Adapters.CarousalAdapter;
import com.dexter9ja.android.Utils.BlurBitmap;
import com.dexter9ja.android.Utils.CarousalDetails;
import com.dexter9ja.android.Utils.PageTransformer;
import com.dexter9ja.android.Utils.SharedCompactActivity;

import org.json.JSONException;

import java.util.ArrayList;

public class ExploreActivity extends SharedCompactActivity {

    Button accountBtn;
    LinearLayout containerView;
    int viewPagerHeight;
    int[][] cardImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        accountBtn = findViewById(R.id.accountBtn);
        containerView = findViewById(R.id.containerView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        viewPagerHeight = displayMetrics.widthPixels;

        cardImages = new int[][]{
                new int[] {
                        R.drawable.block_cards1,
                        R.drawable.block_cards2,
                        R.drawable.block_cards3,
                        R.drawable.block_cards4,
                        R.drawable.block_cards5,
                        R.drawable.block_cards6,
                        R.drawable.block_cards7,
                        R.drawable.block_cards8,
                        R.drawable.block_cards9,
                        R.drawable.block_cards10,
                        R.drawable.block_cards11
                },
                new int[] {
                        R.drawable.gr_card1,
                        R.drawable.gr_card2,
                        R.drawable.gr_card3,
                        R.drawable.gr_card4,
                        R.drawable.gr_card5,
                        R.drawable.gr_card6
                },
                new int[] {
                        R.drawable.key_cards1,
                        R.drawable.key_cards2,
                        R.drawable.key_cards3,
                        R.drawable.key_cards4,
                        R.drawable.key_cards5,
                        R.drawable.key_cards6,
                        R.drawable.key_cards7,
                        R.drawable.key_cards8,
                        R.drawable.key_cards9
                },
                new int[] {
                        R.drawable.kids_card1,
                        R.drawable.kids_card2,
                        R.drawable.kids_card3,
                        R.drawable.kids_card4,
                        R.drawable.kids_card5,
                        R.drawable.kids_card6,
                        R.drawable.kids_card7,
                        R.drawable.kids_card8,
                        R.drawable.kids_card9,
                        R.drawable.kids_card10,
                        R.drawable.kids_card11,
                        R.drawable.kids_card12
                }
        };

        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            if(sharedPrefMngr.loggedIn())
                intent = new Intent(context, DashboardAct.class);
            startActivity(intent);
        });

        setViewPagerAdapter();
    }

    @SuppressLint("InflateParams")
    private void setViewPagerAdapter() {
        for (int[] eachCardImages : cardImages) {
            int starter = (int) Math.ceil(eachCardImages.length / 2) - 1;
            String carousalTitle = "Lorem ipsum";
            LinearLayout carousalLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.carousal_layout, null);
            TextView textView = carousalLayout.findViewById(R.id.textView);
            ViewPager viewPager = carousalLayout.findViewById(R.id.viewPager);

            ArrayList<CarousalDetails> carousalDetails = new ArrayList<>();
            for (int cardImage : eachCardImages) {
                String title = "Lorem ipsum dolor sit";
                String desc = "Lorem ipsum dolor sit amet consectetur adipisicing elit. In iste, maiores minima natus dolores exercitationem animi eligendi, veniam eveniet sequi tempore facere autem.";
                double rating = 3.7;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), cardImage);
                CarousalDetails carousalDetail = new CarousalDetails(bitmap, title, desc, rating);
                carousalDetails.add(carousalDetail);
            }
            CarousalAdapter carousalAdapter = new CarousalAdapter(context, carousalDetails);

            textView.setText(carousalTitle);
            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
            params.height = viewPagerHeight;
            viewPager.setLayoutParams(params);
            viewPager.setOffscreenPageLimit(eachCardImages.length);
            viewPager.setAdapter(carousalAdapter);
            viewPager.setCurrentItem(starter);
            viewPager.setPageTransformer(true, new PageTransformer());
            viewPager.post(() -> {
                View view = viewPager.getChildAt(0);
                ImageView imageView = view.findViewById(R.id.image);
                imageView.post(() -> {
                    try {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        if (!(bitmap == null)) {
                            Bitmap blurredBtmp = BlurBitmap.blurify(bitmap);
                            Drawable drawable = new BitmapDrawable(context.getResources(), blurredBtmp);
                            viewPager.setBackground(drawable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    //setViewPagerChangeListener(viewPager, position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            containerView.addView(carousalLayout);
        }
    }

    private void setViewPagerChangeListener(ViewPager viewPager, int position) {
        View view = viewPager.getChildAt(position);
        ImageView imageView = view.findViewById(R.id.image);
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            if(!(bitmap == null)){
                Bitmap blurredBtmp = BlurBitmap.blurify(bitmap);
                Drawable drawable = new BitmapDrawable(context.getResources(), blurredBtmp);
                viewPager.setBackground(drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
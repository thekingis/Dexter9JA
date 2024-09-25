package com.dexter9ja.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.dexter9ja.android.Utils.SharedCompactActivity;
import com.dexter9ja.android.Utils.SharedPrefMngr;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends SharedCompactActivity {

    SharedPrefMngr sharedPrefMngr;
    Animation sgAnimation;
    TextSwitcher sliderTextSwitcher;
    TextView appNameTextView;
    ImageView imageViewSlider;
    LinearLayout containerView;
    RelativeLayout mainView, dexterPoint, mainLayout;
    Button exploreBtn, accountBtn;
    boolean canExit, stillLoading;
    int sliderCount;
    int[] sliderImages;
    String[] sliderTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        canExit = false;
        stillLoading = true;
        sliderCount = 0;
        sharedPrefMngr = new SharedPrefMngr(this);

        sliderImages = new int[]{
                R.drawable.dext1,
                R.drawable.dext2,
                R.drawable.dext3,
                R.drawable.dext4,
                R.drawable.dext5
        };
        sliderTexts = new String[]{
                "From Excitement to Code Mastery!",
                "Coding is right for Everyone",
                "The place for Champs",
                "Kids and Teens can become Coding Experts",
                "The Best Coding Program Ever Packaged for Kids and Teens"
        };

        mainView = findViewById(R.id.mainView);
        exploreBtn = findViewById(R.id.exploreBtn);
        accountBtn = findViewById(R.id.accountBtn);
        mainLayout = findViewById(R.id.mainLayout);
        dexterPoint = findViewById(R.id.dexterPoint);
        containerView = findViewById(R.id.containerView);
        sliderTextSwitcher = findViewById(R.id.sliderTextSwitcher);
        imageViewSlider = findViewById(R.id.imageViewSlider);
        appNameTextView = findViewById(R.id.appNameTextView);

        sgAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink_grow);
        dexterPoint.startAnimation(sgAnimation);
        sliderTextSwitcher.setInAnimation(context, R.anim.slide_in_right);
        sliderTextSwitcher.setOutAnimation(context, R.anim.slide_out_left);
        String text = "<font color=#9665FF>Dexter</font><font color=#26d48c>9ja</font>";
        appNameTextView.setText(Html.fromHtml(text, 0));

        exploreBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExploreActivity.class);
            startActivity(intent);
        });
        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            if(sharedPrefMngr.loggedIn())
                intent = new Intent(context, DashboardAct.class);
            startActivity(intent);
        });

        new android.os.Handler().postDelayed(this::loadNextPage, 5000);

    }

    private void loadNextPage() {
        stillLoading = false;
        dexterPoint.clearAnimation();
        TransitionManager.beginDelayedTransition(mainView, new AutoTransition());
        mainLayout.setVisibility(View.VISIBLE);
        containerView.setVisibility(View.GONE);
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                animateImageViewChange();
            }
        },0,3500);
    }

    private void animateImageViewChange() {
        sliderCount++;
        if(sliderCount == sliderImages.length)
            sliderCount = 0;
        String sliderText = sliderTexts[sliderCount];
        runOnUiThread(() -> sliderTextSwitcher.setText(sliderText));
        int drawableImage = sliderImages[sliderCount];
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableImage);;
        final Animation anim_out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                imageViewSlider.setImageBitmap(bitmap);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                imageViewSlider.startAnimation(anim_in);
            }
        });
        imageViewSlider.startAnimation(anim_out);
    }

    @Override
    public void onBackPressed() {
        if(stillLoading)
            return;
        if (canExit) {
            super.onBackPressed();
            return;
        }
        canExit = true;
        Toast.makeText(this, "Tap again to Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> canExit = false, 2000);
    }
}
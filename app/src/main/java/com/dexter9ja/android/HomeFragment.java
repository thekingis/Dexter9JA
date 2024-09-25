package com.dexter9ja.android;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dexter9ja.android.Utils.SharedPrefMngr;

public class HomeFragment extends Fragment {

    Context context;
    RelativeLayout bounceView, swingView;
    TextView welcomeTextView;
    String firstName, welcomeText;
    SharedPrefMngr sharedPrefMngr;

    public HomeFragment(Context context) {
        this.context = context;
        this.sharedPrefMngr = new SharedPrefMngr(context);
        this.firstName = this.sharedPrefMngr.getUserFirstName();
        this.welcomeText = "<font>Hi <b>"+firstName+"</b>, Welcome back! Click to continue your learning process from your last progress on your current course.<font>";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bounceView = view.findViewById(R.id.bounceView);
        swingView = view.findViewById(R.id.swingView);
        welcomeTextView = view.findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(Html.fromHtml(welcomeText, 0));

        doBounceAnimation(bounceView);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.swinging);
        swingView.startAnimation(animation);

        return view;
    }

    private void doBounceAnimation(View targetView) {
        Interpolator interpolator = v -> {
            return getPowOut(v,2);//Add getPowOut(v,3); for more up animation
        };
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, 25, 0);
        animator.setInterpolator(interpolator);
        animator.setStartDelay(200);
        animator.setDuration(800);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }

    private float getPowOut(float elapsedTimeRate, double pow) {
        return (float) ((float) 1 - Math.pow(1 - elapsedTimeRate, pow));
    }
}
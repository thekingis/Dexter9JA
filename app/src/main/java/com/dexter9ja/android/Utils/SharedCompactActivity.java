package com.dexter9ja.android.Utils;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SharedCompactActivity extends AppCompatActivity {

    public Context context;
    public Activity activity;
    public ScrollView menuView;
    public SharedPrefMngr sharedPrefMngr;
    public LinearLayout blackFade;
    public boolean canCloseBlackFade, menuIsVisible;
    public static final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[a-z\\d._-]{3,15}$");
    public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        activity = this;
        menuIsVisible = false;
        canCloseBlackFade = false;
        sharedPrefMngr = new SharedPrefMngr(this);

    }

    public static boolean validatePattern(String patternStr, String text) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    public static boolean validateUsername(String usernameStr) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(usernameStr);
        return matcher.matches();
    }

    public static boolean validatePassword(String passwordStr) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(passwordStr);
        return matcher.matches();
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        ArrayList<EditText> editTexts = getAllEditTexts(view);
        if(editTexts.size() > 0){
            for(int x = 0; x < editTexts.size(); x++){
                EditText editText = editTexts.get(x);
                editText.clearFocus();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        view.setOnTouchListener((v, event) -> {
            if(menuIsVisible) {
                boolean isMenuToggle = false;
                boolean isExcluded = false;
                Object tag = view.getTag();
                if(!(tag == null)) {
                    String viewTag = tag.toString();
                    String[] viewTags = viewTag.split(",");
                    isExcluded = Arrays.asList(viewTags).contains("isExcluded");
                    isMenuToggle = Arrays.asList(viewTags).contains("menuToggle");
                }
                if(!isMenuToggle && !isExcluded)
                    toggleMenuLayout(view);
            }
            if (!(view instanceof EditText))
                hideSoftKeyboard(v);
            return false;
        });

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void toggleMenuLayout(View view) {
        if(!(menuView == null) && !(menuView == view)) {
            int mStart = 0, mStop = -menuView.getWidth();
            if (!menuIsVisible) {
                mStart = -menuView.getWidth();
                mStop = 0;
            }
            menuIsVisible = !menuIsVisible;
            ValueAnimator animator = ValueAnimator.ofInt(mStart, mStop);
            animator.addUpdateListener(animation -> {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuView.getLayoutParams();
                params.leftMargin = (Integer) animation.getAnimatedValue();
                menuView.requestLayout();
            });
            animator.setDuration(300);
            animator.start();
            /*Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    menuView.setLayoutParams(params);
                }
            };*/
        }
    }

    private ArrayList<EditText> getAllEditTexts(View view){
        ArrayList<EditText> editTexts = new ArrayList<>();
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                if (innerView instanceof EditText)
                    editTexts.add((EditText) innerView);
            }
        }
        return editTexts;
    }

    /*@Override
    public void onBackPressed() {
        if(menuIsVisible) {
            toggleMenuLayout(null);
            return;
        }
        if(canCloseBlackFade && !(blackFade == null)){
            blackFade.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }*/
}

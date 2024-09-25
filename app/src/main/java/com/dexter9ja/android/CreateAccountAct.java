package com.dexter9ja.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.dexter9ja.android.Utils.ApiUrls;
import com.dexter9ja.android.Utils.SharedCompactActivity;
import com.dexter9ja.android.Utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAccountAct extends SharedCompactActivity {

    RelativeLayout mainView, containerView;
    LinearLayout nameView, emailView, passwordView, detailsView, datePickerView, hintsView;
    TextView headerText, datePicker;
    CalendarView calendarView;
    Button prevBtn, nextBtn, createBtn, doneBtn;
    EditText firstName, lastName, email, confirmEmail, password, confirmPassword, username, phoneNumber;
    int activeLayout;
    LinearLayout[] layouts;
    JSONObject passwordHints;
    DateFormat dateFormat, dateTimeFormat;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        activeLayout = 0;

        mainView = findViewById(R.id.mainView);
        datePicker = findViewById(R.id.datePicker);
        headerText = findViewById(R.id.headerText);
        blackFade = findViewById(R.id.blackFade);
        datePickerView = findViewById(R.id.datePickerView);
        calendarView = findViewById(R.id.calendarView);
        hintsView = findViewById(R.id.hintsView);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        confirmEmail = findViewById(R.id.confirmEmail);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.phoneNumber);
        containerView = findViewById(R.id.containerView);
        nameView = findViewById(R.id.nameView);
        emailView = findViewById(R.id.emailView);
        passwordView = findViewById(R.id.passwordView);
        detailsView = findViewById(R.id.detailsView);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        doneBtn = findViewById(R.id.doneBtn);
        createBtn = findViewById(R.id.createBtn);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String text = "<font color=#FFFFFF>Dexter</font><font color=#26d48c>9ja</font>";
        headerText.setText(Html.fromHtml(text, 0));

        layouts = new LinearLayout[] {nameView, emailView, passwordView, detailsView};
        TransitionManager.beginDelayedTransition(containerView, new AutoTransition());
        nameView.setVisibility(View.VISIBLE);

        passwordHints = new JSONObject();
        try {
            passwordHints.put("^.{8,}$", "Must contain at least 8 characters");
            passwordHints.put(".*\\d.*", "Must contain at least one number");
            passwordHints.put(".*[a-z].*", "Must contain at least one lowercase alphabet");
            passwordHints.put(".*[A-Z].*", "Must contain at least one uppercase alphabet");
            passwordHints.put(".*[@#$%^&+=].*", "Must contain at least one special character");
            passwordHints.put("(\\S+$)$", "Must not contain blank space");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        prevBtn.setOnClickListener((v) -> swipeToView(-1));
        nextBtn.setOnClickListener((v) -> swipeToView(1));
        createBtn.setOnClickListener((v) -> createUserAccount());
        datePicker.setOnClickListener((v) -> datePickerView.setVisibility(View.VISIBLE));
        doneBtn.setOnClickListener((v) -> {
            long selectedDate = calendarView.getDate();
            String dateStr = dateFormat.format(selectedDate);
            datePicker.setText(dateStr);
            datePickerView.setVisibility(View.GONE);
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkMatch(confirmEmail, confirmEmail.getText(), editable, email);
            }
        });
        confirmEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkMatch(confirmEmail, email.getText(), editable, null);
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String passwordStr = editable.toString();
                boolean isValidPassword = validatePassword(passwordStr);
                Editable e = isValidPassword ? editable : null;
                int drawable = isValidPassword ? R.drawable.ic_checked_green : R.drawable.ic_bad_red;
                password.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                checkMatch(confirmPassword, confirmPassword.getText(), e, null);
                for(int i = 0; i < hintsView.getChildCount(); i++){
                    LinearLayout linearLayout = (LinearLayout) hintsView.getChildAt(i);
                    TextView textView = (TextView) linearLayout.findViewById(R.id.textView);
                    String pattern = textView.getTag().toString();
                    boolean valid = validatePattern(pattern, passwordStr);
                    int color = valid ? R.color.green : R.color.red;
                    int drwable = valid ? R.drawable.ic_checked_green : R.drawable.ic_bad_red;
                    textView.setTextColor(ContextCompat.getColor(context, color));
                    textView.setCompoundDrawablesWithIntrinsicBounds(drwable, 0, 0, 0);
                }
            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkMatch(confirmPassword, password.getText(), editable, null);
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String usernameStr = editable.toString();
                boolean isValidUsername = validateUsername(usernameStr);
                int drawable = isValidUsername ? R.drawable.ic_checked_green : R.drawable.ic_bad_red;
                username.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
            }
        });

        setupUI(mainView);
        try {
            addPasswordHints();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void addPasswordHints() throws JSONException {
        for(int i = 0; i < passwordHints.length(); i++){
            String patternKey = Objects.requireNonNull(passwordHints.names()).getString(i);
            String text = passwordHints.getString(patternKey);
            LinearLayout hintTextView = (LinearLayout) getLayoutInflater().inflate(R.layout.hint_text_view, null);
            TextView textView = hintTextView.findViewById(R.id.textView);
            textView.setText(text);
            textView.setTag(patternKey);
            hintsView.addView(hintTextView);
        }
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void createUserAccount() {
        LinearLayout currentView = layouts[activeLayout];
        for(int x = 0; x < currentView.getChildCount(); x++){
            View view = currentView.getChildAt(x);
            if(view instanceof TextView){
                TextView textView = (TextView) view;
                String text = textView.getText().toString();
                if(StringUtils.isEmpty(text)) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        String usernameStr = username.getText().toString();
        boolean isValid = validateUsername(usernameStr);
        if(!isValid){
            Toast.makeText(this, "Your Username is not valid", Toast.LENGTH_LONG).show();
            return;
        }
        if(blackFade.getChildCount() > 0)
            blackFade.removeAllViews();
        canCloseBlackFade = false;
        LinearLayout requestLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.requesting_layout, null);
        TextView textView = requestLayout.findViewById(R.id.textView);
        textView.setText("Creating Account...");
        blackFade.addView(requestLayout);
        blackFade.setVisibility(View.VISIBLE);
        String firstName = this.firstName.getText().toString(),
                lastName = this.lastName.getText().toString(),
                email = this.email.getText().toString(),
                password = this.password.getText().toString(),
                username = this.username.getText().toString(),
                dateOfBirth = this.datePicker.getText().toString(),
                phoneNumber = this.phoneNumber.getText().toString();

        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBody.addFormDataPart("first_name", firstName)
                .addFormDataPart("last_name", lastName)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .addFormDataPart("username", username)
                .addFormDataPart("dob", dateOfBirth)
                .addFormDataPart("user_type", "Student")
                .addFormDataPart("phone", phoneNumber)
                .addFormDataPart("parents_email", "");

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        RequestBody requestBody = multipartBody.build();
        Request request = new Request.Builder()
                .url(ApiUrls.authCreateAccountUrl)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                canCloseBlackFade = true;
                runOnUiThread(() -> {
                    LinearLayout alertBox = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_box, null);
                    TextView textView = alertBox.findViewById(R.id.textView);
                    Button leftBtn = alertBox.findViewById(R.id.leftBtn);
                    Button rightBtn = alertBox.findViewById(R.id.rightBtn);
                    textView.setText("An error occurred. Please try again");
                    leftBtn.setText("Close");
                    rightBtn.setVisibility(View.GONE);
                    blackFade.removeView(requestLayout);
                    blackFade.addView(alertBox);
                    leftBtn.setOnClickListener((v) -> blackFade.setVisibility(View.GONE));
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseString = Objects.requireNonNull(response.body()).string();
                Log.e("message", responseString);
                /*try {
                    canCloseBlackFade = true;
                    JSONObject object = new JSONObject(responseString);
                    boolean hasError = object.getBoolean("hasError");
                    if(hasError){
                        String errorText = object.getString("errorText");
                        runOnUiThread(() -> {
                            LinearLayout alertBox = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_box, null);
                            TextView textView = alertBox.findViewById(R.id.textView);
                            Button leftBtn = alertBox.findViewById(R.id.leftBtn);
                            Button rightBtn = alertBox.findViewById(R.id.rightBtn);
                            textView.setText(errorText);
                            leftBtn.setText("Close");
                            rightBtn.setVisibility(View.GONE);
                            blackFade.removeView(requestLayout);
                            blackFade.addView(alertBox);
                            leftBtn.setOnClickListener((v) -> blackFade.setVisibility(View.GONE));
                        });
                    } else {
                        int userID = object.getInt("userID");
                        String firstName = object.getString("firstName");
                        String lastName = object.getString("lastName");
                        String reputation = object.getString("reputation");
                        String photoURL = object.getString("photoURL");
                        String name = firstName + " " + lastName;
                        sharedPrefMngr.storeUserInfo(userID, photoURL, name, firstName, reputation);
                        Intent intent = new Intent(context, DashboardAct.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    private void checkMatch(EditText editText, Editable text, Editable editable, EditText editTextView) {
        boolean isValidEmail = true, toValidate = !(editTextView == null);
        if(toValidate){
            String emailStr = editTextView.getText().toString();
            isValidEmail = validateEmail(emailStr);
            int drwable = isValidEmail ? R.drawable.ic_checked_green : R.drawable.ic_bad_red;
            editTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drwable, 0);
            if(editText == null)
                return;
        }
        if(editable == null){
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return;
        }
        String str1 = text.toString(), str2 = editable.toString();
        if(StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)){
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return;
        }
        int drawable = str1.equals(str2) && isValidEmail ? R.drawable.ic_checked_green : R.drawable.ic_bad_red;
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
    }

    private void swipeToView(int i) {
        String textStr, matchStr;
        Boolean isValid = null;
        if(i > 0){
            LinearLayout currentView = layouts[activeLayout];
            for(int x = 0; x < currentView.getChildCount(); x++){
                View view = currentView.getChildAt(x);
                if(view instanceof TextView){
                    TextView textView = (TextView) view;
                    String text = textView.getText().toString();
                    if(StringUtils.isEmpty(text)) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            if(currentView == emailView){
                textStr = email.getText().toString();
                matchStr = confirmEmail.getText().toString();
                isValid = validateEmail(textStr);
                if(!isValid){
                    Toast.makeText(this, "Your Email is not valid", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!textStr.equals(matchStr)){
                    Toast.makeText(this, "Your Emails do not match", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if(currentView == passwordView){
                textStr = password.getText().toString();
                matchStr = confirmPassword.getText().toString();
                isValid = validatePassword(textStr);
                if(!isValid){
                    Toast.makeText(this, "Your Password is not valid", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!textStr.equals(matchStr)){
                    Toast.makeText(this, "Your Passwords do not match", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        int prevLayout = activeLayout;
        activeLayout += i;
        boolean isFirstView = activeLayout == -1;
        boolean isLastView = activeLayout == layouts.length;
        activeLayout = isFirstView ? 0 : activeLayout;
        activeLayout = isLastView ? layouts.length - 1 : activeLayout;
        LinearLayout prevView = layouts[prevLayout];
        LinearLayout nextView = layouts[activeLayout];
        int prevBtnShow = activeLayout == 0 ? View.INVISIBLE : View.VISIBLE;
        int nextBtnShow = (activeLayout + 1) == layouts.length ? View.GONE : View.VISIBLE;
        int createBtnShow = (activeLayout + 1) == layouts.length ? View.VISIBLE : View.GONE;
        prevBtn.setVisibility(prevBtnShow);
        nextBtn.setVisibility(nextBtnShow);
        createBtn.setVisibility(createBtnShow);
        TransitionManager.beginDelayedTransition(containerView, new AutoTransition());
        prevView.setVisibility(View.INVISIBLE);
        nextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(activeLayout > 0){
            swipeToView(-1);
            return;
        }
        super.onBackPressed();
    }
}
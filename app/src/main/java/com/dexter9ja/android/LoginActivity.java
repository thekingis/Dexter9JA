package com.dexter9ja.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dexter9ja.android.Utils.ApiUrls;
import com.dexter9ja.android.Utils.Constants;
import com.dexter9ja.android.Utils.SharedCompactActivity;
import com.dexter9ja.android.Utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends SharedCompactActivity {

    RelativeLayout mainView;
    TextView headerText, errorLogTextView;
    ImageView imageLoader;
    EditText usernameBox, passwordBox;
    Button createAccountBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainView = findViewById(R.id.mainView);
        headerText = findViewById(R.id.headerText);
        errorLogTextView = findViewById(R.id.errorLogTextView);
        loginBtn = findViewById(R.id.loginBtn);
        usernameBox = findViewById(R.id.usernameBox);
        passwordBox = findViewById(R.id.passwordBox);
        imageLoader = findViewById(R.id.imageLoader);
        createAccountBtn = findViewById(R.id.createAccountBtn);

        String text = "<font color=#FFFFFF>Dexter</font><font color=#26d48c>9ja</font>";
        headerText.setText(Html.fromHtml(text, 0));

        loginBtn.setOnClickListener(v -> sendLoginRequest());
        createAccountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateAccountAct.class);
            startActivity(intent);
        });

        setupUI(mainView);
    }

    @SuppressLint("SetTextI18n")
    private void sendLoginRequest() {
        errorLogTextView.setText("");
        errorLogTextView.setVisibility(View.GONE);
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        if(StringUtils.isEmpty(username)){
            errorLogTextView.setVisibility(View.VISIBLE);
            errorLogTextView.setText("Please fill in your username");
            return;
        }
        if(StringUtils.isEmpty(password)){
            errorLogTextView.setVisibility(View.VISIBLE);
            errorLogTextView.setText("Please fill in your password");
            return;
        }
        loginBtn.setEnabled(false);
        imageLoader.setVisibility(View.VISIBLE);

        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBody.addFormDataPart("username", username)
                .addFormDataPart("password", password);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        RequestBody requestBody = multipartBody.build();
        Request request = new Request.Builder()
                .url(ApiUrls.authLoginUrl)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    imageLoader.setVisibility(View.GONE);
                    errorLogTextView.setVisibility(View.VISIBLE);
                    errorLogTextView.setText("An error occurred. Please try again");
                    loginBtn.setEnabled(true);
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String responseString = Objects.requireNonNull(response.body()).string();
                    JSONObject responseObj = new JSONObject(responseString);
                    runOnUiThread(() -> {
                        imageLoader.setVisibility(View.GONE);
                    });
                    if(response.isSuccessful()){
                        sharedPrefMngr.storeUserInfo(responseObj);
                        Intent intent = new Intent(context, DashboardAct.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(() -> {
                            errorLogTextView.setVisibility(View.VISIBLE);
                            errorLogTextView.setText("Invalid Username or Password");
                            loginBtn.setEnabled(true);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
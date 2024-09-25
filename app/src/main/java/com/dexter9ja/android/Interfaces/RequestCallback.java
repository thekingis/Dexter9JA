package com.dexter9ja.android.Interfaces;

public interface RequestCallback {
    void onComplete(String response);
    void onError(Exception e);
}

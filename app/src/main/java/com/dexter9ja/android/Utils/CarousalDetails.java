package com.dexter9ja.android.Utils;

import android.graphics.Bitmap;

public class CarousalDetails {

    public Bitmap image;
    public String title, description;
    public double rating;

    public CarousalDetails(Bitmap image, String title, String description, double rating) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.rating = rating;
    }
}

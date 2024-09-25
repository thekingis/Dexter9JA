package com.dexter9ja.android.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.dexter9ja.android.R;
import com.dexter9ja.android.Utils.CarousalDetails;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CarousalAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<CarousalDetails> carousalDetails;
    public static Handler UIHandler;
    static
    {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public CarousalAdapter(Context context, ArrayList<CarousalDetails> carousalDetails) {
        this.context = context;
        this.carousalDetails = carousalDetails;
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    @Override
    public int getCount() {
        return carousalDetails.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return carousalDetails.indexOf(object);
    }

    public ArrayList<CarousalDetails> getAllItems() {
        return carousalDetails;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        getItemPosition(container);
        CarousalDetails carousalDetail = carousalDetails.get(position);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.carousal_view, container, false);
        ImageView imageView =  view.findViewById(R.id.image);
        TextView titleTextView =  view.findViewById(R.id.title);
        TextView descTextView =  view.findViewById(R.id.description);

        imageView.setImageBitmap(carousalDetail.image);
        titleTextView.setText(carousalDetail.title);
        descTextView.setText(carousalDetail.description);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        runOnUI(() -> container.removeView((View)object));
    }
}

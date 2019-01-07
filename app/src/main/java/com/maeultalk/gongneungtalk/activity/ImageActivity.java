package com.maeultalk.gongneungtalk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.contents.setting.RecyclerViewAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {

    PhotoViewAttacher mAttacher;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = (ImageView) findViewById(R.id.imageView);


        Intent intent = getIntent();
        String individual_image = intent.getExtras().getString("individual_image");

        Glide.with(this).load("http://gongneungtalk.cafe24.com/images/" + individual_image)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (mAttacher != null) {
                            mAttacher.update();
                        } else {
                            mAttacher = new PhotoViewAttacher(imageView);
                            //mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        return false;
                    }
                })
                //.thumbnail(0.1f) // 썸네일 설정할 경우 PhotoViewAttacher 제대로 작동 안함.
                //.fitCenter()
                .into(imageView);

    }

}
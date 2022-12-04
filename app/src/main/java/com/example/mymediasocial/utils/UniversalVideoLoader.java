package com.example.mymediasocial.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.ProgressBar;

import com.example.mymediasocial.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sprylab.android.widget.TextureVideoView;

public class UniversalVideoLoader {
    private static final int defaultImage = R.drawable.black_mamba;
    private Context mContext;

    public UniversalVideoLoader(Context context) {
        mContext = context;
    }

    public ImageLoaderConfiguration getConfig() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .considerExifParams(true)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        return configuration;
    }

    public static void setVideo(String videoPath, TextureVideoView view, final ProgressBar mProgressBar, String append) {

        view.setVideoPath(append + videoPath);
//        mVideoView.setMediaController(new MediaController(MainActivity.this));
        TextureVideoView finalView = view;
        view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                finalView.start();
            }
        });


    }


}

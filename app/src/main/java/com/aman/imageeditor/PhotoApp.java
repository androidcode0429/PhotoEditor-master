package com.aman.imageeditor;

import android.app.Application;
import android.content.Context;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

public class PhotoApp extends Application {
    private static PhotoApp sPhotoApp;
    private static final String TAG = PhotoApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        sPhotoApp = this;

        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig(PhotoConstants.TWITTER_CONSUMER_KEY, PhotoConstants.TWITTER_CONSUMER_SECRET))
                .build();
        Twitter.initialize(config);

     /*   FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);

        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest)
                .setReplaceAll(true)
                //    .setEmojiSpanIndicatorEnabled(true)
                //     .setEmojiSpanIndicatorColor(Color.GREEN)
                .registerInitCallback(new EmojiCompat.InitCallback() {
                    @Override
                    public void onInitialized() {
                        super.onInitialized();
                        Log.e(TAG, "Success");
                    }

                    @Override
                    public void onFailed(@Nullable Throwable throwable) {
                        super.onFailed(throwable);
                        Log.e(TAG, "onFailed: " + throwable.getMessage());
                    }
                });

     //   BundledEmojiCompatConfig bundledEmojiCompatConfig = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);*/
    }

    public static PhotoApp getPhotoApp() {
        return sPhotoApp;
    }

    public Context getContext() {
        return sPhotoApp.getContext();
    }
}

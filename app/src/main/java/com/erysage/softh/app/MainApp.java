package com.erysage.softh.app;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by softh on 13.12.2016.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        sFaceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
        sEmotionServiceClient = new EmotionServiceRestClient(getString(R.string.emotion_subscription_key));
        if(sFaceServiceClient == null){
            System.out.print("null");
        }
        VKSdk.initialize(this);

    }

    public static FaceServiceClient getFaceServiceClient() {
        return sFaceServiceClient;
    }
    public static EmotionServiceClient getEmotionServiceClient() {
        return sEmotionServiceClient;
    }
    private static FaceServiceClient sFaceServiceClient;
    private static EmotionServiceClient sEmotionServiceClient;
}

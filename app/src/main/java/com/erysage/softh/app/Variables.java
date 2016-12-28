package com.erysage.softh.app;

import android.content.Context;
import android.graphics.Bitmap;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

/**
 * Created by softh on 14.12.2016.
 */

public class Variables {
    public static Bitmap processed_bitmap;
    public static final String[] vkScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.GROUPS
    };
    public static  String age;

    public static void VkLogin(Context context, String[]scope){
      //  VKSdk.login(this, Variables.sMyScope);
    }
}

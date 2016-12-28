package com.erysage.softh.app.helpers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by softh on 28.12.2016.
 */

public class ViewHelper {
    public static void showMessageInToast(Context context, String text){
        Toast.makeText(context, text,
                Toast.LENGTH_LONG).show();
    }
}

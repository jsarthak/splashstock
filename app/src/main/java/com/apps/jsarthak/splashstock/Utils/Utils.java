package com.apps.jsarthak.splashstock.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Utils {

    Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    public static void setTypeFace(Context context){
        FontsOverride.setDefaultFont(context, "DEFAULT", "font/google_sans_regular.ttf");
        FontsOverride.setDefaultFont(context, "MONOSPACE", "font/google_sans_regular.ttf");
        FontsOverride.setDefaultFont(context, "SERIF", "font/google_sans_regular.ttf");
        FontsOverride.setDefaultFont(context, "SANS_SERIF", "font/google_sans_regular.ttf");
    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    public static void setLightStatusBar(View view, Activity activity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }


}

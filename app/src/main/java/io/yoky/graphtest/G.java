package io.yoky.graphtest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.HashMap;

public class G {
    public static Context ctx;
    public static HashMap<String, Typeface> fonts;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Point getDeviceSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Typeface getTypeface(Context context, String name) {
        if (fonts == null) fonts = new HashMap<>();
        if (fonts.containsKey(name)) return fonts.get(name);
        Typeface typeface = Typeface.createFromAsset(context.getResources().getAssets(), name + ".otf");
        fonts.put(name, typeface);
        return typeface;
    }

    public static float getDisplayScale(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.scaledDensity;
    }
}

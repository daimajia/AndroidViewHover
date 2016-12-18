package com.daimajia.androidviewhover.tools;

import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.Matrix;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;


public class Blur {

    private static final int DEFAULT_BLUR_RADIUS = 10;
    private static final float DEFAULT_SCALE = 2;

    public static Bitmap apply(Context context, Bitmap sentBitmap) {
        return apply(context, sentBitmap, DEFAULT_BLUR_RADIUS, DEFAULT_SCALE);
    }

    public static Bitmap apply(Context context, Bitmap sentBitmap, int radius) {
        // shrink half
        sentBitmap = scaleBitmap(sentBitmap, 1 / DEFAULT_SCALE);
        final Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        final RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        //zoom bitmap
        final Bitmap zoomBitmap = scaleBitmap(bitmap, DEFAULT_SCALE);
        bitmap.recycle();
        sentBitmap.recycle();
        rs.destroy();
        input.destroy();
        output.destroy();
        script.destroy();

        return zoomBitmap;
    }

    public static Bitmap scaleBitmap(Bitmap oldBitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);
    }
}

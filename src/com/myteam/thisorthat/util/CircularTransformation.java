package com.myteam.thisorthat.util;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircularTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        Bitmap finalBitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(finalBitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        source.recycle();

        return finalBitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
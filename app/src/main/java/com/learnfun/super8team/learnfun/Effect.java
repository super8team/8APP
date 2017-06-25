package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by KIM on 2017-06-21.
 */

public class Effect extends View {

    private ArrayList<Bitmap> image = new ArrayList<Bitmap>();
    private Context context;
    private ContentActivity contentActivity;
    private int x;
    private int y;
    private Random random = new Random();

    public Effect(ContentActivity context,int x,int y) {
        super(context);
        this.contentActivity = context;
        this.context = context;
        this.x = x;
        this.y = y;
        Resources r = context.getResources();
        image.add(0, BitmapFactory.decodeResource(r, R.drawable.t1));
        image.add(1, BitmapFactory.decodeResource(r, R.drawable.t2));
        image.add(2, BitmapFactory.decodeResource(r, R.drawable.t3));
        image.add(3, BitmapFactory.decodeResource(r, R.drawable.t4));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int r = random.nextInt(4);
//        Log.i("랜덤값", String.valueOf(r));
        canvas.drawBitmap(image.get(r),x-250,y-140,null);

        contentActivity.getOverlayLayout().removeView(this);


    }
}

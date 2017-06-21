package com.learnfun.super8team.learnfun.AR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.learnfun.super8team.learnfun.Content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bon on 2017-06-07.
 */

public class AROverlayView extends View {
    private final String TAG = "AROverlayView";

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<Content> arPoints;


    public AROverlayView(Context context) {
        super(context);
        this.context = context;

        //Demo points
        arPoints = new ArrayList<Content>();
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
//        Log.e(TAG, "updateCurrentLocation!");
        this.invalidate();
    }

    public void addOverlayContent(Content content) {
        arPoints.add(content);
    }

    public List<Float> getNavigationPoint (Location location, int i) {
        float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
        float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getContentLocation());
        float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

        float[] cameraCoordinateVector = new float[4];
        Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

        // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
        // if z > 0, the point will display on the opposite

            final float changeX  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * this.getWidth();
            final float changeY = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * this.getHeight();

        return new ArrayList<Float>() {{
            add(changeX);
            add(changeY);
        }};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentLocation == null) {
            return;
        }
        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);
        for (int i = 0; i < arPoints.size(); i ++) {

            if(arPoints.get(i).getVisionable()) {
                float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
                float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getContentLocation());
                float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

                float[] cameraCoordinateVector = new float[4];
                Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

                // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
                // if z > 0, the point will display on the opposite
                if (cameraCoordinateVector[2] < 0) {

                    float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
                    float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();

                    canvas.drawCircle(x, y, radius, paint);
                    canvas.drawText(arPoints.get(i).getContentName(), x - (30 * arPoints.get(i).getContentName().length() / 2), y - 80, paint);
                }
            }

        }

    }



}

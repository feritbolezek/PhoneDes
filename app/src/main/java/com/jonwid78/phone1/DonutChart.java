package com.jonwid78.phone1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Ferit on 05-Oct-17.
 */

public class DonutChart extends View {

    Paint paint = null;
    Paint bgpaint = null;

    private Float endValue = 20f;

    public Float getProgressValue() {
        return  endValue;
    }
    /**
     * Set the progress value anywhere from 0 to 100. It will start drawing from 0°(0.0) to 360°(100.0).
     * @param value The amount of progress you wish to be made from the starting point of 0.
     *
     */
    public void setProgressValue(Float value) {
        endValue = value;
    }
    public DonutChart(Context context) {
        super(context);
        setupPaint();
    }

    public DonutChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    public DonutChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupPaint();
    }

    public DonutChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(20,20,getWidth() - 20,getWidth() - 20,-90, (3.6f* 100),false,bgpaint);
        canvas.drawArc(20,20,getWidth() - 20,getWidth() - 20,-90, (3.6f* endValue),false,paint);
    }

    private void setupPaint() {
        paint = new Paint();
        bgpaint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(25);


        bgpaint.setStyle(Paint.Style.STROKE);
        bgpaint.setStrokeWidth(25);

        paint.setColor(getResources().getColor(R.color.colorAccent));
        bgpaint.setColor(getResources().getColor(R.color.colorBlack));

    }


}

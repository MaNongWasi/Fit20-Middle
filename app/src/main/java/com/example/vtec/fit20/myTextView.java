package com.example.vtec.fit20;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by VTEC on 1/20/2017.
 */
public class myTextView extends TextView{
    private String text = "0";

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public myTextView(Context context) {
        this(context, null);
    }

    public myTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public myTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // do extra initialisation and get attributes here
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw first in black
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(250);                // text size
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(22);

        canvas.drawText(getText(), 135, 210, paint);

        // draw again in white, slightly smaller
//        paint.setColor(getResources().getColor(R.color.fitgrey));
        paint.setColor(Color.rgb(97, 99, 101)); //fitgrey
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(250);                // text size
        canvas.drawText(getText(), 135, 210, paint);

    }
}

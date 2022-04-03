package com.example.realtime_http;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

class DrawOn extends View { // 사각형 그리기
    public DrawOn(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint pt = new Paint();
        pt.setColor(Color.GREEN);
        pt.setStrokeWidth(5);

        canvas.drawLine(100,350,100,400, pt);
        canvas.drawLine(100,350,200,350, pt);

        canvas.drawLine(520,350,620,350, pt);
        canvas.drawLine(620,350,620,400, pt);

        canvas.drawLine(100,600,100,650, pt);
        canvas.drawLine(100,650,200,650, pt);

        canvas.drawLine(520,650,620,650, pt);
        canvas.drawLine(620,650,620,600, pt);
    }
}


package com.example.liu.helloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.util.jar.Attributes;

/**
 * Created by liu on 2017/6/22.
 */
public class MyProgressBar extends ProgressBar {
    String text;
    Paint mpaint;

    public MyProgressBar(Context context){
        super(context);
        initText();
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initText();
    }

    public MyProgressBar(Context context, AttributeSet attrs){
        super(context,attrs);
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        this.mpaint.getTextBounds(this.text,0,this.text.length() ,rect);
        int x = (getWidth() /2) - rect.centerX();
        int y = (getHeight() /2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mpaint);
    }

    private void initText(){
        this.mpaint = new Paint();
        this.mpaint.setColor(Color.WHITE);
        this.mpaint.setTextSize(20);
    }

    private void setText(int progress){
        int i = (progress * 100)/this.getMax();
        this.text = String.valueOf(i)+ "%";
    }
    private  void setText(){
        setText(this.getProgress());
    }
}

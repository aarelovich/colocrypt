package com.colocrypt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class CustomButton extends TouchView {

    public interface CustomButtonPressedInterface{
        void buttonPressed(int id);
        void buttonSwiped(int id,boolean totheright);
    }

    private RectF rect;

    private boolean pressed;
    private int rx = 10;
    private int ry = 10;
    private float cx = 50;
    private float cy = 25;
    private float offsety = 0;
    private Paint painter;
    private int ID = -1;
    private CustomButtonPressedInterface comm;
    private int[] pressedcolors;
    private int[] notpressedcolors;
    private LinearGradient whenpressed;
    private LinearGradient notpressed;
    private int textColor = Color.rgb(255,255,255);
    private String text;
    private boolean enableSwipeButton = false;


    public CustomButton(Context context, CustomButtonPressedInterface cbpi, int id) {
        super(context);
        pressed = false;
        rect = new RectF(2,2,98,48);
        painter = new Paint();
        painter.setTextAlign(Paint.Align.CENTER);
        painter.setStrokeCap(Paint.Cap.ROUND);
        painter.setAntiAlias(true);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ID = id;
        comm = cbpi;
        this.setLayoutParams(new LinearLayout.LayoutParams(100,50));
        pressedcolors = new int[2];
        notpressedcolors = new int[2];
        pressedcolors[0] = Color.rgb(80,80,80);
        pressedcolors[1] = Color.rgb(200,200,200);
        notpressedcolors[0] = Color.rgb(200,200,200);
        notpressedcolors[1] =  Color.rgb(80,80,80);
        whenpressed = new LinearGradient(0,0,100,50,pressedcolors[0],pressedcolors[1], Shader.TileMode.MIRROR);
        notpressed = new LinearGradient(0,0,100,50,notpressedcolors[0],notpressedcolors[1], Shader.TileMode.MIRROR);
        text = "Button";
        painter.setTextSize(12);
    }

    public void setEnableSwipeButton(boolean enable){
        enableSwipeButton = enable;
    }

    public void setSize(int width, int height){
        this.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        cx = width/2;
        cy = height/2;
        width = width - 2;
        height = height -2;
        rect.set(2,2,width,height);
        whenpressed = new LinearGradient(0,0,0,height,pressedcolors[0],pressedcolors[1], Shader.TileMode.MIRROR);
        notpressed = new LinearGradient(0,0,0,height,notpressedcolors[0],notpressedcolors[1], Shader.TileMode.MIRROR);
    }

    public void setTextStyle(int size, Typeface tface, Integer color){
        if (color != null) textColor = color;
        if (tface!= null) {
            painter.setTypeface(tface);
        }
        if (size > -1){
            painter.setTextSize(size);
        }
    }

    public void setText(String str){
        text = str;
        invalidate();
    }

    public void setTextYOffset(float yoff){offsety = yoff;}

    public void changePressedColor(int[] colors){
        if (colors != null){
            if (colors.length == 2){
                pressedcolors = colors;
                whenpressed = new LinearGradient(0,0,0,getLayoutParams().height,pressedcolors[0],pressedcolors[1], Shader.TileMode.MIRROR);
            }
        }
    }

    public void changeNotPressedColor(int[] colors){
        if (colors != null){
            if (colors.length == 2){
                notpressedcolors = colors;
                notpressed = new LinearGradient(0,0,0,getLayoutParams().height,notpressedcolors[0],notpressedcolors[1], Shader.TileMode.MIRROR);
            }
        }
    }


    protected void onDraw(Canvas canvas){
        if (pressed){
            painter.setShader(whenpressed);
        }
        else{
            painter.setShader(notpressed);
        }
        painter.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect,rx,ry,painter);

        painter.setShader(null);
        painter.setStyle(Paint.Style.STROKE);

        painter.setColor(textColor);
        painter.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawText(text,cx,cy+offsety,painter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
            pressed = true;
            invalidate();
        }
        else if (event.getActionMasked() == MotionEvent.ACTION_UP){
            boolean send = false;
            if (pressed){
                send = true;
            }
            pressed = false;
            if (send) comm.buttonPressed(ID);
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onFling(MotionEvent event, float velx){
        if (enableSwipeButton){
            comm.buttonSwiped(ID,(velx > 0));
        }
    }
}

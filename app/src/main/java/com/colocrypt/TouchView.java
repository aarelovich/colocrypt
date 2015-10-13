package com.colocrypt;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View{

	private TouchListener touchListener;
	private GestureDetector gestureDetector;
	
	public TouchView(Context context) {
		super(context);	
		//Touch Detection Setup
		touchListener = new TouchListener(this);
		gestureDetector = new GestureDetector(context,touchListener);
		gestureDetector.setOnDoubleTapListener(touchListener);			
	}
	
	@Override 
    public boolean onTouchEvent(MotionEvent event){		
		gestureDetector.onTouchEvent(event);		
		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }
	
	public void OnSingleTap(MotionEvent event){
		//Needs to be reimplemented on the children classes
	}
	
	public void OnDoubleTap(MotionEvent event){
		//Needs to be reimplemented on the children classes
		
	}
	
	public void OnLongPress(MotionEvent event){
		//Needs to be reimplemented on the children classes		
	}

    public void onFling(MotionEvent event, float velx){
        //Needs to be reimplemented on the children classes
    }

	
}

package com.colocrypt;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;


//This class will be used by all views to notify of inputs!


public class TouchListener extends SimpleOnGestureListener{
	
		private boolean DoubleTapDetected = false;
		private TouchView view;
		
		public TouchListener(TouchView v){
			view = v;		
		}
				
		@Override
        public boolean onDown(MotionEvent event)
        {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            view.onFling(e1,velocityX);
            return super.onFling(e1,e2,velocityX,velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            //System.err.println("Gestures: Long Press");
            view.OnLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
        	DoubleTapDetected = true;
        	//System.err.println("Gestures: Double Tap");  
        	view.OnDoubleTap(e);
            return super.onDoubleTap(e);
        }

        //this can be taken as click
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
        	if (DoubleTapDetected){
        		DoubleTapDetected = false;
        		return false;
        	}
        	//System.err.println("Gestures: Single Tap");
        	view.OnSingleTap(e);        
            return super.onSingleTapConfirmed(e);
        }

}
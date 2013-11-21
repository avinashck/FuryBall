package com.furyball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity implements SensorEventListener{
	
	Bitmap back,paddle;
	SensorManager sm;
	GameView gView;
	float x,xLast,sensorX,sensorY,sensorZ;
	private final float NOISE=(float)10.0;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gView=new GameView(this);
		gView.setKeepScreenOn(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(gView);
		sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size()!=0){
			Sensor s=sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
		paddle=BitmapFactory.decodeResource(getResources(),R.drawable.bar);
		gView.resume();	
		x=xLast=sensorX=sensorY=sensorZ=0;
	}
	
	public class GameView extends SurfaceView implements Runnable{
		
		Thread ourThread=null;
		boolean isRunning=true;
		private int screenW;
		private int screenH;
		SurfaceHolder ourHolder;	
		
		public GameView(Context context) {
			super(context);
			back=BitmapFactory.decodeResource(getResources(),R.drawable.my_theme);
			//Drawable d = new BitmapDrawable(getResources(),back);
			//this.setBackgroundDrawable(d);
			ourHolder=getHolder();
		}
	
		/*public void setMyBackground(){
			back=BitmapFactory.decodeResource(getResources(),R.drawable.my_theme);
			Drawable d = new BitmapDrawable(getResources(),back);
			gView.setBackgroundDrawable(d);
		}*/
		
		public void pause(){
			isRunning=false;
			while(true){
				try{
					ourThread.join();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				break;
			}
			ourThread=null;
		}
		
		public void resume(){
			isRunning=true;
			ourThread=new Thread(this);
			ourThread.start();
		}
		
		@Override
		public void onSizeChanged (int w, int h, int oldw, int oldh){
			super.onSizeChanged(w, h, oldw, oldh);
			screenW = w;
			screenH = h;
		}
		
	
		@Override
		public void run() {
			//back=BitmapFactory.decodeResource(getResources(),R.drawable.my_theme);
			//Drawable d = new BitmapDrawable(getResources(),back);
						
			while(isRunning){
				
				if(!ourHolder.getSurface().isValid())
					continue;
				Canvas canvas=ourHolder.lockCanvas();
				x=((screenW-paddle.getWidth())/2);
				float deltaX=Math.abs(xLast-sensorX);
				//sensorX*100
				canvas.drawColor(Color.BLACK);
				if(deltaX<NOISE){
					canvas.drawBitmap(paddle,x-sensorX*50, (int)(screenH*0.8), null);
				}
				ourHolder.unlockCanvasAndPost(canvas);
			}
						
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
				
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sensorX=event.values[0];
		sensorY=event.values[1];
		sensorZ=event.values[2];
	}
	
	@Override
	protected void onPause(){
		sm.unregisterListener(this);
		super.onPause();
	}
	
}

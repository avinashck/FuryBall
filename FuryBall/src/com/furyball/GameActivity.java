package com.furyball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
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
	int xAxis,xLast;
	float x,sensorX,sensorY,sensorZ,yAxis;
	private float collisionPoint,hitPercent;
	private final float NOISE=(float)6.0;
		
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
		xAxis=xLast=0;
		x=sensorX=sensorY=sensorZ=yAxis=0;
	}
	
	public class GameView extends SurfaceView implements Runnable{
		
		Thread ourThread=null;
		boolean isRunning=true;
		private int screenW,ballWidth;
		private int screenH,ballHeight;
		SurfaceHolder ourHolder;
		Bitmap ball,ballAnime,ball2;
		private static final int NO_COLUMNS = 12;
		private int speedX,speedY,ballX,ballY;
		private int currentFrame=0;
		
		public GameView(Context context) {
			super(context);
			ourHolder=getHolder();
			back=BitmapFactory.decodeResource(getResources(),R.drawable.my_theme);
			//Drawable d = new BitmapDrawable(getResources(),back);
			//this.setBackgroundDrawable(d);
			ball=BitmapFactory.decodeResource(getResources(),R.drawable.ball_animation);
			//ball2=BitmapFactory.decodeResource(getResources(),R.drawable.ball);
			//ballAnime=Bitmap.createScaledBitmap(ball, (int)(Math.ceil(((float)ball.getWidth())/NO_COLUMNS))*NO_COLUMNS, ball.getHeight(), true);
			ballWidth=ball.getWidth()/NO_COLUMNS;
			ballHeight=ball.getHeight();
			ballX=75;
			ballY=75;
			speedX=10;
			speedY=10;
		}
		
		public void update(){
			//code for paddle movement
			x=(float) (((screenW-paddle.getWidth())/1.5)-sensorX*50);
			xAxis=(int) (((screenW-paddle.getWidth())/2)-sensorX*50);
			yAxis=(int)(screenH*0.8);
			if(xAxis<paddle.getWidth()/2){
				xAxis=0;
			}
			if(xAxis>screenW-paddle.getWidth()){
				xAxis=screenW-paddle.getWidth();
			}
			
			//code for ball movement
			//ballX=screenH-paddle.getHeight();
			if(ballX>=screenW-ballWidth || ballX<=0){
				speedX=-speedX;
			}
			if(ballY>=screenH-ballHeight || ballY<=0){
				speedY=-speedY;
			}
			
			//code for paddle and ball collision
			if(ballY+ballHeight >= yAxis-(paddle.getHeight())/3) {
				
				if((ballX+ballWidth>x-paddle.getWidth()/2 && x>ballX) ||  (ballX<=x+paddle.getWidth()/2 && ballX>x)) {
					collisionPoint=ballX-x;
					hitPercent=(float) ((collisionPoint/((paddle.getWidth()/2)-ballWidth))-0.1);
					speedX=(int) (hitPercent*10);
					speedY=-speedY;
				}
			}
			
			ballX=ballX+speedX;
			ballY=ballY+speedY;
			
			currentFrame=++currentFrame%NO_COLUMNS;
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
						
			while(isRunning){
				
				//update();
				if(!ourHolder.getSurface().isValid())
					continue;
				Canvas canvas=ourHolder.lockCanvas();
				//x=((screenW-paddle.getWidth())/2);
				//sensorX*100
				canvas.drawColor(Color.BLACK);
				int scrX=currentFrame*ballWidth;
				int scrY=0;
				Rect scr=new Rect(scrX,scrY,scrX+ballWidth,scrY+ballHeight);
				Rect dest=new Rect(ballX,ballY,ballX+ballWidth,ballY+ballHeight);
				canvas.drawBitmap(ball,scr, dest, null);
				int deltaX=xLast-xAxis;//Math.abs(xLast-xAxis);
				if(deltaX>NOISE){
					canvas.drawBitmap(paddle,xAxis, yAxis, null);
				}else{
					canvas.drawBitmap(paddle,xLast, yAxis, null);
				}
				update();
				xLast=xAxis;
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

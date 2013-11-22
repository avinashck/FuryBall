package com.furyball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class GameView extends SurfaceView {
	
	Thread ourThread=null;
	boolean isRunning=true;
	private int screenW;
	private int screenH;
	SurfaceHolder holder;
	Bitmap back,paddle,brick,ball;
	//float x,xLast,sensorX,sensorY,sensorZ;
	//private final float NOISE=(float)10.0;
	final Context myContext;
	private GameThread thread;
	Sprite sprite;
	int bary;
	float barx;
	SensorManager sm;
	
	public GameView(Context context) {
		super(context);
		myContext=context;
		//back=BitmapFactory.decodeResource(getResources(),R.drawable.my_theme);
		//Drawable d = new BitmapDrawable(getResources(),back);
		//this.setBackgroundDrawable(d);
		//paddle=BitmapFactory.decodeResource(getResources(),R.drawable.bar);
		//ourHolder=getHolder();
		//x=xLast=sensorX=sensorY=sensorZ=0;
		
		//sprite=new Sprite();
		
		thread=new GameThread(this);
		holder=getHolder();
		holder.addCallback(new Callback() {
			
			public void surfaceDestroyed(SurfaceHolder holder) {
				thread.setRunning(false);
				boolean retry=true;
				
					while(retry)
					{
						try {
							thread.join();
							retry=false;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
			
			public void surfaceCreated(SurfaceHolder holder) {
				paddle=BitmapFactory.decodeResource(getResources(), R.drawable.bar);
				ball=BitmapFactory.decodeResource(getResources(), R.drawable.ball_animation);
				brick=BitmapFactory.decodeResource(getResources(), R.drawable.brick);
//				bricksprite=new BrickSprite(GameView.this, brick);
				sprite=new Sprite(GameView.this,thread,ball,paddle,brick);
				bary=getHeight()-100;
				thread.setRunning(true);
				thread.start();
		
				sm=(SensorManager)myContext.getSystemService(Context.SENSOR_SERVICE);
				if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size()!=0){
					Sensor s=sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
					sm.registerListener(sprite, s, SensorManager.SENSOR_DELAY_NORMAL);
				}
				
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
				// TODO Auto-generated method stub
				
			}
		});

	}
	
	/*public void assign(float sensorX,float sensorY,float sensorZ){
		this.sensorX=sensorX;
		this.sensorY=sensorY;
		this.sensorZ=sensorZ;
	}*/
	
	@SuppressLint("WrongCall")
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		sprite.onDraw(canvas);
//		bricksprite.onDraw(canvas);
	
	}
	
	/*public void pause(){
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
	}*/
	
	/*public void resume(){
		isRunning=true;
		ourThread=new Thread(this);
		ourThread.start();
	}*/
	
	//@Override
	/*public void onSizeChanged (int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;
		sprite.sizeAssign(screenW,screenH);
	}
	*/
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		sprite.onTouch(event);
		return true;
	}*/
	

	/*@Override
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
			this.onDraw(canvas);
			if(deltaX<NOISE){
				canvas.drawBitmap(paddle,x-sensorX*50, (int)(screenH*0.8), null);
			}
			ourHolder.unlockCanvasAndPost(canvas);
		}
					
	}*/
}
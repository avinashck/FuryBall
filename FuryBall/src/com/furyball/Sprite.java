package com.furyball;
	
import java.util.Random;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.MotionEvent;
import android.widget.Toast;

public class Sprite implements SensorEventListener {
	private static final int NO_COLUMNS = 12;
	Bitmap ball,paddle,brick,scalebit;
	GameView gameview;
	int x,speedx=0,speedy=0;
	int no_bricksperline;
	private int currentFrame=0;
	private int width;
	private int height;
	int brwidth;
	int brheight;
	int init=0;
	float tempwidth;
	String[][] bricks;
	GameThread thread;
	private int y;
	Paint paint=new Paint();
	private int barx;
	private int bary;
	
	private float x2,xLast,sensorX,sensorY,sensorZ;
	private int screenW;
	private int screenH;
	//private final float NOISE=(float)10.0;
	
	public Sprite(GameView gameview,GameThread thread,Bitmap ball,Bitmap paddle,Bitmap brick){
		
		this.gameview=gameview;
		this.ball=ball;
		this.paddle=paddle;
		this.brick=brick;
		this.thread=thread;
		this.no_bricksperline=gameview.getWidth()/brick.getWidth()-2;
		bricks=new String[no_bricksperline][4];
		brwidth=brick.getWidth();
		brheight=brick.getHeight();
		paint.setColor(Color.BLACK);
		for (int i = 0; i < no_bricksperline; ++i)
			for (int j = 0; j < 4; ++j)
				bricks[i][j] = "B";
		
		String strwidth=String.valueOf(((float)(ball.getWidth())/NO_COLUMNS));
		if(strwidth.contains(".")){
			scalebit=Bitmap.createScaledBitmap(ball, (int)(Math.ceil(((float)ball.getWidth())/NO_COLUMNS))*NO_COLUMNS, ball.getHeight(), true);
		}
		else{
			scalebit=ball;
		}
		this.width=scalebit.getWidth()/NO_COLUMNS;
		this.bary=gameview.getHeight()-200;
		this.height=scalebit.getHeight();
		Random random=new Random();
		x = random.nextInt(gameview.getWidth() - width);
	    y = random.nextInt(gameview.getHeight() - height)-100;
	    if(y<=100){
	    	y=120;
	    }
//		speedx=random.nextInt(10)-5;
//		speedy=random.nextInt(10)-5;
		speedx=10;
		speedy=10;
	}

	public Sprite() {
		// TODO Auto-generated constructor stub
		x2=xLast=sensorX=sensorY=sensorZ=0;
	}

	public void update(){
		
		if(x>gameview.getWidth()-width-speedx || x+speedx<0){
			speedx=-speedx;
		}
		if(y>gameview.getHeight()-height-speedy || y+speedy<0){
			speedy=-speedy;
		}
		if(y+height >= bary-paddle.getHeight()) {
			if((x+width>barx-paddle.getWidth()/2 && barx>x) ||  (x<barx+paddle.getWidth()/2 && x>barx)) {
				speedy=-speedy;
			}
		}
			
		if(y>0 && y<=(5*brheight) && init==1){
			if(x!=0){
				int x1=x;
				int y1=y;
				if(x1>(no_bricksperline+1)*brwidth){
					x1=(no_bricksperline)*brwidth;
				}
				if(x1<=brwidth){
					x1=brwidth;
				}
				if(y1<=brheight){
					y1=brheight;
				}
				if(y1>=(4*brheight) && y1<=(5*brheight)){
					y1=4*brheight;
				}
				int posi=(int)Math.floor(x1/brwidth)-1;
				int posj=(int)Math.floor(y1/brheight)-1;
				if(bricks[posi][posj]=="B"){
					bricks[posi][posj]="K";
					speedy=-speedy;
				}	
					
			}
		}
		if(y+height>bary+paddle.getHeight()){
		}
			
			
			
		x=x+speedx;
		y=y+speedy;
		currentFrame=++currentFrame%NO_COLUMNS;
	}
	
	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas canvas){
		checkFinish();
		update();
		for (int i = 0; i < no_bricksperline; ++i){
			for (int j = 0; j < 4; ++j) {
				// ourHolder.lockCanvas();

				if (bricks[i][j].contains("B"))
					canvas.drawBitmap(brick, (i+1)*brick.getWidth(),(j+1)*brick.getHeight(),null);
				init=1;
			}
		}
			
		int srcX=currentFrame*width;
		int srcY=0;
		Rect src=new Rect(srcX, srcY, srcX+width, srcY+height);
		Rect dest=new Rect(x,y,x+width,y+width);
		canvas.drawBitmap(scalebit,src,dest,null);
		x2=((gameview.getWidth()-paddle.getWidth())/2);
		canvas.drawBitmap(paddle, x2-barx*50, bary-paddle.getHeight(),null);
	}
		
	private void checkFinish() {
		int totalbricks=0;
		for (int i = 0; i < no_bricksperline; ++i){
			for (int j = 0; j < 4; ++j){
				if(bricks[i][j] == "K"){
					totalbricks++;
				}
			}
		}
		if(totalbricks==(no_bricksperline*4)){
			thread.setRunning(false);
		}
	}
	
	/*public void onSensorChanged(SensorEvent event){
		sensorX=event.values[0];
	}*/

	/*public void onTouch(MotionEvent event) {
		barx= (int) event.getX();
			
	}*/
	
	/*public void assign(float sensorX, float sensorY, float sensorZ) {
		// TODO Auto-generated method stub
		this.sensorX=sensorX;
		this.sensorY=sensorY;
		this.sensorZ=sensorZ;
	}*/

	public void sizeAssign(int screenW, int screenH) {
		// TODO Auto-generated method stub
		this.screenW=screenW;
		this.screenH=screenH;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		barx=(int)event.values[0];
		
	}
		
}




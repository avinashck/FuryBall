package com.furyball;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class FuryBallView extends View {
	
	private Bitmap playButton;
	private Bitmap playButtonClick;
	private Bitmap titleGraphic;
	private Bitmap back;
	private boolean playButtonClicked;
	private int screenW;
	private int screenH;
	private Context myContext;
	
	public FuryBallView(Context context) {
		super(context);
		
		playButton=BitmapFactory.decodeResource(getResources(),R.drawable.play);
		playButtonClick=BitmapFactory.decodeResource(getResources(),R.drawable.play_click);
		titleGraphic=BitmapFactory.decodeResource(getResources(),R.drawable.title2);
		back=BitmapFactory.decodeResource(getResources(),R.drawable.my_theme);
		Drawable d = new BitmapDrawable(getResources(),back);
		this.setBackgroundDrawable(d);
		myContext=context;
		
	}
		
	@Override
	public void onSizeChanged (int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(titleGraphic, (screenW-titleGraphic.getWidth())/2, (screenH-titleGraphic.getHeight())/4, null);
		canvas.drawBitmap(playButton,(screenW-playButton.getWidth())/2,(int)(screenH*0.7), null);
		if (playButtonClicked) {
			canvas.drawBitmap(playButtonClick,(screenW-playButton.getWidth())/2,(int)(screenH*0.7), null);
		} else {
			canvas.drawBitmap(playButton,(screenW-playButton.getWidth())/2,(int)(screenH*0.7), null);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		int X = (int)event.getX();
		int Y = (int)event.getY();
		switch (eventaction ) {
		case MotionEvent.ACTION_DOWN:
			if (X > (screenW-playButton.getWidth())/2 && X < ((screenW-playButton.getWidth())/2) + playButton.getWidth() && Y > (int)(screenH*0.7) && Y < (int)(screenH*0.7) +	playButton.getHeight()) {
					playButtonClicked = true;
			}
		break;
		case MotionEvent.ACTION_MOVE:
		break;
		case MotionEvent.ACTION_UP:
			if (playButtonClicked) {
				Intent gameIntent = new Intent(myContext, GameActivity.class);
				myContext.startActivity(gameIntent);
			}
			playButtonClicked = false;
			break;
		}
		invalidate();
		return true;
	}

}

package course.labs.bambam;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import course.labs.graphicslab.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	// These variables are for testing purposes, do not modify
	private final static int RANDOM = 0;
	private final static int SINGLE = 1;
	private final static int STILL = 2;
	private static int speedMode = RANDOM;

	private static final String TAG = "Lab-Graphics";

	// The Main view
	private RelativeLayout mFrame;

	// Bubble image's bitmap
	private Bitmap mBitmap;

	// Display dimensions
	private int mDisplayWidth, mDisplayHeight;

	// Sound variables

	// AudioManager
	private AudioManager mAudioManager;
	// SoundPool
	private SoundPool mSoundPool;
	// ID for the bubble popping sound
	private int mSoundID;
	// Audio volume
	private float mStreamVolume;

	// Gesture Detector
	private GestureDetector mGestureDetector;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Set up user interface
		mFrame = (RelativeLayout) findViewById(R.id.frame);

		// Load basic bubble Bitmap
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bam384);

		// Greeting
		Log.i (TAG,"Issuing Toast Message");		
		Toast.makeText(getBaseContext(), "Click empty space to create a bubble",
		        Toast.LENGTH_LONG).show();
			
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Manage bubble popping sound
		// Use AudioManager.STREAM_MUSIC as stream type

		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		mStreamVolume = (float) mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC)
				/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		// TODO - make a new SoundPool, allowing up to 10 streams
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// TODO - set a SoundPool OnLoadCompletedListener that calls
		// setupGestureDetector()

		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override	
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {

				// If sound loading was successful call setupGestureDetector()
				if (0 == status) {
					setupGestureDetector();
				} else {
					Log.i(TAG, "Unable to load sound");
					finish();
				}
			}
		});	
			
		// TODO - load the sound from res/raw/bubble_pop.wav
		//mSoundID = mSoundPool.load(this, R.raw.bubble_pop, 1);	
		mSoundID = mSoundPool.load(this, R.raw.meow, 1);	

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			// Get the size of the display so this View knows where borders are
			mDisplayWidth = mFrame.getWidth();
			mDisplayHeight = mFrame.getHeight();

		}
	}
	
	// SETUP GESTURE DETECTOR - THIS WILL WATCH FOR "MOTION" EVENTS AND REACT
	// Android usually sends events to the onTouch() "callback method"
	// but handling these events has been delegated to this method
	// Set up GestureDetector
	private void setupGestureDetector() {

		mGestureDetector = new GestureDetector(this,
		new GestureDetector.SimpleOnGestureListener() {

			// If a fling gesture starts on a BubbleView then change the
			// BubbleView's velocity

			@Override
			public boolean onFling(MotionEvent event1, MotionEvent event2,
					float velocityX, float velocityY) {

				// TODO - Implement onFling actions.
				// You can get all Views in mFrame one at a time
				// using the ViewGroup.getChildAt() method
				
				// "Look through existing objects"
				// Iterate through mFrame of BubbleViews
				float tapX = event1.getX();
				float tapY = event1.getY();
				
				for(int i=0; i < mFrame.getChildCount(); ++i) {
					View nextChild = mFrame.getChildAt(i);						
					// if a BubbleView exists under the tap XY
					if (((BubbleView) nextChild).intersects(tapX,tapY)) {
						// Fling it!!!
						((BubbleView) nextChild).deflect(velocityX, velocityY);		
						Log.i (TAG,"POPPED Child # " + i);
						Log.i (TAG,"BubbleView count = " + mFrame.getChildCount());				
						return true;
					}
				}
							
				return true;
			}

			// If a single tap intersects a BubbleView, then pop the BubbleView
			// Otherwise, create a new BubbleView at the tap's location and add
			// it to mFrame. You can get all views from mFrame with
			// ViewGroup.getChildAt()

			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {
				// TODO - Implement onSingleTapConfirmed actions.
				// You can get all Views in mFrame using the
				// ViewGroup.getChildCount() method

				// Get tap location
				float tapX = event.getX();
				float tapY = event.getY();
				Log.i (TAG,"X = " + tapX);
				Log.i (TAG,"Y = " + tapY);

				// "Look through existing objects"
				// Iterate through mFrame of BubbleViews
				for(int i=0; i < mFrame.getChildCount(); ++i) {

					View nextChild = mFrame.getChildAt(i);		
					Log.i (TAG,"Checking Child # " + i);

					// if a BubbleView exists under the tap XY
					if (((BubbleView) nextChild).intersects(tapX,tapY)) {
						// Pop it!!!
						((BubbleView) nextChild).stopMovement(true);
						// mFrame.removeViewAt(i);	this works but follow given code below				
						Log.i (TAG,"POPPED Child # " + i);
						Log.i (TAG,"BubbleView count = " + mFrame.getChildCount());				
						return true;
					}
				}

				// Otherwise, create new BubbleView at tap's location 
				BubbleView bubbleView = 
						new BubbleView(getApplicationContext(), tapX, tapY);
				mFrame.addView(bubbleView);  // add view to layout	
				bubbleView.startMovement();  // animation by creating a looping thread
				
				Log.i (TAG,"ADD BubbleView");
				Log.i (TAG,"Total BubbleViews = " + mFrame.getChildCount());
				
				return true;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// TODO - Delegate the touch to the gestureDetector
		return mGestureDetector.onTouchEvent(event);
			
	}

	@Override
	protected void onPause() {

		// TODO - Release all SoundPool resources

		if (null != mSoundPool) {
			mSoundPool.unload(mSoundID);
			mSoundPool.release();
			mSoundPool = null;
		}

		mAudioManager.setSpeakerphoneOn(false);
		mAudioManager.unloadSoundEffects();	

		super.onPause();
	}

	// BubbleView is a View that displays a bubble.
	// This class handles animating, drawing, and popping amongst other actions.
	// A new BubbleView is created for each bubble on the display

	public class BubbleView extends View {

		//private static final int BITMAP_SIZE = 64;
		private static final int BITMAP_SIZE = 384;
		private static final int REFRESH_RATE = 40;
		private final Paint mPainter = new Paint();
		private ScheduledFuture<?> mMoverFuture;
		private int mScaledBitmapWidth;
		private Bitmap mScaledBitmap;

		// location, speed and direction of the bubble
		private float mXPos, mYPos, mDx, mDy, mRadius, mRadiusSquared;
		private long mRotate, mDRotate;

		BubbleView(Context context, float x, float y) {
			super(context);

			// Create a new random number generator to
			// randomize size, rotation, speed and direction
			Random r = new Random();

			// Creates the bubble bitmap for this BubbleView
			createScaledBitmap(r);

			// Radius of the Bitmap
			mRadius = mScaledBitmapWidth / 2;
			Log.i (TAG,"mRadius = " + mRadius);	
			mRadiusSquared = mRadius * mRadius;
			Log.i (TAG,"mRadiusSquared = " + mRadiusSquared);	
			
			// Adjust position to center the bubble under user's finger
			mXPos = x - mRadius;
			mYPos = y - mRadius;

			// Set the BubbleView's speed and direction
			setSpeedAndDirection(r);

			// Set the BubbleView's rotation
			setRotation(r);

			mPainter.setAntiAlias(true);

		}

		private void setRotation(Random r) {
			if (speedMode == RANDOM) {

				// TODO - set rotation in range [1..3]
				int min = 1;
				int max = 3;
				// nextInt is normally exclusive of the top value,
			    // so add 1 to make it inclusive
				mDRotate = r.nextInt((max-min) + 1) + min;

			} else {
				mDRotate = 0;
			}
		}

		private void setSpeedAndDirection(Random r) {

			// Used by test cases
			switch (speedMode) {

			case SINGLE:

				mDx = 20;
				mDy = 20;
				break;

			case STILL:

				// No speed
				mDx = 0;
				mDy = 0;
				break;

			default:

				// TODO - Set mDx and mDy to indicate movement direction and speed 
				// Limit speed in the x and y direction to [-3..3] pixels per movement.
				int min = -3;
				int max = 3;
				mDx = r.nextInt((max-min) + 1) + min;
				mDy = r.nextInt((max-min) + 1) + min;			
				
			}
		}

		private void createScaledBitmap(Random r) {

			if (speedMode != RANDOM) {
				//mScaledBitmapWidth = BITMAP_SIZE * 3;
				mScaledBitmapWidth = BITMAP_SIZE / 3;
			} else {

				// TODO - set scaled bitmap size in range [1..3] * BITMAP_SIZE
				int min = 1;
				int max = 3;
				//mScaledBitmapWidth = (r.nextInt((max-min) + 1) + min) * BITMAP_SIZE;
				mScaledBitmapWidth = BITMAP_SIZE / (r.nextInt((max-min) + 1) + min);
				Log.i (TAG,"mScaledBitmapWidth = " + mScaledBitmapWidth );	
			}

			// TODO - create the scaled bitmap using size set above
			mScaledBitmap = Bitmap.createScaledBitmap(mBitmap,
					mScaledBitmapWidth, mScaledBitmapWidth, false);

		}

		// STARTMOVEMENT() - Creates a looping thread to animate bubble
		// Start moving the BubbleView & updating the display
		private void startMovement() {

			// Creates a WorkerThread
			ScheduledExecutorService executor = Executors
					.newScheduledThreadPool(1);

			// Execute the run() in Worker Thread every REFRESH_RATE
			// milliseconds
			// Save reference to this job in mMoverFuture
			mMoverFuture = executor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {

					// TODO - implement movement logic.
					// Each time this method is run the BubbleView should
					// move one step. If the BubbleView exits the display,
					// stop the BubbleView's Worker Thread.
					// Otherwise, request that the BubbleView be redrawn.					
					
					if(moveWhileOnScreen()) postInvalidate();
                    else stopMovement(false);				
					
				}
			}, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
		}

		// INTERSECTS(X,Y)
		// Returns true if the BubbleView intersects position (x,y)
		//
		private synchronized boolean intersects(float x, float y) {

			// TODO - Return true if the BubbleView intersects position (x,y)
			float tapX = x - mRadius;  // translate to match mXPos coords 
			float tapY = y - mRadius;
					
			Log.i (TAG,"Tap X = " + x);
			Log.i (TAG,"Tap Y = " + y);
			Log.i (TAG,"mXPos = " + mXPos);
			Log.i (TAG,"mYPos = " + mYPos);		
			Log.i (TAG,"mRadiusSquared = " + mRadiusSquared);	
			
			return ((mXPos - tapX) * (mXPos - tapX ) + 
					(mYPos - tapY) * (mYPos - tapY)) <= mRadiusSquared;
			// returns  true || false;

		}

		// STOPMOVEMENT(wasPopped)
		// Cancel the Bubble's movement
		// Remove Bubble from mFrame
		// Play pop sound if the BubbleView was popped
		private void stopMovement(final boolean wasPopped) {

			if (null != mMoverFuture) {

				if (!mMoverFuture.isDone()) {
					mMoverFuture.cancel(true);
				}

				// This work will be performed on the UI Thread
				mFrame.post(new Runnable() {
					@Override
					public void run() {

						// TODO - Remove the BubbleView from mFrame						
						mFrame.removeView(BubbleView.this);
						Log.i (TAG,"stopMovement, BubbleView Count = " + 
								mFrame.getChildCount());
						
						// TODO - If the bubble was popped by user,
						// play the popping sound
						if (wasPopped) {
							Log.i (TAG,"Reached playSoundEffect() for wasPopped");
							//mAudioManager.playSoundEffect(mSoundID);  // this plays a system sound!!
							mSoundPool.play(mSoundID, mStreamVolume, mStreamVolume, 1, 0, 1f);

						}
					}
				});
			}
		}

		// DEFLECT
		// Change the Bubble's speed and direction
		private synchronized void deflect(float velocityX, float velocityY) {
			mDx = velocityX / REFRESH_RATE;
			mDy = velocityY / REFRESH_RATE;
		}

		// ONDRAW 
		// Draw the Bubble at its current location
		@Override
		protected synchronized void onDraw(Canvas canvas) {

			// TODO - save the canvas
			canvas.save();
			
			// TODO - increase the rotation of the original image by mDRotate
			mRotate = mRotate + mDRotate;
			
			// TODO Rotate the canvas by current rotation
			// Hint - Rotate around the bubble's center, not its position			
			canvas.rotate(mRotate, mXPos + mRadius, mYPos + mRadius);
					
			// TODO - draw the bitmap at it's new location
			canvas.drawBitmap(mScaledBitmap, mXPos, mYPos, mPainter);  // redraw bubble on canvas
			
			// TODO - restore the canvas
			canvas.restore();

			
		}

		// MOVE WHILE ON SCREEN
		// Returns true if the BubbleView is still on the screen after the move
		// operation
		private synchronized boolean moveWhileOnScreen() {

			// TODO - Move the BubbleView
			mXPos+=mDx;
			mYPos+=mDy;		
			
			return isOutOfView();

		}

		// IS OUT OF VIEW?
		// Return true if the BubbleView is still on the screen after the move
		// operation
		private boolean isOutOfView() {

			// TODO - Return true if the BubbleView is still on the screen after
			// the move operation
			if (mXPos <  -mScaledBitmapWidth || mXPos - mScaledBitmapWidth >  mDisplayWidth ||
					mYPos <  -mScaledBitmapWidth || mYPos - mScaledBitmapWidth >  mDisplayHeight)
			{
				return false ;
			}

			return true;
		
		}
	}

	// Do not modify below here

	@Override
	public void onBackPressed() {
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_still_mode:
			speedMode = STILL;
			return true;
		case R.id.menu_single_speed:
			speedMode = SINGLE;
			return true;
		case R.id.menu_random_mode:
			speedMode = RANDOM;
			return true;
		case R.id.quit:
			exitRequested();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void exitRequested() {
		super.onBackPressed();
	}
}
package com.coursedroid.apps.photo.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class MainActivity extends Activity {

	private static final String TAG = "DailySelfie";
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	String mCurrentPhotoPath;
	static final int REQUEST_TAKE_PHOTO = 1;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private static int RESULT_LOAD_IMAGE = 2;  //action code for Gallery Intent
		
	/*
	 *  DISPATCH INTENT TO TAKE PICTURE
	 */
	private void dispatchTakePictureIntent(int actionCode) {
		Log.i(TAG, "entered dispatchTakePictureIntent");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				Log.i(TAG, "dispatchTakePictureIntent.mCurrentPhotoPath = " + mCurrentPhotoPath);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch
			
		startActivityForResult(takePictureIntent, actionCode);
		Log.i(TAG, "Exiting dispatchTakePictureIntent()");
	}	
	
	// this method makes the photo available to "photo gallery"
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	// Resizes the taken photo to the ImageView
	private void setPic() {
		
		Log.i(TAG, "entered setPic()");
		
	    // Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();    
	    Log.i(TAG, "targetW = "+targetW+" targetH ="+targetH);

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less 
		 * Sample code differs here from web example
		 * causing crash
		 * */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    /* Decode the JPEG file into a Bitmap */
	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    
	    /* Associate the Bitmap to the ImageView */
	    mImageView.setImageBitmap(bitmap);
	    mImageView.setVisibility(View.VISIBLE);
	    Log.i(TAG, "Exiting setPic()");
	}
	
	/*
	 * Handle Results from Intents
	 * the picture data returned from the takePictureIntent
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.i(TAG, "entered onActivityResult");	      
		
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}

		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

			handleBigCameraPhoto();

		}    
	}
	
	private void handleBigCameraPhoto() {
		Log.i(TAG, "entered handleBigCameraPhoto()");	
		if (mCurrentPhotoPath != null) {		
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}	
	
	private void handleSmallCameraPhoto(Intent intent) {
		
		Log.i(TAG, "entered handleSmallCameraPhoto()");	
		Bundle extras = intent.getExtras();
		mImageBitmap = (Bitmap) extras.get("data");
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(View.VISIBLE);
	}
		
	
	/*
	 *  FILE, DIRECTORY, ALBUM CREATION METHODS
	 */	
	private File createImageFile() throws IOException {
		// Create an image file name
		Log.i(TAG, "entered createImageFile()");
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		Log.i(TAG, "past timeStamp");
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		Log.i(TAG, "imageFileName =" + imageFileName);
		File albumF = getAlbumDir();
		Log.i(TAG, "past getAlbumDir()");
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		Log.i(TAG, "Exiting createImageFile()");
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {

		Log.i(TAG, "entered setUpPhotoFile()");
		File f = createImageFile();
		Log.i(TAG, "past f = createImageFile()");
		mCurrentPhotoPath = f.getAbsolutePath();
		Log.i(TAG, "Exiting setUpPhotoFile():  mCurrentPhotoPath = " + mCurrentPhotoPath);
		return f;
	}
	
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		Log.i(TAG, "entered getAlbumDir()");
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {		
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			Log.i(TAG, "storageDir = " + storageDir);
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.i(TAG, "Failed to create album directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}	
		
		Log.i(TAG, "Exiting getAlbumDir()");
		return storageDir;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageBitmap = null;
		// This bug really hung me up until I stepped through the code
		// compiles without this but results in run-time crash
		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
	
		
		// button to View Selfies using Intent to Android Gallery app
		ImageButton buttonLoadImages = (ImageButton) findViewById(R.id.imageButton1);
        buttonLoadImages.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				// use Android Gallery to display photos
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Use optionsMenu Custom Icon to dispatchTakePictureIntent
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			
			Log.i(TAG, "onOptionsMenu dispatchTakePictureIntent");
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);		
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
}


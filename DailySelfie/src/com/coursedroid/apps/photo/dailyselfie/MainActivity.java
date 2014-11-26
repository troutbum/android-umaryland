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
import android.widget.ImageView;
import android.widget.Toast;

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

	// Create Intent to take picture
//	private void dispatchTakePictureIntent() {
//		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		// Ensure that there's a camera activity to handle the intent
//		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//			// Create the File where the photo should go
//			File f = null;
//			try {
//				f = setUpPhotoFile();
//				mCurrentPhotoPath = f.getAbsolutePath();
//				Log.i(TAG, "mCurrentPhotoPath = " + mCurrentPhotoPath);
//				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));			
//
//			} catch (IOException e) {
//				e.printStackTrace();
//				f = null;
//				mCurrentPhotoPath = null;
//			}
//			// Continue only if the File was successfully created
//			startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//		}     
//	}
	private void dispatchTakePictureIntent(int actionCode) {
		Log.i(TAG, "entered dispatchTakePictureIntent");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				Log.i(TAG, "before f=setUpPhotoFile()");
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				Log.i(TAG, "mCurrentPhotoPath = " + mCurrentPhotoPath);
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
		
		Log.i(TAG, "entered dispatchTakePictureIntent");
		startActivityForResult(takePictureIntent, actionCode);
	}	
	
	
	
	// Setup button listener to take picture
	Button.OnClickListener mTakePicOnClickListener = 
			new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClick dispatchTakePictureIntent");
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
		}
	};
	
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
	    // Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    mImageView.setImageBitmap(bitmap);
	    mImageView.setVisibility(View.VISIBLE);
	}
	
//	// Create Intent to take picture
//	private void dispatchTakePictureIntent() {
//	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	    // checks to make sure Intent can be handled before starting
//	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//	    }
//	}
	
	// check usage of REQUEST_IMAGE_CAPTURE vs REQUEST_TAKE_PHOTO
	// in this example.  preview vs final take?
	
	// result of Intent is returned as a bitmap
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   Log.i(TAG, "entered onActivityResult");	      
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			//
			Log.i(TAG, "requestCode = " + requestCode + "resultCode = " + resultCode);
			//
	        Bundle extras = data.getExtras();
	        // BLOWS UP HERE!
	        Log.i(TAG, "got past extras = data.getExtras()");
	        //
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        //
	        Log.i(TAG, "past imageBitmap = extras.get(data)");
	        //
	        mImageView.setImageBitmap(imageBitmap);
	        //
	        Log.i(TAG, "past setImageBitmap");
	    }
	    
//	    if (mCurrentPhotoPath != null) {
//			setPic();
//			galleryAddPic();
//			mCurrentPhotoPath = null;
//		}
	    
	}
	
//	// Create file to store image with unique file path using timestamp
//	private File createImageFile() throws IOException {
//	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//	    String imageFileName = "JPEG_" + timeStamp + "_";
//	    //
//	    Log.i(TAG, "imageFileName = " + imageFileName);
//	    //
//	    // store in public directory, all apps can acccess
//	    File storageDir = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File image = File.createTempFile(
//	        imageFileName,  /* prefix */
//	        ".jpg",         /* suffix */
//	        storageDir      /* directory */
//	    );
//
//	    // Save a file: path for use with ACTION_VIEW intents
//	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//	    //
//	    Log.i(TAG, "mCurrentPhotoPath = " + mCurrentPhotoPath);
//	    
//	    return image;
//	}
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}


	private File setUpPhotoFile() throws IOException {

		Log.i(TAG, "entered setUpPhotoFile()");
		File f = createImageFile();
		Log.i(TAG, "past f = createImageFile()");
		mCurrentPhotoPath = f.getAbsolutePath();
		Log.i(TAG, "mCurrentPhotoPath = " + mCurrentPhotoPath);
		return f;
	}
	
	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {		
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}	
		return storageDir;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageBitmap = null;
		
		Button picBtn = (Button) findViewById(R.id.btnIntend);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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

	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	
}

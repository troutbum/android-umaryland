package com.example.modernartui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "ModernArtUI";
	static private final String URL = "http://www.moma.org/";
	static private final String CHOOSER_TEXT = "Load " + URL + " with:";
	
	private SeekBar volumeControl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get a reference to the buttons
		final Button button1 = (Button) findViewById(R.id.button1);
		final Button button2 = (Button) findViewById(R.id.button2);
		final Button button3 = (Button) findViewById(R.id.button3);
		final Button button4 = (Button) findViewById(R.id.button4);
		final Button button5 = (Button) findViewById(R.id.button5);
		
		// starting colors of buttons
		final int color1 = 0xFFFF4040;  // 0xAARRGGBB
		final int color2 = 0xFF40FF40;
		final int color3 = 0xFF4040FF;
		final int color4 = 0xFFD3D3D3;
		final int color5 = 0xFF000000;
		
		button1.setBackgroundColor(color1); // 0xAARRGGBB
		button2.setBackgroundColor(color2); // 0xAARRGGBB
		button3.setBackgroundColor(color3); // 0xAARRGGBB
		button4.setBackgroundColor(color4); // 0xAARRGGBB
		button5.setBackgroundColor(color5); // 0xAARRGGBB
		
		
		// Get a reference to the Seek Bar
		volumeControl = (SeekBar) findViewById(R.id.seekBar1);

		// implement Seek Bar method to change colors
		volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				button1.setBackgroundColor(color1 - progressChanged*1000);
				button2.setBackgroundColor(color2 - progressChanged*1000);
				button3.setBackgroundColor(color3 - progressChanged*100);
				button4.setBackgroundColor(color4 - progressChanged*100);
				button5.setBackgroundColor(color5 + progressChanged*1000);
			}
		});			
		
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
			showDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
// from http://developer.android.com/reference/android/app/DialogFragment.html
	void showDialog() {
		DialogFragment newFragment = MyAlertDialogFragment.newInstance(
				R.string.my_random_string);
		newFragment.show(getFragmentManager(), "dialog");
	}	
	
	
	public static class MyAlertDialogFragment extends DialogFragment {

		public static MyAlertDialogFragment newInstance(int title) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Create a customized message for Dialog Fragment
			// use with Builder.setCustomTitle(myMessage)
			//
			TextView myMessage = new TextView(getActivity());
			myMessage.setText("For inspiration \n"
					+ "see the works of: \n"
					+ "\n"
					+ "Piet Mondrian \n"
					+ "Paul Klee \n"
					+ "Sol LeWitt \n"
					+ "\n");
			myMessage.setBackgroundColor(Color.WHITE);
			myMessage.setPadding(10, 15, 15, 10);
			myMessage.setGravity(Gravity.CENTER);
			myMessage.setTextColor(Color.BLACK);
			myMessage.setTextSize(22);

//			int title = getArguments().getInt("title");
			
			return new AlertDialog.Builder(getActivity())
//
//			Use CustomTitle(myMessage) above to get better formatting
//			
//			.setIcon(R.drawable.alert_dialog_icon)
//			.setTitle(title)
//			.setMessage("For Inspiration")
			.setCustomTitle(myMessage)
			.setPositiveButton("Visit MOMA",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					((MainActivity)getActivity()).doPositiveClick();
				}
			}
					)
					.setNegativeButton("Not Now",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							((MainActivity)getActivity()).doNegativeClick();
						}
					}
							)
							.create();
		}
	}

	public void doPositiveClick() {
		// Do stuff here.
		Log.i("FragmentAlertDialog", "Positive click!");
		
		startImplicitActivation();
	}

	public void doNegativeClick() {
		// Do stuff here.
		Log.i("FragmentAlertDialog", "Negative click!");
		
		Toast.makeText(getBaseContext(), "Come Visit Soon!",
		        Toast.LENGTH_LONG).show();
	}
	
	// Start a Browser Activity to view a web page or its URL	
	private void startImplicitActivation() {       
		Log.i(TAG, "Entered startImplicitActivation()");      
		// TODO - Create a base intent for viewing a URL	
		Uri webpage = Uri.parse(URL);
		Intent baseIntent = new Intent(Intent.ACTION_VIEW, webpage);
	
		// Verify it resolves
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(baseIntent, 0);
		boolean isIntentSafe = activities.size() > 0;

		// TODO - Create a chooser intent, for choosing which Activity
		// will carry out the baseIntent
		Intent chooserIntent = Intent.createChooser(baseIntent, CHOOSER_TEXT);     
        
		Log.i(TAG,"Chooser Intent Action:" + chooserIntent.getAction());
              
		// TODO - Start the chooser Activity, using the chooser intent
		// Verify the intent will resolve to at least one activity
		if (baseIntent.resolveActivity(getPackageManager()) != null) {
		    startActivity(chooserIntent);
		}
		
	}
	
}

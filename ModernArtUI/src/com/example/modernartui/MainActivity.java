package com.example.modernartui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int DIALOG_ALERT = 10;
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
				button1.setBackgroundColor(color1 - progressChanged);
				button2.setBackgroundColor(color2 - progressChanged*1000);
				button3.setBackgroundColor(color3 - progressChanged*100);
				button4.setBackgroundColor(color4 - progressChanged*100);
				button5.setBackgroundColor(color5 + progressChanged*100);
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
			showDialog(DIALOG_ALERT);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// adjust this method if you have more than 
	// one button pointing to this method
//	public void onClick(View view) {
//	  showDialog(DIALOG_ALERT);
//	}

	@Override
	protected Dialog onCreateDialog(int id) {
	  switch (id) {
	    case DIALOG_ALERT:
	      Builder builder = new AlertDialog.Builder(this);
	      builder.setMessage("Inspired by the works of artists such as \n"
	      		+ " \n"
	      		+ "Piet Mondrian and Paul Klee \n"
	      		+ " \n "
	      		+ "Click below to learn more!"
	      		+ " \n ");

	      builder.setCancelable(true);
	      builder.setPositiveButton("Visit MOMA", new OkOnClickListener());
	      builder.setNegativeButton("Not Now", new CancelOnClickListener());
	      AlertDialog dialog = builder.create();
	      dialog.show();
	  }
	  return super.onCreateDialog(id);
	}

	private final class CancelOnClickListener implements
	    DialogInterface.OnClickListener {
	  public void onClick(DialogInterface dialog, int which) {
	    Toast.makeText(getApplicationContext(), "Visit Soon!",
	        Toast.LENGTH_LONG).show();
	  }
	}

	private final class OkOnClickListener implements
	    DialogInterface.OnClickListener {
	  public void onClick(DialogInterface dialog, int which) {
	    MainActivity.this.finish();
	  }
	} 
	
}

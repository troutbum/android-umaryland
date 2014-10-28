package com.example.modernartui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

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
		
		final int color1 = 0xFFFF0000;  // 0xAARRGGBB
		final int color2 = 0xFF00FF00;
		final int color3 = 0xFF0000FF;
		final int color4 = 0xFFD3D3D3;
		final int color5 = 0xFF000000;
		
		button1.setBackgroundColor(color1); // 0xAARRGGBB
		button2.setBackgroundColor(color2); // 0xAARRGGBB
		button3.setBackgroundColor(color3); // 0xAARRGGBB
		button4.setBackgroundColor(color4); // 0xAARRGGBB
		button5.setBackgroundColor(color5); // 0xAARRGGBB
		
		volumeControl = (SeekBar) findViewById(R.id.seekBar1);

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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

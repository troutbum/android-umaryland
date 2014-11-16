package course.labs.locationlab;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class PlaceViewActivity extends ListActivity implements LocationListener {
	private static final long FIVE_MINS = 5 * 60 * 1000;
	private static final String TAG = "Lab-Location";

	// Set to false if you don't have network access
	public static boolean sHasNetwork = true;  // changed from false

	private Location mLastLocationReading;
	private PlaceViewAdapter mAdapter;
	private LocationManager mLocationManager;
	private boolean mMockLocationOn = false;

	// default minimum time between new readings
	private long mMinTime = 5000;

	// default minimum distance between old and new readings.
	private float mMinDistance = 1000.0f;

	// A fake location provider used for testing
	private MockLocationProvider mMockLocationProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "Welcome to the Location Lab!");
		
		// Set up the app's user interface. This class is a ListActivity, 
		// so it has its own ListView. ListView's adapter should be a PlaceViewAdapter

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ListView placesListView = getListView();

		// TODO - add a footerView to the ListView
		// You can use footer_view.xml to define the footer	
		// >>>(UI Lab) Put divider and Inflate footerView for footer_view.xml file
	
		getListView().setFooterDividersEnabled(true);
		View footerView = getLayoutInflater().inflate(R.layout.footer_view, null);		

		// TODO - footerView must respond to user clicks, handling 3 cases:

		// There is no current location - response is up to you. One good 
		// solution is to disable the footerView until you have acquired a
		// location.

		// There is a current location, but the user has already acquired a
		// PlaceBadge for this location. In this case issue a Toast message with the text -
		// "You already have this location badge." 
		// Use the PlaceRecord class' intersects() method to determine whether 
		// a PlaceBadge already exists for a given location.

		// There is a current location for which the user does not already have
		// a PlaceBadge. In this case download the information needed to make a new
		// PlaceBadge.

		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "Entered footerView.OnClickListener.onClick()");

				// Toast if no current location
				if (mLastLocationReading == null) {
					Toast.makeText(getBaseContext(), "No Current Location is Available.",
							Toast.LENGTH_LONG).show();
					Log.i(TAG, "Toasted: No Current Location is Available");
				}

				else {					
//					// Check if PlaceBadge exists using intersects()
//					for (int i=0; i < mAdapter.getCount(); ++i) {
//						if (((PlaceViewAdapter) mAdapter.getItem(i)).intersects(mLastLocationReading)) {
//							Toast.makeText(getBaseContext(), "You already have this location badge.",
//									Toast.LENGTH_LONG).show();
//							//return;
//						}
//						else {
//							// Tricky AsyncTask syntax - see Notifications Lab
//							// doInBackground method accessed via .execute()
//							
//							new PlaceDownloaderTask(PlaceViewActivity.this, sHasNetwork).execute(mLastLocationReading);										
//							Log.i(TAG, "Downloading a PlaceRecord via AsyncTask");
//
//							//	this does not work:					
//							//		PlaceDownloaderTask mPlaceDownloaderTask = 
//							//			new PlaceDownloaderTask(PlaceViewActivity.this, mMockLocationOn);					
//							//		PlaceRecord mPlaceRecord = mPlaceDownloaderTask.doInBackground(mLastLocationReading);
//
//						}
//					}

					new PlaceDownloaderTask(PlaceViewActivity.this, sHasNetwork).execute(mLastLocationReading);										
					Log.i(TAG, "Downloading a PlaceRecord via AsyncTask");
					
				}
			}
			
		});

		placesListView.addFooterView(footerView);
		mAdapter = new PlaceViewAdapter(getApplicationContext());
		setListAdapter(mAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();

		startMockLocationManager();

		// TODO - Check NETWORK_PROVIDER for an existing location reading.
		// Only keep this last reading if it is fresh - less than 5 minutes old

		Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location != null) {

			if ( location.getTime() > System.currentTimeMillis() - FIVE_MINS) {
				mLastLocationReading = location;
				Log.i(TAG, "Latest location from provider is less than 5 minutes old");	
			}
		}

		// TODO - register to receive location updates from NETWORK_PROVIDER
		//
		// note: PlaceViewActivity implements LocationListener therefore
		// "this" acts like registering a listener "mLocationListener = new LocationLister()"

		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				mMinTime,mMinDistance, this);

	}

	@Override
	protected void onPause() {

		// TODO - unregister for location updates
		//
		// note: PlaceViewActivity implements LocationListener therefore
		// "this" acts like registering a listener "mLocationListener = new LocationLister()"

		mLocationManager.removeUpdates(this);
			
		shutdownMockLocationManager();
		super.onPause();
	}

	// CALLBACK method used by PlaceDownloaderTask
	public void addNewPlace(PlaceRecord place) {
		Log.i(TAG, "Entered addNewPlace()");

		// TODO - Attempt to add place to the adapter, considering the following cases

		// A PlaceBadge for this location already exists. In this case issue a Toast message
		// with the text - "You already have this location badge." Use the PlaceRecord 
		// class' intersects() method to determine whether a PlaceBadge already exists
		// for a given location. Do not add the PlaceBadge to the adapter
		
		// The place is null. In this case issue a Toast message with the text
		// "PlaceBadge could not be acquired"
		// Do not add the PlaceBadge to the adapter
		
		// The place has no country name. In this case issue a Toast message
		// with the text - "There is no country at this location". 
		// Do not add the PlaceBadge to the adapter
		
		// Otherwise - add the PlaceBadge to the adapter
		
		
		
		// check if place record already exists
		
//		NEED TO CLEAN CHECK LOGIC		
//		if (place.intersects(mLastLocationReading) == true) {
//
//			Toast.makeText(getBaseContext(), "You already have this location badge.",
//					Toast.LENGTH_LONG).show();
//			Log.i(TAG, "Toasted: You already have this location badge.");
//			return;
//		}

		// if PlaceBadge for location does not exist
		if (place.getPlace() == null) {
			Toast.makeText(getBaseContext(), "PlaceBadge could not be acquired.",
					Toast.LENGTH_LONG).show();
			Log.i(TAG, "Toasted: PlaceBadge could not be acquired.");
			return;
		}

		if (place.getCountryName() == null) {		
			Toast.makeText(getBaseContext(), "There is no country at this location.",
					Toast.LENGTH_LONG).show();
			Log.i(TAG, "Toasted: There is no country at this location.");
			return;
		}

		mAdapter.add(place);   //add place
	}
	
				
		
		
	
	// LocationListener methods
	// "this method is called by Android when the location changes"
	@Override
	public void onLocationChanged(Location currentLocation) {

		// TODO - Update last location considering the following cases.
		
		// 1) If there is no last location, set the last location to the current
		// location.
		if (mLastLocationReading == null) {
			mLastLocationReading = currentLocation;
		}	
			
		// 2) If the current location is older than the last location, ignore
		// the current location	
		if (currentLocation.getTime() < mLastLocationReading.getTime()) {
			// do nothing?
		}
		

		// 3) If the current location is newer than the last locations, keep the
		// current location.	
		if (currentLocation.getTime() > mLastLocationReading.getTime()) {
			mLastLocationReading = currentLocation;
		}
		
		//mLastLocationReading = null; (SKELETON)
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// not implemented
	}

	@Override
	public void onProviderEnabled(String provider) {
		// not implemented
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// not implemented
	}
	


	// Returns age of location in milliseconds
	private long ageInMilliseconds(Location location) {
		return System.currentTimeMillis() - location.getTime();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_badges:
			mAdapter.removeAllViews();
			return true;
		case R.id.place_one:
			mMockLocationProvider.pushLocation(37.422, -122.084);
			return true;
		case R.id.place_no_country:
			mMockLocationProvider.pushLocation(0, 0);
			return true;
		case R.id.place_two:
			mMockLocationProvider.pushLocation(38.996667, -76.9275);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void shutdownMockLocationManager() {
		if (mMockLocationOn) {
			mMockLocationProvider.shutdown();
		}
	}

	private void startMockLocationManager() {
		if (!mMockLocationOn) {
			mMockLocationProvider = new MockLocationProvider(
					LocationManager.NETWORK_PROVIDER, this);
		}
	}
}

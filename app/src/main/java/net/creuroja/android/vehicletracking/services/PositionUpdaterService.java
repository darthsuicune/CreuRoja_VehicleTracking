package net.creuroja.android.vehicletracking.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.creuroja.android.vehicletracking.activities.TrackingActivity;
import net.creuroja.android.vehicletracking.model.Settings;
import net.creuroja.android.vehicletracking.model.vehicles.Vehicle;

import org.json.JSONException;

import java.io.IOException;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class PositionUpdaterService extends Service
		implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
	public static final String SERVICE_NAME = "PositionUpdaterService";
	public static final String EXTRA_INDICATIVE =
			"net.creuroja.android.vehicletracking.extra.EXTRA_INDICATIVE";
	public static final String EXTRA_VEHICLE_ID =
			"net.creuroja.android.vehicletracking.extra.EXTRA_VEHICLE_ID";

	String token;
	Vehicle vehicle;
	GoogleApiClient apiClient;
	Location location;
	NotificationDispatcher notificationDispatcher;

	public PositionUpdaterService() {
	}

	@Override public void onCreate() {
		super.onCreate();
		loadToken();
		loadApiClient();
		loadNotificationDispatcher();
	}

	private void loadNotificationDispatcher() {
		notificationDispatcher = new NotificationDispatcher(
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE), this);
	}

	private void loadToken() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		token = prefs.getString(Settings.ACCESS_TOKEN, "");
	}

	private void loadApiClient() {
		apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
		apiClient.connect();
	}

	@Override public void onConnected(Bundle bundle) {
		location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(1500);
		request.setFastestInterval(1000);
		LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
		updatePosition();
	}

	private void updatePosition() {
		if (location == null) {
			location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
			if (location != null) {
				vehicle.setPosition(location.getLatitude(), location.getLongitude());
				upload(location);
			}
		} else {
			upload(location);
		}
	}

	@Override public void onDestroy() {
		super.onDestroy();
		if (apiClient != null && (apiClient.isConnected() || apiClient.isConnecting())) {
			apiClient.disconnect();
		}
		if (notificationDispatcher != null) {
			notificationDispatcher.showFinishedNotification(pendingIntent());
		}
	}

	private PendingIntent pendingIntent() {
		Intent intent = new Intent(this, TrackingActivity.class);
		return PendingIntent.getActivity(this, NotificationDispatcher.REQUEST_TRACKING_ACTIVITY,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		if (vehicle == null && intent != null && intent.getExtras() != null &&
			intent.hasExtra(EXTRA_INDICATIVE) && intent.hasExtra(EXTRA_VEHICLE_ID)) {
			vehicle = new Vehicle(intent.getIntExtra(EXTRA_VEHICLE_ID, 0),
					intent.getStringExtra(EXTRA_INDICATIVE));
		}
		notificationDispatcher.showPermanentNotification(pendingIntent());
		return super.onStartCommand(intent, flags, startId);
	}

	private void upload(Location location) {
		if (vehicle != null) {
			vehicle.setPosition(location.getLatitude(), location.getLongitude());
			VehicleUploadTask task = new VehicleUploadTask();
			task.execute();
		}
	}

	@Override public IBinder onBind(Intent intent) {
		return null;
	}

	@Override public void onConnectionSuspended(int i) {
		Log.d(SERVICE_NAME, "connection suspended: " + i);
	}

	@Override public void onConnectionFailed(ConnectionResult connectionResult) {
		//Nothing to do here
	}

	@Override public void onLocationChanged(Location location) {
		if (this.location != null && location.getAccuracy() < 10) {
			this.location = location;
			Log.d(SERVICE_NAME, "Better location registered");
		}
	}

	private class VehicleUploadTask extends AsyncTask<Void, Void, Void> {

		public VehicleUploadTask() {
		}

		@Override protected Void doInBackground(Void... voids) {
			try {
				vehicle.upload(token);
			} catch (IOException | JSONException e) {
				Log.d(SERVICE_NAME, "Error connecting to server");
				e.printStackTrace();
			}
			return null;
		}
	}
}

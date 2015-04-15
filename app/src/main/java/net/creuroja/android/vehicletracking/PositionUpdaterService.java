package net.creuroja.android.vehicletracking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
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

import java.io.IOException;

import static com.google.android.gms.common.api.GoogleApiClient.*;

public class PositionUpdaterService extends Service implements LocationListener,
		ConnectionCallbacks, OnConnectionFailedListener {
	public static final String SERVICE_NAME = "PositionUpdaterService";

	public static final String EXTRA_INDICATIVE =
			"net.creuroja.android.vehicletracking.extra.EXTRA_INDICATIVE";
	public static final String EXTRA_VEHICLE_ID =
			"net.creuroja.android.vehicletracking.extra.EXTRA_VEHICLE_ID";

	public static final String KEY_PERMANENT_NOTIFICATION = "permanentNotification";
	public static final String KEY_FINISHED_NOTIFICATION = "finishedNotification";
	public static final int NOTIFICATION_PERMANENT = 1;
	public static final int NOTIFICATION_FINISHED = 2;

	private static final int REQUEST_TRACKING_ACTIVITY = 1;

	private Vehicle vehicle;
	private GoogleApiClient apiClient;
	private boolean isConnected = false;
	private SharedPreferences prefs;
	private Location location;

	public PositionUpdaterService() {
	}

	@Override public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		apiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		apiClient.connect();
	}

	@Override public void onDestroy() {
		super.onDestroy();
		apiClient.disconnect();
		setNotification(false);
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		if (isConnected) {
			if (vehicle == null && intent != null && intent.getExtras() != null &&
				intent.hasExtra(EXTRA_INDICATIVE) && intent.hasExtra(EXTRA_VEHICLE_ID)) {
				vehicle = new Vehicle(intent.getIntExtra(EXTRA_VEHICLE_ID, 0),
						intent.getStringExtra(EXTRA_INDICATIVE));
				setNotification(true);
			}
			updatePosition();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void updatePosition() {
		if (location == null) {
			Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
			if (location != null) {
				vehicle.setPosition(location.getLatitude(), location.getLongitude());
				upload(location);
			}
		} else {
			upload(location);
		}
	}

	private void upload(Location location) {
		if (vehicle != null) {
			vehicle.setPosition(location.getLatitude(), location.getLongitude());
			VehicleUploadTask task =
					new VehicleUploadTask(prefs.getString(Settings.ACCESS_TOKEN, ""));
			task.execute();
		}
	}

	private void setNotification(boolean permanent) {
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification;
		int id;
		if (permanent) {
			notificationManager.cancel(NOTIFICATION_FINISHED);
			notification = getPermanentNotification();
			id = NOTIFICATION_PERMANENT;
			notification.flags |= Notification.FLAG_NO_CLEAR;
		} else {
			notificationManager.cancel(NOTIFICATION_PERMANENT);
			notification = getFinishedNotification();
			id = NOTIFICATION_FINISHED;
		}
		notificationManager.notify(id, notification);
	}

	private Notification getPermanentNotification() {
		return loadNotification(false, R.string.notification_permanent_text);
	}

	private Notification getFinishedNotification() {
		return loadNotification(false, R.string.notification_finished_text);
	}

	private Notification loadNotification(boolean ongoing, int text) {
		Intent intent = new Intent(this, TrackingActivity.class);
		PendingIntent pendingIntent = PendingIntent
				.getActivity(this, REQUEST_TRACKING_ACTIVITY, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder builder = new Notification.Builder(this);
		builder.setOngoing(ongoing);
		builder.setContentIntent(pendingIntent);
		builder.setTicker(getString(R.string.notification_title));
		builder.setSmallIcon(R.drawable.notification);
		builder.setAutoCancel(!ongoing);
		builder.setContentTitle(getString(R.string.notification_title));
		builder.setContentText(getString(text));
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			return builder.build();
		} else {
			return builder.getNotification();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override public void onConnected(Bundle bundle) {
		isConnected = true;
		location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(1500);
		request.setFastestInterval(1000);
		LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
	}

	@Override public void onConnectionSuspended(int i) {
		isConnected = false;
	}

	@Override public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override public void onLocationChanged(Location location) {
		if(this.location != null && location.getAccuracy() < 10) {
			this.location = location;
			Log.d("ASDF", "Better location registered");
		}
	}

	private class VehicleUploadTask extends AsyncTask<Void, Void, Void> {
		String token;

		public VehicleUploadTask(String token) {
			this.token = token;
		}

		@Override protected Void doInBackground(Void... voids) {
			try {
				vehicle.upload(token);
			} catch (IOException e) {
				Log.d(SERVICE_NAME, "Error connecting to server");
				e.printStackTrace();
			}

			return null;
		}
	}
}

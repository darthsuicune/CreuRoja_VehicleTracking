package net.creuroja.android.vehicletracking.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import net.creuroja.android.vehicletracking.R;

public class NotificationDispatcher {
	public static final String KEY_PERMANENT_NOTIFICATION = "permanentNotification";
	public static final String KEY_FINISHED_NOTIFICATION = "finishedNotification";
	public static final int NOTIFICATION_PERMANENT = 1;
	public static final int NOTIFICATION_FINISHED = 2;
	public static final int REQUEST_TRACKING_ACTIVITY = 1;

	NotificationManager manager;
	Context context;

	public NotificationDispatcher(NotificationManager manager, Context context) {
		this.manager = manager;
		this.context = context;
	}

	public void showPermanentNotification(PendingIntent intent) {
		manager.cancel(NOTIFICATION_FINISHED);
		Notification notification = loadNotification(intent, true, R.string.notification_permanent_text);
		int id = NOTIFICATION_PERMANENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		manager.notify(id, notification);
	}/**/

	public void showFinishedNotification(PendingIntent intent) {
		manager.cancel(NOTIFICATION_PERMANENT);
		Notification notification = loadNotification(intent, false, R.string.notification_finished_text);
		int id = NOTIFICATION_FINISHED;
		manager.notify(id, notification);
	}

	private Notification loadNotification(PendingIntent intent, boolean ongoing, int text) {
		Notification.Builder builder = new Notification.Builder(context);
		builder.setOngoing(ongoing);
		builder.setContentIntent(intent);
		builder.setTicker(context.getString(R.string.notification_title));
		builder.setSmallIcon(R.drawable.notification);
		builder.setAutoCancel(!ongoing);
		builder.setContentTitle(context.getString(R.string.notification_title));
		builder.setContentText(context.getString(text));
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			return builder.build();
		} else {
			return builder.getNotification();
		}
	}
}

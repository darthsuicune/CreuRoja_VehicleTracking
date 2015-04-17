package net.creuroja.android.vehicletracking.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;

import net.creuroja.android.vehicletracking.R;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static net.creuroja.android.vehicletracking.services.NotificationDispatcher.NOTIFICATION_FINISHED;
import static net.creuroja.android.vehicletracking.services.NotificationDispatcher.NOTIFICATION_PERMANENT;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotificationDispatcherTest extends InstrumentationTestCase {
	NotificationDispatcher dispatcher;
	NotificationManager mockNotificationManager;
	PendingIntent pendingIntent;
	Context context;

	public void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		System.setProperty("dexmaker.dexcache", context.getCacheDir().getPath());
		mockNotificationManager = mock(NotificationManager.class);
		pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		dispatcher = new NotificationDispatcher(mockNotificationManager, context);
	}

	public void testShowPermanentNotificationCancelsPreviousFinished() throws Exception {
		afterRequestingAPermanentNotification();
		itHasCancelled(NOTIFICATION_FINISHED);
	}

	private void afterRequestingAPermanentNotification() {
		dispatcher.showPermanentNotification(pendingIntent);
	}

	private void itHasCancelled(int notification) {
		verify(mockNotificationManager).cancel(notification);
	}

	public void testShowPermanentNotificationDisplaysANotification() throws Exception {
		afterRequestingAPermanentNotification();
		itDisplaysANotification(NOTIFICATION_PERMANENT, R.string.notification_permanent_text);
	}

	private void itDisplaysANotification(int notification, int notificationText) {
		verify(mockNotificationManager).notify(eq(notification),
				argThat(matchesNotificationWith(notificationText,
						(notification == NOTIFICATION_PERMANENT))));
	}

	private Matcher<Notification> matchesNotificationWith(final int textResId,
														  final boolean permanent) {
		return new BaseMatcher<Notification>() {
			@Override public boolean matches(Object o) {
				Notification notification = (Notification) o;
				CharSequence tickerText = context.getString(R.string.notification_title);
				boolean hasFlagOngoing = hasFlag(notification, Notification.FLAG_ONGOING_EVENT);
				boolean hasFlagNoClear = hasFlag(notification, Notification.FLAG_NO_CLEAR);
				boolean hasFlagAutoCancel = hasFlag(notification, Notification.FLAG_AUTO_CANCEL);
				String text = context.getString(textResId);
				String title = context.getString(R.string.notification_title);
				return notification.contentIntent == pendingIntent &&
					   notification.tickerText.equals(tickerText) &&
					   notification.icon == R.drawable.notification &&
					   ((permanent) ? hasFlagOngoing : !hasFlagOngoing) &&
					   ((permanent) ? !hasFlagAutoCancel : hasFlagAutoCancel) &&
					   ((permanent) ? hasFlagNoClear : !hasFlagNoClear) &&
					   notification.extras.getString(Notification.EXTRA_TEXT).equals(text) &&
					   notification.extras.getString(Notification.EXTRA_TITLE).equals(title);
			}

			private boolean hasFlag(Notification notification, int flag) {
				return (notification.flags & flag) == flag;
			}

			@Override public void describeTo(Description description) {
				description.appendText("something something notifications something");
			}
		};
	}

	public void testShowFinishedNotificationCancelsOngoing() throws Exception {
		afterRequestingAFinishNotification();
		itHasCancelled(NOTIFICATION_PERMANENT);
	}

	private void afterRequestingAFinishNotification() {
		dispatcher.showFinishedNotification(pendingIntent);
	}

	public void testShowFinishedNotificationDisplaysANotification() throws Exception {
		afterRequestingAFinishNotification();
		itDisplaysANotification(NOTIFICATION_FINISHED, R.string.notification_finished_text);
	}
}
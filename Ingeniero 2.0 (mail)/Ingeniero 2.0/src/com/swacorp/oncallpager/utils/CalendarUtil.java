package com.swacorp.oncallpager.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.jmv.frre.moduloestudiante.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

public class CalendarUtil {

	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
			"dd-MMM-yyyy 'at' HH:mm", Locale.ENGLISH);

	public static void deleteEventWithID(Activity curActivity, Long id) {

		ContentResolver cr = curActivity.getContentResolver();
		Uri EVENTS_URI = Uri.parse("content://com.android.calendar/events");
		Cursor cursor = cr.query(EVENTS_URI,
						new String[] { "calendar_id", "title", "description",
								"dtstart", "dtend", "eventLocation", "deleted","_id"}, null,
						null, null);
		cursor.moveToFirst();
		// fetching calendars name
		String CNames[] = new String[cursor.getCount()];

		// fetching calendars id
		int cont = 0;

		for (int i = 0; i < CNames.length; i++) {
			String titleL = cursor.getString(1);
			if (titleIsRemedyEvent(curActivity, titleL)) {
				long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
				if (id == (eventId)){
					cr.delete(ContentUris.withAppendedId(EVENTS_URI, eventId), null, null);
					break;
				}
			}
			CNames[i] = cursor.getString(1);
			cursor.moveToNext();
		}
	}

	public static long pushAppointmentsToCalender(Activity curActivity,
			String title, String addInfo, Long eventIDFM, long startDate,
			long endDate, boolean needReminder, boolean needMailService,
			Boolean modify) {

			/***************** Event: note(without alert) *******************/

			String eventUriString = "content://com.android.calendar/events";
			ContentValues eventValues = new ContentValues();

			eventValues.put("calendar_id", 1); // id, We need to choose from
												// our mobile for primary
												// its 1
			eventValues.put("title", title);
			
			if (modify && eventIDFM!=-1){
				deleteEventWithID(curActivity, eventIDFM);
			}
			eventValues.put("description", "Secondary Planned Event");
			eventValues.put("eventLocation", "Dallas");
			eventValues.put("dtstart", startDate);
			eventValues.put("dtend", endDate);

			// values.put("allDay", 1); //If it is bithday alarm or such
			// kind (which should remind me for whole day) 0 for false, 1
			// for true
			eventValues.put("eventStatus", 1); // This information is
			// sufficient for most
			// entries tentative (0),
			// confirmed (1) or canceled
			// (2):
			eventValues.put(Events.EVENT_TIMEZONE, TimeZone.getDefault()
					.getID());

			eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

			Uri eventUri = curActivity.getApplicationContext()
					.getContentResolver()
					.insert(Uri.parse(eventUriString), eventValues);

			long eventID = Long.parseLong(eventUri.getLastPathSegment());

			if (needReminder) {
				/***************** Event: Reminder(with alert) Adding reminder to event *******************/

				String reminderUriString = "content://com.android.calendar/reminders";

				ContentValues reminderValues = new ContentValues();

				reminderValues.put("event_id", eventID);
				reminderValues.put("minutes", 20); // Default value of the
													// system. Minutes is a
													// integer
				reminderValues.put("method", 1); // Alert Methods: Default(0),
													// Alert(1), Email(2),
													// SMS(3)

				Uri reminderUri = curActivity.getApplicationContext()
						.getContentResolver()
						.insert(Uri.parse(reminderUriString), reminderValues);
			}

			/***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

			if (needMailService) {
				String attendeuesesUriString = "content://com.android.calendar/attendees";

				/********
				 * To add multiple attendees need to insert ContentValues
				 * multiple times
				 ***********/
				ContentValues attendeesValues = new ContentValues();

				attendeesValues.put("event_id", eventID);
				attendeesValues.put("attendeeName", "Secondary"); // Attendees
																	// name
				attendeesValues.put("attendeeEmail",
						"swat.secondary@globant.com");// Attendee
														// E
														// mail
														// id
				attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
																// Relationship_None(0),
																// Organizer(2),
																// Performer(3),
																// Speaker(4)
				attendeesValues.put("attendeeType", 2); // None(0), Optional(1),
														// Required(2),
														// Resource(3)
				attendeesValues.put("attendeeStatus", 1); // NOne(0),
															// Accepted(1),
															// Decline(2),
															// Invited(3),
															// Tentative(4)

				Uri attendeuesesUri = curActivity
						.getApplicationContext()
						.getContentResolver()
						.insert(Uri.parse(attendeuesesUriString),
								attendeesValues);
			}

			return eventID;
	}

	public static ArrayList<CalendarEvent> readCalendarEvent(Context context,
			long stTime, long enTime) {
		ArrayList<CalendarEvent> listEvents = new ArrayList<CalendarEvent>();
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse("content://com.android.calendar/events"),
						new String[] { "_id", "title", "description",
								"dtstart", "dtend", "eventLocation", "deleted",  }, null,
						null, null);
		cursor.moveToFirst();
		// fetching calendars name
		String CNames[] = new String[cursor.getCount()];

		// fetching calendars id
		int cont = 0;

		for (int i = 0; i < CNames.length; i++) {

			String eid = cursor.getString(0);

			String desc = cursor.getString(2);
			String title = cursor.getString(1);

			Date mDate = new Date(cursor.getLong(3));
			Date nDate = new Date(cursor.getLong(4));

			long mTime = mDate.getTime();
			long lTime = nDate.getTime();
			if (stTime <= mTime && enTime >= lTime
					&& titleIsRemedyEvent(context, title) && cursor.getLong(6)!=1) {
				CalendarEvent event = new CalendarEvent(eid, title,
						desc, DATE_FORMATTER.format(mDate),
						DATE_FORMATTER.format(nDate), "Dallas");
				listEvents.add(event);
			}

			CNames[i] = cursor.getString(1);
			cursor.moveToNext();
		}
		return listEvents;
	}

	private static boolean titleIsRemedyEvent(Context context, String title) {
		String[] itemNames = context.getResources().getStringArray(
				R.array.change_items_array);
		for (String name : itemNames) {
			if (title.contains(name)) {
				return true;
			}
		}
		return false;
	}
}

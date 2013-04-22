/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static final PooledSimpleDateFormat DATE_TIME_SECONDS_FORMATTER = new PooledSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final PooledSimpleDateFormat DATE_TIME_FORMATTER = new PooledSimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final PooledSimpleDateFormat DATE_FORMATTER = new PooledSimpleDateFormat("yyyy-MM-dd");
	public static final PooledSimpleDateFormat YEAR_FORMATTER = new PooledSimpleDateFormat("yyyy");

	public static boolean isValidDate(ThreadSafeDateFormat sdf, String date) {

		try {
			sdf.parse(date);
		} catch (ParseException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidDate(DateFormat sdf, String date) {

		try {
			sdf.parse(date);
		} catch (ParseException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		}
		return true;
	}

	public static Date getDate(ThreadSafeDateFormat sdf, String date) {

		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static Date getDate(DateFormat sdf, String date) {

		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static long daysBetween(Date startDate, Date endDate) {

		Calendar start = Calendar.getInstance();
		start.setTime(startDate);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		return daysBetween(start, end);
	}

	//TODO check for bugs!
	public static long daysBetween(Calendar startDate, Calendar endDate) {

		startDate = (Calendar) startDate.clone();

		long daysBetween = 0;

		while (startDate.get(Calendar.YEAR) < endDate.get(Calendar.YEAR)) {

			if (startDate.get(Calendar.DAY_OF_YEAR) != 1) {

				int diff = startDate.getMaximum(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);

				diff++;

				startDate.add(Calendar.DAY_OF_YEAR, diff);

				daysBetween += diff;

			} else {

				daysBetween += startDate.getMaximum(Calendar.DAY_OF_YEAR);

				startDate.add(Calendar.YEAR, 1);
			}
		}

		daysBetween += endDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);

		return daysBetween;
	}

	public static int getCurrentYear() {

		return Integer.parseInt(YEAR_FORMATTER.format(new Date()));
	}

	public static java.sql.Date getCurrentSQLDate(boolean includeTime) {

		java.sql.Date date = new java.sql.Date(System.currentTimeMillis());

		if(includeTime){

			return date;

		}else{

			return setTimeToMidnight(date);
		}
	}

	public static <T extends Date> T setTimeToMidnight(T date) {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		date.setTime(calendar.getTimeInMillis());

		return date;
	}

	public static <T extends Date> T setTimeToMaximum(T date) {
		
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		date.setTime(calendar.getTimeInMillis());

		return date;
		
	}
	
}

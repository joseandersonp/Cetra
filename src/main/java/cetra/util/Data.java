package cetra.util;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Data {

	private Data() {
	}

	public static int arrayToInt(byte[] buf, int from) {
		return (int) arrayToUnsignedInt(buf, from);
	}

	public static long arrayToUnsignedInt(byte[] buf, int from) {
		return new BigInteger(Arrays.copyOfRange(buf, from, from + 4)).longValue();
	}

	public static String arrayToString(byte[] buf, int from, int length) {
		return new String(Arrays.copyOfRange(buf, from, from + length));
	}

	public static int arrayToUnsignedShort(byte[] buf, int from) {
		return new BigInteger(Arrays.copyOfRange(buf, from, from + 2)).intValue();
	}

	public static int toUShortLE(byte[] buf, int from) {
		return ((buf[from + 1] & 0xFF) << 8) + (buf[from] & 0xFF);
	}

	public static int toUIntLE(byte[] buf, int from) {
		return (((buf[from + 3] & 0xFF) << 16) + ((buf[from + 2] & 0xFF) << 12) + ((buf[from + 1] & 0xFF) << 8) + (buf[from] & 0xFF));
	}

	public static short arrayToShort(byte[] buf, int from) {
		return (short) arrayToUnsignedShort(buf, from);
	}

	public static void littleEndianShortToArray(byte[] buf, int to, short value) {
		shortToArray(buf, to, Short.reverseBytes(value));
	}

	public static void shortToArray(byte[] buf, int index, short value) {
		buf[index++] = (byte) (value >> 8 & 0xFF);
		buf[index] = (byte) (value & 0xFF);
	}

	public static void intToArray(byte[] buf, int to, int value) {
		buf[to++] = (byte) (value >> 24 & 0xFF);
		buf[to++] = (byte) (value >> 16 & 0xFF);
		buf[to++] = (byte) (value >> 8 & 0xFF);
		buf[to++] = (byte) (value & 0xFF);
	}

	public static void littleEndianIntToArray(byte[] buf, int to, int value) {
		intToArray(buf, to, Integer.reverseBytes(value));
	}

	public static DateTime arrayToDate(byte[] buffer, int from) {

		try {

			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmss");
			String sDate = arrayToString(buffer, from, 14);

			int hours = Array.getInt(buffer, from + 16) * 15 / 60;
			int millis = Integer.parseInt(arrayToString(buffer, from + 14, 2)) * 10;

			DateTimeZone dtZone = DateTimeZone.forOffsetHours(hours);
			DateTime dateTime = dtf.parseDateTime(sDate).withZone(dtZone);
			dateTime.plusMillis(millis);

			return dateTime;
		} catch (Exception e) {
			return null;
		}
	}

	public static DateTime arrayTimeStampToDate(byte[] buffer, int from) {

		try {

			int year = 1900 + Array.getInt(buffer, from++);
			int monthOfYear = Array.getInt(buffer, from++);
			int dayOfMonth = Array.getInt(buffer, from++);
			int hourOfDay = Array.getInt(buffer, from++);
			int minuteOfHour = Array.getInt(buffer, from++);
			int secondsOfMin = Array.getInt(buffer, from++);
			int offsetHours = Array.getInt(buffer, from++) * 15 / 60;

			DateTimeZone zone = DateTimeZone.forOffsetHours(offsetHours);

			DateTime dateTime = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour).withZone(zone).plusSeconds(secondsOfMin);

			return dateTime;

		} catch (Exception e) {
			return null;
		}
	}

	public static void dateTimeStampToArray(byte[] buf, int to, DateTime dateTime) {

		int offsetHour = (Integer.parseInt(dateTime.getZone().getName(0).replaceAll(":.*", "")) * 60 / 15);
		dateTime = dateTime.withZone(new DateTime().getZone());

		buf[to++] = (byte) (dateTime.getYear() - 1900);
		buf[to++] = (byte) dateTime.getMonthOfYear();
		buf[to++] = (byte) dateTime.getDayOfMonth();
		buf[to++] = (byte) dateTime.getHourOfDay();
		buf[to++] = (byte) dateTime.getMinuteOfHour();
		buf[to++] = (byte) dateTime.getSecondOfMinute();
		buf[to++] = (byte) offsetHour;
	}

	public static void stringToArray(byte[] buf, int index, String str) {
		System.arraycopy(str.getBytes(), 0, buf, index, str.length());
	}
	
}

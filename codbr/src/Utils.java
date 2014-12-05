

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.bluetooth.LocalDevice;
import javax.microedition.lcdui.ChoiceGroup;

import org.bouncycastle.util.Arrays;
import org.fcitmuk.openmrs.Cohort;
import org.fcitmuk.openmrs.CohortList;
import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.User;
import org.fcitmuk.openmrs.util.SecurityUtils;
import org.ihs.xform.StringUtils;

public class Utils {

	public static boolean isEmptyOrWhiteSpaceOnly(String string){
		if(string == null || string.trim().length() == 0){
			return true;
		}
		return false;
	}
	
	public static boolean isFirstLaunch(){
		Vector xfus = OpenmrsDataStorage.getXformUsers();
		if(xfus == null ||xfus.size() == 0){
			return true;
		}
		
		return false;
	}
	

	public static void loadCohorts(ChoiceGroup item){
		CohortList stored = OpenmrsDataStorage.getCohorts();
		if(stored != null){
			for (int i = 0; i < stored.size(); i++) {
				Cohort coh = ((Cohort)stored.getCohorts().elementAt(i));
				item.append(coh.getName(), null);
			}
		}
	}
	
	public static User authenticate(String username, String clearTextPassword){
		User xfu = OpenmrsDataStorage.getXformUser(username);
		if(xfu == null){
			//UIUtils.showPopup(ErrorMessages.USER_NOT_EXIST, null, display);
			return null;
		}
		else if(!SecurityUtils.authenticate(xfu, clearTextPassword)){
			//UIUtils.showPopup(ErrorMessages.PASSWORD_NOT_MATCH, null, display);
			return null;
		}
		
		return xfu;
	}

	public static long hexToLong(byte[] bytes) {
		if (bytes.length > 16) {
			throw new IllegalArgumentException("Byte array too long (max 16 elements)");
		}
		long v = 0;
		for (int i = 0; i < bytes.length; i += 2) {
			byte b1 = (byte) (bytes[i] & 0xFF);

			b1 -= 48;
			if (b1 > 9)
				b1 -= 39;

			if (b1 < 0 || b1 > 15) {
				throw new IllegalArgumentException("Illegal hex value: "+ bytes[i]);
			}

			b1 <<= 4;

			byte b2 = (byte) (bytes[i + 1] & 0xFF);
			b2 -= 48;
			if (b2 > 9)
				b2 -= 39;

			if (b2 < 0 || b2 > 15) {
				throw new IllegalArgumentException("Illegal hex value: "+ bytes[i + 1]);
			}

			v |= (((b1 & 0xF0) | (b2 & 0x0F))) & 0x00000000000000FFL;

			if (i + 2 < bytes.length)
				v <<= 8;
		}

		return v;
	}

	public static byte[] longToHex(final long l) {
		long v = l & 0xFFFFFFFFFFFFFFFFL;

		byte[] result = new byte[16];
		Arrays.fill(result, (byte) 0);

		for (int i = 0; i < result.length; i += 2) {
			byte b = (byte) ((v & 0xFF00000000000000L) >> 56);

			byte b2 = (byte) (b & 0x0F);
			byte b1 = (byte) ((b >> 4) & 0x0F);

			if (b1 > 9)
				b1 += 39;
			b1 += 48;

			if (b2 > 9)
				b2 += 39;
			b2 += 48;

			result[i] = (byte) (b1 & 0xFF);
			result[i + 1] = (byte) (b2 & 0xFF);

			v <<= 8;
		}

		return result;
	}
	
	public static String getIMEI() {
		String out = "";
		try {
			out = System.getProperty("com.imei");
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("phone.imei");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.IMEI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.mid.imei");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.sonyericsson.imei");
			}
	 
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("IMEI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.motorola.IMEI");
			}
	 
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.samsung.imei");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.siemens.imei");
			}
	 
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("imei");
			}
	 
		} catch (Exception e) {
			return out == null ? "" : out;
		}
		return out == null ? "" : out;
	}
	//code for getting IMSI of the phone
	public static String getIMSI() {
		String out = "";
		try {
			out = System.getProperty("IMSI");
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("phone.imsi");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.mid.mobinfo.IMSI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.nokia.mid.imsi");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("IMSI");
			}
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("imsi");
			}
		} catch (Exception e) {
			return out == null ? "" : out;
		}
		return out == null ? "" : out;
	}
	
	public static String getBluetoothAddress(){
		try{
			return LocalDevice.getLocalDevice().getBluetoothAddress();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * yyyy/YYYY for year complete : 2013
	 * yy/YY for year 2 digits : 13
	 * MM for month : 01 for Jan
	 * dd/DD for date : 09 for 9th day of month
	 * HH for hour of day : 23 for 11:00 pm or 01 for 1:00 am
	 * mm for minute : 01 for 1 min
	 * ss for second :  01 for 1 sec
	 * SSS for millis : 001 for 1 millis
	 */
	public static String formatDate(Date date, String format)  {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		int dateOfMonth = cal.get(Calendar.DATE);
		int hour =cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int millis = cal.get(Calendar.MILLISECOND);

		//inserting year
		format = StringUtils.replace("yyyy", StringUtils.leftPad(Integer.toString(year), 4, '0'), format);
		format = StringUtils.replace("YYYY", StringUtils.leftPad(Integer.toString(year), 4, '0'), format);
		//if year in short format
		format = StringUtils.replace("yy", StringUtils.leftPad(Integer.toString(year).substring(2), 2, '0'), format);
		format = StringUtils.replace("YY", StringUtils.leftPad(Integer.toString(year).substring(2), 2, '0'), format);
		//inserting month
		format = StringUtils.replace("MM", StringUtils.leftPad(Integer.toString(month), 2, '0'), format);
		//inserting date
		format = StringUtils.replace("dd", StringUtils.leftPad(Integer.toString(dateOfMonth), 2, '0'), format);
		format = StringUtils.replace("DD", StringUtils.leftPad(Integer.toString(dateOfMonth), 2, '0'), format);
		//inserting hour of day
		format = StringUtils.replace("HH", StringUtils.leftPad(Integer.toString(hour), 2, '0'), format);
		//inserting minute
		format = StringUtils.replace("mm", StringUtils.leftPad(Integer.toString(minute), 2, '0'), format);
		//inserting second
		format = StringUtils.replace("ss", StringUtils.leftPad(Integer.toString(second), 2, '0'), format);
		return format = StringUtils.replace("SSS", StringUtils.leftPad(Integer.toString(millis), 3, '0'), format);
	}
	
	/**
	 * yyyy/YYYY for year complete : 2013
	 * yy/YY for year 2 digits : 13
	 * MM for month : 01 for Jan
	 * dd/DD for date : 09 for 9th day of month
	 * HH for hour of day : 23 for 11:00 pm or 01 for 1:00 am
	 * mm for minute : 01 for 1 min
	 * ss for second :  01 for 1 sec
	 * SSS for millis : 001 for 1 millis
	 */
	public static Date parseDate(String date, String format) {
		Calendar cal = Calendar.getInstance();

		int yearIndex = -1;
		if((yearIndex = format.indexOf("yyyy")) != -1
				|| (yearIndex = format.indexOf("YYYY")) != -1){
			cal.set(Calendar.YEAR, Integer.parseInt(date.substring(yearIndex, yearIndex+4)));
		}
		else if((yearIndex = format.indexOf("yy")) != -1
				|| (yearIndex = format.indexOf("YY")) != -1){
			cal.set(Calendar.YEAR, Integer.parseInt(date.substring(yearIndex, yearIndex+2)));
		}
		
		int monthIndex = -1;
		if((monthIndex = format.indexOf("MM")) != -1){
			cal.set(Calendar.MONTH, Integer.parseInt(date.substring(monthIndex, monthIndex+2))-1);
		}
		
		int dateIndex = -1;
		if((dateIndex = format.indexOf("dd")) != -1
				|| (dateIndex = format.indexOf("DD")) != -1){
			cal.set(Calendar.DATE, Integer.parseInt(date.substring(dateIndex, dateIndex+2)));
		}
		
		int hourIndex = -1;
		if((hourIndex = format.indexOf("HH")) != -1){
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(date.substring(hourIndex, hourIndex+2)));
		}
		
		int minuteIndex = -1;
		if((minuteIndex = format.indexOf("mm")) != -1){
			cal.set(Calendar.MINUTE, Integer.parseInt(date.substring(minuteIndex, minuteIndex+2)));
		}
		
		int secondIndex = -1;
		if((secondIndex = format.indexOf("ss")) != -1){
			cal.set(Calendar.SECOND, Integer.parseInt(date.substring(secondIndex, secondIndex+2)));
		}
		
		int millisIndex = -1;
		if((millisIndex = format.indexOf("SSS")) != -1){
			cal.set(Calendar.MILLISECOND, Integer.parseInt(date.substring(millisIndex, millisIndex+2)));
		}
		
		return cal.getTime();
	}
	
	public static void main(String[] args) {
		System.out.println(formatDate(new Date(), "yyyy-MM/dd HH:mm:ss SSS"));
		System.out.println(parseDate("2014-01/10 21:21:21 111", "yyyy-MM/dd HH:mm:ss SSS"));
	}
}

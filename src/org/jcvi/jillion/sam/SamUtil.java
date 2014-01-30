package org.jcvi.jillion.sam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SamUtil {

	
	private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat> () {

		  @Override
		  public DateFormat get() {
		   return super.get();
		  }

		  @Override
		  protected DateFormat initialValue() {
		   return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		  }

		  @Override
		  public void remove() {
		   super.remove();
		  }

		  @Override
		  public void set(DateFormat value) {
		   super.set(value);
		  }

		 };
	private SamUtil(){
		//can not instantiate
	}
	/**
	 * Assert that the first key character is a valid letter
	 * [A-Za-z] and that the second key character is a valid letter or digit
	 * [A-Za-z0-9].
	 * @param key1 the first letter in the key.
	 * @param key2 the second letter in the key.
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	public static boolean isValidKey(char key1, char key2) {
		return assertValid1(key1)  && assertValid2(key2);
			
	}
	/**
	 * Assert that the first key character is a valid letter
	 * [A-Za-z].
	 * @param c
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	private static boolean assertValid1(char c) {
		//A-Zz-z
		if(c<65 || c>122){
			return false;
		}
		//check for special chars between Z-a
		//[91-96] invalid
		if(c > 90 && c<97){
			return false;
		}
		return true;
	}
	/**
	 * Assert that the second key character is a valid letter or digit
	 * [A-Za-z0-9].
	 * @param c
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	private static boolean assertValid2(char c) {
		if(c >=48 && c<=57){
			//digit is good
			return true;
		}
		return assertValid1(c);
		
	}
	
	public static Date toDate(String dateString) throws ParseException{
		return dateFormat.get().parse(dateString);
	}
	
	public static String formatIsoDate(Date date){
		return dateFormat.get().format(date);
	}
}

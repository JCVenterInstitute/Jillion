/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sff;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 454 Universal Accession Numbers are generated for each read based on the date
 * of the run, the machine name, the region the read came from and X-Y location
 * of the well.  The format is described in The FLX Data Analysis Software Manual
 * in section 13.3.7 454 Universal Accession Numbers.
 * @author dkatzel
 *
 *
 */
public final class Sff454NameUtil {
    /**
     * All 454 Universal Accession numbers follow the same 14 character format.  Some tools may add
     * suffixes to the accession number to include mate pair information.
     */
    private static final Pattern UNIVERSAL_ACCCESSION_NUM_PATTERN = Pattern.compile("^([A-Z0-9]{7}\\d\\d[A-Z0-9]{5})\\S*$");
    
    
    private Sff454NameUtil(){
        //can not instantiate
    }
    /**
     * Is the given read id a 454 read.
     * @param readId the read id to check.
     * @return {@code true} if the readId is a single word
     * that starts with a valid
     * 454 universal accession number; {@code false} otherwise.
     */
    public static boolean is454Read(String readId){        
        Matcher matcher = UNIVERSAL_ACCCESSION_NUM_PATTERN.matcher(readId);
        return matcher.matches();         
    }
    /**
     * Parse the Universal Accession Number from a 454 read id
     * that may represent either an unmated 454 read or a mated
     * 454 read.  This method assumes that mate information
     * is appended to the universal accession number as a suffix.
     * @param readId the 454 read id to parse from.
     * @return the Universal Accession Number part of a valid 454 read id.
     * @throws IllegalArgumentException if the given read is not a valid 454 read.
     */
    public static String parseUniversalAccessionNumberFrom(String readId){        
        Matcher matcher = UNIVERSAL_ACCCESSION_NUM_PATTERN.matcher(readId);
        if(matcher.matches()){
            return matcher.group(1);
        }
        throw new IllegalArgumentException("not a valid 454 read id: "+ readId);
    }
    /**
     * Generate the correct 454 Universal Accession Number
     * for the given rigRunName, region number and well location.
     * @param rigRunName the name of the 454 rig run (usually starts 
     * 'R_{year}_{month}_{day}_{hour}_{minute}_{second}_'
     * @param regionNumber the region number of the 454 region to use.
     * @param wellLocation the Location object that refers to the 
     * X and Y coordinates of the well in the region to use.
     * @return the 454 Universal Accession Number that 
     * uniquely represents that read.
     * @throws NullPointerException if rigRunName  or wellLocation is null.
     * @throws IllegalArgumentException if rigRunName is not a valid rig run name.
     * @throws IllegalArgumentException if regionNumber is not a positive number between 0 and 99 inclusive.
     */
    public static String generateAccessionNumberFor(String rigRunName, int regionNumber, Location wellLocation){
        if(rigRunName ==null){
            throw new NullPointerException("rigRunName can not be null");
        }
        if(wellLocation ==null){
            throw new NullPointerException("wellLocation can not be null");
        }
        if(regionNumber<0 || regionNumber >99){
            throw new IllegalArgumentException("region number must be >=0 and <=99 : "+ regionNumber);
        }
        RigRun rigRun = new RigRun(rigRunName);
        
        final Date dateOfRigRun = rigRun.getDate();
        final BigInteger encodedDate = _454DateEncoder.INSTANCE.encode(dateOfRigRun);
        return new StringBuilder()
                    .append(_454Base36Encoder.INSTANCE.encode(encodedDate))
                    .append(rigRun.getRandomizingHashcode())
                    .append(String.format("%02d", regionNumber))
                    .append(wellLocation.encode())
                    .toString();
        
    }
    
    
    /**
     * Parse the Date of the Run when this
     * 454 read was generated.  
     * Universal 454 Accession Numbers encode the date of the run in their names,
     * this method parses that information out. 
     * @param readId the 454 read to parse.
     * @return a Date (not-null) of when this rig run started
     * that produced this readId.
     * @throws IllegalArgumentException is {@link #is454Read(String)}
     * returns false.
     */
    public static Date getDateOfRun(String readId){
        if(!is454Read(readId)){
            throw new IllegalArgumentException(readId + " is not a 454 read");
        }
        final String substring = readId.substring(0,6);
        BigInteger timeStamp = _454Base36Encoder.INSTANCE.decode(substring);        
        return _454DateEncoder.INSTANCE.decode(timeStamp);
    }
    
    /**
     * Parse the region number of where on the machine this
     * 454 read was generated.  
     * Universal 454 Accession Numbers encode the region number in their names,
     * this method parses that information out. 
     * @param readId the 454 read to parse.
     * @return a a positive 2 digit number of which region this read
     * is from. 
     * @throws IllegalArgumentException is {@link #is454Read(String)}
     * returns false.
     */
    public static int getRegionNumber(String readId){
        if(!is454Read(readId)){
            throw new IllegalArgumentException(readId + " is not a 454 read");
        }
        final String substring = readId.substring(7,9);
        return Integer.parseInt(substring);
    }
    
    
    /**
     * {@code Location} represents the pixel coordinates of a well
     * on the PicoTiterPlate device which contains a DNA fragment being sequenced.
     * @author dkatzel
     *
     *
     */
    public static final class Location{
        private final int x,y;
        private static final BigInteger FOURTY_NINETY_SIX = BigInteger.valueOf(4096);
        /**
         * @param x
         * @param y
         */
        public Location(int x, int y) {
            if(x <0){
                throw new IllegalArgumentException("x coordinate can not be negative");
            }
            if(y <0){
                throw new IllegalArgumentException("y coordinate can not be negative");
            }
            this.x = x;
            this.y = y;
        }
        /**
         * Get the X pixel coordinate of this location.
         * @return a positive integer.
         */
        public int getX() {
            return x;
        }
        /**
         * Get the Y pixel coordinate of this location.
         * @return a positive integer.
         */
        public int getY() {
            return y;
        }
        
        private String encode(){
            return _454Base36Encoder.INSTANCE.encode(BigInteger.valueOf(x * 4096L + y));
        }
        
        private static Location decode(String encodedLocation){
            BigInteger value =_454Base36Encoder.INSTANCE.decode(encodedLocation);
            BigInteger x = value.divide(FOURTY_NINETY_SIX);
            BigInteger y = value.mod(FOURTY_NINETY_SIX);
            return new Location(x.intValue(), y.intValue());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Location)) {
                return false;
            }
            Location other = (Location) obj;
            if (x != other.x) {
                return false;
            }
            if (y != other.y) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(30);
            builder.append("Location [x=").append(x).append(", y=").append(y)
                    .append(']');
            return builder.toString();
        }
        
    }
    /**
     * Parse the {@link Location} representing the X and Y coordinates 
     * of this 454 read id when it was run.
     * @param readId the 454 read id to parse from.
     * @return the Universal Accession Number part of a valid 454 read id.
     * @throws IllegalArgumentException is {@link #is454Read(String)}
     * returns false
     */
    public static Location parseLocationOf(String readId){
        if(!is454Read(readId)){
            throw new IllegalArgumentException(readId + " is not a 454 read");
        }
        String subString = readId.substring(9,14);
        return Location.decode(subString);
    }
    /**
     * Singleton helper class to convert to and from
     * 454 base-36 encoding which is different
     * than normal base36 encoding in that
     * digits 0-9 represent numbers 26-35.
     * @author dkatzel
     *
     *
     */
    private static enum _454Base36Encoder{
        INSTANCE;
        
        BigInteger decode(String encodedString){
            StringBuilder decodedBuilder = new StringBuilder(); 
            for(char c : encodedString.toCharArray()){
                final int value;
                if(Character.isDigit(c)){
                    value =Integer.parseInt(Character.toString(c))+26;
                }else{
                    value =c-'A';
                }
                if(value <10){
                    decodedBuilder.append(value);
                }else{
                    decodedBuilder.append((char)((value-10)+'A'));
                }
                
            }
            return new BigInteger(decodedBuilder.toString(),36);
        }
        
        String encode(long value){
            return encode(BigInteger.valueOf(value));
        }
        
        
        String encode(BigInteger value){
            String base36EncodedString = value.toString(36).toUpperCase(Locale.ENGLISH);
            StringBuilder x454EncodedBase36Builder = new StringBuilder();
            for(Character c : base36EncodedString.toCharArray()){
                int offset =Integer.parseInt(c.toString(), 36);
               
                if(offset<26){
                    x454EncodedBase36Builder.append((char)(offset+'A'));
                }else{
                    x454EncodedBase36Builder.append((char)('0'+(offset-26)));
                }
            }
            
            return x454EncodedBase36Builder.toString();
        }
    }
    /**
     * Singleton to handle 454 Universal Accession Number
     * date calculations. 
     * <p> 
     * 454 Universal Accessions Numbers encode dates as a large number that 
     * is generated by multiplying the parts of the date by various
     * amounts and adding the results together.  Different Date parts can be
     * parsed out by division and modulus operations.
     * @author dkatzel
     *
     *
     */
    private static enum _454DateEncoder{
        INSTANCE;
        
        private static final BigInteger SIXTY = BigInteger.valueOf(60);
        private static final BigInteger TWENTY_FOUR = BigInteger.valueOf(24);
        private static final BigInteger THIRTEEN = BigInteger.valueOf(13);
        private static final BigInteger THIRTY_TWO = BigInteger.valueOf(32);
        
        private static final BigInteger MINUTE_MASK = SIXTY;
        private static final BigInteger HOUR_MASK = MINUTE_MASK.multiply(SIXTY);
        private static final BigInteger DAY_MASK = HOUR_MASK.multiply(TWENTY_FOUR);
        private static final BigInteger MONTH_MASK = DAY_MASK.multiply(THIRTY_TWO);
        private static final BigInteger YEAR_MASK = MONTH_MASK.multiply(THIRTEEN);
        
        
        BigInteger encode(Date timestampOfRun){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timestampOfRun);
            BigInteger year = BigInteger.valueOf(calendar.get(Calendar.YEAR) -2000);
            BigInteger month = BigInteger.valueOf(calendar.get(Calendar.MONTH));
            BigInteger day = BigInteger.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            BigInteger hour = BigInteger.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            BigInteger minute = BigInteger.valueOf(calendar.get(Calendar.MINUTE));
            BigInteger second = BigInteger.valueOf(calendar.get(Calendar.SECOND));
            
            return YEAR_MASK.multiply(year)
                                .add(MONTH_MASK.multiply(month))
                                .add(DAY_MASK.multiply(day))
                                .add(HOUR_MASK.multiply(hour))
                                .add(MINUTE_MASK.multiply(minute))
                                .add(second);
                                
        }
        
        Date decode(BigInteger timestamp){
            BigInteger current = timestamp;
            
            BigInteger year = current.divide(YEAR_MASK);
            current = current.mod(YEAR_MASK);
            
            BigInteger month = current.divide(MONTH_MASK);
            current = current.mod(MONTH_MASK);
            
            BigInteger dayOfMonth = current.divide(DAY_MASK);
            current = current.mod(DAY_MASK);
            
            BigInteger hourOfDay = current.divide(HOUR_MASK);
            current = current.mod(HOUR_MASK);
            
            BigInteger minute = current.divide(MINUTE_MASK);
            current = current.mod(MINUTE_MASK);
            
            BigInteger second = current;
            return new GregorianCalendar(
                    year.intValue()+2000, month.intValue()-1, dayOfMonth.intValue(), 
                    hourOfDay.intValue(), minute.intValue(), second.intValue())
            .getTime();
            
        }        
    }
    /**
     * {@code RigRun} is an object
     * representation of a 454 Rig Run.
     * @author dkatzel
     *
     *
     */
    private static final class RigRun{
        private static final Pattern RIG_RUN_NAME_PATTERN = Pattern.compile("^R_(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_.+");
        
        private final Date dateOfRun;
        private final char hash;
        
        private RigRun(String rigRunName){
            this.dateOfRun = parseDateFrom(rigRunName);
            this.hash = generate454Hash(rigRunName);
        }
        /**
         * Get the date of the run.
         * @return
         */
        Date getDate(){
            return dateOfRun;
        }
        /**
         * Get the 'randomizing' hash character used in universal accession
         * numbers to enhance uniqueness.  Since Universal Accessions use date of run
         * and region and well location to generate names, it is possible for name collisions
         * to occur if 2 454 machines
         * start running at the same time.  To avoid this problem, a hash
         * based on the run name is added to the name the reduce the chance of collisions.
         * @return the randomizing 'hash' character computed based on the algorithm
         * specified in the 454 documentation.
         */
        char getRandomizingHashcode(){
            return hash;
        }
        
        private Date parseDateFrom(String rigRunName){
            Matcher matcher = RIG_RUN_NAME_PATTERN.matcher(rigRunName);
            if(!matcher.matches()){
                throw new IllegalArgumentException("invalid rigRunName: " + rigRunName);
            }
            return new GregorianCalendar(
                    Integer.parseInt(matcher.group(1)), 
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)), 
                    Integer.parseInt(matcher.group(5)), 
                    Integer.parseInt(matcher.group(6))).getTime();
        }
        private char generate454Hash(String rigRunName){
            int hash=0;
            for(char c : rigRunName.toCharArray()){
                hash+= c;
                hash%=31;
            }
            return _454Base36Encoder.INSTANCE.encode(hash).charAt(0);
            
        }
    }
}

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
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.BitSet;
import java.util.Scanner;

import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;

/**
 * {@code IOUtil} is a collection of static utility
 * methods that make working
 * with Input and Output easier.
 * @author dkatzel
 *
 */
public final class IOUtil {
    private static final int EOF = -1;
	/**
     * Some methods need to use Log base 2
     * a lot so it's easier to factor this out as a constant.
     */
    private static final double LOG_2 = Math.log(2);
    /**
     * {@value}
     */
    public static final String UTF_8_NAME = "UTF-8";
    /**
     * Singleton for the {@link Charset} implementation for 
     * UTF-8.
     */
    public static final Charset UTF_8 = Charset.forName(UTF_8_NAME);
    
    /**
     * {@code Endian} is a way to specify how the ordering of bytes
     * is read from a byte array.
     * @author dkatzel
     *
     */
    public enum Endian{
    	/**
    	 * Big Endian format has the most significant
    	 * byte as the first byte.
    	 */
        BIG,
        /**
         * Little Endian format has the most significant
         * byte as the last byte.
         */
        LITTLE
    }
    private IOUtil(){}
    /**
     * Recursively delete the given file.
     * @param file the root directory to delete.
     * @throws IOException if deleting the directory or
     * a file under the directory fails.
     * @throws NullPointerException if dir is null.    
     */
    public static void recursiveDelete(File file) throws IOException{
        if(file.exists()){
            if (file.isDirectory()) {
                for(File subfile: file.listFiles()){
                    recursiveDelete(subfile);
                }
            }
        
            //we are here if dir is an empty dir or a file
            delete(file);
        }

    }
    /**
     * Deletes the given file and throws an Exception
     * if the delete fails.  This should be used in preference
     * over {@link File#delete()} since that method returns a boolean
     * result to indicate success or failure instead of 
     * throwing an exception.  If the file does not exist,
     * then this method will not do anything.
     * @param file the file to be deleted; if this parameter
     * is null, then method does nothing.
     * @throws IOException if there is a problem deleting the file.
     * @see #deleteIgnoreError(File)
     */
    public static void delete(File file) throws IOException{
        if(file !=null && file.exists() && !file.delete()){
            throw new IOException("unable to delete "+ file);
        }
    }
    /**
     * Tries to delete the given File but doesn't
     * check to see if the delete was successful.
     * This is the same as calling {@link File#delete()}
     * without checking the return value.   If the file does not exist,
     * then this method will not do anything.
     * @param file the file to delete; if this parameter
     * is null, then method does nothing.
     */
    public static void deleteIgnoreError(File file){
    	//This method exists solely so we don't
    	//have file.delete()s without checking return
    	//values littered throughout the codebase. 
    	//programs like FindBugs will flag these
    	//statements as bad code since we don't check return value
    	//so I would rather have only 1 such warning instead of dozens.
    	if(file!=null && file.exists()){
    		file.delete();
    	}
    }
    
    /**
     * Make the given directory and any non-existent 
     * parent directory as well.  This method should be used
     * in preference over {@link File#mkdirs()} since that method returns a boolean
     * result to indicate success or failure instead of 
     * throwing an exception.
     * @param dir the directory to be created; if dir is null or if it already
     * exists,
     * then this method does not do anything.
     * @throws IOException if there is a problem making the directories.
     */
    public static void mkdirs(File dir) throws IOException{
    	if(dir==null){
    		return;
    	}
        if(dir.exists()){
            return;
        }
        if(!dir.mkdirs()){
            throw new IOException("unable to mkdirs for "+ dir);
        }
    }
    /**
     * Make the given directory.  This method should be used
     * in preference over {@link File#mkdir()} since that method returns a boolean
     * result to indicate success or failure instead of 
     * throwing an exception.
     * @param dir the directory to be created; if dir is null
     * then this method does not do anything. 
     * @throws IOException if there is a problem making the directory.
     */
    public static void mkdir(File dir) throws IOException{
    	if(dir==null){
    		return;
    	}
        if(dir.exists()){
            return;
        }
        if(!dir.mkdir()){
            throw new IOException("unable to mkdir for "+ dir);
        }
    }
    /**
     * Close the given {@link Closeable} and ignore any exceptions
     * that are thrown.  Passing in a null will do nothing.
     * @param closeable the {@link Closeable} object to close.
     */
    public static void closeAndIgnoreErrors(Closeable closeable){
        try {
            if(closeable !=null){
                closeable.close();
            }
        } catch (Exception ignore) {
                //ignored on purpose
        }
    }
    /**
     * Convenience method for closing multiple
     * {@link Closeable}s at the same time, this
     * method is the same as calling {@link #closeAndIgnoreErrors(Closeable)}
     * on each parameter.
     * @param closeables the closeables to close.
     */
    public static void closeAndIgnoreErrors(Closeable...closeables){
       for(Closeable closeable : closeables){
           closeAndIgnoreErrors(closeable);
       }
    }
    /**
     * Close the given Statement and ignore any exceptions
     * that are thrown.  Passing in a null will do nothing.
     * @param statement the Statement object to close
     */
    public static void closeAndIgnoreErrors(Statement statement){
        try {
            if(statement !=null){
                statement.close();
            }
        } catch (SQLException ignore) {
                //ignored on purpose
        }
    }
    /**
     * Close the given {@link ResultSet} and ignore any exceptions
     * that are thrown.  Passing in a null will do nothing.
     * @param resultSet the ResultSet object to close
     */
    public static void closeAndIgnoreErrors(ResultSet resultSet){
        try {
            if(resultSet !=null){
                resultSet.close();
            }
        } catch (SQLException ignore) {
                //ignored on purpose
        }
    }
    /**
     * Close the given {@link Connection} and ignore any exceptions
     * that are thrown.  Passing in a null will do nothing.
     * @param connection the Connection object to close
     */
    public static void closeAndIgnoreErrors(Connection connection){
        try {
            if(connection !=null){
                connection.close();
            }
        } catch (SQLException ignore) {
                //ignored on purpose
        }
    }
    /**
     * Close the given {@link Scanner} and ignore any exceptions
     * that are thrown.  Passing in a null will do nothing.
     * @param scanner the {@link Scanner}  object to close
     */
    public static void closeAndIgnoreErrors(Scanner scanner){
        if(scanner !=null){
            scanner.close();
        }        
    }
    /**
     * Skip <code>numberOfBytes</code> in the {@link InputStream} 
     * and block until those bytes have been skipped. {@link InputStream#skip(long)}
     * will only skip as many bytes as it can without blocking.
     * @param in InputStream to skip.
     * @param numberOfBytes number of bytes to skip.
     * @throws IOException if there is a problem reading the inputstream
     * or if the end of file is reached before the number of bytes to skip
     * has been reached.
     */
    public static void blockingSkip(InputStream in, long numberOfBytes) throws IOException{

        long leftToSkip = numberOfBytes;
        while(leftToSkip >0){
        	//need to do a read() to see if we
        	//are at EOF yet. otherwise we loop forever
        	//since skip will return 0.
        	//this also is the reason for the -1 and +1 
        	//sprinkled around the leftToSkip computation.
        	int value =in.read();
        	if(value == EOF){ 
        		throw new IOException("end of file reached before entire block was skipped");
        	}
        	leftToSkip -= in.skip(leftToSkip-1) +1;
        }

    }
    /**
     * Reads {@code buf.length} bytes of the given inputStream and
     * puts them into the given byte array starting at the given offset.
     * Will keep reading until length number of bytes have been read (possibly blocking). 
     * This is the same as {@link #blockingRead(InputStream, byte[], int, int) blockingRead(in,buf,0, buf.length)}
     * @param in the inputStream to read; can not be null.
     * @param buf the byte array to write the data from the stream to; can not be null.
     * @throws EOFException if EOF is unexpectedly reached.
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if either inputStream  or buf are null.
     * @throws IllegalArgumentException if either offset  or length are negative.
     * @see #blockingRead(InputStream, byte[], int, int)
     */
    public static void blockingRead(InputStream in, byte[] buf) throws IOException{
    	blockingRead(in, buf, 0, buf.length);
    }
    
    /**
     * Reads {@code buf.length} bytes of the given {@link RandomAccessFile}
     * starting at the current file pointer and
     * puts them into the given byte array starting at the given offset.
     * Will keep reading until length number of bytes have been read (possibly blocking). 
     * This is the same as {@link #blockingRead(RandomAccessFile, byte[], int, int) blockingRead(in,buf,0, buf.length)}
     * @param file the {@link RandomAccessFile} to read; can not be null.
     * @param buf the byte array to write the data from the stream to; can not be null.
     * @throws EOFException if EOF is unexpectedly reached.
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if either inputStream  or buf are null.
     * @throws IllegalArgumentException if either offset  or length are negative.
     * @see #blockingRead(RandomAccessFile, byte[], int, int)
     */
    public static void blockingRead(RandomAccessFile file, byte[] buf) throws IOException{
    	blockingRead(file, buf, 0, buf.length);
    }
    /**
     * Reads up to length number of bytes of the given inputStream and
     * puts them into the given byte array starting at the given offset.
     * Will keep reading until length number of bytes have been read (possibly blocking). 
     * @param in the inputStream to read; can not be null.
     * @param buf the byte array to write the data from the stream to; can not be null.
     * @param offset the offset into the byte array to begin writing 
     * bytes to must be {@code >= 0}.
     * @param length the maximum number of bytes to read, must be {@code >= 0}.
     * This number of bytes will be read unless the inputStream ends prematurely
     * (which will throw an IOException). 
     * @throws EOFException if EOF is unexpectedly reached.
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if either inputStream  or buf are null.
     * @throws IllegalArgumentException if either offset  or length are negative.
     */
    public static void blockingRead(InputStream in, byte[] buf, int offset, int length) throws IOException{
        checkBlockingReadInputsAreOK(in, buf, offset, length);
    	int currentBytesRead=0;
        int totalBytesRead=0;
        while((currentBytesRead =in.read(buf, offset+totalBytesRead, length-totalBytesRead))>0){
            totalBytesRead+=currentBytesRead;
            if(totalBytesRead == length){
                break;
            }
        }
        if(currentBytesRead ==EOF){
            throw new EOFException(String.format("end of file after only %d bytes read (expected %d)",totalBytesRead,length));
        }
    }
    
    
    /**
     * Reads up to length number of bytes of the given {@link RandomAccessFile} 
     * starting at the current file pointer and
     * puts them into the given byte array starting at the given offset.
     * Will keep reading until length number of bytes have been read (possibly blocking). 
     * @param file the {@link RandomAccessFile} to read; can not be null.
     * @param buf the byte array to write the data from the stream to; can not be null.
     * @param offset the offset into the byte array to begin writing 
     * bytes to must be {@code >= 0}.
     * @param length the maximum number of bytes to read, must be {@code >= 0}.
     * This number of bytes will be read unless the inputStream ends prematurely
     * (which will throw an IOException). 
     * @throws EOFException if EOF is unexpectedly reached.
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if either inputStream  or buf are null.
     * @throws IllegalArgumentException if either offset  or length are negative.
     */
    public static void blockingRead(RandomAccessFile file, byte[] buf, int offset, int length) throws IOException{
        checkBlockingReadInputsAreOK(file, buf, offset, length);
    	int currentBytesRead=0;
        int totalBytesRead=0;
        while((currentBytesRead =file.read(buf, offset+totalBytesRead, length-totalBytesRead))>0){
            totalBytesRead+=currentBytesRead;
            if(totalBytesRead == length){
                break;
            }
        }
        if(currentBytesRead ==EOF){
            throw new EOFException(String.format("end of file after only %d bytes read (expected %d)",totalBytesRead,length));
        }
    }
	private static void checkBlockingReadInputsAreOK(InputStream in,
			byte[] buf, int offset, int length) {
		if(buf ==null){
        	throw new NullPointerException("byte array can not be null");
        }
        if(in ==null){
        	throw new NullPointerException("inputstream can not be null");
        }
        if(offset <0){
        	throw new IllegalArgumentException("offset must be >= 0");
        }
        if(length <0){
        	throw new IllegalArgumentException("length must be >= 0");
        }
	}
	private static void checkBlockingReadInputsAreOK(RandomAccessFile in,
			byte[] buf, int offset, int length) {
		if(buf ==null){
        	throw new NullPointerException("byte array can not be null");
        }
        if(in ==null){
        	throw new NullPointerException("inputstream can not be null");
        }
        if(offset <0){
        	throw new IllegalArgumentException("offset must be >= 0");
        }
        if(length <0){
        	throw new IllegalArgumentException("length must be >= 0");
        }
	}
   
    public static short[] readUnsignedByteArray(InputStream in, int expectedLength) throws IOException {
        short[] array = new short[expectedLength];
        for(int i=0; i<expectedLength; i++){
            array[i]=(short)in.read();
        }
        return array;
    }
    public static short[] readShortArray(InputStream in, int numberOfShortsToRead) throws IOException {
        short[] array = new short[numberOfShortsToRead];
        for(int i=0; i<numberOfShortsToRead; i++){
        	//borrowed from DataInputStream#readShort()
        	int ch1 = in.read();
            int ch2 = in.read();
            if ((ch1 | ch2) < 0){
                throw new EOFException();
            }
            array[i]= (short)((ch1 << 8) + (ch2 << 0));
        }
        return array;
    }

    public static void putShortArray(ByteBuffer buf, short[] array){
        for(int i=0; i< array.length; i++){
            buf.putShort(array[i]);
        }
    }
    public static void putUnsignedByteArray(ByteBuffer buf, short[] array){
        for(int i=0; i< array.length; i++){
            buf.put((byte)array[i]);
        }
    }
    /**
     * Converts signed java byte value into an unsigned value.
     * @param value the signed value to convert.
     * @return the unsigned value as an int.
     */
    public static int toUnsignedByte(byte value){
       return value & 0xFF;
    }
    /**
     * Converts an unsigned signed byte value into a signed value.
     * @param unsignedByte the unsigned value to convert.
     * @return the signed value as a byte.
     */
    public static byte toSignedByte(int unsignedByte){
    	if(unsignedByte>127){
    		return (byte)(unsignedByte-256);
    	}
    	return (byte)unsignedByte;
    }

    public static short toSignedShort(int unsignedShort){
        if(unsignedShort > Short.MAX_VALUE){
            return (short)(unsignedShort -(2*(Short.MAX_VALUE+1)));
        }
        return (short)unsignedShort;
    }
    public static int toSignedInt(long unsignedInt){
        if(unsignedInt > Integer.MAX_VALUE){
            return (int)(unsignedInt -(2*(Integer.MAX_VALUE+1)));
        }
        return (int)unsignedInt;
    }
    /**
     * Converts signed java short value into an unsigned value.
     * @param value the signed value to convert.
     * @return the unsigned value as an int.
     */
    public static int toUnsignedShort(short value){
        return value & 0xFFFF;
    }
    
    
    /**
     * Converts signed java short value into an unsigned value.
     * @param value the signed value to convert.
     * @return the unsigned value as an long.
     */
    public static long toUnsignedInt(int value){
        return value & 0xFFFFFFFFL;
    }
    
    /**
     * This method changes the endian of the byte array.
     * @param byteArray
     * @return a new byte array which represents the same data as the
     * passed in array, but with the endian switched. (Big Endian -> Little Endian
     * or vice versa)
     */
    public static byte[] switchEndian(byte[] byteArray){
        byte newByteArray[] = new byte[byteArray.length];
        //only need to swap half the array
        for(int i=0; i< byteArray.length /2; i++){

            newByteArray[i] = byteArray[byteArray.length-1 -i];
            newByteArray[byteArray.length-1 -i]=byteArray[i];
        }
        //handle case of odd length
        if(byteArray.length %2 ==1){
            int center =byteArray.length/2;
            newByteArray[center]=byteArray[center];
        }
        return newByteArray;

    }
    
    public static BigInteger readUnsignedLong(InputStream in, Endian endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.toByteArray(in, 8, endian));
     }
    public static long readUnsignedInt(InputStream in, Endian endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.toByteArray(in, 4, endian)).longValue();
     }
    public static long readUnsignedInt(byte[] array){
        return new BigInteger(1,
                 array).longValue();
     }
    public static int readUnsignedShort(byte[] array){
        return new BigInteger(1,
                 array).intValue();
     }
    public static short readUnsignedByte(byte[] array){
        return new BigInteger(1,
                 array).shortValue();
     }
    public static int readUnsignedShort(InputStream in, Endian endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.toByteArray(in, 2, endian)).intValue();
     }
    public static short readUnsignedByte(InputStream in, Endian endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.toByteArray(in, 1, endian)).shortValue();
     }
    
    public static BigInteger getUnsignedLong(ByteBuffer buf) throws IOException{
        byte[] tmp = new byte[8];
        buf.get(tmp);
    	return new BigInteger(1,tmp);
     }
    public static long getUnsignedInt(ByteBuffer buf) throws IOException{
        byte[] tmp = new byte[4];
        buf.get(tmp);
    	return new BigInteger(1,tmp).longValue();
     }
    public static int getUnsignedShort(ByteBuffer buf) throws IOException{
        byte[] tmp = new byte[2];
        buf.get(tmp);
    	return new BigInteger(1,tmp).intValue();
     }
    public static short getUnsignedByte(ByteBuffer buf) throws IOException{
        byte[] tmp = new byte[1];
        buf.get(tmp);
    	return new BigInteger(1,tmp).shortValue();
     }
    
    
    
    
    public static BigInteger readUnsignedLong(InputStream in) throws IOException{
       return readUnsignedLong(in, Endian.BIG);
     }
    public static long readUnsignedInt(InputStream in) throws IOException{
        return readUnsignedInt(in, Endian.BIG);
      }
    public static int readUnsignedShort(InputStream in) throws IOException{
        return readUnsignedShort(in, Endian.BIG);
      }
    public static short readUnsignedByte(InputStream in) throws IOException{
        return readUnsignedByte(in, Endian.BIG);
      }
    
    public static byte[] convertUnsignedIntToByteArray(long unsignedInt){
        byte[] result = new byte[4];
        long currentValue = unsignedInt;
        for(int i=result.length-1; i>=0; i--){
            result[i]=(byte)(currentValue &0xff);
            currentValue>>>=8;
        }
        return result;
    }
    /**
     * Convert an unsigned short into a fully padded
     * byte array.
     * <p/>
     * For Example:<br/>
     * 3 => [0, 3]<br/>
     * 255 => [0, 255]<br/>
     * 256 => [1, 255]<br/>
     * @param unsignedShort
     * @return
     */
    public static byte[] convertUnsignedShortToByteArray(int unsignedShort){
        byte[] result = new byte[2];
        int currentValue = unsignedShort;
        for(int i=result.length-1; i>=0; i--){
            result[i]=(byte)(currentValue &0xff);
            currentValue>>>=8;
        }
        return result;
    }
    public static byte[] convertUnsignedByteToByteArray(short unsignedByte){
        byte[] result = new byte[1];       
        result[0]=(byte)(unsignedByte &0xff);
          
        return result;
    }
    
    public static byte[] convertUnsignedLongToByteArray(BigInteger unsignedLong){
        //BigInteger.toByteArray() only returns the minimum number of bytes
        //(signed) required to represent the number,
        //it's easier to convert to hex, padd with 0's then convert
        //each hex byte than to do the bit math to take an odd number of bits
        //and compute the padded value.
        String hexString =convertToPaddedHex(unsignedLong,16);
        byte[] result = new byte[8];
        for(int i= 0; i<16; i+=2){
            String byteInHex = hexString.substring(i, i+2);
            result[i/2] = (byte) Short.parseShort(byteInHex, 16);
        }
        
       return result;
    }
    
    private static String convertToPaddedHex(BigInteger value, int maxNumberOfHexChars) {
        String hexString =value.toString(16);
        int padding = maxNumberOfHexChars-hexString.length();
        StringBuilder paddingString = new StringBuilder();
        for(int i=0; i< padding; i++){
            paddingString.append('0');
        }
        paddingString.append(hexString);
        return paddingString.toString();
    }
    
    public static InputStream createInputStreamFromFile(File file,long startOffset, int length)throws IOException {
       return new RandomAccessFileInputStream(file, startOffset, length);
    }
    /**
     * Get the number of bits required
     * to represent this value in binary.
     * @param value the value as an positive long.
     * @return the number of bits that are required
     * to represent this value as an unsigned binary
     * integer.
     * @throw IllegalArgumentException if value < 0.
     */
    public static int getUnsignedBitCount(long value) {
        if(value <0){
            throw new IllegalArgumentException("value can not be <0");
        }
        if(value ==Long.MAX_VALUE){
            //special case
            return 64;
        }
        return (int)Math.ceil(Math.log(value+1)/LOG_2);
    }   
    
    /**
     * Get the number of bytes required
     * to represent this value in binary.
     * @param value the value as an positive long.
     * @return the number of bytes that are required
     * to represent this value as an unsigned binary
     * integer.
     * @throw IllegalArgumentException if value < 0.
     */
    public static int getUnsignedByteCount(long value){
        int numBits = getUnsignedBitCount(value);
        return (numBits+7)/8;
    }
    
    /**
     * Convert the given {@link BitSet} into
     * the corresponding byte array.
     * For some reason {@link BitSet}
     * Java API thru java 6 does not include methods for converting
     * to and from a byte array.
     * @param bitset the bitset to convert.
     * @param bitLength the number of bits that need to be present
     * in the byte array.  This needs to be specified because
     * a {@link BitSet} may have trailing 0's
     * which would get truncated otherwise.
     * @return a new byte array containing the smallest 
     * number of bytes required to store the same data as
     * the given {@link BitSet}.
     * @throws NullPointerException if bitset is null.
     * @throws IllegalArgumentException if bitLength <0.
     */
    public static byte[] toByteArray(BitSet bitset, int bitLength){
    	if(bitset ==null){
    		throw new NullPointerException("bitset can not be null");
    	}
    	if(bitLength<0){
    		throw new IllegalArgumentException("bitLength must be >=0");
    	}
    	
    	byte[] bytes = new byte[(bitLength + 7) / 8];
    	for(int i=0; i<bitLength; i++){
    		if(bitset.get(i)){
    			bytes[bytes.length-i/8-1] |= 1<< (i%8);
    		}
    	}
    	return bytes;
    }
    /**
     * Convert the given byte array into
     * the corresponding {@link BitSet}
     * using the <strong>least</strong>
     * number of bits possible.
     * For some reason {@link BitSet}
     * Java API thru java 6 does not include methods for converting
     * to and from a byte array.
     * @param bytes the byte array to convert.
     * @return a new {@link BitSet} containing the same data as
     * the given byte array.
     * @throws NullPointerException if bytes is null.
     */
    public static BitSet toBitSet(byte[] bytes){
    	final BitSet bits;
    	bits = new BitSet();
    	int maxNumberOfBits = bytes.length *8;
    	for(int i=0; i<maxNumberOfBits; i++){
			int value = bytes[bytes.length-i/8-1] & (1<< (i%8));
			if(value !=0){
    			bits.set(i);
    		}
    	}
    	
    	return bits;
    }
    /**
     * Convert the given byte array into
     * the corresponding {@link BitSet}
     * using the <strong>least</strong>
     * number of bits possible.
     * For some reason {@link BitSet}
     * Java API thru java 6 does not include methods for converting
     * to and from a byte array.
     * @param buffer the {@link ByteBuffer} to convert.
     * @return a new {@link BitSet} containing the same data as
     * the given byte array.
     * @throws NullPointerException if bytes is null.
     */
    public static BitSet toBitSet(ByteBuffer buffer){
    	final BitSet bits;
    	bits = new BitSet(8*buffer.remaining());
    	int j=0;
    	while(buffer.remaining() >0){
    		byte value = buffer.get();
    		for(int i=0; i<8; i++){
    			int bit = value & (1<< i);
    			if(bit !=0){
        			bits.set(j+i);
        		}
    		}
    		j+=8;
    	}
    	
    	
    	return bits;
    }
    /**
     * Convert the given single value into
     * the corresponding {@link BitSet}
     * using the <strong>least</strong>
     * number of bits possible.
     * For some reason {@link BitSet}
     * Java API thru java 6 does not include methods for converting
     * to and from a byte array.
     * @param singleValue the value to convert.
     * @return a new {@link BitSet} containing the same data as
     * the given byte array.
     */
    public static BitSet toBitSet(long singleValue){
    	return toBitSet(BigInteger.valueOf(singleValue).toByteArray());    	
    }
    /**
     * Copy the contents of the given inputStream to the given
     * outputStream.  This method buffers internally so there is no
     * need to use a {@link BufferedInputStream}.  This method 
     * <strong>does not</strong> close either stream
     * after processing.
     * @param in the inputStream to read.
     * @param out the outputStream to write to.
     * @return the number of bytes that were copied.
     * @throws IOException if there is a problem reading or writing
     * the streams.
     * @throws NullPointerException if either stream is null.
     */
    public static long copy(InputStream in, OutputStream out) throws IOException{
    	byte[] buf = new byte[2048];
    	long numBytesCopied=0;
    	while(true){
    		int numBytesRead =in.read(buf);
    		if(numBytesRead ==EOF){
    			break;
    		}
    		numBytesCopied+=numBytesRead;
    		out.write(buf, 0, numBytesRead);
    		out.flush();
    	}
    	return numBytesCopied;
    }
    /**
     * Read the contents of the given {@link InputStream}
     * using the default character encoding
     * and return the entire contents of the stream
     * as a String.  This method will not close the stream
     * when it is done.  There is no need to buffer the {@link InputStream}
     * since this method will buffer internally.
     * @param in the inputStream to read as a String; can not be null.
     * @return a String representing the contents of the given
     * String using the default character encoding.
     * @throws IOException if there is a problem reading the Stream.
     * @throws NullPointerException if inputStream is null.
     */
    public static String toString(InputStream in) throws IOException{
    	return toString(in,null);
    }
    /**
     * Read the contents of the given {@link InputStream}
     * using the default character encoding
     * and return the entire contents of the stream
     * as a String.  This method will not close the stream
     * when it is done.  There is no need to buffer the {@link InputStream}
     * since this method will buffer internally.
     * @param in the inputStream to read as a String; can not be null.
     * @param encoding the name of the {@link Charset} encoding to use; if this value
     * is null, then use the default as defined by {@link Charset#defaultCharset()}.
     * @return a String representing the contents of the given
     * String using the default character encoding.
     * @throws IOException if there is a problem reading the Stream.
     * @throws NullPointerException if inputStream is null.
     * @throws UnsupportedEncodingException if the encoding name is not supported.
     */
    public static String toString(InputStream in, String encoding) throws IOException{
    	StringWriter writer = new StringWriter();
    	final Reader reader;
    	if(encoding ==null){
    		reader= new InputStreamReader(in,Charset.defaultCharset());
    	}else{
    		reader = new InputStreamReader(in,encoding);
    	}
    	char[] buf = new char[1024];
    	while(true){
    		int numBytesRead =reader.read(buf);
    		if(numBytesRead ==EOF){
    			break;
    		}
    		writer.write(buf, 0, numBytesRead);
    	}
    	return writer.toString();
    }
    /**
     * Copy the contents of the given {@link InputStream}
     * and return it as a byte[].
     * @param input the inputStream to convert into a byte[].  
     * This stream is not closed when the method finishes.
     * @return a new byte array instance containing all the bytes
     * from the given inputStream.
     * @throws IOException if there is a problem reading the Stream.
     */
	public static byte[] toByteArray(InputStream input) throws IOException {
		return toByteArray(input, Endian.BIG);
	}
	/**
     * Copy the contents of the given {@link InputStream}
     * and return it as a byte[] using the given {@link Endian}
     * order.
     * @param input the inputStream to convert into a byte[].  
     * This stream is not closed when the method finishes.
     * @param endian the {@link Endian} to use; null is considered
     * {@link Endian#BIG} (the default).
     * @return a new byte array instance containing all the bytes
     * from the given inputStream.
     * @throws IOException if there is a problem reading the Stream.
     */
	public static byte[] toByteArray(InputStream input,Endian endian) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        if(endian ==Endian.LITTLE){
        	return switchEndian(output.toByteArray());
        }
        return output.toByteArray();
	}
	 /**
     * Copy the numberOfBytesToRead of the given {@link InputStream}
     * and return it as a byte[].  This is the same
     * as {@link #toByteArray(InputStream, int, Endian)
     * toByteArray(in,numberOfBytesToRead,Endian.BIG)}
     */
	 public static byte[] toByteArray(InputStream in, int numberOfBytesToRead) throws IOException {
	       return toByteArray(in, numberOfBytesToRead, Endian.BIG);
	 }
	 /**
     * Copy the numberOfBytesToRead of the given {@link InputStream}
     * and return it as a byte[] using the given {@link Endian}
     * order.
     * @param in the inputStream to convert into a byte[].  
     * This stream is not closed when the method finishes.
     * @param numberOfBytesToRead the number of bytes to read from the stream;
     * if there aren't enough bytes, then this method will block until
     * more bytes are available or until the stream reaches end of file
     * (which will cause an IOException to be thrown).
     * @param endian the {@link Endian} to use; null is considered
     * {@link Endian#BIG} (the default).
     * @return a new byte array instance containing all the bytes
     * from the given inputStream.
     * @throws EOFException if the end of the file is reached before
     * the given number of bytes.
     * @throws IOException if there is a problem reading the inputStream.
     */
    public static byte[] toByteArray(InputStream in, int numberOfBytesToRead, Endian endian) throws IOException {
        byte[] array = new byte[numberOfBytesToRead];
        blockingRead(in,array,0,numberOfBytesToRead);        
        if(endian == Endian.LITTLE){
            return IOUtil.switchEndian(array);
        }
        return array;
    }
    
    
    /**
     * Compute the number of bits required to
     * store the given value. For example
     * the value 2 requires 2 bits and the value 6 requires 3 bits etc.
     * @param value the value to get the number of bits 
     * for.
     * @return the number of bits needed.
     */
	public static int computeNumberOfBitsIn(int value){
		if(value ==0){
			//special case
			return 1;
		}
		return Integer.SIZE-Integer.numberOfLeadingZeros(value);
	}
}

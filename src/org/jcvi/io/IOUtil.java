/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

import org.jcvi.Range;

public final class IOUtil {

    public enum ENDIAN{
        BIG,
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
     * Deletes the given file and throw an Exception
     * if the delete fails.  This should be used in preference
     * over {@link File#delete()} since that method returns a boolean
     * result to indicate success or failure instead of 
     * throwing an exception.
     * @param file the file to be deleted.
     * @throws IOException if there is a problem deleting the file.
     */
    public static void delete(File file) throws IOException{
        if(!file.delete()){
            throw new IOException("unable to delete "+ file);
        }
    }
    
    public static void deleteIgnoreError(File file){
        file.delete();
    }
    
    /**
     * Make the given directory and any non-existence 
     * parent directory as well.  This method should be used
     * in preference over {@link File#mkdirs()} since that method returns a boolean
     * result to indicate success or failure instead of 
     * throwing an exception.
     * @param dir the directory to be created.
     * @throws IOException if there is a problem making the directories.
     */
    public static void mkdirs(File dir) throws IOException{
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
     * @param dir the directory to be created.
     * @throws IOException if there is a problem making the directory.
     */
    public static void mkdir(File dir) throws IOException{
        if(dir.exists()){
            return;
        }
        if(!dir.mkdir()){
            throw new IOException("unable to mkdir for "+ dir);
        }
    }
    /**
     * Convenience method for {@link #writeToOutputStream(InputStream, OutputStream)}
     * with a default block size of {@code 1024}.
     * <p>
     * Calling this method is the same as calling
     * {@code writeToOutputStream(in, out, 1024);}
     * </p>
     * @param in the inputStream where the bytes to write
     * are stored.
     * @param out the outputStream where the bytes will be written.
     * @param bufferSize the size of the buffer used to read and write blocks of
     * bytes.
     * @throws IOException if there is a problem reading or writing data.
     * @throws IllegalArgumentException if {@code bufferSize < 1}.
     * @see #writeToOutputStream(InputStream, OutputStream, int)
     */
    public static void writeToOutputStream(InputStream in, OutputStream out) throws IOException{
        writeToOutputStream(in, out, 1024);
    }
    /**
     * Writes the contents of the given inputStream to the 
     * given output stream.
     * @param in the inputStream where the bytes to write
     * are stored.
     * @param out the outputStream where the bytes will be written.
     * @param bufferSize the size of the buffer used to read and write blocks of
     * bytes.
     * @throws IOException if there is a problem reading or writing data.
     * @throws IllegalArgumentException if {@code bufferSize < 1}.
     */
    public static void writeToOutputStream(InputStream in, OutputStream out, int bufferSize) throws IOException{
        if(bufferSize <1){
            throw new IllegalArgumentException("can not have a 0 or negative bufferSize");
        }
        byte[] buf = new byte[bufferSize];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
            out.flush();
        }
        
    }
    /**
     * Close the given Closeable and ignore any exceptions
     * that are thrown.  Passing in a null will do nothing.
     * @param closeable the closable object to close
     */
    public static void closeAndIgnoreErrors(Closeable closeable){
        try {
            if(closeable !=null){
                closeable.close();
            }
        } catch (IOException ignore) {
                //ignored on purpose
        }
    }
    /**
     * Conveience method for closing multiple
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
     * Skip <code>numberOfBytes</code> in the input stream 
     * and block until those bytes have been skipped. {@link InputStream#skip(long))
     * will only skip as many bytes as it can without blocking.
     * @param in InputStream to skip.
     * @param numberOfBytes number of bytes to skip.
     * @throws IOException
     */
    public static void blockingSkip(InputStream in, long numberOfBytes) throws IOException{

        long leftToSkip = numberOfBytes;
        while(leftToSkip >0){
            long actuallySkipped=in.skip(leftToSkip);
            leftToSkip -= actuallySkipped;
        }

    }

    /**
     * Reads up to length number of bytes of the given inputStream and
     * puts them into the given byte array starting at the given offset.
     * Will keep reading until length number of bytes have been read (possibly blocking). 
     * @param in
     * @param buf
     * @param offset
     * @param length
     * @return
     * @throws IOException
     */
    public static int blockingRead(InputStream in, byte[] buf, int offset, int length) throws IOException{
        int currentBytesRead=0;
        int totalBytesRead=0;
        while((currentBytesRead =in.read(buf, offset+totalBytesRead, length-totalBytesRead))>0){
            totalBytesRead+=currentBytesRead;
            if(totalBytesRead == length){
                break;
            }
        }
        if(currentBytesRead ==-1){
            throw new IOException(String.format("end of file after only %d bytes read (expected %d) ",totalBytesRead,length));
        }
        return totalBytesRead;
    }

    
    public static byte[] readByteArray(InputStream in, int expectedLength) throws IOException {
       return readByteArray(in, expectedLength, ENDIAN.BIG);
    }
    public static byte[] readByteArray(InputStream in, int expectedLength, ENDIAN endian) throws IOException {
        byte[] array = new byte[expectedLength];
        int bytesRead = blockingRead(in,array,0,expectedLength);
        if(bytesRead != expectedLength){
            throw new IOException("only was able to read "+ bytesRead + "expected "+ expectedLength);
        }
        if(endian == ENDIAN.LITTLE){
            return IOUtil.switchEndian(array);
        }
        return array;
    }
    public static short[] readUnsignedByteArray(InputStream in, int expectedLength) throws IOException {
        short[] array = new short[expectedLength];
        for(int i=0; i<expectedLength; i++){
            array[i]=(short)in.read();
        }
        return array;
    }
    public static short[] readShortArray(InputStream in, int expectedLength) throws IOException {
        short[] array = new short[expectedLength];
        DataInputStream dataStream = new DataInputStream(in);
        for(int i=0; i<expectedLength; i++){
            array[i]=dataStream.readShort();
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
     * Read the entire {@link InputStream} in and
     * return it as a String, this does not close the stream
     * @param in
     * @return
     * @throws IOException
     */
    public static String readStream(InputStream in)
                                        throws IOException {
        final ByteArrayOutputStream expectedOutStream = new ByteArrayOutputStream();
        OutputStreamReader reader = new OutputStreamReader(expectedOutStream);
        reader.read(in);
        return expectedOutStream.toString();
    }
    public static byte[] readStreamAsBytes(InputStream in) throws IOException {
        final ByteArrayOutputStream expectedOutStream = new ByteArrayOutputStream();
        OutputStreamReader reader = new OutputStreamReader(expectedOutStream);
        reader.read(in);
        return expectedOutStream.toByteArray();
        
    }
    /**
     * Converts signed java byte value into an unsigned value.
     * @param value the signed value to convert.
     * @return the unsigned value as an int.
     */
    public static int convertToUnsignedByte(byte value){
       return value & 0xFF;
    }
    
    public static byte convertUnsignedByteToSignedByte(long unsignedByte){
    	if(unsignedByte>127){
    		return (byte)(unsignedByte-256);
    	}
    	return (byte)unsignedByte;
    }

    public static short convertUnsignedShortToSignedShort(long unsignedShort){
        if(unsignedShort > Short.MAX_VALUE){
            return (short)(unsignedShort -(2*(Short.MAX_VALUE+1)));
        }
        return (short)unsignedShort;
    }
    public static int convertUnsignedIntToSignedInt(long unsignedInt){
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
    public static int convertToUnsignedShort(short value){
        return value & 0xFFFF;
    }
    
    
    /**
     * Converts signed java short value into an unsigned value.
     * @param value the signed value to convert.
     * @return the unsigned value as an long.
     */
    public static long convertToUnsignedInt(int value){
        return value & 0xFFFFFFFFL;
    }
    
    public static byte[] reverse(byte[] input){
        ByteBuffer result = ByteBuffer.allocate(input.length);
        for(int i=input.length-1; i>=0; i--){
            result.put(input[i]);
        }
        return result.array();
    }
    public static Properties readPropertiesFromFile(File propertiesFile) throws IOException{
        return readPropertiesFromFile(propertiesFile, new Properties());
     }
    public static Properties readPropertiesFromFile(File propertiesFile, Properties properties) throws IOException{
        return readPropertiesFromStream(new FileInputStream(propertiesFile), properties);
     }
    public static Properties readPropertiesFromStream(InputStream inputStream) throws IOException{
       return readPropertiesFromStream(inputStream, new Properties());
    }
    public static Properties readPropertiesFromStream(InputStream inputStream, Properties props) throws IOException{
        try{
            props.load(inputStream);
            return props;
        }finally{
            closeAndIgnoreErrors(inputStream);
        }
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
    
    public static BigInteger readUnsignedLong(InputStream in, ENDIAN endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 8, endian));
     }
    public static long readUnsignedInt(InputStream in, ENDIAN endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 4, endian)).longValue();
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
    public static int readUnsignedShort(InputStream in, ENDIAN endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 2, endian)).intValue();
     }
    public static short readUnsignedByte(InputStream in, ENDIAN endian) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 1, endian)).shortValue();
     }
    public static BigInteger readUnsignedLong(InputStream in) throws IOException{
       return readUnsignedLong(in, ENDIAN.BIG);
     }
    public static long readUnsignedInt(InputStream in) throws IOException{
        return readUnsignedInt(in, ENDIAN.BIG);
      }
    public static int readUnsignedShort(InputStream in) throws IOException{
        return readUnsignedShort(in, ENDIAN.BIG);
      }
    public static short readUnsignedByte(InputStream in) throws IOException{
        return readUnsignedByte(in, ENDIAN.BIG);
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
            paddingString.append("0");
        }
        paddingString.append(hexString);
        String asHex = paddingString.toString();
        return asHex;
    }
    
    public static InputStream createInputStreamFromFile(File file,Range range)throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fastaFileChannel=null;
       try{
            fastaFileChannel =fileInputStream.getChannel();
            ByteBuffer buf= ByteBuffer.allocate((int)range.size());
            fastaFileChannel.position((int)range.getStart());
            int bytesRead =fastaFileChannel.read(buf);
            if(bytesRead <0){
                throw new IOException("could not read any bytes from file");
            }
            
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(buf.array());
            return inputStream;
        
       }finally{
           IOUtil.closeAndIgnoreErrors(fileInputStream,fastaFileChannel);
       }
    }
}

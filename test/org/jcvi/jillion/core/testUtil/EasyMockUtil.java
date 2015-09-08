/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Apr 17, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.testUtil;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jcvi.jillion.core.io.IOUtil;

/**
 * {@code EasyMockUtil} is utility class
 * which contains helper functions to make working with
 * EasyMock more intent revealing.
 * @author dkatzel
 *
 *
 */
public final class EasyMockUtil {
	
	private EasyMockUtil(){
		//can not instantiate
	}
    /**
     * Argument matcher for EasyMock to allow easyMock to
     * correctly expect {@link Throwable}s.
     * example usage:
     * <code>
     *
     * </code>
     * @param <T>
     * @param in
     * @return
     */
    public static <T extends Throwable> T eqException(T in) {
        EasyMock.reportMatcher(new ThrowableEquals(in));
        return null;
    }

    public static <T extends PropertyChangeEvent> T  eqPropertyChangeEvent(T in) {
        EasyMock.reportMatcher(new PropertyChangeEventEquals(in));
        return null;
    }
    /**
     * Writes the given array to the inputstream specified by the first argument
     * of the Mocked call.
     * <br/>
     * For example:
     * <pre>
     *  expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(new byte[]{1,2,3,4}));
       </pre>
       will tell EasyMock to write the Array <code>[1,2,3,4]</code>
       to <code>mockInputStream</code> when the method read(byte[], 0, 4) is called.
     * @param dataToWrite
     * @return
     */
    public static IAnswer<Integer> writeArrayToInputStream(final byte[] dataToWrite){
        return new IAnswer<Integer>(){

            @Override
            public Integer answer() throws Throwable {
                final Object[] currentArguments = getCurrentArguments();
                int startOffset;
                int length;
                byte[] buf = (byte[])currentArguments[0];
                //InputStream.read(byte[]) 
                if(currentArguments.length ==1){
                    length = dataToWrite.length;
                    startOffset = 0;
                }
                //InputStream.read(byte[], offset, length)
                else{
                    startOffset = ((Integer)currentArguments[1]).intValue();
                    //write all bytes from given array or requested number of bytes
                    //which ever is less
                    length = Math.min(dataToWrite.length ,
                            ((Integer)currentArguments[2]).intValue());
                }
                for(int i=0; i<length; i++){
                    buf[i+startOffset]=dataToWrite[i];
                }
                return length;
            }

        };
    }
    public static void putByte(InputStream mockInputStream, byte value) throws IOException{
        putNumber(mockInputStream, value, 2);
    }
    public static void putShort(InputStream mockInputStream, short value) throws IOException{
        putNumber(mockInputStream, value, 4);
    }
    public static void putInt(InputStream mockInputStream, int value) throws IOException{
        putNumber(mockInputStream, value, 8);
    }
    public static void putLong(InputStream mockInputStream, long value) throws IOException{
        //for some reason inputStream reads longs as an array
        putNumberAsArray(mockInputStream, value, 16);
    }

    private static void putNumber(InputStream mockInputStream, long value,
            int maxNumberOfHexChars) throws IOException {
        String asHex = convertToPaddedHex(value, maxNumberOfHexChars);
        for(int i= 0; i<maxNumberOfHexChars; i+=2){
            String byteInHex = asHex.substring(i, i+2);
            expect(mockInputStream.read()).andReturn(Integer.valueOf(byteInHex, 16));
        }

    }
    private static void putNumberAsArray(InputStream mockInputStream, long value,
            int maxNumberOfHexChars) throws IOException {
        String asHex = convertToPaddedHex(value, maxNumberOfHexChars);
        byte[] array = new byte[maxNumberOfHexChars/2];
        for(int i= 0; i<maxNumberOfHexChars; i+=2){
            String byteInHex = asHex.substring(i, i+2);
            array[i/2] = Integer.valueOf(byteInHex, 16).byteValue();
        }
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(maxNumberOfHexChars/2)))
            .andAnswer(writeArrayToInputStream(array));

    }

    private static String convertToPaddedHex(long value, int maxNumberOfHexChars) {
        String hexString =Long.toHexString(value);
        int padding = maxNumberOfHexChars-hexString.length();
        StringBuilder paddingString = new StringBuilder();
        for(int i=0; i< padding; i++){
            paddingString.append("0");
        }
        paddingString.append(hexString);
        String asHex = paddingString.toString();
        return asHex;
    }
    
    public static void putUnSignedLong(InputStream mockInputStream, BigInteger unsignedLong) throws IOException{
        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(8)))
        .andAnswer(writeArrayToInputStream(IOUtil.convertUnsignedLongToByteArray(unsignedLong)));
    }
    public static void putUnSignedInt(InputStream mockInputStream, long unsignedInt) throws IOException{
        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(4)))
        .andAnswer(writeArrayToInputStream(IOUtil.convertUnsignedIntToByteArray(unsignedInt)));
    }
    public static void putUnSignedShort(InputStream mockInputStream, int unsignedShort) throws IOException{
        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(2)))
        .andAnswer(writeArrayToInputStream(IOUtil.convertUnsignedShortToByteArray(unsignedShort)));
    }
}

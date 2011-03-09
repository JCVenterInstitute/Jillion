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

package org.jcvi.plate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.CommonUtil;
/**
 * @author dkatzel
 *
 *
 */
public final class Well {
    
    private static final Pattern wellPattern = Pattern.compile("([A-P])(\\d+)");
    /**
     * Create a new Well instance for the given well name
     * as a string.
     * @param wellName
     * @return
     */
    public static Well create(String wellName){
        if(wellName ==null){
            throw new IllegalArgumentException("input can not be null");
        }
        Matcher m = wellPattern.matcher(wellName);

        if(m.find()){
            char row = m.group(1).charAt(0);
            byte col = Byte.parseByte(m.group(2));
            if(col >24){
                throw new IllegalArgumentException("invalid column " + col);
            }
            return new Well(row, col);
        }
        throw new IllegalArgumentException(
                "string does not contain a parseable Well : "+ wellName);
    }
    
    /**
     * Compute the Well for a given index for a 384 well plate.
     * If an index >= 384 is given, then this method will "rollover"
     * the index to make it under 384.
     *
     * @param i the index of the well to get
     * @return a {@link Well} representing the <code>i</code>th index.
     */
    public static Well compute384Well(int i) {
        return computeWell(i, 384, 24);
       
    }
    /**
     * Compute the Well for a given index for a 96 well plate.
     * If an index >= 96 is given, then this method will "rollover"
     * the index to make it under 96.
     *
     * @param i the index of the well to get
     * @return a {@link Well} representing the <code>i</code>th index.
     */
    public static Well compute96Well(int i) {
        return computeWell(i, 96, 12);
       
    }

    private static Well computeWell(int index, int totalNumberOfWells,
            int totalNumberOfColumns) {
        int column =  (index % totalNumberOfColumns) +1;
        char row = (char)( 'A'+(index %totalNumberOfWells)/ totalNumberOfColumns);
        return new Well(row,column);
    }
    /**
     * the row of this well.
     */
    private char row;
    /**
     * the column of this well.
     */
    private byte column;
    /**
     * Constructor.
     * @param row the row of this well
     * @param column the column of this well
     */
    private Well(char row, int column){
        if(row >'P' || row <'A'){
            throw new IllegalArgumentException("invalid row "+ row);
        }
        if(column <1 || column >=24){
            throw new IllegalArgumentException("invalid column "+ column);
        }
        this.row = row;
        this.column = (byte)column;
    }

    /**
     * @return the row
     */
    public char getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public byte getColumn() {
        return column;
    }

    /**
     * Returns the hash code value for this object.
     * @return the hash code for this object.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + row;
        result = prime * result + column;
        return result;
    }

    /**
     * Compares two {@link Well}s for equality.
     * The result is <code>true</code> if and only
     * if the argument is not <code>null</code>
     * and is a {@link Well} object that
     * has the similar row and column as this object.
     * @param obj the {@link Object} to compare with.
     * @return <code>true</code> if the objects are
     *  the same; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if(!(obj instanceof Well)){
            return false;
        }
        final Well other = (Well) obj;
        return columnSimilarTo(other) && rowSimilarTo(other);
    }

    private boolean rowSimilarTo(final Well other) {
       return CommonUtil.similarTo(getRow(), other.getRow());
    }
    private boolean columnSimilarTo(final Well other) {
        return CommonUtil.similarTo(getColumn(), other.getColumn());
     }

    /**
     * delegates to {@link #toZeroPaddedString()}.
     */
    @Override
    public String toString() {
        return toZeroPaddedString();
    }
    /**
     * Converts this Well into a String of the form:
     * <pre>
     *&lt;row&gt;&lt;column&gt;
     * </pre>
     * .
     * @return the {@link String} representation of this object.
     */
    public String toUnpaddedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(row);
        sb.append(String.format("%d", column));

        return sb.toString();
    }
    /**
     * Converts this Well into a String of the form:
     * <pre>
     *&lt;row&gt;&lt;0-padded column&gt;
     * </pre>
     * .
     * @return the {@link String} representation of this object.
     */
    public String toZeroPaddedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(row);
        sb.append(String.format("%02d", column));

        return sb.toString();
    }
    
}

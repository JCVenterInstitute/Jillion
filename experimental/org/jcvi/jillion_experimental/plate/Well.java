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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.plate;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code Well} is a class that represents a single well
 * in a reaction plate.
 * @author dkatzel
 *
 *
 */
public final class Well implements Comparable<Well>{
    
    private static final Pattern WELL_NAME_PATTERN = Pattern.compile("([A-P])(\\d+)");
    
    /**
     * A cache of all possible wells we could ever
     * try to create.  This will keep us from
     * needlessly making more instances
     * than we need.
     * The Well is queried and populated by 
     * {@link #getWell(char, int)}.
     */
    private static final Well[][] CACHE = new Well[16][24];
    /**
     * the row of this well.
     */
    private final char row;
    /**
     * the column of this well.
     */
    private final byte column;
    /**
     * Create a Well.getWell instance for the given well name
     * as a string.
     * @param wellName a well name for 96 or 384 well plates.
     * @return
     */
    public static Well create(String wellName){
        if(wellName ==null){
            throw new NullPointerException("input can not be null");
        }
        Matcher m = WELL_NAME_PATTERN.matcher(wellName);

        if(m.find()){
            char row = m.group(1).charAt(0);
            byte col = Byte.parseByte(m.group(2));
            if(col >24){
                throw new IllegalArgumentException("invalid column " + col);
            }
            return getWell(row, col);
        }
        throw new IllegalArgumentException(
                "string does not contain a parseable Well : "+ wellName);
    }
    /**
     * Gets a well from our Cache.  If the
     * well hasn't been created yet, then create a new 
     * instance and put it in the cache before returning.
     * @param row the row of the well
     * @param column the column of the well.
     * @return the cached Well instance (never null).
     */
    private static synchronized Well getWell(char row, int column){
        int rowIndex = (row -'A');
        int columIndex = column-1;
        if(CACHE[rowIndex][columIndex]!=null){
            return CACHE[rowIndex][columIndex];
        }
        Well newWell = new Well(row,column);
        CACHE[rowIndex][columIndex] = newWell;
        return newWell;
    }
    
    /**
     * Compute the Well for a given index for a 384 well plate.
     * If an index >= 384 is given, then this method will "rollover"
     * the index to make it under 384.
     *
     * @param i the index of the well to get
     * @param order the {@link IndexOrder} to use for the given index.
     * @return a {@link Well} representing the <code>i</code>th index 
     * in the given IndexOrder.
     * @throws NullPointerException if order is null.
     * @throws IllegalArgumentException if i <0.
     */
    public static Well compute384Well(int i,IndexOrder order) {
        return computeWell(PlateFormat._384,i, order);
       
    }
    /**
     * Compute the Well for a given index for a 96 well plate.
     * If an index >= 96 is given, then this method will "rollover"
     * the index to make it under 96.
     *
     * @param i the index of the well to get
     * @param order the {@link IndexOrder} to use for the given index.
     * @return a {@link Well} representing the <code>i</code>th index 
     * in the given IndexOrder.
     * @throws NullPointerException if order is null.
     * @throws IllegalArgumentException if i <0.
     */
    public static Well compute96Well(int i,IndexOrder order) {
        return computeWell(PlateFormat._96,i, order);
       
    }
    /**
     * Compute the Well for the given index for the given plate format.
     * This method will "rollover" the index to make it under the max number
     * of wells per plate.
     * @param format the {@link PlateFormat} to use.
     *@param i the index of the well to get
     * @param order the {@link IndexOrder} to use for the given index.
     * @return a {@link Well} representing the <code>i</code>th index 
     * in the given IndexOrder.
     * @throws NullPointerException if order or format is null.
     * @throws IllegalArgumentException if i <0.
     */
    public static Well computeWell(PlateFormat format, int i, IndexOrder order){
        if(format ==null){
            throw new NullPointerException("format can not be null");
        }
        return order.getWell(i, format);
    }
    private static void verifyPositiveIndex(int index) {
		if(index <0){
		    throw new IllegalArgumentException("index can not be <0");
		}
	}
	
    /**
     * Constructor.
     * @param row the row of this well
     * @param column the column of this well
     */
    private Well(char row, int column){
        if(row >'P' || row <'A'){
            throw new IllegalArgumentException("invalid row "+ row);
        }
        if(column <1 || column >24){
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
    
    public int get96WellIndex(){
        return get96WellIndex(IndexOrder.ROW_MAJOR);
    }
    public int get384WellIndex(){
        return get384WellIndex(IndexOrder.ROW_MAJOR);
    }

    public int get96WellIndex(IndexOrder order){
        return getWellIndex(PlateFormat._96,order);
    }
    public int get384WellIndex(IndexOrder order){
        return getWellIndex(PlateFormat._384,order);
    }
    
    public int getWellIndex(PlateFormat format, IndexOrder order){
        return order.getIndex(this,format);
    }
    public int getQuadrantIndex(PlateFormat format, IndexOrder order){
        return order.getQuadrantIndex(this,format);
    }
    public int get96WellQuadrantIndex(IndexOrder order){
        return getQuadrantIndex(PlateFormat._96,order);
    }
    public int get384WellQuadrantIndex(IndexOrder order){
        return getQuadrantIndex(PlateFormat._384,order);
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
        return this.row == other.row && this.column == other.column;
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
    
    /**
     * Wells are compared based on their String values returned by
     * {@link #toZeroPaddedString()}.
     */
     @Override
     public int compareTo(Well o) {
         return this.toZeroPaddedString().compareTo(o.toZeroPaddedString());
     }
    
    /**
     * {@code IndexOrder} defines the well order of indexes
     * into a plate.
     * @author dkatzel
     *
     *
     */
    public static enum IndexOrder{
        /**
         * Each row is filled first, once the row is full,
         * then the next row starts to get populated.
         * <p>
         * Ex: A01, A02, A03...B01, B02, B03...
         */
        ROW_MAJOR{
            @Override
            int getIndex(Well well, PlateFormat type){
                return (well.getRow() -'A') * type.getNumberOfColumns() +well.getColumn() -1;
            }

            @Override
            Well getWell(int index, PlateFormat type) {
                verifyPositiveIndex(index);
                int modIndex = index%type.getNumberOfWells();
                int column =  (modIndex % type.getNumberOfColumns())+1;
                char row = (char)( 'A'+(modIndex %type.getNumberOfWells())/ type.getNumberOfColumns());
                return Well.getWell(row,column);
            }
            
        },
        /**
         * Each column is filled first, once the column is full,
         * then the next column starts to get populated.
         * <p>
         * Ex: A01, B01, C01...A02, B02, C02...
         */
        COLUMN_MAJOR{
            int getIndex(Well well, PlateFormat type){
                 return ((well.getColumn()-1) * type.getNumberOfRows()) + (well.getRow() -'A');
            }
            @Override
            Well getWell(int index, PlateFormat type) { 
                verifyPositiveIndex(index);
                int modIndex = index%type.getNumberOfWells();
                char row = (char)( 'A'+(modIndex %type.getNumberOfRows()));
                int column =  (modIndex / type.getNumberOfRows()) +1;
                
                return Well.getWell(row,column);
            }
        },
        /**
         * The wells are filled in a quadrant by quadrant
         * using a checkerboard pattern
         * where every other well in each row is initially skipped
         * to be filled in later by a different quadrant.
         * <p>
         * First quadrant:
         * A01, A03, A05...A23, C01, C03...<br/>
         * Second quadrant:
         * A02, A04, A06...A24, C02, C04...<br/>
         * Third quadrant:
         * B01, B03, B05...B23, D01, D03...<br/>
         * Fourth quadrant:
         * B02, B04, B06...B24, D02, D04...
         */
        CHECKERBOARD{
            int getIndex(Well well, PlateFormat type){
                int column = well.getColumn()-1;
                int row = well.getRow()-'A';
                final int quadrantIndex = computeQuadrantIndex(column, row);
                int fullRows = computeNumberOfFilledRows(type, row);
                int partialRow = (column/2 +1)-1;
                int offsetIntoQuadrant =fullRows+partialRow;
                return type.getNumberOfWellsPerQuadrant()*quadrantIndex+offsetIntoQuadrant;
            }
            private int computeNumberOfFilledRows(PlateFormat type, int row) {
                return row>1 ? (row/2) * type.getNumberOfColumns()/2 : 0;
            }
            private int computeQuadrantIndex(int column, int row) {
                final int block;
                if(row%2==0){
                    if(column%2==0){
                        block=0;
                    }
                    else{
                        block=1;
                    }
                }else{
                    if(column%2==0){
                        block=2;
                    }else{
                        block=3;
                    }
                }
                return block;
            }
            @Override
            Well getWell(int index, PlateFormat type) { 
                verifyPositiveIndex(index);
                int modIndex = index%type.getNumberOfWells();
                int quadrantIndex = modIndex/type.getNumberOfWellsPerQuadrant();
                int offsetIntoQuadrant = modIndex % type.getNumberOfWellsPerQuadrant();
                int rowIndex = (offsetIntoQuadrant /(type.getNumberOfColumns()/2)) *2 + (quadrantIndex /2);
                int column =((offsetIntoQuadrant % (type.getNumberOfColumns()/2)) *2)+1 +(quadrantIndex%2);
                char row = (char)('A'+rowIndex);
                
                return Well.getWell(row,column);
            }
        },
        /**
         * Order of a 384 well plate to get picked a quickly 
         * as possible using the hamilton robot.  The wells are arrayed so that 
         * all 8 spans can be used at the same time.  Each column is filled one at a time,
         * but the odd wells are used first then the evens.
         * Once the column is full, then the next column starts to get populated.
         * <p>
         * Ex: A01, C01, E01...B01, D01,...A02, C02, E02...
         */
        HAMILTON_OPTIMIZED_COLUMN_MAJOR{
            int getIndex(Well well, PlateFormat type){
                int rowOffset = well.getRow() -'A';
                int rowIndex =rowOffset/2;
                if(rowOffset %2!=0){
                    rowIndex += type.getNumberOfRows()/2;
                }
                 return ((well.getColumn()-1) * type.getNumberOfRows()) + rowIndex;
            }
            @Override
            Well getWell(int index, PlateFormat type) { 
                verifyPositiveIndex(index);
                int modIndex = index%type.getNumberOfWells();
                int colIndex = modIndex / type.getNumberOfRows();
                int rowIndex = modIndex%type.getNumberOfRows();
                if(rowIndex >= type.getNumberOfRows()/2){
                    rowIndex = (rowIndex%(type.getNumberOfRows()/2))*2 +1;
                }else{
                    rowIndex = (rowIndex%(type.getNumberOfRows()/2))*2;
                }
                char row = (char)( 'A'+rowIndex);
                int column =  colIndex+1;
                
                return Well.getWell(row,column);
            }
        },
        /**
         * Well order for an Applied Biosystems
         * 3130 machine using 16 capillaries at a time.
         * Only {@link WellType#_96} is supported.
         * <p>
         * Ex: A01, A02, B01, B02,...H02, A03, A04, B03, B04....
         */
        ABI_3130_16_CAPILLARIES{
            int getIndex(Well well, PlateFormat type){
                if(type != PlateFormat._96){
                    throw new IllegalArgumentException("only 96 well plates supported");
                }
                int rowIndex =well.getRow() -'A';
                int colIndex =well.getColumn()-1;
                int capilaryIndex =colIndex /2;
                return capilaryIndex*16 + rowIndex*2 + colIndex%2;
                
           }
           @Override
           Well getWell(int index, PlateFormat type) { 
               verifyPositiveIndex(index);
               if(type != PlateFormat._96){
                   throw new IllegalArgumentException("only 96 well plates supported");
               }
               int modIndex = index%type.getNumberOfWells();
               int capilaryIndex = modIndex/16;
               int i = modIndex % 16;
               
               char row = (char)( 'A'+(i /2));
              
               int column =   (modIndex%2) +capilaryIndex*2+1;
               return Well.getWell(row, column);
           }
        }
        
        ;
        
        abstract  int getIndex(Well well, PlateFormat type);
        
        
        
        abstract Well getWell(int index, PlateFormat type);
        /**
         * Create a new {@link Comparator} instance
         * that compares wells using this IndexOrder's
         * index for 96 well plates.
         * @return a new Comparator instance.
         */
        public Comparator<Well> create96WellComparator(){
            return createWellComparator(PlateFormat._96);
        }
        /**
         * Create a new {@link Comparator} instance
         * that compares wells using this IndexOrder's
         * index for 384 well plates.
         * @return a new Comparator instance.
         */
        public Comparator<Well> create384WellComparator(){
            return createWellComparator(PlateFormat._384);
        }
        
        public Comparator<Well> createWellComparator(PlateFormat format){
            return new IndexOrderComparator(format, this);
        }
        
        public int getQuadrantIndex(Well well, PlateFormat type){
            int wellIndex = getIndex(well, type);
            return wellIndex/type.getNumberOfWellsPerQuadrant();
        }
        
    }
    /**
     * {@code IndexOrderComparator} is a Comparator
     * that will compare Wells based on PlateFormat
     * and IndexOrder.
     * 
     * @author dkatzel
     */
    private static final class IndexOrderComparator implements Comparator<Well>, Serializable{
        
        private static final long serialVersionUID = 8181294609649588216L;
        
        private final PlateFormat type;
        private final IndexOrder order;
        

        private IndexOrderComparator(PlateFormat type, IndexOrder order) {
            this.type = type;
            this.order = order;
        }


        @Override
        public int compare(Well o1, Well o2) {
            final int o1Index =o1.getWellIndex(type, order);
            final int o2Index=o2.getWellIndex(type, order);           
            return Integer.valueOf(o1Index).compareTo(o2Index);
        }
        
        
    }

}

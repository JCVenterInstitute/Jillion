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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.phredQuality;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.glyph.num.ByteGlyph;

public final class PhredQuality extends ByteGlyph{
    //90 should be enough for anybody
    public static final byte MAX_VALUE = 90;
    public static final byte MIN_VALUE = 0;
    
    public static final byte TIGR_TED_EDIT_VALUE = 99;
    public static final byte TIGR_CLOE_EDIT_VALUE = 40;
    
    private static final Map<Byte, PhredQuality> MAP;
    static{
        MAP = new HashMap<Byte, PhredQuality>();
        for(byte b=MIN_VALUE; b<=MAX_VALUE; b++){
            MAP.put(Byte.valueOf(b), new PhredQuality(b));
        }
    }
    
    private PhredQuality(byte b) {
        super(b);        
    }
    
    public static int convertErrorProbability(double errorProbability){
        return (int)Math.round(-10.0 * Math.log10(errorProbability));
    }
    public double getErrorProbability(){
        return Math.pow(10, this.getNumber()/-10D);
       
    }
    
    public static PhredQuality valueOf(int b){
       return valueOf((byte)b);
    }
    public static PhredQuality valueOf(byte b){
        if(b<MIN_VALUE || b > MAX_VALUE){
            throw new IllegalArgumentException("value of our range "+b);
        }
        return MAP.get(Byte.valueOf(b));
    }
    public static List<PhredQuality> valueOf(byte[] bytes){
        List<PhredQuality> list = new ArrayList<PhredQuality>(bytes.length);
        for(int i=0; i<bytes.length; i++){
            list.add(valueOf(bytes[i]));
        }
        return list;
    }
    public static PhredQuality tigrValueOf(int b){
        return tigrValueOf((byte)b);
     }
    /**
     * returns the {@link PhredQuality} of a quality 
     * that has been created at TIGR.  The TIGR legacy editor
     * TED stored human edited qualities as <code>99</code>
     * which is out of phred quality ranges.  TED's successor,
     * CLOE, stored human edited qualities as <code>40</code>.
     * This method will change any TIGR TED edited qualities
     * from <code>99</code> to <code>40</code>; anything else 
     * is left as is.
     * @param b
     * @return if b == 99 return valueOf(40) else return valueOf(b)
     */
    public static PhredQuality tigrValueOf(byte b){
        if(b == TIGR_TED_EDIT_VALUE){
            return valueOf(TIGR_CLOE_EDIT_VALUE);
        }
       return valueOf(b);
    }
    public static List<PhredQuality> tigrValueOf(byte[] bytes){
        List<PhredQuality> list = new ArrayList<PhredQuality>(bytes.length);
        for(int i=0; i<bytes.length; i++){
            list.add(tigrValueOf(bytes[i]));
        }
        return list;
    }
    public static byte[] toArray(Collection<PhredQuality> qualities){
        ByteBuffer buf = ByteBuffer.allocate(qualities.size());
        for(PhredQuality quality : qualities){
            buf.put(quality.getNumber());
        }
        return buf.array();
    }
    

    @Override
    public String toString() {        
        return String.format("Q%02d",this.getNumber());
    }
    
    public PhredQuality increaseBy(byte delta){
        return valueOf((byte)(getNumber() + delta));
    }
    public PhredQuality decreaseBy(byte delta){
        return increaseBy((byte)(-delta));
    }
    public PhredQuality increaseBy(PhredQuality q){
        return increaseBy(q.getNumber());
    }
    public PhredQuality decreaseBy(PhredQuality q){
        return decreaseBy(q.getNumber());
    }
    
}

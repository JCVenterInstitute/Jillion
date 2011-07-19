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
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;


public class DefaultNumericGlyph implements NumericGlyph{
    private final Number number;
    DefaultNumericGlyph(Number number){
        if(number ==null){
            throw new IllegalArgumentException("number can not be null");
        }
        this.number = number;
    }
    @Override
    public Number getNumber() {
        return number;
    }


    @Override
    public String getName() {
        return number.toString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Long.valueOf(number.longValue()).hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultNumericGlyph)){
            return false;
        }
        DefaultNumericGlyph other = (DefaultNumericGlyph) obj;
       return number.longValue()==other.number.longValue();
    }
    @Override
    public String toString() {
        return getNumber().toString();
    }
    
    
    
}

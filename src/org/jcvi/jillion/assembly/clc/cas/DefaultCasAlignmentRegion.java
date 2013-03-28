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
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

final class DefaultCasAlignmentRegion implements CasAlignmentRegion{

    private final CasAlignmentRegionType type;
    private final long length;
    
    
    /**
     * @param type
     * @param length
     */
    public DefaultCasAlignmentRegion(CasAlignmentRegionType type, long length) {
        if(type ==null){
            throw new NullPointerException("type can not be null");
        }
        if(length <0){
            throw new IllegalArgumentException("length can not < 0 : "+ length);
        }
        this.type = type;
        this.length = length;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public CasAlignmentRegionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DefaultCasAlignmentRegion [type=" + type + ", length=" + length
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (length ^ (length >>> 32));
        result = prime * result + type.hashCode();
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
        if (!(obj instanceof DefaultCasAlignmentRegion)) {
            return false;
        }
        DefaultCasAlignmentRegion other = (DefaultCasAlignmentRegion) obj;
        if (length != other.length) {
            return false;
        }
       return type.equals(other.type);
    }

}

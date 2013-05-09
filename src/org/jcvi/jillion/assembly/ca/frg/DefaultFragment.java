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
 * Created on Mar 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.trace.Trace;

public class DefaultFragment implements Fragment{
    private final String id;
    private final NucleotideSequence bases;
    private final QualitySequence qualities;
    private final Range validRange, vectorClearRange;
    private final String comment;
    private final Library library;
    public DefaultFragment(String id, Trace trace,Range validRange,Range vectorClearRange, Library library,String comment){
        this(id, trace.getNucleotideSequence(), trace.getQualitySequence(),validRange,vectorClearRange,library,comment);
    }
    public DefaultFragment(String id, Trace trace,Range validRange,Range vectorClearRange, Library library){
        this(id, trace,validRange,vectorClearRange,library,null);
    }
    public DefaultFragment(String id, Trace trace,Range validRange,Library library){
        this(id, trace,validRange,validRange,library,null);
    }
    public DefaultFragment(String id, Trace trace,Library library){
        this(id, trace,
        		new Range.Builder(trace.getNucleotideSequence().getLength()).build(),
        		library);
    }
    public DefaultFragment(String id, NucleotideSequence bases,
            QualitySequence qualities,Range validRange,Range vectorClearRange, Library library,String comment){
        if(id ==null){
            throw new IllegalArgumentException("id can not be null");
        }
        this.id = id;
        this.validRange = validRange;
        this.bases = bases;
        this.qualities = qualities;
        this.comment = comment;
        this.library = library;
        this.vectorClearRange = vectorClearRange;
    }

    @Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }


    @Override
    public NucleotideSequence getNucleotideSequence() {
        return bases;
    }
    public String getComment() {
        return comment;
    }
    @Override
    public Library getLibrary() {
        return library;
    }
    @Override
    public Range getVectorClearRange() {
        return vectorClearRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultFragment)){
            return false;
        }
        DefaultFragment other = (DefaultFragment) obj;
        return ObjectsUtil.nullSafeEquals(getId(), other.getId());
    }

    
}

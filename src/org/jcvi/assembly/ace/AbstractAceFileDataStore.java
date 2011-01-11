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
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;
/**
 * {@code AbstractAceFileVisitor} is an abstract
 * implementation of {@link AceFileVisitor}
 * that does all the computations required
 * to parse a valid Ace File.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractAceFileDataStore extends AbstractAceFileVisitor implements AceContigDataStore{
   
   
    private DefaultAceContig.Builder contigBuilder;
    /**
     * Visit the given fully constructed AceContig. 
     * @param contig the fully constructed AceContig
     * that was built from an Ace File.
     */
    protected abstract void  visitContig(AceContig contig);
    
    
    
    @Override
    public void visitEndOfContig() {
        if(contigBuilder !=null){
            visitContig(contigBuilder.build());
            contigBuilder=null;
        }
    }
    
    
    @Override
    protected void visitNewContig(String contigId, String consensus) {
        contigBuilder= new DefaultAceContig.Builder(contigId, consensus);
        
    }
    
    protected void visitAceRead(String readId, String validBasecalls, int offset, SequenceDirection dir, Range validRange, PhdInfo phdInfo,
            int ungappedFullLength){
        contigBuilder.addRead(readId, validBasecalls ,offset, dir, 
                validRange ,phdInfo,ungappedFullLength);
    }

    

}

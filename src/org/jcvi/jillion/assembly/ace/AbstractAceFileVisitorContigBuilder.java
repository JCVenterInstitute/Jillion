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
package org.jcvi.jillion.assembly.ace;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code AbstractAceFileVisitorContigBuilder} is an abstract
 * implementation of {@link AceFileVisitor}
 * that does all the computations required
 * to populate instances of {@link AceContigBuilder}
 * for each contig object that gets parsed
 * from an ace file.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractAceFileVisitorContigBuilder extends AbstractAceFileVisitor {
   
   
    private AceContigBuilder contigBuilder;

    /**
     * Visit the current AceContig data represented as an {@link AceContigBuilder}
     * object.  This AceContigBuilder contains the current contig id, its consensus sequence and all the underlying reads
     * as described by the ace file.  Clients are free to modify this {@link AceContigBuilder}
     * anyway they want including but not limited to adding/removing reads,
     * changing the contig id, editing reads etc.
     * <p/>
     * Only contigs that are visited as determined by {@link #visitBeginContig(String, int, int, int, boolean)
     * will cause this method to be called on them after they have been fully parsed.
     * @param contigBuilder an {@link AceContigBuilder} instance; never null.
     */
    protected abstract void visitContig(final AceContigBuilder contigBuilder);
        
    
    
    @Override
	protected final EndContigReturnCode handleEndOfContig() {
    	if(contigBuilder !=null){
            visitContig(contigBuilder);
            contigBuilder=null;
        }
        return getEndContigReturnCode();
	}

    protected EndContigReturnCode getEndContigReturnCode(){
    	return EndContigReturnCode.KEEP_PARSING;
    }
    
	@Override
    protected void visitNewContig(String contigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean complemented) {
        contigBuilder= new AceContigBuilder(contigId, consensus);
        contigBuilder.setComplemented(complemented);
        
    }
    @Override
    protected void visitAceRead(String readId, NucleotideSequence validBasecalls, int offset, Direction dir, Range validRange, PhdInfo phdInfo,
            int ungappedFullLength){
        contigBuilder.addRead(readId, validBasecalls ,offset, dir, 
                validRange ,phdInfo,ungappedFullLength);
    }

    

}

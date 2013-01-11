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
 * Created on Jun 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Before;
import org.junit.Test;
public class TestAceContigBuilderInvalidRead {

    private final String consensus = "ACGT";
    private final String contigId = "id";
    private AceContigBuilder sut;
    @Before
    public void setup(){
        sut = new AceContigBuilder(contigId, consensus);
    }
    
    @Test(expected= IllegalArgumentException.class)
    public void readThatGoesOffTheReferenceShouldThrowException(){
        String readId = "readId";
        int offset =1;
        String validBases = consensus;
        
        Range clearRange = new Range.Builder(validBases.length()).build();
        PhdInfo phdInfo = createMock(PhdInfo.class);
        addReadToBuilder(readId, validBases, offset, Direction.FORWARD, clearRange, phdInfo);
        assertEquals(sut.numberOfReads(),0);
    }
    
    private void addReadToBuilder(String id,String validBases,int offset,Direction dir, Range validRange, PhdInfo phdInfo){
    	sut.addRead(id, new NucleotideSequenceBuilder(validBases).build(), offset, dir, 
    			validRange, phdInfo,validBases.length());
        
    }
}

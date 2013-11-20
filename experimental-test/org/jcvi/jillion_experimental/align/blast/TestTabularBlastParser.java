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
package org.jcvi.jillion_experimental.align.blast;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.math.BigDecimal;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestTabularBlastParser {

    ResourceHelper resources = new ResourceHelper(TestTabularBlastParser.class);
    BlastVisitor sut;
    @Before
    public void setup(){
        sut = createMock(BlastVisitor.class);
    }
    @Test
    public void parseFile() throws IOException{
        setupExpectations();
        replay(sut);
        TabularBlastParser.create(resources.getFile("files/tabular.out")).parse(sut);
        verify(sut);
    }
    
    @Test
    public void parseInputStream() throws IOException{
        setupExpectations();
        replay(sut);
        TabularBlastParser.create(resources.getFileAsStream("files/tabular.out")).parse(sut);
        verify(sut);
    }
    /**
     * 
     */
    private void setupExpectations() {
        sut.visitFile();
        
        
        sut.visitHsp(HspBuilder.create("AF178033")
                .subject("EMORG:AF031391")
                .percentIdentity(85.48D)
                .alignmentLength(806)
                .numMismatches(117)
                .numGapOpenings(0)
                .queryRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,1,806)))
                .subjectRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,99,904)))
                .eValue(new BigDecimal("0.0"))
                .bitScore(new BigDecimal("644.8"))
                .build());
        
        sut.visitHsp(HspBuilder.create("AF178033")
                .subject("EMORG:AF353201")
                .percentIdentity(85.36D)
                .alignmentLength(806)
                .numMismatches(118)
                .numGapOpenings(0)
                .queryRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,1,806)))
                .subjectRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,99,904)))
                .eValue(new BigDecimal("1e-179"))
                .bitScore(new BigDecimal("636.8"))
                .build());
        
        sut.visitHsp(HspBuilder.create("AF178033")
                .subject("EMORG:AF353200")
                .percentIdentity(84.99D)
                .alignmentLength(806)
                .numMismatches(121)
                .numGapOpenings(0)
                .queryRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,1,806)))
                .subjectRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,99,904)))
                .eValue(new BigDecimal("2e-172"))
                .bitScore(new BigDecimal("613.0"))
                .build());
        
        sut.visitEnd();
        
        
    }

}

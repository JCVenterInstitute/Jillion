/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.align.blast;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.math.BigDecimal;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.experimental.align.blast.BlastHitImpl.Builder;
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
        
        sut.visitHit(new BlastHitImpl.Builder("AF178033","EMORG:AF031391")
        				.addHsp(HspBuilder.forBlastN().query("AF178033")
        		                .subject("EMORG:AF031391")
        		                .percentIdentity(85.48D)
        		                .alignmentLength(806)
        		                .numMismatches(117)
        		                .numGapOpenings(0)
        		                .queryRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,1,806)))
        		                .subjectRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,99,904)))
        		                .eValue(new BigDecimal("0.0"))
        		                .bitScore(new BigDecimal("644.8"))
        		                .build())
    		                .build());
       
        
        sut.visitHit(new BlastHitImpl.Builder("AF178033","EMORG:AF353201")
						.addHsp(HspBuilder.forBlastN().query("AF178033")
				                .subject("EMORG:AF353201")
				                .percentIdentity(85.36D)
				                .alignmentLength(806)
				                .numMismatches(118)
				                .numGapOpenings(0)
				                .queryRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,1,806)))
				                .subjectRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,99,904)))
				                .eValue(new BigDecimal("1e-179"))
				                .bitScore(new BigDecimal("636.8"))
				                .build())
			            .build());
        sut.visitHit(new BlastHitImpl.Builder("AF178033","EMORG:AF353200")
						.addHsp(HspBuilder.forBlastN().query("AF178033")
				                .subject("EMORG:AF353200")
				                .percentIdentity(84.99D)
				                .alignmentLength(806)
				                .numMismatches(121)
				                .numGapOpenings(0)
				                .queryRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,1,806)))
				                .subjectRange(DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,99,904)))
				                .eValue(new BigDecimal("2e-172"))
				                .bitScore(new BigDecimal("613.0"))
				                .build())
				            .build());
        
        sut.visitEnd();
        
        
    }

}

/*
 * Created on Aug 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.Arrays;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
public class TestMultipleContigFileVisitors {

    private ContigFileVisitor visitor_1,visitor_2;
    private MultipleContigFileVisitors sut;
    @Before
    public void setup(){
        visitor_1 = createMock(ContigFileVisitor.class);
        visitor_2 = createMock(ContigFileVisitor.class);
        sut = new MultipleContigFileVisitors(Arrays.asList(visitor_1,visitor_2));
    }
    
    @Test
    public void visitBasecallsLine(){
        String line = "basecalls line";
        visitor_1.visitBasecallsLine(line);
        visitor_2.visitBasecallsLine(line);
        replay(visitor_1,visitor_2);
        sut.visitBasecallsLine(line);
        verify(visitor_1,visitor_2);
    }
    
    @Test
    public void visitEndOfFile(){
        visitor_1.visitEndOfFile();
        visitor_2.visitEndOfFile();
        replay(visitor_1,visitor_2);
        sut.visitEndOfFile();
        verify(visitor_1,visitor_2);
    }
    
    @Test
    public void visitLine(){
        String line = "line";
        visitor_1.visitLine(line);
        visitor_2.visitLine(line);
        replay(visitor_1,visitor_2);
        sut.visitLine(line);
        verify(visitor_1,visitor_2);
    }
    
    @Test
    public void visitNewContig(){
        String contigId = "contig_id";
        visitor_1.visitNewContig(contigId);
        visitor_2.visitNewContig(contigId);
        replay(visitor_1,visitor_2);
        sut.visitNewContig(contigId);
        verify(visitor_1,visitor_2);
    }
    
    @Test
    public void visitNewRead(){
        String readId = "readId";
        int offset = 1234;
        Range validRange = Range.buildRange(0,10);
        SequenceDirection dir = SequenceDirection.FORWARD;
        visitor_1.visitNewRead(readId, offset, validRange,dir);
        visitor_2.visitNewRead(readId, offset, validRange,dir);
        replay(visitor_1,visitor_2);
        sut.visitNewRead(readId, offset, validRange,dir);
        verify(visitor_1,visitor_2);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullVisitorsThrowsNPE(){
        new MultipleContigFileVisitors(null);
    }
}

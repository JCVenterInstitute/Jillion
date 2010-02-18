/*
 * Created on Dec 19, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jcvi.Range;
import org.jcvi.assembly.annot.DefaultExon;
import org.jcvi.assembly.annot.Exon;
import org.jcvi.assembly.annot.Frame;
import org.jcvi.assembly.annot.Strand;
import org.jcvi.assembly.annot.ref.CodingRegionState;
import org.jcvi.assembly.annot.ref.DefaultCodingRegion;
import org.jcvi.assembly.annot.ref.DefaultRefGene;
import org.jcvi.assembly.annot.ref.RefGene;
import org.jcvi.assembly.annot.ref.writer.RefGeneTxtWriter;
import org.jcvi.assembly.annot.ref.writer.RefGeneWriter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

public class TestRefGeneTxtWriter {
    Exon exon1 = new DefaultExon(Frame.ZERO, 20,300);
    Exon exon2 = new DefaultExon(Frame.ONE, 501,600);
    
    RefGene singleExon = new DefaultRefGene("singleExon", "segment 1", 
            Strand.REVERSE, Range.buildRange(0,822),
            new DefaultCodingRegion( Range.buildRange(0,822),
                    CodingRegionState.COMPLETE,
                    CodingRegionState.INCOMPLETE,
                    Arrays.asList(exon1)));
    
    RefGene twoExons = new DefaultRefGene("twoExons", "chromosome 3", 
            Strand.FORWARD, Range.buildRange(0,711),
            new DefaultCodingRegion( Range.buildRange(20,600),
                    CodingRegionState.NONE,
                    CodingRegionState.UNKNOWN,
                    Arrays.asList(exon1, exon2)));
    OutputStream out;
    RefGeneWriter sut;
    
    @Before
    public void setup(){
        out = createMock(OutputStream.class);
        sut = new RefGeneTxtWriter(out);
    }
    
    @Test
    public void write() throws IOException{
        //single exon is broken into 2 pieces
        final String singleExonString = "0\tsingleExon\tsegment 1\t"+Strand.REVERSE +
        "\t0\t822\t0\t822\t2\t20,23,\t22,300,\t0\tsingleExon\tcmpl\tincmpl\t0,0,\t\n";
        
        final String twoExonString ="0\ttwoExons\tchromosome 3\t"+Strand.FORWARD +
        "\t0\t711\t20\t600\t2\t20,501,\t300,600,\t0\ttwoExons\tnone\tunk\t0,1,\t\n"
        ;
        out.write(isA(byte[].class));
        expectLastCall().andAnswer(new IAnswer<Object>(){

            @Override
            public Object answer() throws Throwable {
                String actual = new String((byte[])EasyMock.getCurrentArguments()[0]);
                assertEquals("written bytes don't match"+ actual,singleExonString, actual);
                return null;
            }
            
        });
        out.write(isA(byte[].class));
        expectLastCall().andAnswer(new IAnswer<Object>(){

            @Override
            public Object answer() throws Throwable {
                String actual = new String((byte[])EasyMock.getCurrentArguments()[0]);
                assertEquals("written bytes don't match"+ actual,twoExonString, actual);
                return null;
            }
            
        });
        replay(out);
        sut.write(Arrays.asList(singleExon,twoExons));
        verify(out);
    }
    
}

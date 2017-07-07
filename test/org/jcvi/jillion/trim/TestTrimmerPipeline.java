/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.trim;

import java.util.Arrays;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.jcvi.jillion.trace.Trace;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestTrimmerPipeline {

    
    @Test
    public void shortCircuitRange(){
        NucleotideTrimmer trimmer = createMock(NucleotideTrimmer.class);
        expect(trimmer.trim(isA(NucleotideSequenceBuilder.class))).andReturn(Range.of(9,19)).anyTimes();
        
        replay(trimmer);
        
        TrimmerPipeline sut = new TrimmerPipelineBuilder()
                                    .add(trimmer)
                                    .filterRange(range-> range.getBegin() > 5)
                                    .build();
        
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("A", 100);
      
        assertTrue(sut.trim(seq).isEmpty());
    }
    @Test
    public void nucleotideTrimmerOnly(){
        NucleotideTrimmer trimmer = createMock(NucleotideTrimmer.class);
        expect(trimmer.trim(isA(NucleotideSequenceBuilder.class))).andReturn(Range.of(9,19)).anyTimes();
        
        replay(trimmer);
        
        TrimmerPipeline sut = new TrimmerPipelineBuilder()
                                    .add(trimmer)
                                    .build();
        
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("A", 100);
        
        Range actual = sut.trim(seq);
        
        assertEquals(Range.of(9,19), actual);
        QualitySequence quals = new QualitySequenceBuilder(new byte[]{20,20,20,20,20}).build();
        assertEquals(Range.ofLength(quals.getLength()), sut.trim(quals));
        
        Trace trace = createMock(Trace.class);
        expect(trace.getNucleotideSequence()).andStubReturn(seq);
        expect(trace.getQualitySequence()).andStubReturn(quals);
        expect(trace.getLength()).andReturn(seq.getLength());
        
        replay(trace);
        assertEquals(Range.of(9,19), sut.trim(trace));
    }
    
    
    @Test
    public void QualityTrimmerOnly(){
        
        byte[] bytes = new byte[100];
        Arrays.fill(bytes, (byte)20);
        
        QualityTrimmer trimmer = createMock(QualityTrimmer.class);
        expect(trimmer.trim(isA(QualitySequenceBuilder.class))).andReturn(Range.of(9,19)).anyTimes();
        
        replay(trimmer);
        
        TrimmerPipeline sut = new TrimmerPipelineBuilder()
                                    .add(trimmer)
                                    .build();
        
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("A", 100);
        
        Range actual = sut.trim(seq);
        
        assertEquals(Range.ofLength(100), actual);
        QualitySequence quals = new QualitySequenceBuilder(bytes).build();
        assertEquals(Range.of(9,19), sut.trim(quals));
        
        
        Trace trace = createMock(Trace.class);
        expect(trace.getNucleotideSequence()).andStubReturn(seq);
        expect(trace.getQualitySequence()).andStubReturn(quals);
        expect(trace.getLength()).andReturn(quals.getLength());
        
        replay(trace);
        assertEquals(Range.of(9,19), sut.trim(trace));
    }
    
    @Test
    public void multipleNucTrimmersShouldCombine(){
        NucleotideTrimmer trimmer = createMock(NucleotideTrimmer.class);
        
        Capture<NucleotideSequenceBuilder> seqCapture = EasyMock.newCapture();
        
        expect(trimmer.trim(and(isA(NucleotideSequenceBuilder.class), capture(seqCapture))))
                                .andStubAnswer(() ->{ 
                                    Range r= new Range.Builder(seqCapture.getValue().getLength())
                                                .contractBegin(10)
                                                .build();
                                  //  System.out.println(r);
                                    return r;
                                }
                                );
        
        replay(trimmer);
        
        TrimmerPipeline sut = new TrimmerPipelineBuilder()
                        .add(trimmer)
                        .add(trimmer)
                        .build();
                
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("A", 100);
        
        Range actual = sut.trim(seq);
        
        assertEquals(Range.of(20, 99), actual);
    }
    
    @Test
    public void multipleQualTrimmersShouldCombine(){
        QualityTrimmer qualtrimmer = createMock(QualityTrimmer.class);
        
        Capture<QualitySequenceBuilder> qualCapture = EasyMock.newCapture();
        
        expect(qualtrimmer.trim(and(isA(QualitySequenceBuilder.class), capture(qualCapture))))
                                .andStubAnswer(() ->{ 
                                    return new Range.Builder(qualCapture.getValue().getLength())
                                                .contractEnd(10)
                                                .build();
                                }
                                );
        
        replay(qualtrimmer);
        
        TrimmerPipeline sut = new TrimmerPipelineBuilder()
                        .add(qualtrimmer)
                        .add(qualtrimmer)
                        .build();
                
        byte[] bytes = new byte[100];
        Arrays.fill(bytes, (byte)20);
        QualitySequence quals = new QualitySequenceBuilder(bytes).build();
        
        Range actual = sut.trim(quals);
        
        assertEquals(Range.of(0, 79), actual);
    }
    
    @Test
    public void trimTraceShouldIntersectNucAndQualResults(){
        QualityTrimmer qualtrimmer = createMock(QualityTrimmer.class);
        
        Capture<QualitySequenceBuilder> qualCapture = EasyMock.newCapture();
        
        expect(qualtrimmer.trim(and(isA(QualitySequenceBuilder.class), capture(qualCapture))))
                                .andStubAnswer(() ->{ 
                                    return new Range.Builder(qualCapture.getValue().getLength())
                                                .contractEnd(10)
                                                .build();
                                }
                                );
        
        
        NucleotideTrimmer trimmer = createMock(NucleotideTrimmer.class);
        
        Capture<NucleotideSequenceBuilder> seqCapture = EasyMock.newCapture();
        
        expect(trimmer.trim(and(isA(NucleotideSequenceBuilder.class), capture(seqCapture))))
                                .andStubAnswer(() ->{ 
                                    return new Range.Builder(seqCapture.getValue().getLength())
                                                .contractBegin(10)
                                                .build();
                                }
                                );
        
        replay(qualtrimmer, trimmer);
        
        TrimmerPipeline sut = new TrimmerPipelineBuilder()
                        .add(qualtrimmer)
                        .add(qualtrimmer)
                         .add(trimmer)
                         .add(trimmer)
                        
                        .build();
                
        byte[] bytes = new byte[100];
        Arrays.fill(bytes, (byte)20);
        QualitySequence quals = new QualitySequenceBuilder(bytes).build();
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("A", 100);
        
        
        Trace trace = createMock(Trace.class);
        expect(trace.getNucleotideSequence()).andStubReturn(seq);
        expect(trace.getQualitySequence()).andStubReturn(quals);
        
        replay(trace);
        
        Range actual = sut.trim(trace);
        
        assertEquals(Range.of(20, 79), actual);
    }
}

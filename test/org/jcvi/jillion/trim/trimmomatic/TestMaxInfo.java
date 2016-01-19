package org.jcvi.jillion.trim.trimmomatic;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestMaxInfo {

    MaxInfoTrimmer sut = new MaxInfoTrimmer(100, .998);
    @Test
    public void goodQualityDontTrimAnything(){
        QualitySequence quals = qualFor("FDEFF?DFEFE?BEEEEED=DB:DCEAEEB,CC=@B=5?B?CC5C?B+A??=>:CC<9-B2=@>-?:-<A@@A?9>*0<:'0%6,>:9&-:>?:>==B??");
    
        assertEquals(Range.ofLength(quals.getLength()), sut.trim(quals));
    }
    
    @Test
    public void trimOffAFewBasesFromEnd(){
        QualitySequence quals = qualFor("C??=-C=ACCD?BD56DDD?DD5CD.=*;BC5-C:ACA??D=-A?C@:??5AC:==CC=C:A?>4:186?58C5C#########################");
        assertEquals(Range.ofLength(quals.getLength() -2), sut.trim(quals));
    }
    
    @Test
    public void trimMore(){
        QualitySequence quals = qualFor("C??=-C=ACCD?BD56DDD?DD5CD.=*;BC5-C:ACA??D=-A?C@:??5AC:==CC=C:A?>4:186?58C5C#########################");
        assertEquals(Range.ofLength(47), new MaxInfoTrimmer(40, .998).trim(quals));
    }
    
    @Test
    public void trimLess(){
        QualitySequence quals = qualFor("C??=-C=ACCD?BD56DDD?DD5CD.=*;BC5-C:ACA??D=-A?C@:??5AC:==CC=C:A?>4:186?58C5C#########################");
        assertEquals(Range.ofLength(75), new MaxInfoTrimmer(40, .2).trim(quals));
    }

    
    private static QualitySequence qualFor(String encoded){
        return FastqQualityCodec.SANGER.decode(encoded, true);
    }
}

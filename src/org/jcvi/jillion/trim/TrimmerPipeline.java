package org.jcvi.jillion.trim;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.Trace;

public class TrimmerPipeline {

    private final List<NucleotideTrimmer> nucleotideTrimmers;
    private final List<QualityTrimmer> qualityTrimmers;
    
    
        
    TrimmerPipeline(List<NucleotideTrimmer> nucleotideTrimmers,
            List<QualityTrimmer> qualityTrimmers) {
        this.nucleotideTrimmers = new ArrayList<>(nucleotideTrimmers);
        this.qualityTrimmers = new ArrayList<>(qualityTrimmers);
    }



    public Range trim(Trace trace){
        Range nucRange, qualRange;
        if(!nucleotideTrimmers.isEmpty()){
            nucRange = trim(trace.getNucleotideSequence());            
        }else{
            nucRange = Range.ofLength(trace.getLength());
        }
        
        if(!qualityTrimmers.isEmpty()){
            qualRange = trim(trace.getQualitySequence());            
        }else{
            qualRange = Range.ofLength(trace.getLength());
        }
        
        
        return nucRange.intersection(qualRange);
    }
    
    public Range trim(NucleotideSequence seq){
        NucleotideSequenceBuilder builder = seq.toBuilder();
        
        Range.Builder fullTrimRange = new Range.Builder(builder.getLength());
         for(NucleotideTrimmer trimmer : nucleotideTrimmers){
             Range currentRange = trimmer.trim(builder);
             fullTrimRange.contractBegin(currentRange.getBegin());
             fullTrimRange.setEnd(fullTrimRange.getBegin()+ currentRange.getLength()-1);
             
             builder.trim(currentRange);
         }
         
         return fullTrimRange.build();
    }
    
    public Range trim(QualitySequence seq){
        QualitySequenceBuilder builder = seq.toBuilder();
        
        Range.Builder fullTrimRange = new Range.Builder(builder.getLength());
         for(QualityTrimmer trimmer : qualityTrimmers){
             Range currentRange = trimmer.trim(builder);
             fullTrimRange.contractBegin(currentRange.getBegin());
             fullTrimRange.setEnd(fullTrimRange.getBegin()+ currentRange.getLength()-1);
             
             builder.trim(currentRange);
         }
         
         return fullTrimRange.build();
    }
}

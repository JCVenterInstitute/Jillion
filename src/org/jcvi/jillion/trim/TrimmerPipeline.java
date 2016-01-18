package org.jcvi.jillion.trim;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.Builder;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.Trace;

public class TrimmerPipeline {

    private static final long NOT_SET =1;
    
    private static Range EMPTY = Range.ofLength(0);
    private static Range.Builder EMPTY_BUILDER = new Range.Builder(0);
    
    private final List<NucleotideTrimmer> nucleotideTrimmers;
    private final List<QualityTrimmer> qualityTrimmers;
    
    private final Predicate<Rangeable> predicate;
    private final long minLength;
        
    TrimmerPipeline(List<NucleotideTrimmer> nucleotideTrimmers,
            List<QualityTrimmer> qualityTrimmers,
            Predicate<Rangeable> predicate, long minLength) {
        this.nucleotideTrimmers = new ArrayList<>(nucleotideTrimmers);
        this.qualityTrimmers = new ArrayList<>(qualityTrimmers);
        this.predicate = predicate;
        this.minLength = minLength;
        
    }



    public Range trim(Trace trace){
        Range.Builder range;
        if(nucleotideTrimmers.isEmpty()){
           return trim(trace.getQualitySequence());
        }else{           
            range = nucTrim(trace.getNucleotideSequence());  
        }
        
        if(!qualityTrimmers.isEmpty()){                     
            range.intersect(qualTrim(trace.getQualitySequence()));  
        }
        
       
        if(minLength !=NOT_SET && range.getLength() < minLength){
            return EMPTY;
        }
        
        if(predicate !=null && predicate.test(range)){
            //short circuit
            return EMPTY;
        }
        return range.build();
    }
    
    public Range trim(NucleotideSequence seq){
        Builder builder = nucTrim(seq);
        if(builder == EMPTY_BUILDER){
            return EMPTY;
        }
        return builder.build();
    }
    
    private Range.Builder nucTrim(NucleotideSequence seq){
        long length = seq.getLength();
        if(minLength !=NOT_SET && length < minLength){
            return EMPTY_BUILDER;
        }
        NucleotideSequenceBuilder builder = seq.toBuilder();
       
        Range.Builder fullTrimRange = new Range.Builder(length);
        if(predicate !=null && predicate.test(fullTrimRange)){
            //short circuit
            return EMPTY_BUILDER;
        }
         for(NucleotideTrimmer trimmer : nucleotideTrimmers){
             Range currentRange = trimmer.trim(builder);
             if(minLength !=NOT_SET && currentRange.getLength() < minLength){
                 return EMPTY_BUILDER;
             }
             fullTrimRange.contractBegin(currentRange.getBegin());
             fullTrimRange.setEnd(fullTrimRange.getBegin() + currentRange.getLength()-1);
             
             if(predicate !=null && predicate.test(fullTrimRange)){
                 //short circuit
                 return EMPTY_BUILDER;
             }
             builder.trim(currentRange);
         }
         
         return fullTrimRange;
    }
    public Range trim(QualitySequence seq){
        Range.Builder builder = qualTrim(seq);
        if(builder == EMPTY_BUILDER){
            return EMPTY;
        }
        return builder.build();
    }
    private Range.Builder qualTrim(QualitySequence seq){
        long length = seq.getLength();
        
        if(minLength !=NOT_SET && length < minLength){
            return EMPTY_BUILDER;
        }
        QualitySequenceBuilder builder = seq.toBuilder();
        
        Range.Builder fullTrimRange = new Range.Builder(length);
        
        if(predicate !=null && predicate.test(fullTrimRange)){
            //short circuit
            return EMPTY_BUILDER;
        }
         for(QualityTrimmer trimmer : qualityTrimmers){
             Range currentRange = trimmer.trim(builder);
             if(minLength !=NOT_SET && currentRange.getLength() < minLength){
                 return EMPTY_BUILDER;
             }
             fullTrimRange.contractBegin(currentRange.getBegin());
             fullTrimRange.setEnd(fullTrimRange.getBegin() + currentRange.getLength()-1);
            
             if(predicate !=null && predicate.test(fullTrimRange)){
                 //short circuit
                 return EMPTY_BUILDER;
             }
             builder.trim(currentRange);
         }
         
         return fullTrimRange;
    }
}

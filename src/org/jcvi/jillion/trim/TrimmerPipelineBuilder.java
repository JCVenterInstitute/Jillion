package org.jcvi.jillion.trim;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class TrimmerPipelineBuilder {

    final List<NucleotideTrimmer> nucleotideTrimmers = new ArrayList<>();
    final List<QualityTrimmer> qualityTrimmers = new ArrayList<>();
    
    
    Predicate<Rangeable> rangePredicate = null;
    Predicate<NucleotideSequence> seqPredicate = null;
    Predicate<QualitySequence> qualityPredicate = null;
    long minLength = -1;
    
    public TrimmerPipelineBuilder minLength(long length){
        if(length < 0){
            throw new IllegalArgumentException("min length must be >= 0");
        }
        this.minLength = length;
        return this;
    }
    public TrimmerPipelineBuilder filterRange(Predicate<Rangeable> predicate){
        this.rangePredicate = predicate;
        return this;
    }
    
    public TrimmerPipelineBuilder filterSequence(Predicate<NucleotideSequence> predicate){
        this.seqPredicate = predicate;
        return this;
    }
    
    public TrimmerPipelineBuilder filterQualities(Predicate<QualitySequence> predicate){
        this.qualityPredicate = predicate;
        return this;
    }
    
    public TrimmerPipelineBuilder add(NucleotideTrimmer trimmer){
        Objects.requireNonNull(trimmer);
        nucleotideTrimmers.add(trimmer);
        return this;
    }
    
    public TrimmerPipelineBuilder add(QualityTrimmer trimmer){
        Objects.requireNonNull(trimmer);
        qualityTrimmers.add(trimmer);
        return this;
    }
    
    public TrimmerPipeline build(){
        return new TrimmerPipeline(this);
    }
    
}

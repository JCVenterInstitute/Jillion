package org.jcvi.jillion.trim;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrimmerPipelineBuilder {

    private final List<NucleotideTrimmer> nucleotideTrimmers = new ArrayList<>();
    private final List<QualityTrimmer> qualityTrimmers = new ArrayList<>();
    
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
        return new TrimmerPipeline(nucleotideTrimmers, qualityTrimmers);
    }
    
}

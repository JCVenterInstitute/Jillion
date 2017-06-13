package org.jcvi.jillion.align.exonerate.vulgar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.ToIntFunction;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.IupacTranslationTables;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class VulgarProtein2Genome {

    private final List<VulgarElement> elements;

    private final List<Range> targetExons;
    private final List<Range> queryRanges;
    
    private final String queryId, targetId;
    
    private final float score;
    
    private Direction queryStrand, targetStrand;
    private final DirectedRange queryRange, targetRange;
    
    public VulgarProtein2Genome(String queryId, String targetId, List<VulgarElement> elements, float score,
            String queryStrand, DirectedRange queryRange, String targetStrand, DirectedRange targetRange) {
        this.elements = elements;
        this.queryId = Objects.requireNonNull(queryId);
        this.targetId = Objects.requireNonNull(targetId);
        this.score = score;
        this.queryRange = Objects.requireNonNull(queryRange);
        this.targetRange = Objects.requireNonNull(targetRange);
        
        targetExons = computeTargetExons();
        queryRanges = computeQueryRanges();
        
        this.queryStrand = parseStrand(queryStrand);
        this.targetStrand = parseStrand(targetStrand);
        
      
    }
    
    private Direction parseStrand(String strand){
        if(".".equals(strand)){
            return null;
        }
        if("+".equals(strand)){
            return Direction.FORWARD;
        }
        return Direction.REVERSE;
    }
    
    public Optional<Direction> getQueryStrand(){
        return Optional.ofNullable(queryStrand);
    }
    
    public Optional<Direction> getTargetStrand(){
        return Optional.ofNullable(targetStrand);
    }
    
    public float getScore() {
        return score;
    }




    public String getQueryId() {
        return queryId;
    }




    public String getTargetId() {
        return targetId;
    }




    public List<VulgarElement> getElements() {
        return elements;
    }




    public List<Range> getTargetExons() {
        return targetExons;
    }




    public List<Range> getQueryRanges() {
        return queryRanges;
    }




    private List<Range> computeQueryRanges() {
        return computeRanges(queryRange.getBegin(), VulgarElement::getQueryLength);
    }



    private List<Range> computeRanges(long startOffset, ToIntFunction<VulgarElement> function) {
        List<Range> exons = new ArrayList<Range>();
        long currentOffset=startOffset;
        for(VulgarElement e : elements){
            int len = function.applyAsInt(e);
            if(e.getOp() == VulgarOperation.Match || e.getOp() == VulgarOperation.Split_Codon){
                exons.add(new Range.Builder(len).shift(currentOffset).build());
            }
            currentOffset+=len;
        }
        return Ranges.merge(exons);
    }
    private List<Range> computeTargetExons() {
        return computeRanges(targetRange.getBegin(), VulgarElement::getTargetLength);
    }
    //this functional interface is created and used
    //instead of Function<String, Sequence>
    //because we will most often use DataStore::get
    //which throws exceptions
    //so we have to make our own version that can also throw an Exception.
    @FunctionalInterface
    public interface ToSequenceFunction<T extends Sequence<?>, E extends Exception>{

         T apply(String t) throws E;
        
    }
    
    
    public <E extends Exception, E2 extends Exception> AlignmentResult align(ToSequenceFunction<NucleotideSequence, E> targetFunction,
                                 ToSequenceFunction<ProteinSequence, E2> queryFunction) throws E, E2{
        return align(targetFunction.apply(targetId), queryFunction.apply(queryId));
    }
    public AlignmentResult align(NucleotideSequence target, ProteinSequence query){
        
       
        NucleotideSequence cds =getExonSequence(target, targetExons);
        ProteinSequence translated = IupacTranslationTables.STANDARD.translate(cds);
        
        
        System.out.println(query.toBuilder(queryRange.asRange()).build().toString(AminoAcid::get3LetterAbbreviation));
        System.out.println(translated.toString(AminoAcid::get3LetterAbbreviation));
        
        //assume no gaps
        
        Iterator<AminoAcid> queryIter = query.iterator(queryRange.asRange());
        Iterator<AminoAcid> subIter = translated.iterator();
        int matches=0, misMatches=0;
        while(queryIter.hasNext() && subIter.hasNext()){
            if(queryIter.next() == subIter.next()){
                matches++;
            }else{
                misMatches++;
            }
        }
        while(queryIter.hasNext()){
            queryIter.next();
            misMatches++;
        }
        
        while(subIter.hasNext()){
            subIter.next();
            misMatches++;
        }
        System.out.println("matches = " + matches);
        System.out.println("misMatches = " + misMatches);
        double ident = matches /(double)(matches + misMatches) *100;
        System.out.println("percent ident = " + ident);
        return new AlignmentResult(ident, matches, misMatches);
    }
    
    
    public static class AlignmentResult{
        private final double percentIdentity;
        private final int matches, misMatches;
        
        protected AlignmentResult(double percentIdentity, int matches,
                int misMatches) {
            this.percentIdentity = percentIdentity;
            this.matches = matches;
            this.misMatches = misMatches;
        }

        public double getPercentIdentity() {
            return percentIdentity;
        }

        public int getMatches() {
            return matches;
        }

        public int getMisMatches() {
            return misMatches;
        }

        @Override
        public String toString() {
            return "AlignmentResult [percentIdentity=" + percentIdentity
                    + ", matches=" + matches + ", misMatches=" + misMatches
                    + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + matches;
            result = prime * result + misMatches;
            long temp;
            temp = Double.doubleToLongBits(percentIdentity);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof AlignmentResult)) {
                return false;
            }
            AlignmentResult other = (AlignmentResult) obj;
            if (matches != other.matches) {
                return false;
            }
            if (misMatches != other.misMatches) {
                return false;
            }
            if (Double.doubleToLongBits(percentIdentity) != Double
                    .doubleToLongBits(other.percentIdentity)) {
                return false;
            }
            return true;
        }
        
        
    }

    private NucleotideSequence getExonSequence(NucleotideSequence t,
            List<Range> rangesToKeep) {
        NucleotideSequenceBuilder builder = t.toBuilder();
        
        List<Range> complement = Range.ofLength(builder.getLength()).complement(rangesToKeep);
        for(int i = complement.size()-1; i >=0; i--){
            builder.delete(complement.get(i));
        }
        return builder.build();
    }
    
    
}

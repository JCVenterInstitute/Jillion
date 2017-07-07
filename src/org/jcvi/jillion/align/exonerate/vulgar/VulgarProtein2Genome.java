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
package org.jcvi.jillion.align.exonerate.vulgar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.IupacTranslationTables;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class VulgarProtein2Genome {

    private final List<VulgarElement> elements;

    private final List<Range> targetExons, targetGaps;
    private final List<Range> queryRanges, queryGaps;
    
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
        
        targetExons = new ArrayList<>();
        targetGaps = new ArrayList<>();
        
        queryRanges = new ArrayList<>();
        queryGaps = new ArrayList<>();
        
//        BiConsumer<Long, VulgarElement> insertionConsumer = (offset, e) -> queryGaps.add(new Range.Builder(e.getTargetLength() /3).shift(offset - queryRange.getBegin()).build());
//        BiConsumer<Long, VulgarElement> deletionConsumer = (offset, e) -> targetGaps.add(new Range.Builder(e.getQueryLength() *3).shift(offset - targetRange.getBegin() + queryRange.getBegin()).build());
//        
//        
//        computeRanges(queryRange.getBegin(), VulgarElement::getQueryLength, queryRanges, insertionConsumer);
//        computeRanges(targetRange.getBegin(), VulgarElement::getTargetLength, targetExons, deletionConsumer);
//        
        long queryOffset = 0;
        long targetOffset = 0;
        
        for(VulgarElement e : elements){
           
            if(e.getOp() == VulgarOperation.Match || e.getOp() == VulgarOperation.Split_Codon || e.getOp() == VulgarOperation.Gap){
                queryRanges.add(new Range.Builder(e.getQueryLength()).shift(queryOffset).build());
                targetExons.add(new Range.Builder(e.getTargetLength()).shift(targetOffset).build());
                
                if(e.getOp() == VulgarOperation.Gap){
                    queryGaps.add(new Range.Builder(e.getTargetLength()/3).shift(queryOffset).build());
                    targetGaps.add(new Range.Builder(e.getQueryLength()*3).shift(targetOffset).build());
                }
            }
            queryOffset+=e.getQueryLength();
            targetOffset+=e.getTargetLength();
            
        }
        
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




    private void computeRanges(long startOffset, ToIntFunction<VulgarElement> function,
            List<Range> exons, BiConsumer<Long, VulgarElement> gapConsumer) {

        long currentOffset=startOffset;
        
        for(VulgarElement e : elements){
            int len = function.applyAsInt(e);
            if(e.getOp() == VulgarOperation.Match || e.getOp() == VulgarOperation.Split_Codon){
                exons.add(new Range.Builder(len).shift(currentOffset).build());
            }else if(e.getOp() == VulgarOperation.Gap){
                gapConsumer.accept(currentOffset, e);
                exons.add(new Range.Builder(len).shift(currentOffset).build());
            }
            currentOffset+=len;
        }

        
        mergeInPlace(exons);
    }
   
    private static void mergeInPlace(List<Range> ranges){
        List<Range> merged = Ranges.merge(ranges);
        ranges.clear();
        ranges.addAll(merged);
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
        
       
        NucleotideSequence cds =getExonSequence(target, targetExons, targetGaps);
        ProteinSequence translated = IupacTranslationTables.STANDARD.translate(cds);
        
        ProteinSequenceBuilder queryBuilder = query.toBuilder(queryRange.asRange());
        for(int i= queryGaps.size()-1; i>=0; i--){
            Range gap = queryGaps.get(i);
            char[] gaps = new char[(int) gap.getLength()];
            if(gaps.length > 1){
                System.out.println("here");
            }
            Arrays.fill(gaps, AminoAcid.Gap.asChar());
            queryBuilder.insert((int)gap.getBegin(), new String(gaps));
        }
       
        ProteinSequence querySeq = queryBuilder.build();
        String queryAlignment = querySeq.toString(AminoAcid::get3LetterAbbreviation).replaceAll("(.{62})", "$1\n");
        String translatedAlignment = translated.toString(AminoAcid::get3LetterAbbreviation).replaceAll("(.{62})", "$1\n");
        String cdsAlignment = cds.toString().replaceAll("(.{62})", "$1\n");
        
        try(BufferedReader qr = new BufferedReader(new StringReader(queryAlignment));
            BufferedReader tr = new BufferedReader(new StringReader(translatedAlignment));
                BufferedReader cdsr = new BufferedReader(new StringReader(cdsAlignment))){
            String qLine, tLine;
            while( (qLine = qr.readLine()) !=null && (tLine = tr.readLine()) !=null){
                System.out.println(qLine);
                System.out.println(tLine);
                System.out.println(cdsr.readLine()+"\n");
            }
            System.out.println("======");
            while((qLine = qr.readLine()) !=null){
                System.out.println(" QLINE REMAINING = " +qLine);
            }
            while((tLine = tr.readLine()) !=null){
                System.out.println(" T_LINE REMAINING = " +tLine);
            }
            
        }catch(IOException impossible){
            impossible.printStackTrace();
        }
        
        
        
       
        
        Iterator<AminoAcid> queryIter = querySeq.iterator();
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
        return new AlignmentResult(queryId, targetId, ident, matches, misMatches);
    }
    
    
    public static class AlignmentResult{
        private final double percentIdentity;
        private final int matches, misMatches;
        private final String queryId, targetId;
        
        protected AlignmentResult(String queryId, String targetId, double percentIdentity, int matches,
                int misMatches) {
            this.percentIdentity = percentIdentity;
            this.matches = matches;
            this.misMatches = misMatches;
            this.queryId = queryId;
            this.targetId = targetId;
        }

        public String getQueryId() {
            return queryId;
        }

        public String getTargetId() {
            return targetId;
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
            result = prime * result
                    + ((queryId == null) ? 0 : queryId.hashCode());
            result = prime * result
                    + ((targetId == null) ? 0 : targetId.hashCode());
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
            if (queryId == null) {
                if (other.queryId != null) {
                    return false;
                }
            } else if (!queryId.equals(other.queryId)) {
                return false;
            }
            if (targetId == null) {
                if (other.targetId != null) {
                    return false;
                }
            } else if (!targetId.equals(other.targetId)) {
                return false;
            }
            return true;
        }
        
        
    }

    private NucleotideSequence getExonSequence(NucleotideSequence t,
            List<Range> rangesToKeep, List<Range> gaps) {
       //ranges To keep should be in gapped coords
        
        NucleotideSequenceBuilder builder = t.toBuilder(targetRange.asRange());
        ListIterator<Range> iter = gaps.listIterator(gaps.size());
        while(iter.hasPrevious()){
//        for(Range g : gaps){
            Range g = iter.previous();
            int offset = (int) g.getBegin();
            Nucleotide[] array = new Nucleotide[(int) g.getLength()];
            
            Arrays.fill(array, Nucleotide.Gap);
            
            builder.insert(offset, array);
        }
        
        List<Range.Builder> gappedRangesToKeep = new ArrayList<>();
        for (Range r : rangesToKeep) {
            gappedRangesToKeep.add(r.toBuilder());
        }
        for (Range gap : gaps) {
            for (Range.Builder b : gappedRangesToKeep) {

                if (b.startsAfter(gap)) {
                    b.shift(gap.getLength());
                }
            }
        }
//       
//        List<Range> allRangesToKeep = gappedRangesToKeep.stream().map(Range.Builder::build).collect(Collectors.toList());
//        allRangesToKeep.addAll(gaps);
//        
//        mergeInPlace(allRangesToKeep);
//        
//        NucleotideSequenceBuilder builder = t.toBuilder();
//        ListIterator<Range> iter = gaps.listIterator(gaps.size());
//        while(iter.hasPrevious()){
//            Range r = iter.previous();
//            int offset = (int) r.getBegin();
//            Nucleotide[] array = new Nucleotide[(int) r.getLength()];
//            
//            Arrays.fill(array, Nucleotide.Gap);
//            
//            builder.insert(offset, array);
//        }
        
//        
//        List<Range> complement = Range.ofLength(builder.getLength()).complement(allRangesToKeep);
////        List<Range> complement = Range.ofLength(builder.getLength()).complement(rangesToKeep);
//        for(int i = complement.size()-1; i >=0; i--){
//            builder.delete(complement.get(i));
//        }
       
        return builder.build();
    }
    
    
}

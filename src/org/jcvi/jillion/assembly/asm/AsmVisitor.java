/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.jillion.assembly.asm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.TextFileVisitor;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code AsmVisitor} is a visitor implementation
 * to traverse a Celera Assembler .ASM file.
 * <p/>
 * The ASM file is the sole output of the 
 * Celera Assembler pipeline.  It provides a precise description
 * of the assembly as a hierarchical data structure.  The messages
 * are visited in a "definition before reference" order.  Every
 * identifier is defined in a visit message before it is referenced.
 * For example, both reads will be visited before the visit
 * method that defines these reads as a mate pair.
 * <p/>
 * Many messages in an ASM file are nested so context is important.
 * A {@link #visitReadLayout(char, String, Range, Direction, List)}
 * might explain a read layout for a unitig or contig
 * depending on if that call was made from inside a
 * {@link #visitUnitig(String, long, float, float, UnitigStatus, NucleotideSequence, List, int)}
 * block or a 
 * {@link #visitContig(String, long, boolean, NucleotideSequence, List, int, int, int)}
 * block.
 * @author dkatzel
 *
 *
 */
public interface AsmVisitor extends TextFileVisitor{
    /**
     * {@code MateStatus} indicates the
     * relative positioning of the mate
     * of this read in the assembly.
     * @author dkatzel
     *
     *
     */
    public enum MateStatus{
        /**
         * Mate pair was unprocessed by 
         * CA pipeline; should never appear.
         */
        UNASSIGNED('Z'),
        /**
         * The pair assembled into one
         * scaffold with the proper orientation
         * and within the acceptable distance.
         */
        GOOD('G'),
        /**
         * The pair assembled into one
         * scaffold with the proper orientation
         * but were placed too close
         * together.
         */
        BAD_SHORT('C'),
        /**
         * The pair assembled into one
         * scaffold with the proper orientation
         * but were placed too far apart.
         */
        BAD_LONG('L'),
        /**
         * The pair assembled into one
         * scaffold but in the same
         * orientation.
         */
        SAME_ORIENTATION('S'),
        /**
         * The pair assembled into one
         * scaffold but in the oriented
         * away from each other.
         */
        OUTTIE_ORIENTATION('O'),
        /**
         * This read has no mate.
         */
        NO_MATE('N'),
        /**
         * Neither read in the mate
         * pair were assembled
         */
        BOTH_SINGLETON('H'),
        /**
         * This read's mate was placed
         * in a singleton unitig.
         */
        SINGLETON_MATE('A'),
        /**
         * Both reads in the pair
         * were assembled into degenerate
         * unitig.
         */
        BOTH_DEGENERATE('D'),
        /**
         * This read's mate was
         * assembled into a degenerate unitig.
         */
        DEGENERATE('E'),
        /**
         * Both reads were placed in surrogate
         * unitigs.
         */
        BOTH_SURROGATE('U'),
        SURROGATE('R'),
        /**
         * Each mate was placed in different
         * scaffolds.
         */
        DIFFERENT_SCAFFOLD('F'),
        /**
         * <strong>DO NOT USE</strong>.
         * Old deprecated bad mate status
         * included here only to support 
         * old asm files.
         */
        @Deprecated
        DEPRECATED_BAD('B')
        ;
        private final char code;
        
        private static final Map<Character,MateStatus> MAP;
        static{
            MAP = new HashMap<Character,MateStatus>();
            for(MateStatus status : values()){
                MAP.put(status.code, status);
            }
        }
        private MateStatus(char code) {
            this.code = code;
        }
        
        public static MateStatus parseMateStatus(String statusCode){
            return parseMateStatus(statusCode.charAt(0));
        }
        public static MateStatus parseMateStatus(char statusCode){
            Character valueOf = Character.valueOf(statusCode);
            if(!MAP.containsKey(valueOf)) {
               throw new IllegalArgumentException("invalid mate status code :"+ statusCode);
            }
            return MAP.get(valueOf);
            
        }

		public char getCode() {
			return code;
		}
        
        
    }
    /**
     * {@code UnitigStatus} represents the unitig's
     * disposition after scaffold construction.
     * @author dkatzel
     *
     */
    public enum UnitigStatus{
        /**
         * Placed in scaffold or promoted
         * to its own scaffold.
         */
        UNIQUE('U'),
        /**
         * Added cautiously at one or more
         * scaffold locations.
         */
        REPEAT_SURROGATE('S'),
        /**
         * Left out of the assembly 
         * as a degenerate.
         */
        REPEAT_DEGENERATE('N'),
        /**
         * No longer used.
         */
        CHIMER('C'),
        /**
         * Always changed to {@link #REPEAT_SURROGATE}
         * or {@link #REPEAT_DEGENERATE} during scaffolding.
         */
        UNRESOVLED('X')
        ;
        private final char code;
        
        private static final Map<Character,UnitigStatus> MAP;
        static{
            MAP = new HashMap<Character,UnitigStatus>();
            for(UnitigStatus status : values()){
                MAP.put(status.code, status);
            }
        }
        private UnitigStatus(char code) {
            this.code = code;
        }
        
        public static UnitigStatus parseUnitigStatus(String statusCode){
            return parseUnitigStatus(statusCode.charAt(0));
        }
        public static UnitigStatus parseUnitigStatus(char statusCode){
            Character valueOf = Character.valueOf(statusCode);
            if(!MAP.containsKey(valueOf)) {
                throw new IllegalArgumentException("invalid unitig status code :"+ statusCode);
             }
            return MAP.get(valueOf);
        }

		public char getCode() {
			return code;
		}
        
        
    }
    
    /**
     * {@code LinkOrientation} represents the
     * the relative orientation of the two
     * unitigs or the two contigs being linked.
     * @author dkatzel
     *
     */
    public enum LinkOrientation{
        /**
         * Indicates forward-forward.
         */
         NORMAL('N'),
         /**
          * Indicates reverse - reverse.
          */
         ANTI_NORMAL('A'),
         /**
          * Indicates reverse-forward.
          */        
         OUTIE('O'),
         /**
          * Indicates foward-reverse.
          */
         INNIE('I')
        ;
        private final char code;
        
        private static final Map<Character,LinkOrientation> MAP;
        static{
            MAP = new HashMap<Character,LinkOrientation>();
            for(LinkOrientation status : values()){
                MAP.put(status.code, status);
            }
        }
        private LinkOrientation(char code) {
            this.code = code;
        }
        
        public static LinkOrientation parseLinkOrientation(String statusCode){
            return parseLinkOrientation(statusCode.charAt(0));
        }
        public static LinkOrientation parseLinkOrientation(char statusCode){
            Character valueOf = Character.valueOf(statusCode);
            if(!MAP.containsKey(valueOf)) {
               throw new IllegalArgumentException("invalid link orientation code :"+ statusCode);
            }
            return MAP.get(Character.valueOf(statusCode));
        }

		public char getCode() {
			return code;
		}
        
        
    }
    
    /**
     * {@code OverlapType} describes the pair-wise
     * alignment of contigs/unitigs induced by the mate pairs
     * in this link.
     * @author dkatzel
     *
     */
    public enum OverlapType{
        /**
         * These two unitigs/contigs have no overlap
         * and the link is determined
         * only by mate pairs.
         */
         NO_OVERLAP('N'){
             int getExpectedNumberOfMatePairEvidenceRecords(int numberOfContributingEdges){
                 return numberOfContributingEdges;
             }
         },
         /**
          * Regular overlap.
          */
         REGULAR('O'),
         /**
          * TODO: what does this mean?
          */        
         TANDEM('T'),
         /**
          * TODO: what does this mean?
          */ 
         CONTAINMENT_1_BY_2('C'),
         /**
          * TODO: what does this mean?
          */ 
         CONTAINMENT_2_BY_1('I'),
        ;
        private final char code;
        
        private static final Map<Character,OverlapType> MAP;
        static{
            MAP = new HashMap<Character,OverlapType>();
            for(OverlapType status : values()){
                MAP.put(status.code, status);
            }
        }
        private OverlapType(char code) {
            this.code = code;
        }
        
        public static OverlapType parseOverlapType(String statusCode){
            return parseOverlapType(statusCode.charAt(0));
        }
        public static OverlapType parseOverlapType(char statusCode){
            Character valueOf = Character.valueOf(statusCode);
            if(!MAP.containsKey(valueOf)){
                throw new IllegalArgumentException("invalid overlap code :"+ statusCode);                
            }
            return MAP.get(valueOf);
        }
        /**
         * This is the expected number of mate pair
         * evidence records in the "jumplist" of a link
         * message.  The expected number varies based on
         * which overlap type this unitig/contig is.
         * @param numberOfContributingEdges the number of edges
         * specified in the message.
         * @return the expected number of mate pairs to prove
         * evidence of this link.
         */
        int getExpectedNumberOfMatePairEvidenceRecords(int numberOfContributingEdges){
            return numberOfContributingEdges-1;
        }

		public char getCode() {
			return code;
		}
        
        
    }
    
    /**
     * {@code OverlapStatus} describes the pair-wise
     * alignment of contigs/unitigs induced by the mate pairs
     * in this link.
     * @author dkatzel
     *
     */
    public enum OverlapStatus{
        /**
         * TODO: what does this mean?
         */
         IN_ASSEMBLY('A'),
         /**
          * TODO: what does this mean?
          */
         POLYMORPHISM('P'),
         /**
          * TODO: what does this mean?
          */        
         BAD('B'),
         /**
          * TODO: what does this mean?
          */ 
         CHIMERA('C'),
         /**
          * TODO: what does this mean?
          */ 
         UNKNOWN('U'),
        ;
        private final char code;
        
        private static final Map<Character,OverlapStatus> MAP;
        static{
            MAP = new HashMap<Character,OverlapStatus>();
            for(OverlapStatus status : values()){
                MAP.put(status.code, status);
            }
        }
        private OverlapStatus(char code) {
            this.code = code;
        }
        
        public static OverlapStatus parseOverlapStatus(String statusCode){
            return parseOverlapStatus(statusCode.charAt(0));
        }
        public static OverlapStatus parseOverlapStatus(char statusCode){            
            Character valueOf = Character.valueOf(statusCode);
            if(!MAP.containsKey(valueOf)){
                throw new IllegalArgumentException("invalid overlap status code :"+ statusCode);
            }
            return MAP.get(valueOf);
        }

		public char getCode() {
			return code;
		}
        
        
    }
    
    /**
     * {@code NestedContigMessageTypes}
     * is an enum of all the possible 
     * nested messages inside a contig message
     * in an asm file.
     * @author dkatzel
     *
     *
     */
    public enum NestedContigMessageTypes{
        /**
         * Visit all the reads that map to this contig.
         * Including this as part of the Set returned by 
         * {@link AsmVisitor#visitContig(String, long, boolean, NucleotideSequence, QualitySequence, int, int, int)}
         * will make this visitor call
         * {@link AsmVisitor#visitReadLayout(char, String, Range, Direction, List)}
         * n times where n is the number of reads in the contig currently being visited.
         */
        READ_MAPPING,
        /**
         * Visit all the unitigs that map to this contig.
         * Including this as part of the Set returned by 
         * {@link AsmVisitor#visitContig(String, long, boolean, NucleotideSequence, QualitySequence, int, int, int)}
         * will make this visitor call
         * {@link AsmVisitor#visitUnitigLayout(UnitigLayoutType, String, DirectedRange, List)
         * n times where n is the number of unitigs in the contig currently being visited.
         */
        UNITIG_MAPPING,
        /**
         * Visit all the variant messages of this contig.
         * Including this as part of the Set returned by 
         * {@link AsmVisitor#visitContig(String, long, boolean, NucleotideSequence, QualitySequence, int, int, int)}
         * will make this visitor call
         * {@link AsmVisitor#visitVariance(Range, int, int, long, long, SortedSet)
         * n times where n is the number of variant records in the contig currently being visited.
         */
        VARIANTS
    }
    
    interface MatePairEvidence{
        String getRead1();
        String getRead2();
    }
    
    
    /**
     * {@code UnitigLayoutType} describes how 
     * the assembler decided to place this unitig
     * in the scaffold.  If the unitig represents
     * a repeat, then the type explains the which
     * repeat resolution level was used.
     * @author dkatzel
     *
     */
    public enum UnitigLayoutType{
        /**
         * This unitig was unique
         */
        UNIQUE('U'),
        /**
         * Least aggressive level of repeat resolution.
         * A Rock is a placed unitig that was consistently
         * positioned by at least two mate pairs.
         */
        REPEAT_ROCK('R'),
        /**
         * Middle aggressive level of repeat resolution.
         * A Stone is a placed unitig that was 
         * positioned by one mate pair and confirmed by an overlap
         * tiling across the gap containing it.
         */
        REPEAT_STONE('S'),
        /**
         * Most aggressive level of repeat resolution.
         * A pebble is a placed unitig that was placed
         * only using overlap tiling.  This level of repeat
         * resolution hasn't been used in Celera Assembler
         * since the 2000 assembly of Drosophila melanogaster.
         */
        REPEAT_PEBBLE('P'),
        /**
         * This unitig represents a single read?
         */
        SINGLE_READ('s')
        ;
        private final char code;
        
        private static final Map<Character,UnitigLayoutType> MAP;
        
        static{
            MAP = new HashMap<Character,UnitigLayoutType>();
            for(UnitigLayoutType status : values()){
                MAP.put(status.code, status);
            }
        }
        private UnitigLayoutType(char code) {
            this.code = code;
        }
        
        public static UnitigLayoutType parseUnitigLayoutType(String typeCode){
            return parseUnitigLayoutType(typeCode.charAt(0));
        }
        public static UnitigLayoutType parseUnitigLayoutType(char typeCode){
            Character valueOf = Character.valueOf(typeCode);
            if(!MAP.containsKey(valueOf)){
                throw new IllegalArgumentException("invalid unitg layout type : "+ typeCode);
            }
            return MAP.get(valueOf);
        }

		public char getCode() {
			return code;
		}
        
        
    }
    /**
     * Describes statistics about one library as observed 
     * after partial assembly.
     * @param externalId the externally unique UID; the value is the same as the 
     * string as the DST accession field in the input frg file.
     * @param internalId an internal integer value that associates this message with
     * future messages visited further on in the assembly pipeline.
     * @param meanOfDistances
     * @param stdDev
     * @param min
     * @param max
     * @param histogram a histogram of the distances observed in one library.
     * A normal distribution indicates a well-constructed library; other distributions
     * can indicate library problems.
     * 
     */
    void visitLibraryStatistics(String externalId, long internalId,
            float meanOfDistances, float stdDev,
            int min, int max, List<Integer> histogram);
    
    /**
     * Visit one read and get its status in the assembly.
     * @param externalId the unique external id of this read from the frg file.
     * @param internalId an internal integer value that associates this read with
     * future messages visited further on in the assembly pipeline. 
     * @param mateStatus the {@link MateStatus} of this read's mate.
     * @param isSingleton is this read a singleton.  If {@code true}
     * then this read could not be assembled; {@code false} otherwise.
     * @param clearRange the final clear range of this read determined
     * by the Celera Assembler trimming modules (ex: Overlap Based Trimming).
     */
    void visitRead(String externalId, long internalId, MateStatus mateStatus,
            boolean isSingleton, Range clearRange);
    /**
     * This is duplicated information provided for convenience.
     * @param externalIdOfRead1 the unique id for one read.
     * @param externalIdOfRead2the unique id for the other read.
     * @param mateStatus the combined status of the individual reads.
     */
    void visitMatePair(String externalIdOfRead1,String externalIdOfRead2, MateStatus mateStatus);
    
    /**
     * Visit a Unitig generated by the unitigger module.  Most unitigs are components of a contigs; some
     * unitigs are themselves a contig.  Some unitigs will be generated by splitting in the scaffold module;
     * those unitigs will appear in a later message.
     * generated by the scaffold.
     * @param externalId the unique external id of this unitig.
     * @param internalId an internal integer value that associates this unitig with
     * future messages visited further on in the assembly pipeline. 
     * @param aStat gives the likelihood this unitig derives
     * from a unique locus of the genome, as opposed to being a collapse 
     * of reads from two copies of a genomic repeat. An aStat of 0
     * indicates no preference.  Too-short unitigs have their A-stat set to zero. 
     * Negative values indicate repetitiveness. Positive values indicate uniqueness. 
     * @param measureOfPolymorphism experimental measure of polymorphism
     * observed between reads in the unitig.  Introduced in CA version 6,
     * not yet used by assembler.
     * @param status the unitig status.
     * @param consensusSequence the consensus of this unitig.
     * @param consensusQualities the consensus qualities of this unitig.
     * @param numberOfReads number of reads in the unitig, should always
     * be >=1.
     * @return {@code true} if the read layouts of this unitig
     * should be visited {@code false} otherwise.  If a unitig's
     * read layouts are to be visited, then the {@link #visitReadLayout(char, long, Range, Direction, List)}
     * will be called {@code numberOfReads} times followed by {@link #visitEndOfUnitig()}.
     * If the read layouts are not to be visited, then the next visit call 
     * (aside from many {@link #visitLine(String)}s) will be {@link #visitEndOfUnitig()}.
     */
    boolean visitUnitig(String externalId, long internalId, float aStat, float measureOfPolymorphism,
            UnitigStatus status, NucleotideSequence consensusSequence, QualitySequence consensusQualities,
            int numberOfReads);
    /**
     * The current unitig has been completely visited.
     */
    void visitEndOfUnitig();
    /**
     * Visit one read layout onto the the current unitig or contig
     * depending on if the visitor is currently visiting
     * a unitig or a contig.
     * @param readType the type of the read, usually 'R' for
     * random read.  This is the same type as from the frg file.
     * @param externalReadId the read id.
     * @param readRange the {@link DirectedRange} which has the gapped range on the unitig or contig that this read
     * aligns to and the {@link Direction} of the read on this unitig or contig.
     * @param gapOffsets the gap offsets of this read onto the frg sequence.
     */
    void visitReadLayout(char readType, String externalReadId, 
            DirectedRange readRange, List<Integer> gapOffsets);
    
    /**
     * Visit one unitig layout onto the the current contig.
     * @param type the {@link UnitigLayoutType} that explains
     * why the unitig is layed out here.
     * @param unitigExternalId the external id of this unitig.
     * @param unitigRange the gapped {@link DirectedRange} on the contig that this unitig
     * aligns to in the {@link Direction} of the unitig on the contig.
     * If direction is {@link Direction#REVERSE}, then the contig uses the 
     * reverse complement of the unitig's consensus sequence.
     * @param gapOffsets the gap offsets of this layed out unitig onto the unitig consensus sequence
     * (after reverse complementing?).
     */
    void visitUnitigLayout(UnitigLayoutType type, String unitigExternalId, 
            DirectedRange unitigRange, List<Integer> gapOffsets);
    /**
     * Indicates connections between unitigs.  They summarize the edges in the unitig graph whose nodes
     * are unitigs.  The graph's edges are induced by mate pairs that have one read
     * in each unitig.
     * @param externalUnitigId1 the external id of one of the unitigs.
     * @param externalUnitigId2 the external id of one the other unitig.
     * @param orientation The {@link LinkOrientation} of these two unitigs relative
     * to each other.
     * @param overlapType the {@link OverlapType}.
     * @param status the {@link OverlapStatus}.
     * @param isPossibleChimera {@code true} if this unitg link a possible chimera;
     * {@code false} otherwise.
     * @param numberOfEdges the number of contributing edges.
     * @param meanDistance the mean edge distance, may be negative.
     * @param stddev the standard deviation of edge distances.
     * @param matePairEvidence a set of {@link MatePairEvidence}s
     * that show the mate pairs that were used to determine
     * that these two unitigs are linked.
     */
    void visitUnitigLink(String externalUnitigId1,String externalUnitigId2, LinkOrientation orientation,
            OverlapType overlapType, OverlapStatus status, boolean isPossibleChimera,
            int numberOfEdges, float meanDistance, float stddev, Set<MatePairEvidence> matePairEvidence);
    
    /**
     * Indicates connections between contigs.  They summarize the edges in a graph whose nodes
     * are contigs.  The graph's edges are induced by mate pairs that have one read
     * in each contig.
     * @param externalContigId1 the external id of one of the contigs.
     * @param externalContigId2 the external id of one the other contig.
     * @param orientation The {@link LinkOrientation} of these two contigs relative
     * to each other.
     * @param overlapType the {@link OverlapType}.
     * @param status the {@link OverlapStatus}.
     * @param numberOfEdges the number of contributing edges.
     * @param meanDistance the mean edge distance, may be negative.
     * @param stddev the standard deviation of edge distances.
     * @param matePairEvidence a set of {@link MatePairEvidence}s
     * that show the mate pairs that were used to determine
     * that these two contigs are linked.
     */
    void visitContigLink(String externalContigId1,String externalContigId2, LinkOrientation orientation,
            OverlapType overlapType, OverlapStatus status, int numberOfEdges,
            float meanDistance, float stddev, Set<MatePairEvidence> matePairEvidence);
    /**
     * Describes one contig.  A contig represents a contiguous span
     * of the target genome.  The contig contains a layout of reads, a layout of unitigs
     * and any variants found in the underlying reads.
     * @param externalId the unique external id of this contig.
     * @param internalId an internal integer value that associates this contig with
     * future messages visited further on in the assembly pipeline. 
     * @param isDegenerate is this contig a degenerate contig (not placed in a scaffold).
     * @param consensusSequence the gapped consensus sequences of this contig.
     * @param consensusQualities the consensus qualities of this contig.
     * @param numberOfReads the number of reads in this contig.
     * @param numberOfUnitigs the number of unitigs that have been 
     * laid out in this contig.
     * @param numberOfVariants the number of variant (alternate allele)
     * consensus regions.
     * @return A Set of {@link NestedContigMessageTypes} to visit for this contig;
     * can not be null.  If no nested messages should be visited, then 
     * return an empty set. Regardless of the return value, {@link #visitEndOfContig()}
     * will be called after any nested messages (if any) are visited.   Ithe read and unitig layouts as well
     * as the variant records for this contig; {@code false} otherwise. If a contig's
     * layouts are to be visited then calls to {@link #visitReadLayout(char, String, Range, Direction, List)}
     * will be called {@code numberOfReads} times, before the call to {@link #visitEndOfContig()}.
     */
    Set<NestedContigMessageTypes> visitContig(String externalId, long internalId, boolean isDegenerate,
            NucleotideSequence consensusSequence, QualitySequence consensusQualities,
            int numberOfReads, int numberOfUnitigs, int numberOfVariants);
    
    interface VariantRecord extends Comparable<VariantRecord>{
        /**
         * The internal read ids that contribute to this variant.
         * @return a list of IIDs 
         */
        List<Long> getContributingReadIIDs();
        /**
         * The weight of this variant, the greater
         * the number the more major this variant is.
         * @return
         */
        int getWeight();
        /**
         * The {@link NucleotideSequence}
         * of this variant.
         * @return
         */
        NucleotideSequence getVariantSequence();
    }
    /**
     * A Variance message indicates alternative sequence(s) for small
     * regions of the contig consensus.  A variant whose sequence
     * length is 1 is commonly known as a SNP.
     * @param range the location of this variant on the contig consensus sequence.
     * @param numberOfSpanningReads the number of reads that give 
     * coverage at the variant positions. Some spanning reads may not have contributed to the consensus
     * due to poor quality or lack of confirmation in the other reads.
     * @param anchorSize Ancor size used to detect variants, Currently, Celera Assembler
     * defaults to 11.
     * @param internalAccessionId the internal accession id of this variant.
     * @param accessionForPhasedVariant the accession for a different variant
     * that is phased with this one.  If set to a negative number, then
     * this variant has not related to another variant? Linking all the variants that are phased
     * with one another can be used to dephase mixed (or diploid?) sequence.
     * @param variantRecords a list of {@link VariantRecord} objects
     * that explain all the details of each part of the SNP.
     */
    void visitVariance(Range range, int numberOfSpanningReads,
            int anchorSize,long internalAccessionId, long accessionForPhasedVariant,
            SortedSet<VariantRecord> variantRecords);
    /**
     * There are no more nested messages for the current contig.
     */
    void visitEndOfContig();
    /**
     * Scaffold Messages define one scaffold per message.  A scaffold
     * is the maximal unit of contiguous sequence output
     * by Celera Assembler.
     * A scaffold may consist of one contig or multiple contigs
     * and their relative coordinates.
     * @param externalId the unique external id of this scaffold.
     * @param internalId an internal integer value that associates this scaffold with
     * future messages visited further on in the assembly pipeline. 
     * @param numberOfContigPairs  the number of contig pairs in this scaffold.
     * when numberOfContigPairs = 0, then this scaffold consists of exactly one contig.
     * When numberOfContigPairs >0, then the scaffold consists of multiple contigs whose
     * order, orientation and separation are derived from mate pairs.
     * @return {@code true} to visit the contig pair messages of this scaffold;
     * {@code false} otherwise.
     */
    boolean visitScaffold(String externalId, long internalId, int numberOfContigPairs);
    /**
     * This contig is the only contig in this scaffold (as previously determined
     * by {@link #visitScaffold(String, long, int)} having a numberOfContigPairs = 0.
     * @param externalContigId the contig external id.
     */
    void visitSingleContigInScaffold(String externalContigId);
    /**
     * A contig pair message defines a pair of contigs that belong to a scaffold.
     * A scaffold with three contigs {1,2,3} would be represented by two pair message
     * (1,2) and (2,3).  Note that the first contig of a pair can have a reverse orientation
     * to preserve its orientation in the previous contig pair message.
     * @param externalContigId1 the external id of one of the contigs in this pair.
     * @param externalContigId2 the external id of the other contig in this pair.
     * @param meanDistance the the predicted number of bases in the gap between contigs.  A negative
     * distance indicates that the contigs overlap (according to mate pairs)
     * but their consensus sequences do not align.
     * @param stddev standard deviation of the distance distribution of the mates that span the contigs.
     * @param orientation the relative {@link LinkOrientation} of the two contigs.
     */
    void visitContigPair(String externalContigId1,String externalContigId2, 
            float meanDistance, float stddev, LinkOrientation orientation);
    /**
     * There are no more nested messages for the current scaffold.
     */
    void visitEndOfScaffold();
    
    /**
     * Indicates connections between scaffolds.   
     *  They summarize the edges in a graph whose nodes
     * are scaffolds.  The graph's edges are induced by mate pairs that have one read
     * in each scaffold.
     * By definition, the mates
     * in the scaffold link were not used to build a scaffold; they may 
     * have been too few in number or inconsistent with other mates.
     * @param externalScaffoldId1 the external id of one of the scaffolds.
     * @param externalScaffoldId2 the external id of one the other scaffold.
     * @param orientation The {@link LinkOrientation} of these two scaffold relative
     * to each other.
     * @param overlapType the {@link OverlapType}.
     * @param status the {@link OverlapStatus}.
     * @param numberOfEdges the number of contributing edges.
     * @param meanDistance the mean edge distance, may be negative.
     * @param stddev the standard deviation of edge distances.
     * @param matePairEvidence a set of {@link MatePairEvidence}s
     * that show the mate pairs that were used to determine
     * that these two scaffolds are linked.
     */
    void visitScaffoldLink(String externalScaffoldId1,String externalScaffoldId2, LinkOrientation orientation,
            OverlapType overlapType, OverlapStatus status, int numberOfEdges,
            float meanDistance, float stddev, Set<MatePairEvidence> matePairEvidence);
}
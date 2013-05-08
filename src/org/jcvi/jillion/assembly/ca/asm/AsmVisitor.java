/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ca.asm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code AsmVisitor} is a visitor implementation
 * to traverse a Celera Assembler .ASM file.
 * <p/>
 * The ASM file is the output of the 
 * Celera Assembler pipeline.  It provides a precise description
 * of the assembly as a hierarchical data structure.  The messages
 * are visited in a "definition before reference" order.  Every
 * identifier is defined in a visit message before it is referenced.
 * For example, both reads will be visited before the visit
 * method that defines these reads as a mate pair.
 * 
 * @author dkatzel
 *
 *
 */
public interface AsmVisitor{
	
	
	/*
	 * ASM message order:
	 * MDI + (library)
	 * AFG + (read)
	 * AMP + (mate pair info)
	 * UTG +
	 * ----MPS (unitig read placement)
	 * ULK + (unitig links?)
	 * CCO +
	 * ----VAR + (variant record)
	 * ----MPS + (contig read placement)
	 * ----UPS + (unitig mapping to cotig)
	 * CLK + (contig links)
	 * SCF + (scaffold)
	 * ----CTP + (contig pair
	 * SLK + (scaffold links)
	 */
	/**
	 * {@code AsmVisitorCallback}
	 * is a callback mechanism to allow the
	 * {@link AsmVisitor} instances
	 * to communicate with the parser
	 * that is parsing the ASM file.
	 * @author dkatzel
	 *
	 */
	interface AsmVisitorCallback{
		/**
		 * {@code AsmVisitorMemento} is a marker
		 * interface that {@link AsmFileParser}
		 * instances can use to "rewind" back
		 * to the position in its ASM file
		 * in order to revisit portions of the ASM file. 
		 * {@link AsmVisitorMemento} should only be used
		 * by the {@link AsmFileParser} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		interface AsmVisitorMemento{
			
		}
		/**
		 * Is this callback capable of
		 * creating {@link AsmVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link AsmVisitorMemento}
		 * 
		 * @return a {@link AsmVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		AsmVisitorMemento createMemento();
		/**
		 * Tell the {@link AsmFileParser} to halt parsing
		 * the ASM file. If the ASM file is not completely
		 * parsed, then any visitors still being visited
		 * will have their halted() methods
		 * called instead of their visitEnd() methods. 
		 */
		void haltParsing();
	}
	
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
            long min, long max, List<Long> histogram);
    
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
     * @param externalIdOfRead2 the unique id for the other read.
     * @param mateStatus the combined status of the individual reads.
     */
    void visitMatePair(String externalIdOfRead1,String externalIdOfRead2, MateStatus mateStatus);
    
    /**
     * Visit a Unitig generated by the unitigger module.  Most unitigs are components of a contigs; some
     * unitigs are themselves a contig.  Some unitigs will be generated by splitting in the scaffold module;
     * those unitigs will appear in a later message
     * generated by the scaffold.
     * @param callback the {@link AsmVisitorCallback} instance
     * which can be used to callback to the parser.
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
     * @return an instance of {@link AsmUnitigVisitor} to be used
     * to visit this unitig; if a {@code null} value
     * is returned, then this unitig is skipped by the parser.
     */
    AsmUnitigVisitor visitUnitig(AsmVisitorCallback callback, String externalId, long internalId, float aStat, float measureOfPolymorphism,
            UnitigStatus status, NucleotideSequence consensusSequence, QualitySequence consensusQualities,
            long numberOfReads);
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
     * @param callback the {@link AsmVisitorCallback} instance
     * which can be used to callback to the parser.
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
     * @return an instance of {@link AsmContigVisitor} to be used
     * to visit this contig; if a {@code null} value
     * is returned, then this contig is skipped by the parser.
     */
    AsmContigVisitor visitContig(AsmVisitorCallback callback, String externalId, long internalId, boolean isDegenerate,
            NucleotideSequence consensusSequence, QualitySequence consensusQualities,
            long numberOfReads, long numberOfUnitigs, long numberOfVariants);

    /**
     * Visit a scaffold that is made up of multiple contigs. A scaffold
     * is the maximal unit of contiguous sequence output
     * by Celera Assembler.
     * A scaffold may consist of one contig or multiple contigs
     * and their relative coordinates.
     * @param callback the {@link AsmVisitorCallback} instance
     * which can be used to callback to the parser.
     * @param externalId the unique external id of this scaffold.
     * @param internalId an internal integer value that associates this scaffold with
     * future messages visited further on in the assembly pipeline. 
     * @param numberOfContigPairs  the number of contig pairs in this scaffold.
     * when numberOfContigPairs = 0, then this scaffold consists of exactly one contig.
     * When numberOfContigPairs >0, then the scaffold consists of multiple contigs whose
     * order, orientation and separation are derived from mate pairs.
     * @return an instance of {@link AsmScaffoldVisitor} that will be used
     * to visit the underlying scaffold data.  If a {@code null}
     * is returned, then this scaffold will be skipped.
     */
    AsmScaffoldVisitor visitScaffold(AsmVisitorCallback callback, String externalId, long internalId, int numberOfContigPairs);
    /**
     * Visit a scaffold that is only made up of one contig. A scaffold
     * is the maximal unit of contiguous sequence output
     * by Celera Assembler.
     * A scaffold may consist of one contig or multiple contigs
     * and their relative coordinates.
     * 
     * This method returns void since there is no underlying data
     * for this scaffold since it only contains a single
     * contig that has already been fully described.
     * 
     * @param callback the {@link AsmVisitorCallback} instance
     * which can be used to callback to the parser.
     * @param externalId the unique external id of this scaffold.
     * @param internalId an internal integer value that associates this scaffold with
     * future messages visited further on in the assembly pipeline. 
     * @param externalContigId the contig external id that is the only contig
     * in this scaffold.
     */
    void visitScaffold(AsmVisitorCallback callback, String externalId, long internalId, String externalContigId);

    
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
    /**
     * The end of the ASM file has been reached.
     */
    void visitEnd();
    /**
     * The parser has stopped parsing the ASM file
     * due to {@link AsmVisitorCallback#haltParsing()}
     * being called. the end of the ASM file was
     * not yet reached.
     */
    void halted();
}

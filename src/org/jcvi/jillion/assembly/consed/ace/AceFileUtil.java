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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.JillionUtil;

/**
 * {@code AceFileUtil} is a utility class to perform 
 * common operations on ace related objects.
 * @author dkatzel
 *
 *
 */
public final class AceFileUtil {
	
    /**
     * This is the default value in consed that is used to distinguish
     * between high and low quality basecalls.  In consed
     * high quality bases are represented by uppercase
     * letters and low quality by lowercase letters.
     * Currently that value is set to 26.
     */
    public static final PhredQuality ACE_DEFAULT_HIGH_QUALITY_THRESHOLD = PhredQuality.valueOf(26);
    /**
     * The date format used in consed to represent chromatogram time stamps 
     * in phd records as well as the DS lines in an ace file.
     * A read's timestamps must be identical strings in both the phd
     * and the ace for consed to make the read editable and to see the qualities
     * in the align window.
     */
    private static final DateFormat CHROMAT_DATE_TIME_FORMATTER = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US);
    /**
     * This is the timestamp format used in some consed 
     * tags.
     */
    private static final DateFormat TAG_DATE_TIME_FORMATTER = new SimpleDateFormat("yyMMdd:HHmmss", Locale.US);
    
    private static final String CONTIG_HEADER = "CO %s %d %d %d %s%n";
    static{
    	CHROMAT_DATE_TIME_FORMATTER.setLenient(false);
    	TAG_DATE_TIME_FORMATTER.setLenient(false);
    }
    
    private AceFileUtil(){
		//private constructor.
	}
    /**
     * Parse the date of a consed formatted phd date time stamp.
     * @param text the timestamp as text
     * @return the equivalent time as a {@link Date}.
     * @throws ParseException if there is a problem parsing the text.
     */
    public static synchronized Date parsePhdDate(String text) throws ParseException{
    	return CHROMAT_DATE_TIME_FORMATTER.parse(text);
    }
    /**
     * format the given {@link Date} representing
     * a phd date time stamp into a consed formatted phd time stamp.
     * @param date the phd date to format.
     * @return the equivalent time as a String.
     */
    public static synchronized String formatPhdDate(Date date){
    	return CHROMAT_DATE_TIME_FORMATTER.format(date);
    }
    
    /**
     * Parse the date of a consed formatted tag date time stamp.
     * @param text the timestamp as text
     * @return the equivalent time as a {@link Date}.
     * @throws ParseException if there is a problem parsing the text.
     */
    public static synchronized Date parseTagDate(String text) throws ParseException{
    	return TAG_DATE_TIME_FORMATTER.parse(text);
    }
    /**
     * Format the given {@link Date} representing
     * a date time stamp for a consed tag
     * into a consed formatted time stamp.
     * @param date the date to format.
     * @return the equivalent time as a String.
     */
    public static synchronized String formatTagDate(Date date){
    	return TAG_DATE_TIME_FORMATTER.format(date);
    }
    /**
     * Convert a {@link NucleotideSequence} into a string
     * where the gaps are represented by '*'s like ace files require.
     * @param basecalls input basecalls, can not be null.
     * @return a String with the all uppercase basecalls except all the '-'
     * have been replaced by '*'.
     * @throws NullPointerException if basecalls are null.
     */
    public static String convertToAcePaddedBasecalls(NucleotideSequence basecalls){
        return convertToAcePaddedBasecalls(basecalls,null);
     }
    /**
     * Convert a {@link NucleotideSequence} into a string
     * where the gaps are represented by '*'s like ace files require.
     * If the optional qualities list is provided, then the returned 
     * String will return a basecalls in both upper and lowercase
     * depending on the quality value as determined by {@link AceFileUtil#ACE_DEFAULT_HIGH_QUALITY_THRESHOLD}.
     * 
     * @param basecalls input basecalls, can not be null or contain any null elements.
     * @param optionalQualities optional ungapped quality list in the same
     * orientation as the basecalls; or null if no qualities are to be used.
     * @return a String with the all uppercase basecalls except all the '-'
     * have been replaced by '*'.
     * @throws NullPointerException if basecalls are null or any Nucleotide in the basecall list 
     * is null or if the optionalQualities list is not null but contains a null in the list.
     * @throws IllegalArgumentException if optionalQualities is provided but does not have 
     * enough ungapped qualities to cover all the ungapped basecalls in the input list.
     */
     public static String convertToAcePaddedBasecalls(NucleotideSequence basecalls,QualitySequence optionalQualities){
         long length = basecalls.getLength();
		StringBuilder result = new StringBuilder((int)length);
		Iterator<PhredQuality> qualityIterator = optionalQualities ==null?null : optionalQualities.iterator();
         for(Nucleotide base : basecalls){
             if(base == Nucleotide.Gap){
                 result.append('*');
             }
             else{
                 if(optionalQualities==null){
                	 result.append(base);
                 }else{                 
                     if(!qualityIterator.hasNext()){
                         throw new IllegalArgumentException(
                                 String.format("not enough ungapped qualities for input basecalls found only %d qualities",optionalQualities.getLength()));
                     }
                     PhredQuality quality =qualityIterator.next();
                     if(quality.compareTo(ACE_DEFAULT_HIGH_QUALITY_THRESHOLD)<0){
                         result.append(base.toString().toLowerCase(Locale.ENGLISH));
                     }
                     else{
                         result.append(base);
                     }
                 }
             }
         }
         
         String consedBasecalls= result.toString().replaceAll("(.{50})", "$1"+String.format("%n"));
         if(length %50 ==0){
             //if the last line is full, then we will have an extra %n
             //so strip it off
             return consedBasecalls.substring(0,consedBasecalls.length()-1);
         }
         return consedBasecalls;
     }

    
    private static String createPhdRecord(PhdInfo phdInfo){
        return String.format("DS CHROMAT_FILE: %s PHD_FILE: %s TIME: %s", 
                
                phdInfo.getTraceName(),
                phdInfo.getPhdName(),
                formatPhdDate(phdInfo.getPhdDate())
                );
    }
    
    private static String createQualityRangeRecord(NucleotideSequence gappedValidBases, 
            Range ungappedValidRange, Direction dir, long ungappedFullLength){
        int numberOfGaps = gappedValidBases.getNumberOfGaps();
        Range gappedValidRange =buildGappedValidRangeFor(
                ungappedValidRange,numberOfGaps,dir,ungappedFullLength);

        
       return String.format("QA %d %d %d %d",
                gappedValidRange.getBegin(CoordinateSystem.RESIDUE_BASED), gappedValidRange.getEnd(CoordinateSystem.RESIDUE_BASED),
                gappedValidRange.getBegin(CoordinateSystem.RESIDUE_BASED), gappedValidRange.getEnd(CoordinateSystem.RESIDUE_BASED)
                );
    }
    private static Range buildGappedValidRangeFor(Range ungappedValidRange, int numberOfGaps,Direction dir, long ungappedFullLength){
       Range gappedValidRange=  Range.of( 
               ungappedValidRange.getBegin(),
               ungappedValidRange.getEnd()+numberOfGaps);
        
        if(dir==Direction.REVERSE){
            gappedValidRange = AssemblyUtil.reverseComplementValidRange(gappedValidRange, ungappedFullLength+numberOfGaps);
           
        }
        return gappedValidRange;
    }
    public static synchronized String createAcePlacedReadRecord(String readId, AssembledRead placedRead, Phd phd, PhdInfo phdInfo){
        
        NucleotideSequence nucleotideSequence = placedRead.getNucleotideSequence();
		final NucleotideSequence gappedValidBasecalls = nucleotideSequence; 
        final Range ungappedValidRange = placedRead.getReadInfo().getValidRange();
        final Direction dir = placedRead.getDirection(); 
        final NucleotideSequence fullBasecalls = phd.getNucleotideSequence();
        
        final NucleotideSequence fullGappedValidRange = AssemblyUtil.buildGappedComplementedFullRangeBases(placedRead, fullBasecalls);
        final QualitySequence qualities;
        if(dir ==Direction.REVERSE){
        	qualities = new QualitySequenceBuilder(phd.getQualitySequence())
        						.reverse()
        						.build();
        }else{
        	qualities = phd.getQualitySequence();
        }
        
        StringBuilder readRecord = new StringBuilder();
        readRecord.append(String.format("RD %s %d 0 0%n",
                                            readId,
                                            fullGappedValidRange.getLength()));
        
        
        readRecord.append(String.format("%s%n%n",
                AceFileUtil.convertToAcePaddedBasecalls(fullGappedValidRange,qualities)));
        readRecord.append(String.format("%s%n",createQualityRangeRecord(
                gappedValidBasecalls,ungappedValidRange,dir, 
                fullBasecalls.getUngappedLength())));
        readRecord.append(String.format("%s%n",createPhdRecord(phdInfo)));
        return readRecord.toString();
    }
    public static void writeAceContigHeader(String contigId, long consensusLength, long numberOfReads,
    		int numberOfBaseSegments, boolean isComplimented, OutputStream out) throws IOException{
    	writeString(String.format(CONTIG_HEADER, 
                contigId, 
                consensusLength,
                numberOfReads,
                numberOfBaseSegments,
                isComplimented? "C":"U"),
                
                out);
    }
    public static void writeAceFileHeader(long numberOfContigs, long numberOfReads, OutputStream out) throws IOException{
        writeString(String.format("AS %d %d%n%n", numberOfContigs, numberOfReads), out);
    }
    private static void writeString(String s, OutputStream out) throws IOException{
        out.write(s.getBytes(IOUtil.UTF_8));
        
    }
    
    
    public static void writeWholeAssemblyTag(
            WholeAssemblyAceTag wholeAssemblyTag, OutputStream out) throws IOException {
        writeString(String.format("WA{%n%s %s %s%n%s%n}%n", 
                wholeAssemblyTag.getType(),
                wholeAssemblyTag.getCreator(),                
                AceFileUtil.formatTagDate(wholeAssemblyTag.getCreationDate()),
                wholeAssemblyTag.getData()), out);

        
    }
    public static void writeConsensusTag(ConsensusAceTag consensusTag,
            OutputStream out) throws IOException {
        StringBuilder tagBodyBuilder = new StringBuilder();
        if(consensusTag.getData() !=null){
            tagBodyBuilder.append(consensusTag.getData());
        }
        if(!consensusTag.getComments().isEmpty()){
            for(String comment :consensusTag.getComments()){
                tagBodyBuilder.append(String.format("COMMENT{%n%sC}%n",comment));            
            }
        }
        Range range = consensusTag.asRange();
        writeString(String.format("CT{%n%s %s %s %d %d %s%s%n%s}%n", 
                consensusTag.getId(),
                consensusTag.getType(),
                consensusTag.getCreator(),
                range.getBegin(),
                range.getEnd(),
                AceFileUtil.formatTagDate(consensusTag.getCreationDate()),
                consensusTag.isTransient()?" NoTrans":"",
                        tagBodyBuilder.toString()), out);
        
    }
    public static void writeReadTag(ReadAceTag readTag, OutputStream out) throws IOException {
        Range range = readTag.asRange();
    	writeString(String.format("RT{%n%s %s %s %d %d %s%n}%n", 
                        readTag.getId(),
                        readTag.getType(),
                        readTag.getCreator(),
                        range.getBegin(),
                        range.getEnd(),
                        AceFileUtil.formatTagDate(readTag.getCreationDate())), out);
        
    }
    
    public static void writeAceContig(AceContig contig,
            PhdDataStore phdDataStore, 
            OutputStream out) throws IOException, DataStoreException{
        final NucleotideSequence consensus = contig.getConsensusSequence();
        writeAceContigHeader(
                contig.getId(), 
                consensus.getLength(),
                contig.getNumberOfReads(),
                0,
                contig.isComplemented(),
                out);
        out.flush();
        writeString(String.format("%s%n%n%n",AceFileUtil.convertToAcePaddedBasecalls(consensus)), out);
        out.flush();
        writeFakeUngappedConsensusQualities(consensus, out);
        writeString(String.format("%n"), out);
        out.flush();
        List<IdAlignedReadInfo> assembledFroms = getSortedAssembledFromsFor(contig);
        StringBuilder assembledFromBuilder = new StringBuilder();
        StringBuilder placedReadBuilder = new StringBuilder();
        
        for(IdAlignedReadInfo assembledFrom : assembledFroms){
            String id = assembledFrom.getId();
            final Phd phd = phdDataStore.get(id);
            final AceAssembledRead realPlacedRead = contig.getRead(id);
             long fullLength = realPlacedRead.getReadInfo().getUngappedFullLength();
            assembledFromBuilder.append(createAssembledFromRecord(realPlacedRead,fullLength));
            placedReadBuilder.append(createPlacedReadRecord(realPlacedRead, phd));
        }
        assembledFromBuilder.append(String.format("%n"));
        placedReadBuilder.append(String.format("%n"));
        writeString(assembledFromBuilder.toString(),out);
        out.flush();
        writeString(placedReadBuilder.toString(),out);
        out.flush();
    }
    private static List<IdAlignedReadInfo> getSortedAssembledFromsFor(
            Contig<AceAssembledRead> contig){
        List<IdAlignedReadInfo> assembledFroms = new ArrayList<IdAlignedReadInfo>((int)contig.getNumberOfReads());
        StreamingIterator<AceAssembledRead> iter = null;
        try{
        	iter = contig.getReadIterator();
        	while(iter.hasNext()){
        		AceAssembledRead read = iter.next();
        		long fullLength =read.getReadInfo().getUngappedFullLength();
	            assembledFroms.add(IdAlignedReadInfo.createFrom(read, fullLength));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        Collections.sort(assembledFroms);
        return assembledFroms;
    }
    
    private static void writeFakeUngappedConsensusQualities(NucleotideSequence consensus,
            OutputStream out) throws IOException {
        StringBuilder result = new StringBuilder();
        int numberOfQualitiesSoFar=0;
        for(int i=0; i< consensus.getLength(); i++){
            Nucleotide base = consensus.get(i);
            if(base.isGap()){
                continue;
            }
            result.append(" 90");
            numberOfQualitiesSoFar++;
            if(numberOfQualitiesSoFar%50==0){
                result.append(String.format("%n"));
            }
        }
        writeString(String.format("BQ%n%s%n", result.toString()), out);
    }
   

    private static String createAssembledFromRecord(AceAssembledRead read, long fullLength){
    	IdAlignedReadInfo assembledFrom = IdAlignedReadInfo.createFrom(read, fullLength);
        return String.format("AF %s %s %d%n",
                assembledFrom.getId(),
                assembledFrom.getDirection()==Direction.FORWARD? "U":"C",
                        assembledFrom.getStartOffset());
    }
    
    
    private static String createPlacedReadRecord(AceAssembledRead read, Phd phd){
        return AceFileUtil.createAcePlacedReadRecord(
                read.getId(),read,
                phd, 
                read.getPhdInfo());
        
    }
    private static final class IdAlignedReadInfo implements Comparable<IdAlignedReadInfo>{
    	private static final int TO_STRING_BUFFER_SIZE = 30;
		private final String id;
	    private final byte dir;
	    private final int startOffset;
	    private static final Direction[] DIRECTION_VALUES = Direction.values();
	    public static IdAlignedReadInfo createFrom(AssembledRead read, long ungappedFullLength){
	        final Range validRange;
	        Direction dir = read.getDirection();
	        Range readValidRange = read.getReadInfo().getValidRange();
	        if(dir==Direction.REVERSE){
	            validRange = AssemblyUtil.reverseComplementValidRange(readValidRange, ungappedFullLength);
	        }
	        else{
	            validRange = readValidRange;
	        }
	        return new IdAlignedReadInfo(read.getId(), 
	                (int)(read.getGappedStartOffset()-validRange.getBegin()+1),dir);
	    }
	    
	    
		private IdAlignedReadInfo(String id, int startOffset, Direction dir) {
			this.id = id;
			this.dir = (byte)dir.ordinal();
			this.startOffset = startOffset;
		}


		@Override
	    public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + id.hashCode();
	        return result;
	    }
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj){
	            return true;
	        }
	        if (obj == null){
	            return false;
	        }
	        if (!(obj instanceof IdAlignedReadInfo)){
	            return false;
	        }
	        IdAlignedReadInfo other = (IdAlignedReadInfo) obj;
	        return id.equals(other.getId());
	    }
	    public String getId() {
	        return id;
	    }

	    public int getStartOffset() {
	        return startOffset;
	    }
	    
	    public Direction getDirection(){
	        return DIRECTION_VALUES[dir];
	    }
	    @Override
	    public String toString() {
	        StringBuilder builder = new StringBuilder(TO_STRING_BUFFER_SIZE);
	        builder.append(id).append(' ')
	        		.append(startOffset)
	        		.append("is complemented? ")
	        		.append(getDirection() ==Direction.REVERSE);
	        return builder.toString();
	    }
	    /**
	    * Compares two AssembledFrom instances and compares them based on start offset
	    * then by Id.  This should match the order of AssembledFrom records 
	    * (and reads) in an .ace file.
	    */
	    @Override
	    public int compareTo(IdAlignedReadInfo o) {
	    	return JillionUtil.compare(getStartOffset(), o.getStartOffset());	    	
	    }
    	
    }
    
    /**
     * Compute the consensus quality sequence as computed by the same algorithm consed uses.
     * @param contig the contig to compute the consensus qualities for; can not be null.
     * ConsensusQualities will only represent the qualities for the UNGAPPED 
     * consensus sequence.
     * @return a {@link QualitySequence} can not be null.
     * @throws DataStoreException 
     * @throws NullPointerException if contig is null.
     */
    public static QualitySequence computeConsensusQualities(Contig<? extends AssembledRead> contig, QualitySequenceDataStore readQualities) throws DataStoreException{
    	return ConsedConsensusQualityComputer.computeConsensusQualities(contig, readQualities);
    }
    
    
    
}

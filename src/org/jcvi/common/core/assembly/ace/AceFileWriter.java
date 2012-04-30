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
/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;
public class AceFileWriter {

    private static final String CONTIG_HEADER = "CO %s %d %d %d %s%n";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public static void writeAceFileHeader(int numberOfContigs, int numberOfReads, OutputStream out) throws IOException{
        writeString(String.format("AS %d %d%n%n", numberOfContigs, numberOfReads), out);
    }
   
    public static void writeAceTags(AceTags aceTags, OutputStream out) throws IOException{
        for(ReadAceTag readTag : aceTags.getReadTags()){
            writeReadTag(readTag, out);
        }
        for(ConsensusAceTag consensusTag : aceTags.getConsensusTags()){
            writeConsensusTag(consensusTag, out);
        }
        for(WholeAssemblyAceTag wholeAssemblyTag : aceTags.getWholeAssemblyTags()){
            writeWholeAssemblyTag(wholeAssemblyTag, out);
        }
    }
    public static void writeWholeAssemblyTag(
            WholeAssemblyAceTag wholeAssemblyTag, OutputStream out) throws IOException {
        writeString(String.format("WA{%n%s %s %s%n%s%n}%n", 
                wholeAssemblyTag.getType(),
                wholeAssemblyTag.getCreator(),                
                AceFileUtil.TAG_DATE_TIME_FORMATTER.print(wholeAssemblyTag.getCreationDate().getTime()),
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
                AceFileUtil.TAG_DATE_TIME_FORMATTER.print(consensusTag.getCreationDate().getTime()),
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
                        AceFileUtil.TAG_DATE_TIME_FORMATTER.print(readTag.getCreationDate().getTime())), out);
        
    }
    
    public static void writeAceContigHeader(String contigId, long consensusLength, int numberOfReads,
    		int numberOfBaseSegments, boolean isComplimented, OutputStream out) throws IOException{
    	writeString(String.format(CONTIG_HEADER, 
                contigId, 
                consensusLength,
                numberOfReads,
                numberOfBaseSegments,
                isComplimented? "C":"U"),
                
                out);
    }
    
    public static void writeAceContig(AceContig contig,
            PhdDataStore phdDataStore, 
            OutputStream out) throws IOException, DataStoreException{
        final NucleotideSequence consensus = contig.getConsensus();
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
            final AcePlacedRead realPlacedRead = contig.getRead(id);
             long fullLength = realPlacedRead.getUngappedFullLength();
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
            Contig<AcePlacedRead> contig){
        List<IdAlignedReadInfo> assembledFroms = new ArrayList<IdAlignedReadInfo>(contig.getNumberOfReads());
        CloseableIterator<AcePlacedRead> iter = null;
        try{
        	iter = contig.getReadIterator();
        	while(iter.hasNext()){
        		AcePlacedRead read = iter.next();
        		long fullLength =read.getUngappedFullLength();
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
            result.append(" 99");
            numberOfQualitiesSoFar++;
            if(numberOfQualitiesSoFar%50==0){
                result.append(String.format("%n"));
            }
        }
        writeString(String.format("BQ%n%s%n", result.toString()), out);
    }
   

    private static String createAssembledFromRecord(AcePlacedRead read, long fullLength){
    	IdAlignedReadInfo assembledFrom = IdAlignedReadInfo.createFrom(read, fullLength);
        return String.format("AF %s %s %d%n",
                assembledFrom.getId(),
                assembledFrom.getDirection()==Direction.FORWARD? "U":"C",
                        assembledFrom.getStartOffset());
    }
    
    
    private static String createPlacedReadRecord(AcePlacedRead read, Phd phd){
        return AceFileUtil.createAcePlacedReadRecord(
                read.getId(),read,
                phd, 
                read.getPhdInfo());
        
    }
  
    
    private static void writeString(String s, OutputStream out) throws IOException{
        out.write(s.getBytes(UTF_8));
        
    }
    
    private static final class IdAlignedReadInfo implements Comparable<IdAlignedReadInfo>{
    	private final String id;
	    private final byte dir;
	    private final int startOffset;
	    private static final Direction[] DIRECTION_VALUES = Direction.values();
	    public static IdAlignedReadInfo createFrom(PlacedRead read, long ungappedFullLength){
	        final Range validRange;
	        Direction dir = read.getDirection();
	        Range readValidRange = read.getValidRange();
	        if(dir==Direction.REVERSE){
	            validRange = AssemblyUtil.reverseComplementValidRange(readValidRange, ungappedFullLength);
	        }
	        else{
	            validRange = readValidRange;
	        }
	        return new IdAlignedReadInfo(read.getId(), 
	                (int)(read.getGappedContigStart()-validRange.getBegin()+1),dir);
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
	        StringBuilder builder = new StringBuilder();
	        builder.append(id).append(" ").append(startOffset).append("is complemented? ").append(getDirection() ==Direction.REVERSE);
	        return builder.toString();
	    }
	    /**
	    * Compares two AssembledFrom instances and compares them based on start offset
	    * then by Id.  This should match the order of AssembledFrom records 
	    * (and reads) in an .ace file.
	    */
	    @Override
	    public int compareTo(IdAlignedReadInfo o) {
	        int cmp= Integer.valueOf(getStartOffset()).compareTo(o.getStartOffset());
	        if(cmp !=0){
	            return cmp;
	        }
	        return getId().compareTo(o.getId());
	    }
    	
    }
}

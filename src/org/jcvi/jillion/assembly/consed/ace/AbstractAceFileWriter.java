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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.JillionUtil;
/**
 * {@code AbstractAceFileWriter}
 * handles most of the work of writing
 * out an Ace formatted file.
 * @author dkatzel
 *
 */
abstract class AbstractAceFileWriter implements AceFileWriter{
	protected static final String CR = "\n";
	
	protected static final int DEFAULT_BUFFER_SIZE = 2<<14; 
	private final boolean createBsRecords;
	
	 protected AbstractAceFileWriter(boolean createBsRecords) {
		this.createBsRecords = createBsRecords;
	}

	protected void writeAceContigHeader(Writer tempWriter, String contigId, long consensusLength, long numberOfReads,
	    		int numberOfBaseSegments, boolean isComplimented) throws IOException{
	    	String formattedHeader = String.format("CO %s %d %d %d %s\n", 
	                contigId, 
	                consensusLength,
	                numberOfReads,
	                numberOfBaseSegments,
	                isComplimented? "C":"U");
	    	
	    	tempWriter.write(formattedHeader);
	    }

	public void write(Writer tempWriter, AceContig contig, PhdDataStore phdDataStore) throws IOException {		
		final NucleotideSequence consensus = contig.getConsensusSequence();
        writeAceContigHeader(tempWriter,
                contig.getId(), 
                consensus.getLength(),
                contig.getNumberOfReads(),
                0,
                contig.isComplemented());
        tempWriter.write(String.format("%s\n\n\n",AceFileUtil.convertToAcePaddedBasecalls(consensus)));
        writeConsensusQualities(tempWriter,contig.getConsensusQualitySequence());
       
        tempWriter.write(CR);
        List<IdAlignedReadInfo> assembledFroms = IdAlignedReadInfo.getSortedAssembledFromsFor(contig);
        StringBuilder assembledFromBuilder = new StringBuilder();
        StringBuilder placedReadBuilder = new StringBuilder();
        
        for(IdAlignedReadInfo assembledFrom : assembledFroms){
            String id = assembledFrom.getId();
           
            final AceAssembledRead realPlacedRead = contig.getRead(id);
             long fullLength = realPlacedRead.getReadInfo().getUngappedFullLength();
            assembledFromBuilder.append(createAssembledFromRecord(realPlacedRead,fullLength));
            placedReadBuilder.append(createPlacedReadRecord(realPlacedRead,phdDataStore));
        }
       
        placedReadBuilder.append(CR);
        tempWriter.write(assembledFromBuilder.toString());
        if(createBsRecords){
        	for(AceBaseSegment bs : BaseSegmentUtil.computeBestSegmentsFor(contig)){
        		Range gappedRange = bs.getGappedConsensusRange();
        		tempWriter.write(String.format("BS %d %d %s\n", 
        				gappedRange.getBegin(CoordinateSystem.RESIDUE_BASED),
        				gappedRange.getEnd(CoordinateSystem.RESIDUE_BASED),
        				bs.getReadName()));
        	}
        }
        tempWriter.write(CR);
        tempWriter.write(placedReadBuilder.toString());

		
	}
	
	private void writeConsensusQualities(Writer tempWriter, QualitySequence consensusQualities) throws IOException {
			if(consensusQualities ==null){
				throw new NullPointerException("consensus qualities can not be null");
			}
			int qualityLength = (int)consensusQualities.getLength();
			int numberOfLines = qualityLength/50+1;
			StringBuilder formattedString = new StringBuilder(3+ 3* qualityLength+numberOfLines);
			formattedString.append("BQ\n");
			Iterator<PhredQuality> iter = consensusQualities.iterator();
			int i=1;
			while(i <qualityLength){
				formattedString.append(String.format("%02d",iter.next().getQualityScore()));
				if(i%50==0){
					formattedString.append(CR);
				}else{
					formattedString.append(' ');
				}
				i++;
			}
			//last quality handled specially so we don't add an extra CR
			formattedString.append(String.format("%02d",iter.next().getQualityScore()));
			formattedString.append(CR);
			tempWriter.write(formattedString.toString());
		
	}

	private String createAssembledFromRecord(AceAssembledRead read, long fullLength){
    	IdAlignedReadInfo assembledFrom = IdAlignedReadInfo.createFrom(read, fullLength);
        return String.format("AF %s %s %d\n",
                assembledFrom.getId(),
                assembledFrom.getDirection()==Direction.FORWARD? "U":"C",
                        assembledFrom.getStartOffset());
    }
    
    
    private String createPlacedReadRecord(AceAssembledRead read, PhdDataStore phdDatastore) throws IOException{
    	 Phd phd;
		try {
			phd = phdDatastore.get(read.getId());
		} catch (DataStoreException e) {
			throw new IOException("error writing quality values for read "+ read.getId(),e);
		}
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
	    
	    public static List<IdAlignedReadInfo> getSortedAssembledFromsFor(
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
}

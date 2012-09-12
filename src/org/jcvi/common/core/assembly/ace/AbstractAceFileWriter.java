package org.jcvi.common.core.assembly.ace;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

abstract class AbstractAceFileWriter implements AceFileWriter{
	protected static final String CR = "\n";
	
	protected static final int DEFAULT_BUFFER_SIZE = 2<<14; 
	private final boolean computeConsensusQualities;
	
	
	 protected AbstractAceFileWriter(boolean computeConsensusQualities) {
		this.computeConsensusQualities = computeConsensusQualities;
	}

	protected void writeAceContigHeader(Writer tempWriter, String contigId, long consensusLength, int numberOfReads,
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
        if(computeConsensusQualities){
        	computeConsensusQualities(tempWriter,contig,phdDataStore);
        }else{
        	writeFakeUngappedConsensusQualities(tempWriter,consensus);
        }
        tempWriter.write(CR);
        List<IdAlignedReadInfo> assembledFroms = IdAlignedReadInfo.getSortedAssembledFromsFor(contig);
        StringBuilder assembledFromBuilder = new StringBuilder();
        StringBuilder placedReadBuilder = new StringBuilder();
        
        for(IdAlignedReadInfo assembledFrom : assembledFroms){
            String id = assembledFrom.getId();
           
            final AcePlacedRead realPlacedRead = contig.getRead(id);
             long fullLength = realPlacedRead.getReadInfo().getUngappedFullLength();
            assembledFromBuilder.append(createAssembledFromRecord(realPlacedRead,fullLength));
            placedReadBuilder.append(createPlacedReadRecord(realPlacedRead,phdDataStore));
        }
        assembledFromBuilder.append(CR);
        placedReadBuilder.append(CR);
        tempWriter.write(assembledFromBuilder.toString());

        tempWriter.write(placedReadBuilder.toString());

		
	}
	
	private void computeConsensusQualities(Writer tempWriter, AceContig contig,PhdDataStore phdDataStore) throws IOException {
		NucleotideSequence consensusSequence = contig.getConsensusSequence();
		double[] qualities = new double[(int)consensusSequence.getUngappedLength()];
		StreamingIterator<AcePlacedRead> readIterator = contig.getReadIterator();
		try{
			while(readIterator.hasNext()){
				AcePlacedRead read = readIterator.next();
				int startOffset = (int)consensusSequence.getUngappedOffsetFor((int)read.getGappedStartOffset());
				QualitySequence ungappedQualities;
				try {
					ungappedQualities = AssemblyUtil.getUngappedComplementedValidRangeQualities(read,phdDataStore.get(read.getId()).getQualitySequence());
				} catch (DataStoreException e) {
					throw new IOException("error computing consensus qualities when examining read "+read.getId(),e);
				}
				Iterator<Nucleotide> basesIterator = read.getNucleotideSequence().iterator();
				Iterator<PhredQuality> qualIterator = ungappedQualities.iterator();
				int i=0;
				Iterator<Nucleotide> consensusIterator = consensusSequence.iterator(read.asRange());
				while(basesIterator.hasNext()){
					Nucleotide consensus = consensusIterator.next();
					Nucleotide base = basesIterator.next();
					double qualValue;
					if(base.isGap()){
						qualValue=0D;
					}else{						
						qualValue =qualIterator.next().getErrorProbability();
					}
					if(!consensus.isGap()){
						if(base == consensus){
							qualities[startOffset+i] -=qualValue;
						}else{
							qualities[startOffset+i] +=qualValue;
						}
						i++;	
					}
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(readIterator);
		}
        int numberOfLines = qualities.length/50+1;
		StringBuilder formattedString = new StringBuilder(3+ 3* qualities.length+numberOfLines);
		formattedString.append("BQ\n");
		for(int i=1; i<qualities.length;i++){
			formattedString.append(getNormalizedQualValueAsString(qualities, i-1));
			if(i%50==0){
				formattedString.append(CR);
			}else{
				formattedString.append(" ");
			}
		}
		formattedString.append(getNormalizedQualValueAsString(qualities, qualities.length-1));
		formattedString.append(CR);
		tempWriter.write(formattedString.toString());
		
	}

	private String getNormalizedQualValueAsString(double[] qualities, int i) {
		int value;
		if(qualities[i]<=0){
			value = PhredQuality.MAX_VALUE;
		}else{
			value = PhredQuality.computeQualityScore(qualities[i]);
		}
		value = Math.min(value, 99);
		String qualString = String.format("%02d",value);
		return qualString;
	}

	private String createAssembledFromRecord(AcePlacedRead read, long fullLength){
    	IdAlignedReadInfo assembledFrom = IdAlignedReadInfo.createFrom(read, fullLength);
        return String.format("AF %s %s %d\n",
                assembledFrom.getId(),
                assembledFrom.getDirection()==Direction.FORWARD? "U":"C",
                        assembledFrom.getStartOffset());
    }
    
    
    private String createPlacedReadRecord(AcePlacedRead read, PhdDataStore phdDatastore) throws IOException{
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
	 private void writeFakeUngappedConsensusQualities(Writer tempWriter, NucleotideSequence consensus) throws IOException {

	        int length = (int)consensus.getUngappedLength();
	        int numberOfLines = length/50+1;
	        StringBuilder result = new StringBuilder(3+3*length+numberOfLines);	      
	        result.append("BQ\n");
			for(int i=1; i<= length-1; i++){
	            result.append("99");
	            if(i%50==0){
	                result.append(CR);
	            }else{
	            	result.append(" ");
	            }
	        }
			result.append("99\n");
			
	        tempWriter.write(result.toString());
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
	            Contig<AcePlacedRead> contig){
	        List<IdAlignedReadInfo> assembledFroms = new ArrayList<IdAlignedReadInfo>(contig.getNumberOfReads());
	        StreamingIterator<AcePlacedRead> iter = null;
	        try{
	        	iter = contig.getReadIterator();
	        	while(iter.hasNext()){
	        		AcePlacedRead read = iter.next();
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
	        int cmp= Integer.valueOf(getStartOffset()).compareTo(o.getStartOffset());
	        if(cmp !=0){
	            return cmp;
	        }
	        return getId().compareTo(o.getId());
	    }
    	
    }
}

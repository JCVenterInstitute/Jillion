package org.jcvi.common.core.assembly.ace;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
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
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class DefaultAceFileWriter implements AceFileWriter2{
	
	private static final String CR = "\n";
	
	private static final int DEFAULT_BUFFER_SIZE = 2<<14; 
	//builder option for writing BS records or not
		//optional phd datastore to handle upper vs lowercase bases
		//option for quality threshold for upper vs lowercase
		
		//what about when I make phd datastores per contig?
	//ans: only used in a few places to make parital ace files
	//so make different writer implementation to make parital aces
	//which won't write out file header
	//best segments aren't used anymore in current writer
	//so I'll have to re-add them.
	
	private final boolean createBsRecords;
	private final PhdDataStore phdDatastore;
	private final OutputStream out;
	private final File tempFile;
	private long numberOfContigs=0;
	private long numberOfReads=0;
	private final Writer tempWriter;
	
	ByteArrayOutputStream tagOutputStream = new ByteArrayOutputStream();
	
	private DefaultAceFileWriter(OutputStream out, PhdDataStore phdDatastore,File tmpDir,
			boolean createBsRecords) throws IOException {
		this.out = new BufferedOutputStream(out,DEFAULT_BUFFER_SIZE);
		this.phdDatastore = phdDatastore;
		this.createBsRecords = createBsRecords;
		IOUtil.mkdirs(tmpDir);
		this.tempFile = File.createTempFile("aceWriter", null, tmpDir);
		tempFile.deleteOnExit();
		tempWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)),DEFAULT_BUFFER_SIZE);
	}

	@Override
	public void close() throws IOException {
		
		tempWriter.close();
		writeAceFileHeader();
		copyTempFileData();
		copyTagData();
		out.close();
		IOUtil.deleteIgnoreError(tempFile);
		
	}

	private void copyTagData() throws IOException {
		if(tagOutputStream.size()>0){
			out.write(tagOutputStream.toByteArray());
		}
	}

	private void copyTempFileData() throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(tempFile);
		try{
		IOUtil.copy(in, out);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	 private void writeAceFileHeader() throws IOException{
	     out.write(String.format("AS %d %d%s%s", numberOfContigs, numberOfReads,CR,CR).getBytes(IOUtil.UTF_8));
	 }
	 private void writeAceContigHeader(String contigId, long consensusLength, int numberOfReads,
	    		int numberOfBaseSegments, boolean isComplimented) throws IOException{
	    	String formattedHeader = String.format("CO %s %d %d %d %s\n", 
	                contigId, 
	                consensusLength,
	                numberOfReads,
	                numberOfBaseSegments,
	                isComplimented? "C":"U");
	    	
	    	tempWriter.write(formattedHeader);
	    }
	@Override
	public void write(AceContig contig) throws IOException {
		numberOfContigs++;
		numberOfReads+=contig.getNumberOfReads();
		
		final NucleotideSequence consensus = contig.getConsensusSequence();
        writeAceContigHeader(
                contig.getId(), 
                consensus.getLength(),
                contig.getNumberOfReads(),
                0,
                contig.isComplemented());
        tempWriter.write(String.format("%s\n\n\n",AceFileUtil.convertToAcePaddedBasecalls(consensus)));
        writeFakeUngappedConsensusQualities(consensus);
        tempWriter.write(CR);
        List<IdAlignedReadInfo> assembledFroms = IdAlignedReadInfo.getSortedAssembledFromsFor(contig);
        StringBuilder assembledFromBuilder = new StringBuilder();
        StringBuilder placedReadBuilder = new StringBuilder();
        
        for(IdAlignedReadInfo assembledFrom : assembledFroms){
            String id = assembledFrom.getId();
           
            final AcePlacedRead realPlacedRead = contig.getRead(id);
             long fullLength = realPlacedRead.getReadInfo().getUngappedFullLength();
            assembledFromBuilder.append(createAssembledFromRecord(realPlacedRead,fullLength));
            placedReadBuilder.append(createPlacedReadRecord(realPlacedRead));
        }
        assembledFromBuilder.append(CR);
        placedReadBuilder.append(CR);
        tempWriter.write(assembledFromBuilder.toString());

        tempWriter.write(placedReadBuilder.toString());

		
	}
	
	private String createAssembledFromRecord(AcePlacedRead read, long fullLength){
    	IdAlignedReadInfo assembledFrom = IdAlignedReadInfo.createFrom(read, fullLength);
        return String.format("AF %s %s %d\n",
                assembledFrom.getId(),
                assembledFrom.getDirection()==Direction.FORWARD? "U":"C",
                        assembledFrom.getStartOffset());
    }
    
    
    private String createPlacedReadRecord(AcePlacedRead read) throws IOException{
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
	 private void writeFakeUngappedConsensusQualities(NucleotideSequence consensus) throws IOException {

	        int length = (int)consensus.getUngappedLength();
	        int numberOfLines = length/50+1;
	        StringBuilder result = new StringBuilder(3+3*length+numberOfLines);	      
	        result.append("BQ\n");
			for(int i=0; i< length-1; i++){
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
	@Override
	public void write(ReadAceTag readTag) throws IOException {
		Range range = readTag.asRange();
    	String formattedTag =String.format("RT{\n%s %s %s %d %d %s\n}\n", 
                        readTag.getId(),
                        readTag.getType(),
                        readTag.getCreator(),
                        range.getBegin(),
                        range.getEnd(),
                        AceFileUtil.formatTagDate(readTag.getCreationDate()));
    	
    	tagOutputStream.write(formattedTag.getBytes(IOUtil.UTF_8));
	}

	@Override
	public void write(ConsensusAceTag consensusTag) throws IOException {
		 StringBuilder tagBodyBuilder = new StringBuilder();
	        if(consensusTag.getData() !=null){
	            tagBodyBuilder.append(consensusTag.getData());
	        }
	        if(!consensusTag.getComments().isEmpty()){
	            for(String comment :consensusTag.getComments()){
	                tagBodyBuilder.append(String.format("COMMENT{\n%sC}\n",comment));            
	            }
	        }
	        Range range = consensusTag.asRange();
	        String formattedTag=String.format("CT{\n%s %s %s %d %d %s%s\n%s}\n", 
	                consensusTag.getId(),
	                consensusTag.getType(),
	                consensusTag.getCreator(),
	                range.getBegin(),
	                range.getEnd(),
	                AceFileUtil.formatTagDate(consensusTag.getCreationDate()),
	                consensusTag.isTransient()?" NoTrans":"",
	                        tagBodyBuilder.toString());
	        
	        tagOutputStream.write(formattedTag.getBytes(IOUtil.UTF_8));
		
	}

	@Override
	public void write(WholeAssemblyAceTag wholeAssemblyTag) throws IOException {
		String formattedTag =String.format("WA{\n%s %s %s\n%s\n}\n", 
                wholeAssemblyTag.getType(),
                wholeAssemblyTag.getCreator(),                
                AceFileUtil.formatTagDate(wholeAssemblyTag.getCreationDate()),
                wholeAssemblyTag.getData());
		
		tagOutputStream.write(formattedTag.getBytes(IOUtil.UTF_8));
		
	}

	public static class Builder{

		private boolean createBsRecords=false;
		private final PhdDataStore phdDataStore;
		private final OutputStream out;
		private File tmpDir;
		
		public Builder(File outputAceFile,PhdDataStore datastore) throws FileNotFoundException{
			this(new FileOutputStream(outputAceFile), datastore);
		}
		
		public Builder(OutputStream out,PhdDataStore datastore){
			if(out==null){
				throw new NullPointerException("output can not be null");	
			}
			if(datastore==null){
				throw new NullPointerException("datastore can not be null");				
			}
			this.phdDataStore = datastore;
			this.out=out;
		}
		public Builder tmpDir(File tmpDir){
			this.tmpDir = tmpDir;
			return this;
		}
		

		public AceFileWriter2 build() throws IOException {
			return new DefaultAceFileWriter(out, phdDataStore, tmpDir,createBsRecords);
		}
		
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

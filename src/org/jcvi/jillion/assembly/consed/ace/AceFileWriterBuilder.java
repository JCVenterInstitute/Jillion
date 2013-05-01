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

import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
/**
 * {@code AceFileWriterBuilder} 
 * is a builder
 * for creating new
 * {@link AceFileWriter} instances
 * that handles
 * writing out correctly formatted
 * ace files that can be viewed
 * with consed.
 * Since some portions of an ace file,
 * such as the header line contain information
 * that can not be known until the entire file
 * is written, this implementation will first
 * write out data to a temporary file.
 * The location of the temporary file
 * can be configured via {@link #tmpDir(File)}.
 * @author dkatzel
 *
 */
public final class AceFileWriterBuilder{
	
		private boolean createBsRecords=false;
		private final PhdDataStore phdDataStore;
		private final OutputStream out;
		private File tmpDir;
		/**
		 * Create a new Builder instance
		 * which will build a new instance of 
		 * {@link AceFileWriterBuilder} with the given required
		 * parameters.
		 * @param outputAceFile a {@link File} representating
		 * the path to the output of the ace file to write.
		 * 
		 * @param datastore the {@link PhdDataStore}
		 * needed to write out the ace data.
		 * The {@link PhdDataStore} needs to be consulted
		 * to get the quality values of the reads
		 * to compute consensus quality values,
		 * get the full length nucleotide sequences of the reads
		 * in the contigs and 
		 * to format the full length nucleotide sequences
		 * by encoding high vs low quality values by using
		 * upper vs lowercase letters respectively.
		 * @throws IOException 
		 */
		public AceFileWriterBuilder(File outputAceFile,PhdDataStore datastore) throws IOException{
			if(outputAceFile ==null){
				throw new NullPointerException("output ace file can not be null");	
			}
			if(datastore==null){
				throw new NullPointerException("datastore can not be null");				
			}
			IOUtil.mkdirs(outputAceFile.getParentFile());
			this.phdDataStore = datastore;
			this.out=new FileOutputStream(outputAceFile);
		}
		
		/**
		 * Change the temporary directory used
		 * to keep temp files during the writing process.
		 * If this option is not specified,
		 * then the system's default temp dir area is used.
		 * @param tmpDir the path to the tmpDirectory.
		 * @return this
		 * @throws IOException if there is a problem creating the temp directory
		 * if it does not exist.
		 * @throws NullPointerException if tmpDir is null.
		 * @throws IllegalArgumentException if tmpDir exists but is not
		 * a directory.
		 */
		public AceFileWriterBuilder tmpDir(File tmpDir) throws IOException{
			if(tmpDir==null){
				throw new NullPointerException("tmp dir path can not be null");
			}
			if(tmpDir.exists() && !tmpDir.isDirectory()){
				throw new IllegalArgumentException("tmp dir must be a directory");
			}
			IOUtil.mkdirs(tmpDir);
			this.tmpDir = tmpDir;
			return this;
		}
		
		/**
		 * Legacy versions of consed
		 * required ace contigs to include information
		 * that indicated which read phrap had chosen to be 
		 * the consensus at a particular offset.  This information
		 * is no longer required by current versions of consed since
		 * it is phrap specific.  Including
		 * Base Segments is not recommended since generating
		 * them is computationally expensive and can throw
		 * RuntimeExceptions if there is no read that matches the consensus
		 * at a particular offset (for example a 0x region or if the consensus
		 * is an ambiguity).
		 * @return this.
		 */
		public AceFileWriterBuilder includeBaseSegments(){
			createBsRecords=true;
			return this;
		}
		/**
		 * Create a new instance of {@link AceFileWriterBuilder}
		 * with the given paramters.
		 * @return a new instance; never null.
		 * @throws IOException if there is a problem
		 * creating the temp directory (it doesn't
		 * already exist) or the temp file.
		 * 
		 */
		public AceFileWriter build() throws IOException {
			return new DefaultAceFileWriter(out, phdDataStore, 
					tmpDir,
					createBsRecords);
		}

/**
 * Private implementation of {@link AceFileWriter}.
 * @author dkatzel
 *
 */
private static final class DefaultAceFileWriter extends AbstractAceFileWriter{
	
	private final PhdDataStore phdDatastore;
	private final OutputStream out;
	private final File tempFile;
	private long numberOfContigs=0;
	private long numberOfReads=0;
	private final Writer tempWriter;
	
	ByteArrayOutputStream tagOutputStream = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
	
	private DefaultAceFileWriter(OutputStream out, PhdDataStore phdDatastore,File tmpDir,
			boolean createBsRecords) throws IOException {
		super(createBsRecords);
		this.out = new BufferedOutputStream(out,DEFAULT_BUFFER_SIZE);
		this.phdDatastore = phdDatastore;
		IOUtil.mkdirs(tmpDir);
		this.tempFile = File.createTempFile("aceWriter", null, tmpDir);
		tempFile.deleteOnExit();
		tempWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(tempFile), IOUtil.UTF_8),
				DEFAULT_BUFFER_SIZE);
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
	 
	@Override
	public void write(AceContig contig) throws IOException {
		numberOfContigs++;
		numberOfReads+=contig.getNumberOfReads();
		write(tempWriter, contig, phdDatastore);
		
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
}
	
}

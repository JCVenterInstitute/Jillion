/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.AceFileWriter;
import org.jcvi.jillion.assembly.consed.ace.AceFileWriterBuilder;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.ace.WholeAssemblyAceTag;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.joda.time.DateTimeUtils;
/**
 * {@code ConsedAssemblyTransformerBuilder}
 * will build an {@link AssemblyTransformer}
 * instance that will convert an assembly
 * into a consed package (ace file, phd file(s) etc).
 * 
 * @author dkatzel
 *
 */
public class ConsedAssemblyTransformerBuilder {
	/**
	 * The default qualiyt value to use if qualities not provided: 26.
	 */
	public static final PhredQuality DEFAULT_QUALITY_VALUE = PhredQuality.valueOf(26);
	private final File rootDir;
	private final String filePrefix;
	
	private boolean createPhdBall = true;
	
	private File chromatInputDir = null;
	private byte defaultQualityValue = DEFAULT_QUALITY_VALUE.getQualityScore();
	
	private AceAssemblyTransformerPostProcessor postProcessor =null;
	/**
	 * Create a new ConsedAssemblyTransformerBuilder instance that will
	 * write out a consed package using the given parameters.
	 * @param consedRootDir the root directory of the consed package to write.
	 * Underneath this directory, this transformer will create and populate "edit_dir" and "phd_dir" or
	 * "phdball_dir" directories. if the consedRootDir does not yet exist,
	 * then the directory will be created. Can not be null.
	 * @param filePrefix the file prefix to use for the ace and phd files to write.
	 * These files will named $filePrefix.ace.1 and $filePrefix.phd.ball.1 etc.
	 * @throws NullPointerException if any parameters are null.  Can not be null.
	 */
	public ConsedAssemblyTransformerBuilder(File consedRootDir, String filePrefix){
		
		if(consedRootDir ==null){
			throw new NullPointerException("root dir can not be null");
		}
		if(filePrefix ==null){
			throw new NullPointerException("file prefix can not be null");
		}
		if(filePrefix.isEmpty()){
			throw new IllegalArgumentException("file prefix can not be empty");
		}
		this.rootDir = consedRootDir;
		this.filePrefix = filePrefix;
		
	}
	/**
	 * Should individual phd files in the "phd_dir" directory be created instead of using one giant
	 * phd.ball file.  Creating individual files
	 * is not recommended for next-gen assemblies since there will potentially be millions of 
	 * reads and therefore millions of files in a single directory.  However, this option is provided
	 * so that consed packages can be used on legacy consed installations or other ace and phd
	 * parser libraries and tools that don't support phd.ball.
	 * If this method is not called, then the default
	 * is to use a phd.ball file (similar to {@link #createIndividualPhdFiles(boolean), createIndividualPhdFiles(false)}
	 * @param createIndividualPhdFiles {@code true} if should create individual phd files in the phd_dir; {@code false}
	 * if should use phd.ball in phdball_dir instead (default is {@code false}.
	 * @return this.
	 */
	public ConsedAssemblyTransformerBuilder createIndividualPhdFiles(boolean createIndividualPhdFiles){
		this.createPhdBall = !createIndividualPhdFiles;
		return this;
	}
	/**
	 * Provide a directory which contains sanger chromatograms
	 * to be included in this consed package.  If the directory is not null
	 * and contains sanger trace files, then those traces will be copied and coverted into 
	 * scf format under $consed_rootDir/chromat_dir so consed can see them and display the
	 * raw chromatogram channel wave forms. 
	 * @param chromatDir the chormatDir to get the traces from;
	 * if {@code null} then there are no chroamtograms (defaults to {@code null}
	 * @return this
	 */
	public ConsedAssemblyTransformerBuilder inputChromatogramDir(File chromatDir){
		this.chromatInputDir = chromatDir;
		return this;
	}
	/**
	 * Set the {@link AceAssemblyTransformerPostProcessor} to use
	 * to modify the Ace contigs after the transformation service
	 * has provided all the alignment information.
	 * @param postProcessor the {@link AceAssemblyTransformerPostProcessor}
	 * to use; or {@code null} if there is no post processor (defaults to {@code null} ).
	 * @return this
	 */
	public ConsedAssemblyTransformerBuilder postProcessor(AceAssemblyTransformerPostProcessor postProcessor){
		this.postProcessor = postProcessor;
		return this;
	}
	/**
	 * Set the default quality value to use if an aligned read
	 * does not have associated quality data.  If not called,
	 * then the default is {@value #DEFAULT_QUALITY_VALUE}
	 * @param qualityValue the quality value to use;
	 * must be between 0 and {@link Byte#MAX_VALUE}.
	 * @return this.
	 * @throws IllegalArgumentException if if qualityScore < 0 or > {@link Byte#MAX_VALUE}.
	 */
	public ConsedAssemblyTransformerBuilder setDefaultQualityValue(int qualityValue){
		//Phredquality object does all validation for us
		PhredQuality qual = PhredQuality.valueOf(qualityValue);
		this.defaultQualityValue = qual.getQualityScore();
		return this;
	}
	/**
	 * Create a new {@link AssemblyTransformer} instance
	 * using the provided configuration.
	 * @return a new {@link AssemblyTransformer}
	 * will never be null.
	 * @throws IOException if there is a problem creating the consed root dir
	 * or any of the underlying files (which may initially be empty until
	 * the {@link AssemblyTransformer} methods have been called.
	 */
	public AssemblyTransformer build() throws IOException{
		return new ConsedAssemblyTransformer(this);
	}
	
	private static final class ConsedAssemblyTransformer implements AssemblyTransformer{
		private final File rootDir, editDir, chromatInputDir, chromatDir;
		private final String filePrefix;
		
		private final AceAssemblyTransformerPostProcessor postProcessor;
		
		private final PhdConsedTransformerHelper phdHelper;
		private final byte defaultQualityValue;
		
		private final Map<String, AceContigBuilder> builderMap = new LinkedHashMap<String, AceContigBuilder>();
		
		private final Map<URI,Date> uriDates = new HashMap<URI, Date>();
		private final Map<URI,File> uri2File = new HashMap<URI, File>();
		
		private final Map<URI,Map<String,String>> comments = new HashMap<URI, Map<String,String>>();
		
		public ConsedAssemblyTransformer(ConsedAssemblyTransformerBuilder builder) throws IOException{
			
			
			this.rootDir = builder.rootDir;
			this.filePrefix = builder.filePrefix;
			this.chromatInputDir = builder.chromatInputDir;
			this.postProcessor = builder.postProcessor ==null? NullAceAssemblyTransformerPostProcessor.INSTANCE : builder.postProcessor;
		
			IOUtil.mkdirs(rootDir);
			this.editDir = new File(rootDir, "edit_dir");
			if(chromatInputDir ==null){
				this.chromatDir = null;				
			}else{
				//has chromats
				this.chromatDir = new File(rootDir, "chromat_dir");
				IOUtil.mkdirs(chromatDir);
				//TODO copy chromatograms? to chromat_dir?
			}
			if(builder.createPhdBall){
				phdHelper = new PhdBallConsedTransformerHelper(rootDir);
			}else{
				phdHelper = new IndividualPhdConsedTransformerHelper(rootDir);
			}
			
			this.defaultQualityValue = builder.defaultQualityValue;
			
			//add default
			Date currentDate = new Date(DateTimeUtils.currentTimeMillis());
			uriDates.put(null, currentDate);
			comments.put(null, computeRequiredCommentsFor(null, currentDate));
			
		}

		@Override
		public void referenceOrConsensus(String id,
				NucleotideSequence gappedReference) {
			if(builderMap.containsKey(id)){
				throw new IllegalStateException("reference with id " + id + " already exists");
			}
			builderMap.put(id, new AceContigBuilder(id, gappedReference));
			
		}

		@Override
		public void endAssembly() {
			final PhdDataStore phdDataStore;
			try {
				phdDataStore =phdHelper.createDataStore();
			} catch (IOException e) {
				throw new IllegalStateException("error reading phd data", e);
			}
			Map<String, AceContigBuilder> updatedBuilders = postProcessor.postProcess(builderMap, phdDataStore);
			
			File outputAceFile = new File(editDir, String.format("%s.ace.1",filePrefix ));
			final AceFileWriter aceWriter;
			try {
				aceWriter = new AceFileWriterBuilder(outputAceFile, phdDataStore)
											.build();
			} catch (IOException e) {
				throw new IllegalStateException("error creating ace writer", e);
			}
			try{
				for(AceContigBuilder builder : updatedBuilders.values()){
					try {
						for(Entry<Range,AceContig> entry : ConsedUtil.split0xContig(builder,true).entrySet()){
		                    AceContig splitContig = entry.getValue();
		                    postProcessor.postProcess(splitContig);
		                    aceWriter.write(splitContig);
						}
					
						
					} catch (IOException e) {
						throw new IllegalStateException("error writing assembly " + builder.getContigId(), e);
					}
				}
				
				WholeAssemblyAceTag phdBallTag = phdHelper.createPhdBallWholeAssemblyTag();
				if(phdBallTag !=null){
					aceWriter.write(phdBallTag);
				}	
			} catch (IOException e) {
				throw new IllegalStateException("error creating ace tag", e);
			}finally{
				IOUtil.closeAndIgnoreErrors(aceWriter);
			}
			
		}

		@Override
		public void notAligned(String id,
				NucleotideSequence nucleotideSequence,
				QualitySequence qualitySequence, PositionSequence positions,
				URI uri) {
			//don't care
			
		}

		@Override
		public void aligned(String readId, NucleotideSequence nucleotideSequence,
				QualitySequence qualitySequence, PositionSequence positions,
				URI sourceFileUri, String referenceId, long gappedStartOffset,
				Direction direction,
				NucleotideSequence gappedSequence,
				ReadInfo readInfo) {
			
			//TODO handle symlink to uri?
			if(!builderMap.containsKey(referenceId)){
				throw new IllegalStateException("unknown reference id " + referenceId);
			}
			if(gappedStartOffset > Integer.MAX_VALUE){
				//TODO Violation of LSP ?
				throw new IllegalArgumentException("gapped start offset is > int max"+ readId);
			}
			final QualitySequence qualities;
			if(qualitySequence ==null){
				qualities = createDefaultQualitySequenceFor(nucleotideSequence);
			}else{
				qualities = qualitySequence;
			}
			final Date phdDate;
			Map<String, String> requiredComments;
			
			if(uriDates.containsKey(sourceFileUri)){
				phdDate = uriDates.get(sourceFileUri);
				requiredComments = comments.get(sourceFileUri);
			}else{
				File file = new File(sourceFileUri);
				phdDate = new Date(file.lastModified());
				uriDates.put(sourceFileUri, phdDate);
				uri2File.put(sourceFileUri, file);
				
				
				//first time we've seen this file
				if(file.getName().endsWith(".scf")){
					InputStream in=null;
					OutputStream out = null;
					try{
						in = new BufferedInputStream(new FileInputStream(file));
						File newFile = new File(chromatDir, FileUtil.getBaseName(file));
						out = new BufferedOutputStream(new FileOutputStream(newFile));
						IOUtil.copy(in, out);
					} catch (IOException e) {
						throw new IllegalStateException("error copying chromatogram file to chromat_dir", e);
					}finally{
						IOUtil.closeAndIgnoreErrors(in, out);
					}
				}
				requiredComments = computeRequiredCommentsFor(uri2File.get(sourceFileUri), phdDate);
				comments.put(sourceFileUri, requiredComments);
			}
			PhdBuilder phdBuilder = new PhdBuilder(readId, nucleotideSequence, qualities)
										.comments(requiredComments);
			
			if(positions ==null){
				phdBuilder.fakePeaks();			
			}else{				
				phdBuilder.peaks(positions);
			}
			final PhdInfo phdInfo;
			try {
				phdInfo = phdHelper.writePhd(phdBuilder.build(), phdDate);
			} catch (IOException e) {
				throw new IllegalStateException("error writing phd record for "+ readId, e);
			}
			
			builderMap.get(referenceId)
							.addRead(readId, gappedSequence, (int)gappedStartOffset, direction, readInfo.getValidRange(), 
									phdInfo, readInfo.getUngappedFullLength());
			
		}

		private Map<String,String> computeRequiredCommentsFor(File file, Date phdDate ){
			if(file ==null){
				return PhdUtil.createPhdTimeStampCommentFor(phdDate);
			}
			try{
				if(isChromatogramFile(file)){
					return PhdUtil.createPhdTimeStampAndChromatFileCommentsFor(phdDate, file.getName());
				}
				return PhdUtil.createPhdTimeStampCommentFor(phdDate);
			}catch(Exception e){
				return PhdUtil.createPhdTimeStampCommentFor(phdDate);
			}

			
			
		}
		
		private boolean isChromatogramFile(File f){
			return !f.getName().endsWith(".fastq") && !f.getName().endsWith(".sff");
			
		}
		
		 private QualitySequence createDefaultQualitySequenceFor(NucleotideSequence rawSequence) {
		        int numberOfQualities =(int) rawSequence.getUngappedLength();
				byte[] qualities = new byte[numberOfQualities];
				Arrays.fill(qualities, defaultQualityValue);
		        return new QualitySequenceBuilder(qualities).build();
		    }

		@Override
		public void assemblyCommand(String name, String version,
				String parameters) {
			//no-op
			
		}

		@Override
		public void referenceFile(URI uri) {
			//no-op
			
		}

		@Override
		public void readFile(URI uri) {
			//TODO handle symlink to uri?
			
		}
		
		
	}
	
	private enum NullAceAssemblyTransformerPostProcessor implements AceAssemblyTransformerPostProcessor{
		INSTANCE;

		@Override
		public Map<String, AceContigBuilder> postProcess(
				Map<String, AceContigBuilder> builderMap, PhdDataStore phdDataStore) {
			return builderMap;
		}

		@Override
		public void postProcess(AceContig contig) {
			//no-op
			
		}
		
		
		
	}
	
	
}

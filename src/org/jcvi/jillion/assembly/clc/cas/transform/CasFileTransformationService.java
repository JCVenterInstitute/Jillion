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
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.clc.cas.AbstractAlignedReadCasVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasFileInfo;
import org.jcvi.jillion.assembly.clc.cas.CasFileParser;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStore;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStoreBuilderVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasParser;
import org.jcvi.jillion.assembly.clc.cas.CasUtil;
import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.sff.SffFileIterator;

public class CasFileTransformationService implements AssemblyTransformationService{

	private final File casFile;
	private final File casDir;
	private final File chromatDir;
	
	private FastqQualityCodec qualityCodec;
	
	public CasFileTransformationService(File casFile) throws IOException{
		this(casFile, null);
	}
	public CasFileTransformationService(File casFile, File chromatDir) throws IOException {
		if(casFile ==null){
			throw new NullPointerException("cas file can not be null");
		}
		if(!casFile.exists()){
			throw new FileNotFoundException(casFile.getAbsolutePath());
		}
		if(chromatDir !=null){
			if(!chromatDir.exists()){
				throw new FileNotFoundException(chromatDir.getAbsolutePath());
			}
			if(!chromatDir.isDirectory()){
				throw new IOException("chromat dir must be a directory"+ chromatDir.getAbsolutePath());
			}
		}
		this.casFile = casFile;
		casDir = casFile.getParentFile();
		this.chromatDir = chromatDir;
	}



	public FastqQualityCodec getQualityCodec() {
		return qualityCodec;
	}
	public void setQualityCodec(FastqQualityCodec qualityCodec) {
		this.qualityCodec = qualityCodec;
	}
	protected File getCasFile() {
		return casFile;
	}
	protected File getCasDir() {
		return casDir;
	}
	protected File getChromatDir() {
		return chromatDir;
	}
	
	@Override
	public final void transform(AssemblyTransformer transformer) throws IOException{
		if(transformer == null){
			throw new NullPointerException("transformer can not be null");
		}
		CasGappedReferenceDataStoreBuilderVisitor gappedRefVisitor = new CasGappedReferenceDataStoreBuilderVisitor(casDir);
		 
		 CasParser casParser = CasFileParser.create(casFile);
		casParser.parse(gappedRefVisitor);
		 
		 CasGappedReferenceDataStore gappedReferenceDataStore = gappedRefVisitor.build();
	
		 StreamingIterator<DataStoreEntry<NucleotideSequence>> idIter =null;
		 try{
			 idIter = gappedReferenceDataStore.entryIterator();
			 while(idIter.hasNext()){
				 DataStoreEntry<NucleotideSequence> entry = idIter.next();
				 transformer.referenceOrConsensus(entry.getKey(), entry.getValue());
			 }
			 
		 }catch(DataStoreException e){
			 throw new IOException("error getting gapped DataStore elements", e);
		 }finally{
			 IOUtil.closeAndIgnoreErrors(idIter);
		 }
		 
		 Visitor visitor = new Visitor(casFile, gappedReferenceDataStore, transformer,chromatDir, qualityCodec);
		 casParser.parse(wrapVisitor(visitor));
		 transformer.endAssembly();
		 
	}
	
	protected CasFileVisitor wrapVisitor(final CasFileVisitor transformationVisitor){
		return transformationVisitor;
	}
	
	private static class Visitor extends AbstractAlignedReadCasVisitor{

		private final AssemblyTransformer transformer;
		private final File chromatDir;
		public Visitor(File casFile,  
				CasGappedReferenceDataStore gappedReferenceDataStore,
				AssemblyTransformer transformer,
				File chromatDir,
				FastqQualityCodec qualityCodec) {
			super(casFile, gappedReferenceDataStore);
			this.transformer = transformer;
			this.chromatDir = chromatDir;
			//maybe null
			this.setQualityCodec(qualityCodec);
		}

		@Override
		public void visitAssemblyProgramInfo(String name, String version,
				String parameters) {
			transformer.assemblyCommand(name, version, parameters);
			super.visitAssemblyProgramInfo(name, version, parameters);
		}

		@Override
		public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
			for(String filename : referenceFileInfo.getFileNames()){
				try {
					File f = CasUtil.getFileFor(this.getWorkingDir(), filename);
					transformer.referenceFile(f.toURI());			
				
				} catch (FileNotFoundException e) {
					throw new IllegalStateException("reference file not found :" + filename,e );
				}
				
			}
			super.visitReferenceFileInfo(referenceFileInfo);
		}

		@Override
		public void visitReadFileInfo(CasFileInfo readFileInfo) {
			for(String filename : readFileInfo.getFileNames()){
				try {
					File f = CasUtil.getFileFor(this.getWorkingDir(), filename);
					transformer.readFile(f.toURI());			
				
				} catch (FileNotFoundException e) {
					throw new IllegalStateException("read file not found :" + filename, e);
				}
				
			}
			super.visitReadFileInfo(readFileInfo);
		}

		@Override
		protected void notAligned(Trace currentTrace) {
			ReadData readData = (ReadData)currentTrace;
			transformer.notAligned(readData.getId(),
					readData.getNucleotideSequence(),
					readData.getQualitySequence(),
					readData.getPositions(),
					readData.getUri()
					);
			
		}

		@Override
		protected void aligned(Trace traceOfRead, String referenceId,
				CasPlacedRead read) {
			ReadData readData = (ReadData)traceOfRead;
			
			transformer.aligned(
			readData.getId(),
			readData.getNucleotideSequence(),
			readData.getQualitySequence(),
			readData.getPositions(),
			readData.getUri(),
			
			referenceId,
			read.getGappedStartOffset(),
			read.getDirection(),
			read.getNucleotideSequence(),
			read.getReadInfo()
			);
		}

		@Override
		protected StreamingIterator<? extends Trace> createFastqIterator(
				File illuminaFile) throws DataStoreException {
			try {
				FastqDataStore datastore = new FastqFileDataStoreBuilder(illuminaFile)
												.hint(DataStoreProviderHint.ITERATION_ONLY)
												.build();
			return new FastqReadDatadAdaptedIterator(datastore.iterator(), illuminaFile);
			} catch (IOException e) {
				throw new IllegalStateException("fastq file no longer exists! : "+ illuminaFile.getAbsolutePath(), e);
			}
			}

		@Override
		protected StreamingIterator<? extends Trace> createSffIterator(
				File sffFile) throws DataStoreException {
			
			return new FlowgramReadDataAdaptedIterator(SffFileIterator.createNewIteratorFor(sffFile),
					sffFile);
		}

		@Override
		protected StreamingIterator<? extends Trace> createFastaIterator(
				File fastaFile) throws DataStoreException {
			try{
			NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
												.hint(DataStoreProviderHint.ITERATION_ONLY)
												.build();

			if(chromatDir ==null){
				//fasta(s) only
				return new FastaReadDataAdaptedIterator(datastore.iterator(), fastaFile);
			}else{
				return new ChromatDirFastaReadDataAdaptedIterator(datastore.iterator(), fastaFile, chromatDir);
			}
			}catch(IOException e){
				throw new DataStoreException("error parsing fasta file", e);
			}
		}
		
		
		
	}
}
 

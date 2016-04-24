/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

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
import org.jcvi.jillion.trace.fastq.FastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.sff.SffFileDataStoreBuilder;

public class CasFileTransformationService implements AssemblyTransformationService{

	
	private final File chromatDir;
	
	private FastqQualityCodec qualityCodec;
	private final CasParser casParser;
	
	private boolean fakeQVs=false;
	
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
		this.chromatDir = chromatDir;
		this.casParser = CasFileParser.create(casFile, true);
	}
	
	public CasFileTransformationService(CasParser parser) throws IOException{
		this(parser, null);
	}
	
	public CasFileTransformationService(CasParser parser, File chromatDir) throws IOException{

		Objects.requireNonNull(parser);
		this.casParser = parser;		
		this.chromatDir = chromatDir;
	}

    public void setFakeQVs(boolean fakeQVs) {
        this.fakeQVs = fakeQVs;
    }
    public FastqQualityCodec getQualityCodec() {
		return qualityCodec;
	}
	public void setQualityCodec(FastqQualityCodec qualityCodec) {
		this.qualityCodec = qualityCodec;
	}
	
	protected File getCasDir() {
		return casParser.getWorkingDir();
	}
	protected File getChromatDir() {
		return chromatDir;
	}
	
	@Override
	public final void transform(AssemblyTransformer transformer) throws IOException{
		if(transformer == null){
			throw new NullPointerException("transformer can not be null");
		}
		CasGappedReferenceDataStoreBuilderVisitor gappedRefVisitor = new CasGappedReferenceDataStoreBuilderVisitor(casParser.getWorkingDir());
		 
		
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
		 
		 Visitor visitor = new Visitor(casParser.getWorkingDir(), gappedReferenceDataStore, transformer,chromatDir, qualityCodec, fakeQVs);
		 casParser.parse(wrapVisitor(visitor));
		 transformer.endAssembly();
		 
	}
	
	protected CasFileVisitor wrapVisitor(final CasFileVisitor transformationVisitor){
		return transformationVisitor;
	}
	
	private static class Visitor extends AbstractAlignedReadCasVisitor{

		private final AssemblyTransformer transformer;
		private final File chromatDir;
		private final boolean fakeQVs;
		public Visitor(File workingDir,  
				CasGappedReferenceDataStore gappedReferenceDataStore,
				AssemblyTransformer transformer,
				File chromatDir,
				FastqQualityCodec qualityCodec,
				boolean fakeQVs) {
			super(workingDir, gappedReferenceDataStore);
			this.transformer = transformer;
			this.chromatDir = chromatDir;
			this.fakeQVs = fakeQVs;
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
        protected StreamingIterator<? extends Trace> createIteratorFor(
                FastqFileDataStore datastore) throws DataStoreException {
		    //I think the get() on the optional is safe here
		    //since we are always using real files?
		    return new FastqReadDatadAdaptedIterator(datastore.iterator(), datastore.getFile().get());
        }

      

		@Override
		protected StreamingIterator<? extends Trace> createSffIterator(
				File sffFile) throws DataStoreException, IOException {
			
			return new FlowgramReadDataAdaptedIterator( new SffFileDataStoreBuilder(sffFile).hint(DataStoreProviderHint.ITERATION_ONLY).build().iterator(),
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
 

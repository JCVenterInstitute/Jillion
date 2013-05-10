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
package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.clc.cas.AbstractAlignedReadCasVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStore;
import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.AceFileWriter;
import org.jcvi.jillion.assembly.consed.ace.AceFileWriterBuilder;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.ace.WholeAssemblyAceTag;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBallWriter;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdFileDataStoreBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdWriter;
import org.jcvi.jillion.assembly.util.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.DateUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.sff.SffFileIterator;

public class Cas2Consed extends  AbstractAlignedReadCasVisitor{

	private final Map<String, AceContigBuilder> contigBuilders;
	private final File consedOutputDir;
	private final Date phdDate = new Date();
	
	private final PhdWriter phdOut;
	
	private File chromatDir = null;
	private final File phdFile;
	
	private String prefix;
	
	public Cas2Consed(File casFile,	CasGappedReferenceDataStore gappedReferenceDataStore, File consedOutputDir,
			String prefix) throws DataStoreException, IOException {
		super(casFile, gappedReferenceDataStore);
		
		if(consedOutputDir ==null){
			throw new NullPointerException("output dir can not be null");
		}
		if(prefix == null){
			throw new NullPointerException("prefix can not be null");
		}
		this.prefix = prefix.trim();
		if(this.prefix.isEmpty()){
			throw new IllegalArgumentException("prefix must contain non-whitespace");
		}
		contigBuilders = new LinkedHashMap<String, AceContigBuilder>();
		this.consedOutputDir = consedOutputDir;
		
		StreamingIterator<String> referenceIdIterator = null;
		StreamingIterator<NucleotideSequence> referenceSequenceIterator = null;
		
		referenceIdIterator = gappedReferenceDataStore.idIterator();
		referenceSequenceIterator = gappedReferenceDataStore.iterator();
		
		try{
			while(referenceIdIterator.hasNext()){
				String id = referenceIdIterator.next();
				NucleotideSequence seq = referenceSequenceIterator.next();
				contigBuilders.put(id, new AceContigBuilder(id, seq));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(referenceIdIterator, referenceSequenceIterator);
		}
		File phdDir = new File(consedOutputDir, "phdball_dir");
		
		phdFile = new File(phdDir, "phd.ball.1");
		IOUtil.mkdirs(phdDir);
		phdOut = new PhdBallWriter(phdFile);
	}

	public final File getPhdBallFile() {
		return phdFile;
	}

	public final void setChromatDir(File chromatDir) {
		this.chromatDir = chromatDir;
	}

	@Override
	protected void visitUnMatched(Trace currentTrace) {
		//no-op
		//we don't care about reads that don't align
	}

	@Override
	protected final void visitMatch(String referenceId, CasPlacedRead read,
			Trace traceOfRead) {
		AceContigBuilder builder = contigBuilders.get(referenceId);
		if(!(traceOfRead instanceof PhdReadRecord)){
			throw new IllegalStateException("not a valid phd record " + traceOfRead);
		}
		PhdReadRecord phdReadRecord = (PhdReadRecord) traceOfRead;
		PhdInfo phdInfo = phdReadRecord.getPhdInfo();
		
		//add read to the correct builder
		builder.addRead(read.getId(), read.getNucleotideSequence(), (int) read.getGappedStartOffset(),
				read.getDirection(), read.getReadInfo().getValidRange(), phdInfo, read.getReadInfo().getUngappedFullLength());
		
		//write out phd record to phdball
		try {
			phdOut.write(phdReadRecord.getPhd());
		} catch (IOException e) {
			throw new IllegalStateException("error writing out phd record for  " + traceOfRead, e);
		}
		afterVisitMatch(referenceId, read,	phdReadRecord.getPhd(), phdInfo);
	}

	protected void afterVisitMatch(String referenceId, CasPlacedRead read,	Phd phd, PhdInfo phdInfo){
		//no-op by default
	}
	
	protected void postProcess(AceContigBuilder builder){
		//no-op by default
	}
	
	protected void visitAce(Range scaffoldRange, AceContig contig){
		//no-op
	}
	
	@Override
	public void visitEnd() {
		//we have visited the entire cas file
		//time to write out the data
		try {
			phdOut.close();
			PhdDataStore phdDataStore = new PhdFileDataStoreBuilder(phdFile)
										.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
										.build();
			File editDir = new File(consedOutputDir, "edit_dir");
			File aceFile = new File(editDir, prefix + ".ace.1");
			//TODO customize ace writer?
			AceFileWriter aceWriter = new AceFileWriterBuilder(aceFile, phdDataStore)
										.build();
			Iterator<Entry<String, AceContigBuilder>> referenceEntryIter = contigBuilders.entrySet().iterator();
			
			
			while(referenceEntryIter.hasNext()){
				Entry<String, AceContigBuilder> refEntry = referenceEntryIter.next();
				AceContigBuilder contigBuilder = refEntry.getValue();
				contigBuilder.recallConsensus(MostFrequentBasecallConsensusCaller.INSTANCE);
				postProcess(contigBuilder);
				visitBeginReference(refEntry.getKey());
				for(Entry<Range,AceContig> entry : ConsedUtil.split0xContig(contigBuilder,true).entrySet()){
                    AceContig splitContig = entry.getValue();
                    
                    visitAce(entry.getKey(), splitContig);
                    
					aceWriter.write(splitContig);
                }
				visitEndReference();
				//allows seen builders to be garbage collected if needed
				referenceEntryIter.remove();
			}
			aceWriter.write(new WholeAssemblyAceTag("phdBall", "consed",
                    DateUtil.getCurrentDate(), "../phdball_dir/"+phdFile.getName()));
			aceWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void visitBeginReference(String key) {
		// TODO Auto-generated method stub
		
	}

	protected void visitEndReference() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected StreamingIterator<PhdReadRecord> createFastqIterator(
			File illuminaFile) throws DataStoreException {
		try {
			FastqDataStore datastore = new FastqFileDataStoreBuilder(illuminaFile)
											.hint(DataStoreProviderHint.ITERATION_ONLY)
											.build();
			return new FastqConsedPhdAdaptedIterator( 
	        		datastore.iterator(),
	                illuminaFile, 
	                phdDate);
		} catch (IOException e) {
			throw new IllegalStateException("fastq file no longer exists! : "+ illuminaFile.getAbsolutePath());
		}
	}

	@Override
	protected StreamingIterator<PhdReadRecord> createSffIterator(File sffFile)
			throws DataStoreException {
		return new FlowgramConsedPhdAdaptedIterator(
                SffFileIterator.createNewIteratorFor(sffFile),
                sffFile,
                phdDate);
	}

	@Override
	protected StreamingIterator<PhdReadRecord> createFastaIterator(
			File file) throws DataStoreException {
		if(chromatDir ==null){
			try {
				NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(file)
																.hint(DataStoreProviderHint.ITERATION_ONLY)
																.build();
				return new QualFastaConsedPhdAdaptedIterator(
		                datastore.iterator(),
		                file,
		                phdDate, PhredQuality.valueOf(30));
	        } catch (IOException e) {
				throw new DataStoreException("error reading fasta file "+ file.getAbsolutePath(),e);
			}
		}else{
			try {
	        	//there should only be a few sanger traces so we can take the memory 
	        	//hit and store it all in memory
				NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(file)
															.build();
				return new ChromatDirFastaConsedPhdAdaptedIterator(
						datastore.iterator(),
						file,
		                phdDate, PhredQuality.valueOf(30),
		                chromatDir);
	        } catch (IOException e) {
				throw new DataStoreException("error reading fasta file for chromatogram "+ file.getAbsolutePath(),e);
			}
		}
	}

	
	
}

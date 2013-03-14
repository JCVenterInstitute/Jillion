package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.ace.AceContig;
import org.jcvi.jillion.assembly.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.ace.AceFileWriter;
import org.jcvi.jillion.assembly.ace.AceFileWriterBuilder;
import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.assembly.ace.WholeAssemblyAceTag;
import org.jcvi.jillion.assembly.ace.consed.ConsedUtil;
import org.jcvi.jillion.assembly.clc.cas.AbstractAlignedReadCasVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStore;
import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.assembly.util.slice.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.DateUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.sanger.phd.IndexedPhdFileDataStore;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStore;
import org.jcvi.jillion.trace.sanger.phd.PhdWriter;
import org.jcvi.jillion.trace.sff.SffFileIterator;

public class Cas2Consed extends  AbstractAlignedReadCasVisitor{

	private final Map<String, AceContigBuilder> contigBuilders;
	private final File consedOutputDir;
	private Date phdDate = new Date();
	
	private final OutputStream phdOut;
	
	private final File chromatDir = null;
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
		phdOut = new BufferedOutputStream(new FileOutputStream(phdFile));
	}

	@Override
	protected void visitUnMatched(Trace currentTrace) {
		//no-op
		//we don't care about reads that don't align
	}

	@Override
	protected void visitMatch(String referenceId, CasPlacedRead read,
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
			PhdWriter.writePhd(phdReadRecord.getPhd(), phdOut);
		} catch (IOException e) {
			throw new IllegalStateException("error writing out phd record for  " + traceOfRead, e);
		}
	}

	
	@Override
	public void visitEnd() {
		//we have visited the entire cas file
		//time to write out the data
		try {
			phdOut.close();
			PhdDataStore phdDataStore = IndexedPhdFileDataStore.create(phdFile);
			File editDir = new File(consedOutputDir, "edit_dir");
			File aceFile = new File(editDir, prefix + ".ace.1");
			//TODO customize ace writer?
			AceFileWriter aceWriter = new AceFileWriterBuilder(aceFile, phdDataStore)
										.build();
			
			Iterator<AceContigBuilder> builderIterator = contigBuilders.values().iterator();
			while(builderIterator.hasNext()){
				AceContigBuilder contigBuilder = builderIterator.next();
				contigBuilder.recallConsensus(MostFrequentBasecallConsensusCaller.INSTANCE);
				//TODO add hook to post process contig builder? add consensus recall, quality computation etc?
				
				for(Entry<Range,AceContig> entry : ConsedUtil.split0xContig(contigBuilder,true).entrySet()){
                    AceContig splitContig = entry.getValue();
                    //TODO use the range and contig to give to writer listeners 
					aceWriter.write(splitContig);
                }
				//allows seen builders to be garbage collected if needed
				builderIterator.remove();
			}
			aceWriter.write(new WholeAssemblyAceTag("phdBall", "consed",
                    DateUtil.getCurrentDate(), "../phdball_dir/"+phdFile.getName()));
			aceWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
				NucleotideSequenceFastaDataStore datastore = new NucleotideSequenceFastaFileDataStoreBuilder(file)
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
				NucleotideSequenceFastaDataStore datastore = new NucleotideSequenceFastaFileDataStoreBuilder(file)
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

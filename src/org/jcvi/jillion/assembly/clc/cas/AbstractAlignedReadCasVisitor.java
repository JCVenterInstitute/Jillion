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
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.clc.cas.ReAlignReads.ReAlignResult;
import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.assembly.clc.cas.read.DefaultCasPlacedRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.sff.SffFlowgram;
import org.jcvi.jillion.trace.sff.SffUtil;
/**
 * {@code AbstractAlignedReadCasVisitor} is a {@link CasFileVisitor}
 * that handles iterating over the aligned reads in the cas file.
 * Cas files don't actually store any read information, just pointers
 * to where the input read files exist on the file system.
 * This means that all the read alignments in the cas must be matched up 
 * exactly with the input read files.  This can get very complicated
 * so this class handles all of that for you.
 * <p>
 * This abstract class finds all the read files to be parsed
 * and maps them to the alignment information inside the cas file.
 * For each read that aligned (matched), the aligned()
 * method is called.
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractAlignedReadCasVisitor extends AbstractReadCasVisitor{

	private final CasGappedReferenceDataStore gappedReferenceDataStore;

	private FastqQualityCodec qualityCodec=null;
	
	private Map<String, ReAlignReads> reAligners = new HashMap<String, ReAlignReads>();
	/**
	 * Create a new AbstractAlignedReadCasVisitor instance that will
	 * modify the Cas Match alignments to add extra insertions into the reads (gaps)
	 * that will make it correctly align to the gapped reference in the provided
	 * {@link CasGappedReferenceDataStore}.  The gapped datastore must have
	 * indexes for all the references in the cas file being visited
	 * but can only have some gapped reference sequences available by ID.
	 * If the {@link CasGappedReferenceDataStore#contains(String)} returns {@code false}
	 * for any cas match, then that match will be skipped.
	 * 
	 * @param workingDir the working directory of the cas file;
	 * may be null (means current directory).
	 * 
	 * @param gappedReferenceDataStore {@link CasGappedReferenceDataStore}
	 * which contains the precomputed gapped referenced by 
	 * in this cas file for these alignments; can not be null.
	 * 
	 * @throws NullPointerException if gappedReferenceDataStore is null.
	 */
	public AbstractAlignedReadCasVisitor(File workingDir,
			CasGappedReferenceDataStore gappedReferenceDataStore) {
		super(workingDir);
		if(gappedReferenceDataStore ==null){
			throw new NullPointerException("gapped Reference DataStore can not be null");
		}
		this.gappedReferenceDataStore = gappedReferenceDataStore;
	}

	
	
	public FastqQualityCodec getQualityCodec() {
		return qualityCodec;
	}



	public void setQualityCodec(FastqQualityCodec qualityCodec) {
		this.qualityCodec = qualityCodec;
	}



	public final CasGappedReferenceDataStore getGappedReferenceDataStore() {
		return gappedReferenceDataStore;
	}

	
    /**
     * The given {@link Trace} aligned to the given reference id
     * with the given alignment.
     * @param referenceId the reference id that the read aligned to.
     * @param read the alignment information of the read to the reference.
     * @param traceOfRead the complete {@link Trace} that aligned.
     */
    protected abstract void  aligned(Trace traceOfRead, String referenceId, CasPlacedRead read);
   
    
	
	@Override
	protected void aligned(Trace currentTrace, String referenceId, CasMatch match) {
		CasAlignment alignment = match.getChosenAlignment();
		long refIndex = alignment.getReferenceIndex();
		String refId = gappedReferenceDataStore.getIdByIndex(refIndex);
		String readId = currentTrace.getId();
		try {
			
			if(refId ==null){
				closeIterator();
				throw new IllegalStateException("could not get get gapped reference for index "+ refIndex);
			
			}
			if(!gappedReferenceDataStore.contains(refId)){
				return;
			}
			ReAlignReads reAligner =reAligners.get(refId);
			if(reAligner ==null){
				NucleotideSequence gappedReference = gappedReferenceDataStore.get(refId);
				reAligner = new ReAlignReads(gappedReference, true);
				reAligners.put(refId, reAligner);
			}
			 
	        Range trimRange = match.getTrimRange();
	        if(trimRange ==null && currentTrace instanceof SffFlowgram){
	        	//CLC uses the trimmed flowgrams when aligning
	        	//if the trimRange for this match isn't explicitly set
	        	//and the read is a flowgram, then use it's trim range 
	        	trimRange = SffUtil.computeTrimRangeFor((SffFlowgram)currentTrace);
	        }
	        Direction dir = alignment.readIsReversed()? Direction.REVERSE : Direction.FORWARD;
	       ReAlignResult reAlignResult= reAligner.realignValidBases(currentTrace.getNucleotideSequence(), 
	    		   alignment.getStartOfMatch(),
	        		dir,
	        			alignment.getAlignmentRegions(), trimRange);
	        
	       CasPlacedRead read=  new DefaultCasPlacedRead(readId, 
	    		  (ReferenceMappedNucleotideSequence) reAlignResult.getGappedValidBases(),
	    		   reAlignResult.getGappedStartOffset(), reAlignResult.getValidRange(), 
	    		   dir,(int)currentTrace.getNucleotideSequence().getLength());
	       
	      
	        
	        AbstractAlignedReadCasVisitor.this.aligned(currentTrace, refId, read);
		} catch (Throwable e) {
			closeIterator();
			throw new IllegalStateException("processing read " + readId + " for reference "+ refId, e);
		
		}
	}



	
}

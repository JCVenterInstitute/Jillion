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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceContigReadVisitor;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigReadVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileParser;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class AceContigTestUtil {

   
	/**
	 * Pull out the full length sequences (which are stored in the 
	 * ace file) for all reads and return it as a {@link NucleotideSequenceDataStore}.
	 * @param aceFile
	 * @return
	 * @throws IOException
	 */
	public static final NucleotideSequenceDataStore createFullLengthSeqDataStoreFrom(File aceFile) throws IOException{
		final Map<String,NucleotideSequence> fullSequences = new HashMap<String, NucleotideSequence>();
		
		AceFileVisitor visitor2 = new AbstractAceFileVisitor(){

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {

				return new AbstractAceContigVisitor() {
					@Override
					public AceContigReadVisitor visitBeginRead(final String readId,
							final int gappedLength) {
						return new AbstractAceContigReadVisitor() {
							NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(gappedLength);
							@Override
							public void visitBasesLine(String mixedCaseBasecalls) {
								builder.append(mixedCaseBasecalls);
							}

							@Override
							public void visitEnd() {
								//full length means ungapped right?
								builder.ungap();
								fullSequences.put(readId, builder.build());
							}
							
						};
					}
					
				};
			}
			
		};
		
		
		
		AceFileParser.create(aceFile).parse(visitor2);
		return DataStore.of(fullSequences, NucleotideSequenceDataStore.class);
	}
    
    public static  void assertContigsEqual(Contig<? extends AssembledRead> expected, Contig<? extends AssembledRead> actual) {
        assertEquals(expected.getId(), actual.getId()); 
        assertEquals(expected.getConsensusSequence(), actual.getConsensusSequence());
        assertEquals(expected.getId(),expected.getNumberOfReads(), actual.getNumberOfReads());
        StreamingIterator<? extends AssembledRead> iter = null;
        try{
        	iter = expected.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead expectedRead = iter.next();
        		assertPlacedReadParsedCorrectly(expectedRead, actual.getRead(expectedRead.getId()));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        //now iterate over actual
        try{
        	iter = actual.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead actualRead = iter.next();
        		assertPlacedReadParsedCorrectly(actualRead, expected.getRead(actualRead.getId()));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
    }

    public static  void assertPlacedReadParsedCorrectly(AssembledRead expected,
            AssembledRead actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getId(), expected.getGappedStartOffset(), actual.getGappedStartOffset());
        assertEquals(expected.getId(), expected.getGappedEndOffset(), actual.getGappedEndOffset());
        assertEquals(expected.getId(), expected.getGappedLength(), actual.getGappedLength());
        assertEquals(expected.getId(), expected.getReadInfo().getValidRange(), actual.getReadInfo().getValidRange());
        assertEquals(expected.getId(), expected.getNucleotideSequence(), actual.getNucleotideSequence());
        
    }
}

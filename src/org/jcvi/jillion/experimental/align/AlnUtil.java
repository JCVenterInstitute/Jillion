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
package org.jcvi.jillion.experimental.align;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCollectors;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.core.io.TextLineParser;

public final class AlnUtil {
	
	private AlnUtil(){
		//can not instantiate
	}
	/**
	 * Checks to see if the given header string
	 * is valid. A valid header is a single line
	 * that starts with the text
	 * "CLUSTAL" or "MUSCLE".
	 * @param header the header to validate; can not be null.
	 * @return {@code true}
	 * if the header is valid,
	 * {@code false} otherwise.
	 * @throws NullPointerException if header is null
	 * @throws IllegalStateException if there is a problem
	 * parsing the header text (should not happen).
	 */
	 public static boolean validHeader(String header) {
		 if(header.isEmpty()){
			 return false;
		 }
		 //check is one line?
		 TextLineParser parser=null;
		 try(BufferedReader reader = new BufferedReader(new StringReader(header))){
			
			 reader.readLine();
			 if(reader.read() !=-1){
				 return false;
			 }
		 }catch(IOException e){
			 //will never happen
			 throw new IllegalStateException("error reading aln header", e);
		 }finally{
			 IOUtil.closeAndIgnoreErrors(parser);
		 }
		 
	    	//first line of aln must say either "CLUSTAL W" or "CLUSTALW"
		 //muscle non-strict starts with "MUSCLE"
		 //other info in first line is ignored.
	    	return header.startsWith("CLUSTAL") || header.startsWith("MUSCLE");
			
		}
	 /**
	  * Compute the consensus of the alignment in the given aln file
	  * using the given ConsensusCaller.
	  * 
	  * @param alnFile the alignment file to parse, can not be null and must exist.
	  * @param consensusCaller the ConsensusCaller to use; can not be null.
	  * @return a new {@link NucleotideSequence} of the called consensus.
	  * 
	  * @throws DataStoreException
	  * @throws IOException if there is a problem parsing the file.
	  */
	 public static NucleotideSequence computeConsensus(File alnFile, ConsensusCaller consensusCaller) throws DataStoreException, IOException{
	     Objects.requireNonNull(consensusCaller);
	     
	     return GappedNucleotideAlignmentDataStore.createFromAlnFile(alnFile)
                     .entryIterator()
                     .toStream()
                     .collect(ConsensusCollectors.toDataStoreConsensus(consensusCaller));

	 }
}

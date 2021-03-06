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
package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.clc.cas.CasFileParser;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStore;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStoreBuilderVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasParser;
import org.jcvi.jillion.assembly.consed.ace.AceAssembledRead;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStore;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStoreBuilder;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractTestCas2Consed {
	private final ResourceHelper RESOURCES = new ResourceHelper(AbstractTestCas2Consed.class); 
	 private TigrContigDataStore expectedDataStore;
	 private final String pathToCas;
	 private final String pathToExpectedContig;;
	 
   
	@Rule
	    public TemporaryFolder folder = new TemporaryFolder();
	    
	protected AbstractTestCas2Consed(String pathToCas, String pathToContig) {
		this.pathToCas = pathToCas;
		this.pathToExpectedContig = pathToContig;
	}
	@Before
    public void setup() throws IOException{
	 NucleotideFastaDataStore fastas = new NucleotideFastaFileDataStoreBuilder(RESOURCES.getFile("../files/15050.fasta"))
		.build();
        
		expectedDataStore = new TigrContigFileDataStoreBuilder(RESOURCES.getFile(pathToExpectedContig), fastas)
        						.build();
   	    
	 }
	    
	    @Test
	    public void parseCasWithoutValidating() throws IOException, DataStoreException{	    	
	        parseCas(false);
	    }
	    @Test
	    public void parseCasAndValidate() throws IOException, DataStoreException{
	    	
	        parseCas(true);
	    }
		private void parseCas(boolean validate) throws IOException,
				DataStoreException {
			File casFile = RESOURCES.getFile(pathToCas);
	        CasGappedReferenceDataStoreBuilderVisitor gappedRefVisitor = new CasGappedReferenceDataStoreBuilderVisitor(casFile.getParentFile());
	        CasParser casFileParser = CasFileParser.create(casFile, validate);
			casFileParser.parse(gappedRefVisitor);
	        CasGappedReferenceDataStore gappedReferenceDataStore = gappedRefVisitor.build();
	        
	        File consedDir = folder.newFolder("consed");
	        String prefix = "cas2consed";
	      Cas2Consed cas2consed = new Cas2Consed(casFile,gappedReferenceDataStore, consedDir, prefix);
	     
	      casFileParser.parse(cas2consed);

	      File editDir = new File(consedDir, "edit_dir");
	      File aceFile = new File(editDir, prefix+".ace.1");
	      AceFileDataStore dataStore =new AceFileDataStoreBuilder(aceFile)
												.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED)
												.build();
	        assertEquals("# contigs", expectedDataStore.getNumberOfRecords(), dataStore.getNumberOfRecords());
	        StreamingIterator<AceContig> iter = dataStore.iterator();
	       try{
		        while(iter.hasNext()){
		        	AceContig contig = iter.next();
		        
		    	  TigrContig expectedContig= getExpectedContig(contig.getId());
		    	  assertEquals("consensus", expectedContig.getConsensusSequence(),
		    			  contig.getConsensusSequence());
		    	  assertEquals("# reads", expectedContig.getNumberOfReads(), contig.getNumberOfReads());
		    	  
		    	  assertReadsCorrectlyPlaced(contig, expectedContig);
		    	  
		      }
	        }finally{
	        	IOUtil.closeAndIgnoreErrors(iter);
	        }
		}

		private void assertReadsCorrectlyPlaced(AceContig contig,
				TigrContig expectedContig) {
			StreamingIterator<AceAssembledRead> iter = null;
			try{
				iter = contig.getReadIterator();
				while(iter.hasNext()){
					AceAssembledRead actualRead = iter.next();
				String readId = actualRead.getId();
				AssembledRead expectedRead = expectedContig.getRead(readId);
				assertEquals("read basecalls", expectedRead
						.getNucleotideSequence(), actualRead
						.getNucleotideSequence());
				assertEquals("read offset", expectedRead.getGappedStartOffset(),
						actualRead.getGappedStartOffset());

				}
			}finally{
				IOUtil.closeAndIgnoreErrors(iter);
			}
		}
	    /**
	     * cas2Consed now appends coordinates to the end of the contig
	     * if they don't get full reference length, strip that out 
	     * to get the corresponding expected flap assembly which
	     * doesn't do that.
	     */
	    private TigrContig getExpectedContig(String actualContigId) throws DataStoreException{
	        String IdWithoutCoordinates = actualContigId.replaceAll("_.+", "");
	        return expectedDataStore.get(IdWithoutCoordinates);
	    }
}

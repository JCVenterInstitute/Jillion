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
/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.internal.ResourceHelper;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class AceTagsFromAceFileDataStoreConsensusTagNoTimeInfo {

    ResourceHelper RESOURCES = new ResourceHelper(AceTagsFromAceFileDataStoreConsensusTagNoTimeInfo.class);
    String fileName = "files/sample.ContigTagNotTimeInfo.ace";
    
    WholeAssemblyAceTag expectedWholeAssemblyTag = new WholeAssemblyAceTag(
            "phrap_params", "phrap", 
            new DateTime(1999, 6, 21,16, 19, 47, 0).toDate(), 
            "/usr/local/genome/bin/phrap standard.fasta.screen -new_ace -view \nphrap version 0.990319");
    
    ConsensusAceTag consensusTag0 = new ConsensusAceTagBuilder(
                                            "Contig1", "repeat", "consed",
                                            new DateTime(1997, 12, 18, 18, 6, 23, 0).toDate(), 
                                            Range.of(976,986), 
                                            false)
                                            .build();
    ConsensusAceTag consensusTag1 = new ConsensusAceTagBuilder(
            "Contig1", "comment", "consed",
            new DateTime(1997, 12, 18, 18, 6, 23, 0).toDate(), 
            Range.of(996,1007), 
            false)
            .appendData("This is line 1 of a comment\nThere may be any number of lines\n")
            .build();
    
    ConsensusAceTag consensusTag2 = new ConsensusAceTagBuilder(
            "Contig1", "oligo", "consed",
            new DateTime(1997, 12, 18, 18, 6, 23, 0).toDate(), 
            Range.of(963,987), 
            false)
            .appendData("standard.1 acataagacattctaaatttttact 50 U\nseq from clone\n")
            .build();
    
    ConsensusAceTag consensusTag3 = new ConsensusAceTagBuilder(
            "Contig853", "join", "consed",
            new DateTime(2009, 12, 28, 11, 38, 57, 0).toDate(), 
            Range.of(437,437), 
            false)
            .addComment("old contigs:\nContig844 pinned pos: 511 length: 1324 reads: 1\nContig850 pinned pos: 23 length: 208,876 reads: 29,325\nace file: /local/closure10/HMP/HMP084/Newbler_091709_consed/hmp084/assembly/cons\ned/edit_dir/454Contigs.ace.176\nnew contig Contig853  length: 208,876 reads: 29,326\n")
            .build();
    
    ConsensusAceTag consensusTag4 = new ConsensusAceTagBuilder(
            "Contig853", "contigEndPair", "consed",
            new DateTime(2009, 12, 28, 12, 10, 44, 0).toDate(), 
            Range.of(10,10), 
            false)
            .appendData("3\n<-gap\nggcctcgggg\n")
            .build();
    ReadAceTag readTag1 = new ReadAceTag("djs14_680.s1", "matchElsewhereLowQual",
    		"phrap", new DateTime(1999, 8, 23, 11, 43, 56, 0).toDate(), 
    		Range.of(903,932)
    		, true);

    
    
    @Parameters
    public static List<Object[]> data(){
    	Function<File, AceFileDataStore> defaultImpl = (file)-> {
    		try{
    			return new AceFileDataStoreBuilder(file).build();
    		}catch(IOException e){
    			throw new UncheckedIOException(e);
    		}
    	};
    	Function<File, AceFileDataStore> indexed = (file)-> {
    		try{
    			return new AceFileDataStoreBuilder(file)
    					.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
    					.build();
    		}catch(IOException e){
    			throw new UncheckedIOException(e);
    		}
    	};
    	
    	Function<File, AceFileDataStore> large = (file)-> {
    		try{
    			return new AceFileDataStoreBuilder(file)
    					.hint(DataStoreProviderHint.ITERATION_ONLY)
    					.build();
    		}catch(IOException e){
    			throw new UncheckedIOException(e);
    		}
    	};
    	
    	return Arrays.<Object[]>asList(
    			new Object[]{defaultImpl },
    			new Object[]{indexed },
    			new Object[]{large }
    			);
    }
    
    
    private final AceFileDataStore sut;
    
    public AceTagsFromAceFileDataStoreConsensusTagNoTimeInfo(Function<File, AceFileDataStore> factory) throws IOException{
    	this.sut = factory.apply(RESOURCES.getFile(fileName));
    }
    
        
    private <E> List<E> toList(Iterator<E> iter){
    	List<E> list = new ArrayList<E>();
    	while(iter.hasNext()){
    		list.add(iter.next());
    	}
    	return list;
    }
    @Test
    public void wholeAssemblyTag() throws DataStoreException{
        List<WholeAssemblyAceTag> tags = toList(sut.getWholeAssemblyTagIterator());
        assertEquals(1,tags.size());
        final WholeAssemblyAceTag wholeAssemblyAceTag = tags.get(0);
        assertEquals(expectedWholeAssemblyTag, wholeAssemblyAceTag);
    }
    @Test
    public void readTag() throws DataStoreException{
        List<ReadAceTag> tags = toList(sut.getReadTagIterator());
        assertEquals(1,tags.size());
        final ReadAceTag readTag = tags.get(0);
        assertEquals(readTag1, readTag);
    }
    @Test
    public void consensusTags() throws DataStoreException{
        List<ConsensusAceTag> actualTags = toList(sut.getConsensusTagIterator());
        assertEquals(5, actualTags.size());
        assertEquals(consensusTag0, actualTags.get(0));
        assertEquals(consensusTag1, actualTags.get(1));
        assertEquals(consensusTag2, actualTags.get(2));
        assertEquals(consensusTag3, actualTags.get(3));
        assertEquals(consensusTag4, actualTags.get(4));
    }
    
}

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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.internal.fasta.qual.DefaultQualityFastaFileDataStoreBuilder;
/**
 * {@code DefaultQualityFastaFileDataStore} is the default implementation
 * of {@link QualitySequenceFastaDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeQualityFastaFileDataStore
 *
 */
final class DefaultQualityFastaFileDataStore {
    
	private DefaultQualityFastaFileDataStore(){
		//can not instantiate
	}
    public static QualityFastaDataStore create(File fastaFile) throws IOException{
    	return create(fastaFile,DataStoreFilters.alwaysAccept(), null);
    }
    
    public static QualityFastaDataStore create(File fastaFile, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) throws IOException{
    	return create(FastaFileParser.create(fastaFile), filter, recordFilter);
    }
    
    public static QualityFastaDataStore create(InputStream fastaStream) throws IOException{
    	return create(fastaStream,DataStoreFilters.alwaysAccept(), null);
    }
    public static QualityFastaDataStore create(InputStream fastaStream, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) throws IOException{
    	
    	FastaParser parser = FastaFileParser.create(fastaStream);
    	
    	return create(parser, filter, recordFilter);
    }
	public static QualityFastaDataStore create(FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) throws IOException {
		DefaultQualityFastaFileDataStoreBuilder builder = createBuilder(filter, recordFilter);
		parser.parse(builder);
    	return builder.build();
	}

    private static DefaultQualityFastaFileDataStoreBuilder createBuilder(Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter){
    	return new DefaultQualityFastaFileDataStoreBuilder(filter, recordFilter);
    }

}

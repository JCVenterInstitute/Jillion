package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * Builder class that will create
 * new instances of {@link SamFileDataStoreBuilder}
 * using the provided configuration information.
 * 
 * @author dkatzel
 *
 */
public final class SamFileDataStoreBuilder {

    private final File samFile;
    private File baiFile;
    private SamAttributeValidator validator = ReservedAttributeValidator.INSTANCE;
    
    private Predicate<SamRecord> filter;
    /**
     * Create a new Builder instance that will parse the given
     * sam or bam encoded file.  
     * If there is an accompanying BAI file in the same directory named
     * {@code samFile.getName() + ".bai"}, then the index will be automatically
     * detected and used by the Datastore to improve parsing runtime.
     * If there is an index file but it does not follow the usual
     * bam index conventions, then use {@link #indexFile(File)} method
     * to set the path to the index file.
     * 
     * @param samFile the sam or bam file to use; can not be null.
     * 
     * @throws IOException if the file does not exist or is not readable.
     * @throws NullPointerException if samFile is null.
     * 
     * @see #indexFile(File)
     */
    public SamFileDataStoreBuilder(File samFile) throws IOException{
        IOUtil.verifyIsReadable(samFile);
        this.samFile = samFile;
    }
    /**
     * Set the non-standard path to the bam index file if there is one
     * and it doesn't follow the usual bam file and naming convention
     * of being in the same folder as the bam file and named
     * {@code bamFile.getName() + ".bai"}.
     * 
     * @param baiFile the bai index file; can not be null, and
     * must exist and be readable.
     * 
     * @return this
     * @throws IOException if the file does not exist or is not readable.
     * @throws NullPointerException if baiFile is null.
     * 
     */
    public SamFileDataStoreBuilder indexFile(File baiFile) throws IOException{
        IOUtil.verifyIsReadable(baiFile);
        this.baiFile = baiFile;
        
        return this;
    }
    /**
     * Set the {@link SamAttributeValidator} to use when parsing the
     * sam or bam file.  If this method is not called, then the default
     * validator, {@link ReservedAttributeValidator},  is used.
     * 
     * @param validator the validator to use; can not be null
     * @return this
     * 
     * @throws NullPointerException if validator is null.
     * 
     * @see ReservedAttributeValidator
     */
    public SamFileDataStoreBuilder validator(SamAttributeValidator validator){
        Objects.requireNonNull(validator);
        this.validator = validator;
        
        return this;
    }
    
    /**
     * Add a filter that will exclude any {@link SamRecord}s in the
     * sam or bam file that do not pass the filter.  If this method
     * is called, then no filtering is performed, all records will
     * be included in the datastore to be built.
     * 
     * @param filter the filter used to exclude records, can not be null.
     * 
     * @throws NullPointerException if filter is null.
     * 
     * @return this.
     */
    public SamFileDataStoreBuilder filter(Predicate<SamRecord> filter){
        Objects.requireNonNull(filter);
        this.filter = filter;
        
        return this;
    }
    
    /**
     * Create a new {@link SamFileDataStore} using the configuration
     * provided so far.
     * 
     * @return a new {@link SamFileDataStore}; will never be null.
     * 
     * @throws IOException if there is a problem parsing the sam or bam file
     * or the bai index file if there is one.
     */
    public SamFileDataStore build() throws IOException{
        SamParser parser;
        if(baiFile ==null){
            parser = SamParserFactory.create(samFile, validator);
        }else{
            parser = SamParserFactory.createUsingIndex(samFile, baiFile, validator);
        }
        SamHeader header = parser.getHeader();
        if(SortOrder.QUERY_NAME.equals(header.getSortOrder())){
            return new QuerySortedSamFileDataStore(parser, filter);
        }
        return new DefaultSamFileDataStore(parser, filter);
    }
}

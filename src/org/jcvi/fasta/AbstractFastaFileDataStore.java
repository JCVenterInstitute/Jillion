/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;
import org.jcvi.datastore.DataStore;
/**
 * {@code AbstractFastaFileDataStore} is a {@link DataStore} implementation
 * of FastaRecords parsed from a Fasta file.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractFastaFileDataStore<T extends FastaRecord> implements FastaVisitor, DataStore<T>{

    @Override
    public void visitLine(String line) {
        
    }

    @Override
    public void visitEndOfFile() {
        
    }

    @Override
    public void visitFile() {
        
    }

    @Override
    public void visitBodyLine(String bodyLine) {
        
    }

    @Override
    public void visitDefline(String defline) {
        
    }

    
}

/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.fasta;

import java.io.IOException;
import java.util.Objects;

import org.jcvi.jillion.core.Sequence;

class FastaWriterAdapterImpl<S, T extends Sequence<S>, F extends FastaRecord<S, T>> implements FastaWriter<S, T, F> {

    private final FastaWriter<S, T, F> delegate;
    private final FastaRecordAdapter<S, T, F> adapter;
    
    public FastaWriterAdapterImpl(FastaWriter<S, T, F> delegate,
            FastaRecordAdapter<S, T, F> adapter) {
        this.delegate = Objects.requireNonNull(delegate);
        this.adapter = Objects.requireNonNull(adapter);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void write(String id, T sequence, String optionalComment)
            throws IOException {
        F adapted = adapter.adapt(id, sequence, optionalComment);
        if(adapted !=null){
            delegate.write(adapted);
        }
    }
    
}

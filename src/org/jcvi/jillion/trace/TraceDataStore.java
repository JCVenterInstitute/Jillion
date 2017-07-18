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
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;

public interface TraceDataStore<T extends Trace> extends DataStore<T> {
    /**
     * Return a new DataStore is a "view" of just the {@link org.jcvi.jillion.core.residue.nt.NucleotideSequence}s from this datastore.  
     * This DataStore
     * is the backing datastore so all calls to the returned Sequence Datastore will delegate to this trace datastore
     * and then get adapted to return just the sequence.  When this trace datastore closes, the returned
     * datastore will also close and vice versa.  Closing this datastore will close the other as well.
     * 
     * @return A new DataStore instance which is a linked view of this trace DataStore
     * but adapted so that all the records will just be the sequences.
     * 
     * @since 5.3
     */
    default NucleotideSequenceDataStore asSequenceDataStore(){
        return DataStore.adapt(NucleotideSequenceDataStore.class, this, Trace::getNucleotideSequence);
    }
    /**
     * Return a new DataStore is a "view" of just the {@link org.jcvi.jillion.core.qual.QualitySequence}s from this datastore.  
     * This DataStore
     * is the backing datastore so all calls to the returned Sequence Datastore will delegate to this trace datastore
     * and then get adapted to return just the sequence.  When this trace datastore closes, the returned
     * datastore will also close and vice versa.  Closing this datastore will close the other as well.
     * 
     * @return A new DataStore instance which is a linked view of this trace DataStore
     * but adapted so that all the records will just be the sequences.
     * 
     * @since 5.3
     */
    default QualitySequenceDataStore asQualityDataStore(){
        return DataStore.adapt(QualitySequenceDataStore.class, this, Trace::getQualitySequence);
    }
}

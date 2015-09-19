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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
/**
 * {@code FastaDataStore} is a marker interface
 * for a {@link DataStore} for {@link FastaRecord}s.
 * @author dkatzel
 *
 * @param <S> the type of object in the sequence encoding.
 * @param <T> the type of {@link Sequence} of in the fasta.
 * @param <F> the type of {@link FastaRecord} in the datastore.
 */
public interface FastaDataStore<S, T extends Sequence<S>,F extends FastaRecord<S,T>> extends DataStore<F>{

    

}
